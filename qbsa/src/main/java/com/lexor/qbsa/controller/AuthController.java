package com.lexor.qbsa.controller;

import com.intuit.oauth2.exception.OAuthException;
import com.lexor.qbsa.util.Utility;

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
    public String renewAuth() throws URISyntaxException, OAuthException {
        Utility.renew();
        return "renew ok!";
    }

    @GET
    public Response requestAuthentication(@Context HttpServletRequest req) throws URISyntaxException {
        HttpSession session= req.getSession(true);
        Response res = Utility.auth(session);
        if (res == null) {
            res = Response.serverError().build();
        }
        return res;
    }
}
