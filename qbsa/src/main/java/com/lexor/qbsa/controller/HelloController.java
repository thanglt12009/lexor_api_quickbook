package com.lexor.qbsa.controller;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.mvc.Viewable;

/**
 * REST Web Service
 *
 * @author thanh
 */
@Path("hello")
@RequestScoped
public class HelloController {

//    @Context
//    private UriInfo context;

    /**
     * Creates a new instance of HelloResource
     */
    public HelloController() {
    }

    /**
     * Retrieves representation of an instance of com.lexor.qbsa.HelloController
     * @return 
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getHtml() {
        Map<String, String> model = new HashMap<>();
        model.put("hello", "Hello");
        model.put("world", "World");
        return new Viewable("/hello", model); 
    }
}
