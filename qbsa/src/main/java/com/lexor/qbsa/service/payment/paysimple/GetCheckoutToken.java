package com.lexor.qbsa.service.payment.paysimple;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import javax.inject.Inject;
import org.jvnet.hk2.annotations.Service;

@Service
public class GetCheckoutToken {
    
    @Inject
    private PaySimpleConfigService config;
    
    public String run() {
        BufferedReader reader;
        String response = "";
        String token = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(config.getApiUrl() + "/v4/checkouttoken");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", SecurityUtil.generatePaySimpleAuthorization(config.getApiLoginId(), config.getApiKey()));
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "2");

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write("{}");

            writer.flush();
            writer.close();
            os.close();

            int res_code = connection.getResponseCode();
            if (res_code == HttpURLConnection.HTTP_OK) {
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

            if (res_code == HttpURLConnection.HTTP_OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = mapper.readValue(response, Map.class);
                token = (String)((Map<String, Object>)map.get("Response")).get("JwtToken");
            }
        } catch (Exception e) {
            System.out.println("Exception in GetCheckoutToken " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return token;
    }
}
