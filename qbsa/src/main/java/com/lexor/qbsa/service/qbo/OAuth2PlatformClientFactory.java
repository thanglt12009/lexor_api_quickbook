package com.lexor.qbsa.service.qbo;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.Environment;
import com.intuit.oauth2.config.OAuth2Config;
import com.lexor.qbsa.util.ConfigHelper;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import org.jvnet.hk2.annotations.Service;

@Service
@Singleton
public class OAuth2PlatformClientFactory {

    public static OAuth2PlatformClient client;
    public static OAuth2Config oauth2Config;

    @PostConstruct
    public void init() {
        try {
            //initialize the config
            oauth2Config = new OAuth2Config.OAuth2ConfigBuilder(ConfigHelper.properties.getProperty("OAuth2AppClientId"), ConfigHelper.properties.getProperty("OAuth2AppClientSecret")) //set client id, secret
                    .callDiscoveryAPI(Environment.SANDBOX) // call discovery API to populate urls
                    .buildConfig();
            //build the client
            client = new OAuth2PlatformClient(oauth2Config);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public OAuth2PlatformClient getOAuth2PlatformClient() {
        return client;
    }

    public OAuth2Config getOAuth2Config() {
        return oauth2Config;
    }

    public String getPropertyValue(String proppertyName) {
        return ConfigHelper.properties.getProperty(proppertyName);
    }

}
