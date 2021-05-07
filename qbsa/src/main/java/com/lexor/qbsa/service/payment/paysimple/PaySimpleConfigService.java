package com.lexor.qbsa.service.payment.paysimple;

import com.lexor.qbsa.util.ConfigHelper;
import javax.annotation.PostConstruct;
import net.authorize.Environment;
import org.jvnet.hk2.annotations.Service;

@Service
public class PaySimpleConfigService {

    private static final String API_LOGIN_ID = "payment.paysimple.apiloginid";
    private static final String API_KEY = "payment.paysimple.apikey";
    private static final String SAND_BOX = "payment.paysimple.sandbox";
    private static final String API_URL = "payment.paysimple.apiurl";

    private String apiLoginId = null;
    private String apiKey = null;
    private Environment environment = Environment.SANDBOX;
    private String apiUrl = null;


    @PostConstruct
    public void init() {
        this.apiLoginId = ConfigHelper.properties.getProperty(API_LOGIN_ID);
        this.apiKey = ConfigHelper.properties.getProperty(API_KEY);
        this.apiUrl = ConfigHelper.properties.getProperty(API_URL);
        this.environment = Environment.SANDBOX;
        if ("True".equals(ConfigHelper.properties.getProperty(SAND_BOX))) {
            this.environment = Environment.SANDBOX;
        } else {
            this.environment = Environment.PRODUCTION;
        }
    }

    /**
     * read Api_Login_Id
     * read Transaction_key
     * @return
     */
    public String getApiLoginId() {
    	return this.apiLoginId;
    }
    
    public String getApiKey() {
    	return this.apiKey;
    }
    
    public String getApiUrl() {
    	return this.apiUrl;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

}
