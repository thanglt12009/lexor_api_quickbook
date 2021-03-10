package com.lexor.qbsa.controller;

import com.intuit.oauth2.exception.OAuthException;
import com.lexor.qbsa.domain.Configurations;
import com.lexor.qbsa.repository.ConfigurationsRepository;
import com.lexor.qbsa.util.Utility;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "callback")
@Path("callback")
@RequestScoped
public class CallbackController {

    private static final Logger LOG = Logger.getLogger(WebhooksController.class.getName());

    @Inject
    private ConfigurationsRepository configRepository;

    @GET
    @Produces("application/json")
    public Response getToken(@Context final HttpServletRequest request) throws OAuthException, URISyntaxException {
        HttpSession session = request.getSession(true);
        String csrfToken = (String) session.getAttribute("csrfToken");
        String target = (String) session.getAttribute("target");
        String state = request.getParameter("state");
        String realmId = request.getParameter("realmId");
        String authCode = request.getParameter("code");
        if (csrfToken.equals(state)) {
            session.setAttribute("realmId", realmId);
            session.setAttribute("auth_code", authCode);
            Utility.getToken(authCode, session);
            Configurations config;
            try {
                config = configRepository.getByKey("quickbook_refreshtoken");
                if (config != null) {
                    config.setValue(Utility.refreshToken);
                    configRepository.update(config.getId(), config);
                } else {
                    config = new Configurations("quickbook_refreshtoken", Utility.refreshToken);
                    configRepository.persist(config);
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "{0}", ex);
            }
        }
        if (target == null) {
            return Response.ok("{\"status\":\"ok\"}").build();
        } else {
            return Response.temporaryRedirect(new URI(target)).build();
        }
    }
}
