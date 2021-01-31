package com.lexor.qbsa;

import com.intuit.oauth2.exception.OAuthException;

import com.lexor.qbsa.domain.Configurations;
import com.lexor.qbsa.repository.ConfigurationsRepository;
import com.lexor.qbsa.service.qbo.OAuth2PlatformClientFactory;
import com.lexor.qbsa.util.Utility;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class DataLoader implements ApplicationEventListener {

    private static final Logger LOG = Logger.getLogger(DataLoader.class.getName());

    @Inject
    private ConfigurationsRepository configRepository;

    @Inject
    OAuth2PlatformClientFactory factory;

    @Override
    public void onEvent(ApplicationEvent event) {
        switch (event.getType()) {
            case INITIALIZATION_FINISHED:
//                try {
//                    Utility.renew();
//                } catch (URISyntaxException | OAuthException e) {
//                    LOG.log(Level.INFO, "Cannot renew with quickbook, please re-authenticate");
////                    e.printStackTrace();
//                }

                // Load CompanyConfig table with realmIds and access tokens
                Utility.init();
                loadConfig();
                break;
            default:
                break;
        }
    }

    /**
     * Read access tokens and other properties from the configuration file and
     * load it in the in-memory h2 database
     *
     */
    private void loadConfig() {
        try {
            Configurations config;
            config = configRepository.getByKey("quickbook_refreshtoken");
            if (config != null) {
                Utility.refreshToken = config.getValue();
                Utility.renew();
            }
        } catch (SQLException | URISyntaxException | OAuthException ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new RequestEventListener() {

            private volatile long startTime;

            @Override
            public void onEvent(RequestEvent event) {
                switch (event.getType()) {
                    case RESOURCE_METHOD_START:
                        startTime = System.currentTimeMillis();
                        LOG.log(Level.INFO, "Resource method {0} started at {1}", new Object[]{event.getUriInfo().getMatchedResourceMethod().getHttpMethod(), startTime});
                        break;
                    case FINISHED:
                        LOG.log(Level.INFO, "Request finished. Processing time {0} ms.", System.currentTimeMillis() - startTime);
                        break;
                    default:
                        break;
                }
            }
        };

    }
}
