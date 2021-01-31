package com.lexor.qbsa.service.queue;

import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.util.Logger;
import com.lexor.qbsa.domain.CompanyConfig;
import com.lexor.qbsa.service.qbo.OAuth2PlatformClientFactory;
import com.lexor.qbsa.service.qbo.QBODataService;
import com.lexor.qbsa.util.QBOServiceHelper;
import com.lexor.qbsa.util.Utility;
import com.lexor.qbsa.util.qualifier.QueryAPI;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

/**
 * Class for implementing QBO Query api
 *
 * @author dderose
 *
 */
@QueryAPI
@Service
public class QueryService implements QBODataService {

    private static final org.slf4j.Logger LOG = Logger.getLogger();

    @Inject
    OAuth2PlatformClientFactory factory;

    @Inject
    public QBOServiceHelper helper;

    @Override
    public void callDataService(CompanyConfig companyConfig) throws Exception {

        // create data service
        DataService service = helper.getDataService(Utility.realmId, Utility.bearerToken);

        try {
            LOG.info("Calling Query API ");
            String query = "select * from ";
            //Build query list for each subscribed entities
            List<String> subscribedEntities = Arrays.asList(companyConfig.getWebhooksSubscribedEntites().split(","));
            subscribedEntities.forEach(entity -> executeQuery(query + entity, service));

        } catch (Exception ex) {
            LOG.error("Error loading app configs", ex.getCause());
        }

    }

    /**
     * Call executeQuery api for each entity
     *
     * @param query
     * @param service
     */
    public void executeQuery(String query, DataService service) {
        try {
            LOG.info("Executing Query " + query);
            service.executeQuery(query);
            LOG.info(" Query complete");
        } catch (FMSException ex) {
            LOG.error("Error loading app configs", ex.getCause());
        }
    }

}
