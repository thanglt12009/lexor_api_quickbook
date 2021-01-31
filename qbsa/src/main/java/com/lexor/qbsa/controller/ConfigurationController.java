package com.lexor.qbsa.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lexor.qbsa.domain.Configurations;
import com.lexor.qbsa.repository.ConfigurationsRepository;
import com.lexor.qbsa.service.payment.authorizenet.CreateAnAcceptPaymentTransaction;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
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
@Path("config")
@RequestScoped
public class ConfigurationController extends ApplicationController {
    
    private static final Logger LOG = Logger.getLogger(ConfigurationController.class.getName());

    @Inject
    private ConfigurationsRepository configRepository;

    /**
     * Creates a new instance of HelloRestResource
     */
    public ConfigurationController() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setConfig(String config, @Context HttpServletRequest request) throws IOException {

        if (clientInWhitelist(request)) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(config, Map.class);
            if (map.containsKey("key") && map.containsKey("value")) {
                String key = map.get("key");
                String value = map.get("value");
                try {
                    Configurations configInfo = configRepository.getByKey(key);
                    if (configInfo == null) {
                        configInfo = new Configurations(key, value);
                        configRepository.persist(configInfo);
                    } else {
                        configInfo.setValue(value);
                        configRepository.update(configInfo.getId(), configInfo);
                    }
                    return Response.ok("{\"status\": \"ok\"}").build();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, "{0}", ex);
                }
            }
            return Response.serverError().entity("{\"status\": \"error\"}").build();
        } else {
            return Response.serverError().entity("{\"status\": \"rejected\"}").build();
        }
    }

    @GET
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemByKey(@PathParam("key") String key, @Context HttpServletRequest request) throws URISyntaxException {
        if (clientInWhitelist(request)) {
            try {
                Configurations configInfo = configRepository.getByKey(key);
                String value = "";
                if (configInfo != null) {
                    value = configInfo.getValue();
                }
                Map<String, String> data = new HashMap<>();
                String json = "";
                try {
                    data.put("key", key);
                    data.put("value", value);
                    json = new ObjectMapper().writeValueAsString(data);
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(CreateAnAcceptPaymentTransaction.class.getName()).log(Level.SEVERE, null, ex);
                }
                return Response.ok(json).build();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "{0}", ex);
            }
            return Response.serverError().entity(ERROR).build();
        }
        return Response.serverError().entity(REJECTED).build();
    }
}
