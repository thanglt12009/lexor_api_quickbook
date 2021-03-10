package com.lexor.qbsa.controller;

import com.intuit.oauth2.exception.OAuthException;
import com.lexor.qbsa.util.Utility;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

@Path("auth")
@RequestScoped
public class AuthController {
    
    private static final Logger LOG = Logger.getLogger(WebhooksController.class.getName());

    @GET
    @Path("renew")
    public Response renewAuth(@Context HttpServletRequest req) throws URISyntaxException, OAuthException {
        Utility.renew();
        return Response.ok("{\"status\":\"ok\"}").build();
    }

    @GET
    public Response requestAuthentication(@Context HttpServletRequest req) throws URISyntaxException {
        HttpSession session= req.getSession(true);
        String target = req.getParameter("target");
        Response res = Utility.auth(session, target);
        if (res == null) {
            res = Response.serverError().build();
        }
        return res;
    }
}
