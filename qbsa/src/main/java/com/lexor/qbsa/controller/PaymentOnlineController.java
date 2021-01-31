package com.lexor.qbsa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexor.qbsa.service.payment.authorizenet.CreateAnAcceptPaymentTransaction;
import com.lexor.qbsa.service.queue.QueueService;
import com.lexor.qbsa.util.Utility;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.mvc.Viewable;

/**
 * REST Web Service
 *
 * @author thanh
 */
@Path("paymentonline")
@RequestScoped
public class PaymentOnlineController extends ApplicationController {
    private static final Logger LOG = Logger.getLogger(WebhooksController.class.getName());
    @Inject
    private QueueService queueService;

//    @Context
//    private UriInfo context;
    @Inject
    private CreateAnAcceptPaymentTransaction acceptCaller;

    /**
     * Creates a new instance of HelloResource
     */
    public PaymentOnlineController() {
    }

    /**
     * Retrieves representation of an instance of com.lexor.qbsa.HelloController
     *
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getHtml() {
        Map<String, String> model = new HashMap<>();
        model.put("hello", "Hello");
        model.put("world", "World");
        return new Viewable("/paymentonline", model);
    }

    @POST
    @Path("/transactionCaller")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response transactionCaller(
            @FormParam("amount") String amount,
            @FormParam("dataDesc") String dataDesc,
            @FormParam("dataValue") String dataValue,
            @FormParam("customer") String customer,
            @FormParam("invoice") String invoice) throws URISyntaxException {

        Double amt = Double.parseDouble(amount);
        Map<String, String> payload = acceptCaller.run(amt, dataDesc, dataValue);
        String json = "";
        try {
            payload.put("CustomerId", customer);
            payload.put("InvoiceId", invoice);
            json = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }

        if (payload.containsKey("TransId")) {
            String paymentData = String.format("{\"TotalAmt\": %.2f,\"Line\": [{\"Amount\": %.2f,\"LinkedTxn\": [{\"TxnId\": \"%s\",\"TxnType\": \"Invoice\"}]}],\"CustomerRef\": {\"value\": \"%s\"}, \"PrivateNote\": \"TransId=%s,AuthCode=%s\"}", amt, amt, invoice, customer, payload.get("TransId"), payload.get("AuthCode"));
            Response res = Utility.doPost("payment", paymentData);
            System.out.println(res.getEntity());
            String paymentRes = (String) res.getEntity();
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(paymentRes, Map.class);
                Map<String, Object> paymentJson = (Map<String, Object>) map.get("Payment");
                String id = (String) paymentJson.get("Id");
                payload.put("PaymentId", id);
                json = new ObjectMapper().writeValueAsString(payload);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "{0}", ex);
            }
        }
        try {
            queueService.add("authorizenet", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok(json).build();
    }
    
    @POST
    @Path("/authorizecallback")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response testReceiveData(String data, @Context HttpServletRequest request) throws IOException {
        try {
            queueService.add("authorizenet", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok("{\"status\":\"ok\"}").build();
    }
}
