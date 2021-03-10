package com.lexor.qbsa.service.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.ipp.data.Entity;
import com.intuit.ipp.data.EventNotification;
import com.intuit.ipp.data.WebhooksEvent;
import com.intuit.ipp.services.WebhooksService;
import com.intuit.ipp.util.DateUtils;
import com.lexor.qbsa.domain.CompanyConfig;
import com.lexor.qbsa.domain.Configurations;
import com.lexor.qbsa.domain.PayloadQueue;
import com.lexor.qbsa.repository.ConfigurationsRepository;
import com.lexor.qbsa.repository.PayloadQueueRepository;
import com.lexor.qbsa.service.CompanyConfigService;
import com.lexor.qbsa.service.qbo.WebhooksServiceFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

/**
 * Callable task to process the queue 1. Retrieves the payload from the queue 2.
 * Converts json to object 3. Queries CompanyConfig table to get the last CDC
 * performed time for the realmId 4. Performs CDC for all the subscribed
 * entities using the lastCDCTime retrieved in step 3 5. Updates the
 * CompanyConfig table with the last CDC performed time for the realmId - time
 * when step 4 was performed
 *
 * @author dderose
 *
 */
@Service
public class QueueProcessor implements Callable<Object> {

    private static final Logger LOG = Logger.getLogger(QueueProcessor.class.getName());

    @Inject
    private PayloadQueueRepository payloadQueueRepository;

    @Inject
    private CompanyConfigService companyConfigService;

    @Inject
    WebhooksServiceFactory webhooksServiceFactory;
        
    @Inject
    private ConfigurationsRepository configRepository;

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String ERP_WEB_HOOK_CONFIG = "erp_webhook";

    @Override
    public Object call() throws Exception {
        try {
            Configurations config = configRepository.getByKey(ERP_WEB_HOOK_CONFIG);
            PayloadQueue p = new PayloadQueue();
            p.setStatus(0);
            long count = payloadQueueRepository.count(p);
            int page_size = 10;
            long remain = count;
            int page_num = 0;
            while (remain > 0) {
                List<PayloadQueue> payloads = payloadQueueRepository.find(p, new int[]{page_size, page_num});
                payloads.forEach((PayloadQueue o) -> {
                    PayloadQueue t;
                    try {
                        t = payloadQueueRepository.get(o.getId());
                    } catch (SQLException ex) {
                        LOG.log(Level.SEVERE, "{0}", ex);
                        t = null;
                    }
                    if (t != null && 0 == t.getStatus()) {
                        String payload = o.getPayload();
                        o.setStatus(1);
                        try {
                            payloadQueueRepository.updateStatus(o);
                        } catch (SQLException ex) {
                            LOG.log(Level.SEVERE, "{0}", ex);
                        }
                        if ("quickbook".equals(t.getSource())) {
                            // create webhooks service
                            WebhooksService service = webhooksServiceFactory.getWebhooksService();

                            //Convert payload to obj
                            WebhooksEvent event = service.getWebhooksEvent(payload);
                            EventNotification eventNotification = event.getEventNotifications().get(0);

                            Entity entity = eventNotification.getDataChangeEvent().getEntities().get(0);
                            Map<String, String> data = new HashMap<>();
                            String json = "";
                            try {
                                data.put("name", entity.getName());
                                data.put("id", entity.getId());
                                data.put("operation", entity.getOperation());
                                data.put("lastUpdated", entity.getLastUpdated());
                                json = new ObjectMapper().writeValueAsString(data);
                                if (config != null) {
                                    this.doPost(config.getValue(), json);
                                }
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, "{0}", ex);
                            }

                            // get the company config
                            CompanyConfig companyConfig = companyConfigService.getCompanyConfigByRealmId(eventNotification.getRealmId());
                            // perform cdc with last updated timestamp and subscribed entities
                            try {
                                String cdcTimestamp = DateUtils.getStringFromDateTime(DateUtils.getCurrentDateTime());
                                companyConfig.setLastCdcTimestamp(cdcTimestamp);
                            } catch (ParseException ex) {
                                LOG.log(Level.SEVERE, "{0}", ex);
                            }
                            // update cdcTimestamp in companyconfig 
                            companyConfigService.update(companyConfig);
                        } else if ("authorizenet".equals(t.getSource())) {
                            try {
                                if (config != null) {
                                    this.doPost(config.getValue(), payload);
                                }
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, "{0}", ex);
                            }
                        }
                        o.setStatus(2);
                        try {
                            payloadQueueRepository.updateStatus(o);
                        } catch (SQLException ex) {
                            LOG.log(Level.SEVERE, "{0}", ex);
                        }
                    }
                });
                if (payloads.size() > 0) {
                    remain -= payloads.size();
                } else {
                    remain = 0;
                }
                page_num += 1;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
    public void doPost(String apiEndpoint, String postData) {

        String response = "";
        BufferedReader in;
        String line;
        HttpURLConnection connection = null;
        try {
            URL url;
            url = new URL(apiEndpoint);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//            connection.setConnectTimeout(50000);

            try(OutputStream os = connection.getOutputStream()) {
                os.write(postData.getBytes("utf-8"));
                os.flush();
                os.close();
            }
            if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            while ((line = in.readLine()) != null) {
                response += line;
            }
            in.close();
            LOG.log(Level.INFO, "{0}", response);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
