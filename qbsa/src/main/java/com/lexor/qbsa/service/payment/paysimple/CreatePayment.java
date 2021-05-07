package com.lexor.qbsa.service.payment.paysimple;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import org.jvnet.hk2.annotations.Service;

@Service
public class CreatePayment {
    
    @Inject
    private PaySimpleConfigService config;
    
    public String run(String postData) {
        BufferedReader reader;
        String response = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(config.getApiUrl() + "/payment");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", SecurityUtil.generatePaySimpleAuthorization(config.getApiLoginId(), config.getApiKey()));
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            try(OutputStream os = connection.getOutputStream()) {
                os.write(postData.getBytes("utf-8"));
                os.flush();
                os.close();
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String line;
            StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            response = out.toString();
            reader.close();
        } catch (Exception e) {
            System.out.println("Exception in GetCheckoutToken " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }
        
}
