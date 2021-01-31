package com.lexor.qbsa.util;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.IEntity;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.security.OAuth2Authorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.ipp.util.Config;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import org.apache.commons.lang.StringUtils;
import com.lexor.qbsa.service.qbo.OAuth2PlatformClientFactory;

import javax.servlet.http.HttpSession;
import java.util.List;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

@Service
public class QBOServiceHelper {

    @Inject
    OAuth2PlatformClientFactory factory;

    private static final org.slf4j.Logger LOG = com.intuit.ipp.util.Logger.getLogger();

    public DataService getDataService(String realmId, String accessToken) throws FMSException {

        String url = factory.getPropertyValue("IntuitAccountingAPIHost") + "/v3/company";
        Config.setProperty(Config.BASE_URL_QBO, url);
        //create oauth object
        OAuth2Authorizer oauth = new OAuth2Authorizer(accessToken);
        //create context
        Context context = new Context(oauth, ServiceType.QBO, realmId);

        // create dataservice
        return new DataService(context);
    }



    /**
     * Queries data from QuickBooks
     *
     * @param session
     * @param sql
     * @return
     */
    public List<? extends IEntity> queryData(HttpSession session, String sql) {

        String realmId = (String)session.getAttribute("realmId");
        if (StringUtils.isEmpty(realmId)) {
            LOG.error("Relam id is null ");
        }
        String accessToken = (String)session.getAttribute("access_token");

        try {

            //get DataService
            DataService service = getDataService(realmId, accessToken);

            // get data
            QueryResult queryResult = service.executeQuery(sql);
            return queryResult.getEntities();
        }
        /*
         * Handle 401 status code -
         * If a 401 response is received, refresh tokens should be used to get a new access token,
         * and the API call should be tried again.
         */
        catch (InvalidTokenException e) {
            LOG.error("Error while calling executeQuery :: " + e.getMessage());

            //refresh tokens
            LOG.info("received 401 during companyinfo call, refreshing tokens now");
            OAuth2PlatformClient client  = factory.getOAuth2PlatformClient();
            String refreshToken = (String)session.getAttribute("refresh_token");

            try {
                BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
                session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
                session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());

                //call company info again using new tokens
                LOG.info("calling companyinfo using new tokens");
                DataService service = getDataService(realmId, accessToken);

                // get data
                QueryResult queryResult = service.executeQuery(sql);
                return queryResult.getEntities();

            } catch (OAuthException e1) {
                LOG.error("Error while calling bearer token :: " + e.getMessage());

            } catch (FMSException e1) {
                LOG.error("Error while calling company currency :: " + e.getMessage());
            }

        } catch (FMSException e) {
            List<Error> list = e.getErrorList();
            list.forEach(error -> LOG.error("Error while calling executeQuery :: " + error.getMessage()));
        }
        return null;
    }
}