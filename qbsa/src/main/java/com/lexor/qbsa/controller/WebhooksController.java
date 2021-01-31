package com.lexor.qbsa.controller;

import com.intuit.ipp.util.StringUtils;
import com.lexor.qbsa.service.queue.QueueService;
import com.lexor.qbsa.service.security.SecurityService;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Path("quickbookwebhook")
@RequestScoped
public class WebhooksController {

    private static final Logger LOG = Logger.getLogger(WebhooksController.class.getName());

    private static final String SIGNATURE = "intuit-signature";
    private static final String SUCCESS = "Success";
    private static final String ERROR = "Error";

    @Inject
    SecurityService securityService;

    @Inject
    private QueueService queueService;

    /**
     * Method to receive webhooks event notification 1.Validates payload 2.Adds
     * it to a queue 3.Sends success response back
     *
     * Note: Queue processing occurs in an async thread
     *
     * @param request
     * @param entity
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response webhooks(final String entity, @Context final HttpServletRequest request) {

        LOG.log(Level.INFO, "Hook json from webhook: {0}", entity);

        String signature = request.getHeader(SIGNATURE);

        // if signature is empty return 401
        if (!StringUtils.hasText(signature)) {
            LOG.info("no signature, response sent FORBIDDEN");
            return Response.status(Response.Status.FORBIDDEN).type("text/plain").entity(ERROR).build();
        }

        // if payload is empty, don't do anything
        if (!StringUtils.hasText(entity)) {
            LOG.info("nothing to process");
            return Response.status(Response.Status.OK).type("text/plain").entity(SUCCESS).build();
        }

        LOG.info("request received ");

        //if request valid - push to queue
        if (securityService.isRequestValid(signature, entity)) {
            try {
                queueService.add("quickbook", entity);
                LOG.info("queue added");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            LOG.info("response sent FORBIDDEN");
            return Response.status(Response.Status.FORBIDDEN).type("text/plain").entity(ERROR).build();
        }

        LOG.info("response sent ");
        return Response.status(Response.Status.OK).type("text/plain").entity(SUCCESS).build();
    }
}
