package com.lexor.qbsa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexor.qbsa.service.payment.authorizenet.AuthorizeNetConfigService;
import com.lexor.qbsa.service.payment.authorizenet.CreateAnAcceptPaymentTransaction;
import com.lexor.qbsa.service.payment.authorizenet.GetAnAcceptPaymentToken;
import com.lexor.qbsa.service.payment.paysimple.GetCheckoutToken;
import com.lexor.qbsa.service.queue.QueueService;
import com.lexor.qbsa.util.ErpUtility;
import com.lexor.qbsa.util.Utility;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
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
    
    @Inject
    private GetAnAcceptPaymentToken acceptHostedToken;

    @Inject
    private AuthorizeNetConfigService authorizeConfig;
    
    @Inject
    private GetCheckoutToken checkoutToken;

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
    public Response getHtml(@Context final HttpServletRequest request) {
        String qbchecked = request.getParameter("qbchecked");

        if (!Utility.doRenew() || qbchecked == null || qbchecked.trim().length() == 0) {
            try {
                String url = request.getRequestURL().toString();
                String query = request.getQueryString();
                String reqString = url + "?" + query + "&qbchecked=true";
                return Response.temporaryRedirect(new URI("/qbsa/api/auth?target=" + URLEncoder.encode(reqString, "UTF-8"))).build();
//                return Response.temporaryRedirect(new URI("/qbsa/api/auth")).build();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "{0}", ex);
            }
        }

        String customer_id = request.getParameter("customer_id");
        String quickbook_customer_id = request.getParameter("quickbook_customer_id");
        String order_id = request.getParameter("order_id");

        Map<String, String> model = new HashMap<>();
        model.put("customer_id", customer_id);
        model.put("quickbook_customer_id", quickbook_customer_id);
        model.put("order_id", order_id);
        model.put("clientKey", authorizeConfig.getClientKey());
        model.put("apiLoginID", authorizeConfig.getApiLoginId());
//        model.put("jsurl", authorizeConfig.getJsUrl());
        model.put("apiurl", authorizeConfig.getApiUrl());
        return Response.ok( new Viewable("/paymentonline", model)).build();
    }

    @POST
    @Path("/requestPayment")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response requestPayment(
            @FormParam("amount") String amount,
            @FormParam("paymentgate") String paymentgate,
            @FormParam("customer_id") String customer_id) throws URISyntaxException {

        Double amt;
        String hpToken = "";
        Map<String, String> payload = new HashMap<>();
        try {
            amt = Double.parseDouble(amount);
        } catch(Exception ex) {
            amt = 0.0;
        }
        if ("authorize".equals(paymentgate)) {
            hpToken = acceptHostedToken.run(amt);
        }
        if ("paysimple".equals(paymentgate)) {
            hpToken = checkoutToken.run();
            try {
                Response erpres = ErpUtility.doGetCustomer(customer_id);
                String cusDataStr = (String) erpres.getEntity();
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(cusDataStr, Map.class);
                if (!map.containsKey("message")) {
                    Map<String, Object> contactInfo = (Map<String, Object>) map.get("contactInfo");
                    String firstName = (String) contactInfo.get("firstName");
                    String lastName = (String) contactInfo.get("lastName");
                    String cellPhone = (String) contactInfo.get("cellPhone");
                    String busPhone = (String) contactInfo.get("busPhone");
                    String email = (String) contactInfo.get("email");
                    payload.put("firstName", firstName);
                    payload.put("lastName", lastName);
                    payload.put("cellPhone", cellPhone);
                    payload.put("busPhone", busPhone);
                    payload.put("email", email);
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "{0}", e);
            }
        }
        String json = "";
        try {
            payload.put("hp_token", hpToken);
            json = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
        return Response.ok(json).build();
    }
    
    @POST
    @Path("/completePayment")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response completePayment(@Context HttpServletRequest request) throws URISyntaxException {

        String account = request.getParameter("account");
        
        String json = "";
        Map<String, String> payload = new HashMap<>();
        try {
            payload.put("account", account);
            json = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
//        try {
//            queueService.add("paysimple", json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return Response.ok(account).build();
    }

    @POST
    @Path("/transactionCaller")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response transactionCaller(
            @FormParam("amount") String amount,
            @FormParam("dataDesc") String dataDesc,
            @FormParam("dataValue") String dataValue,
            @FormParam("customer_id") String customer_id,
            @FormParam("quickbook_customer_id") String quickbook_customer_id,
            @FormParam("order_id") String order_id,
            @FormParam("payment_method") String payment_method) throws URISyntaxException {

        Double amt = Double.parseDouble(amount);
        Map<String, String> payload = acceptCaller.run(amt, dataDesc, dataValue);

        if (payload.containsKey("TransId")) {
            
            if (quickbook_customer_id == null || quickbook_customer_id.trim().length() == 0) {
                // get customer info
                Response erpres = ErpUtility.doGetCustomer(customer_id);
                String cusDataStr = (String) erpres.getEntity();
                if (cusDataStr.trim().length() > 0) {
                    // create qb customer
                    String qbcus = ErpUtility.convertErpCustomerToQbCustomer(customer_id, cusDataStr);
                    if (qbcus != null) {
                        Response res = Utility.doPost("customer", qbcus);
                        String cusRes = (String) res.getEntity();
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            Map<String, Object> map = mapper.readValue(cusRes, Map.class);
                            Map<String, Object> customerJson = (Map<String, Object>) map.get("Customer");
                            quickbook_customer_id = (String) customerJson.get("Id");
                            payload.put("QuickBookCustomerId", quickbook_customer_id);
                            ErpUtility.doNotifyCompleteCustomerQB(customer_id, quickbook_customer_id, order_id);
                        } catch (Exception ex) {
                            LOG.log(Level.SEVERE, "{0}", ex);
                        }
                    }
                }
            }

            Map<String, Object> paymentPayload = new HashMap<>();
            paymentPayload.put("TotalAmt", String.format("%.2f", amt));
            Map<String, Object> CustomerRef = new HashMap<>();
            CustomerRef.put("value", quickbook_customer_id);
            paymentPayload.put("CustomerRef", CustomerRef);
            paymentPayload.put("PrivateNote", String.format("TransId=%s,AuthCode=%s", payload.get("TransId"), payload.get("AuthCode")));
            paymentPayload.put("PaymentRefNum", order_id);
            String paymentData = "";
            try {
                paymentData = new ObjectMapper().writeValueAsString(paymentPayload);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "{0}", ex);
            }

            //String paymentData = String.format("{\"TotalAmt\": %.2f,\"CustomerRef\": {\"value\": \"%s\"}, \"PrivateNote\": \"TransId=%s,AuthCode=%s\", \"TxnSource\": \"%s\", \"PaymentRefNum\": \"%s\" }", amt, quickbook_customer_id, payload.get("TransId"), payload.get("AuthCode"), payload.get("TransId"), order_id);

            Response res = Utility.doPost("payment", paymentData);
            System.out.println(res.getEntity());
            String paymentRes = (String) res.getEntity();
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(paymentRes, Map.class);
                Map<String, Object> paymentJson = (Map<String, Object>) map.get("Payment");
                String paymentId = (String) paymentJson.get("Id");
                payload.put("PaymentId", paymentId);
                ErpUtility.doNotifyCompletePaymentQB(paymentId, payload.get("TransId"), order_id, amt);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "{0}", ex);
            }
        }
        String json = "";
        try {
            payload.put("CustomerId", customer_id);
            payload.put("QuickBookCustomerId", quickbook_customer_id);
            payload.put("OrderId", order_id);
            json = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
        try {
            queueService.add("authorizenet", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.ok(json).build();
    }
    
    @POST
    @Path("/transactionResponse")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response transactionResponse(
            @FormParam("amount") String amount,
            @FormParam("transId") String transId,
            @FormParam("authorization") String authorization,
            @FormParam("customer_id") String customer_id,
            @FormParam("quickbook_customer_id") String quickbook_customer_id,
            @FormParam("order_id") String order_id) throws URISyntaxException {

        Double amt = Double.parseDouble(amount);
        Map<String, String> payload = new HashMap<>();
        payload.put("TransId", transId);
        payload.put("AuthCode", authorization);

        if (quickbook_customer_id == null || quickbook_customer_id.trim().length() == 0) {
            // get customer info
            Response erpres = ErpUtility.doGetCustomer(customer_id);
            String cusDataStr = (String) erpres.getEntity();
            if (cusDataStr.trim().length() > 0) {
                // create qb customer
                String qbcus = ErpUtility.convertErpCustomerToQbCustomer(customer_id, cusDataStr);
                if (qbcus != null) {
                    Response res = Utility.doPost("customer", qbcus);
                    String cusRes = (String) res.getEntity();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> map = mapper.readValue(cusRes, Map.class);
                        Map<String, Object> customerJson = (Map<String, Object>) map.get("Customer");
                        quickbook_customer_id = (String) customerJson.get("Id");
                        payload.put("QuickBookCustomerId", quickbook_customer_id);
                        ErpUtility.doNotifyCompleteCustomerQB(customer_id, quickbook_customer_id, order_id);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, "{0}", ex);
                    }
                }
            }
        }
//        else {
//            ErpUtility.doNotifyCompleteCustomerQB(customer_id, quickbook_customer_id, order_id);
//        }

        Map<String, Object> paymentPayload = new HashMap<>();
        paymentPayload.put("TotalAmt", String.format("%.2f", amt));
        Map<String, Object> CustomerRef = new HashMap<>();
        CustomerRef.put("value", quickbook_customer_id);
        paymentPayload.put("CustomerRef", CustomerRef);
        paymentPayload.put("PrivateNote", String.format("TransId=%s,AuthCode=%s", payload.get("TransId"), payload.get("AuthCode")));
        paymentPayload.put("PaymentRefNum", order_id);
        String paymentData = "";
        try {
            paymentData = new ObjectMapper().writeValueAsString(paymentPayload);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }

        //String paymentData = String.format("{\"TotalAmt\": %.2f,\"CustomerRef\": {\"value\": \"%s\"}, \"PrivateNote\": \"TransId=%s,AuthCode=%s\", \"TxnSource\": \"%s\", \"PaymentRefNum\": \"%s\" }", amt, quickbook_customer_id, payload.get("TransId"), payload.get("AuthCode"), payload.get("TransId"), order_id);

        Response res = Utility.doPost("payment", paymentData);
        System.out.println(res.getEntity());
        String paymentRes = (String) res.getEntity();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(paymentRes, Map.class);
            Map<String, Object> paymentJson = (Map<String, Object>) map.get("Payment");
            String paymentId = (String) paymentJson.get("Id");
            payload.put("PaymentId", paymentId);
            ErpUtility.doNotifyCompletePaymentQB(paymentId, payload.get("TransId"), order_id, amt);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
        String json = "";
        try {
            payload.put("CustomerId", customer_id);
            payload.put("QuickBookCustomerId", quickbook_customer_id);
            payload.put("OrderId", order_id);
            json = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
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

    @GET
    @Path("/iframecommunicator")
    @Produces(MediaType.TEXT_HTML)
    public Response getIframeCommunicator(@Context final HttpServletRequest request) {
        return Response.ok( new Viewable("/iframecommunicator"))
                .header("Content-Security-Policy", "frame-ancestors 'self' * *.authorize.net")
                .header("content-security-policy", "default-src https: data: 'unsafe-inline' 'unsafe-eval'")
                .header("strict-transport-security", "max-age=31536000; includeSubDomains")
                .header("x-content-type-options", "nosniff")
                .header("x-frame-options", "SAMEORIGIN")
                .header("x-xss-protection", "1")
                .build();
    }
}
