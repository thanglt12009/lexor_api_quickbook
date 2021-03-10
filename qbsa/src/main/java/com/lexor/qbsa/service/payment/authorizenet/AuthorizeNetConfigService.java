package com.lexor.qbsa.service.payment.authorizenet;

import com.lexor.qbsa.util.ConfigHelper;
import javax.annotation.PostConstruct;
import net.authorize.Environment;
import org.jvnet.hk2.annotations.Service;

@Service
public class AuthorizeNetConfigService {

    private static final String API_LOGIN_ID = "payment.authorizenet.apiloginid";
    private static final String TRANSACTION_KEY = "payment.authorizenet.transactionkey";
    private static final String CLIENT_KEY = "payment.authorizenet.clientkey";
    private static final String SAND_BOX = "payment.authorizenet.sandbox";
    private static final String RETURN_URL = "payment.authorizenet.returnurl";

    private String apiLoginId = null;
    private String transactionKey = null;
    private String clientKey = null;
    private Environment environment = Environment.SANDBOX;
    private String returnUrl = null;


    @PostConstruct
    public void init() {
        this.apiLoginId = ConfigHelper.properties.getProperty(API_LOGIN_ID);
        this.transactionKey = ConfigHelper.properties.getProperty(TRANSACTION_KEY);
        this.clientKey = ConfigHelper.properties.getProperty(CLIENT_KEY);
        this.returnUrl = ConfigHelper.properties.getProperty(RETURN_URL);
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
    
    public String getTransactionKey() {
    	return this.transactionKey;
    }
    
    public String getClientKey() {
    	return this.clientKey;
    }
    
    public String getReturnUrl() {
    	return this.returnUrl;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

}
