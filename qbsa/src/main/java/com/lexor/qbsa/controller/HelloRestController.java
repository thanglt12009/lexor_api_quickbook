package com.lexor.qbsa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.ipp.data.Entity;
import com.intuit.ipp.data.EventNotification;
import com.intuit.ipp.data.WebhooksEvent;
import com.intuit.ipp.services.WebhooksService;
import com.lexor.qbsa.domain.PayloadQueue;
import com.lexor.qbsa.repository.PayloadQueueRepository;
import com.lexor.qbsa.service.payment.authorizenet.CreateAnAcceptPaymentTransaction;
import com.lexor.qbsa.service.qbo.WebhooksServiceFactory;
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
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author thanh
 */
@Path("hellorest")
@RequestScoped
public class HelloRestController extends ApplicationController {
    
    private static final Logger LOG = Logger.getLogger(HelloRestController.class.getName());

    @Inject
    WebhooksServiceFactory webhooksServiceFactory;

    @Inject
    private PayloadQueueRepository payloadQueueRepository;

    /**
     * Creates a new instance of HelloRestResource
     */
    public HelloRestController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.lexor.qbsa.HelloRestController
     *
     * @return
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getHtml() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey HTML" + "</h1></body>" + "</html> ";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response testReceiveData(String data, @Context HttpServletRequest request) throws IOException {
        System.out.println(data);
        return Response.ok("{\"status\":\"ok\"}").build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@PathParam("id") String id, @Context HttpServletRequest request) throws URISyntaxException {
        if (clientInWhitelist(request)) {
            try {
                Integer payloadId = Integer.parseInt(id);
                PayloadQueue payload = payloadQueueRepository.get(payloadId);
                WebhooksService service = webhooksServiceFactory.getWebhooksService();
                WebhooksEvent event = service.getWebhooksEvent(payload.getPayload());
                EventNotification eventNotification = event.getEventNotifications().get(0);
                Entity entity = eventNotification.getDataChangeEvent().getEntities().get(0);
                Map<String, String> data = new HashMap<>();
                String json = "";
                try {
                    data.put("name", entity.getName());
                    data.put("id", entity.getId());
                    data.put("operation", entity.getOperation());
                    data.put("lastUpdated", entity.getLastUpdated());
                    json = new ObjectMapper().writeValueAsString(data);
                } catch (JsonProcessingException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                return Response.ok(json).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Response.serverError().entity(REJECTED).build();
    }
}
