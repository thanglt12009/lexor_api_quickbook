package com.lexor.qbsa.controller;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

@Path("/payment")
public class PaymentController extends ApplicationController {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@PathParam("id") String id, @Context HttpServletRequest request) throws URISyntaxException {
        if (clientInWhitelist(request)) {
            return doGet("payment/" + id);
        } else {
            return Response.serverError().entity(REJECTED).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newPayment(String entity, @Context HttpServletRequest request) throws IOException {

        if (clientInWhitelist(request)) {
            return doPost("payment", entity);
        } else {
            return Response.serverError().entity(REJECTED).build();
        }
    }
}
