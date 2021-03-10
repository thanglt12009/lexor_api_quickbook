/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lexor.qbsa.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.Response;
import org.json.JSONException;

/**
 *
 * @author thanh
 */
public class ErpUtility {
    
    private static final Logger LOG = Logger.getLogger(ErpUtility.class.getName());
    
    public static String apiHost = "";
    public static String accessToken = "";
    
    public static void init() {
        apiHost = ConfigHelper.properties.getProperty("LexorAPIHost");
        accessToken = ConfigHelper.properties.getProperty("LexorAccessToken");
    }

    public static Response doGetCustomer(String params) {
        BufferedReader reader;
        String response = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(apiHost + "/lxerp/api/qbservice/getCustomerInfo/" + params);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("x-access-token", accessToken);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
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
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return Response.status(connection.getResponseCode()).entity(response).build();
            }
        } catch (IOException | JSONException e) {
            LOG.log(Level.INFO, "Exception in doGet{0}", e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return Response.ok(response).build();
    }
    
    public static String convertErpCustomerToQbCustomer(String customerId ,String erpCustomer) {
        String json = "";
        Map<String, Object> payload = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(erpCustomer, Map.class);
            if (map.containsKey("message")) {
                return null;
            } else {
                Map<String, Object> contactInfo = (Map<String, Object>) map.get("contactInfo");
                
                String firstName = (String) contactInfo.get("firstName");
                String lastName = (String) contactInfo.get("lastName");
                String cellPhone = (String) contactInfo.get("cellPhone");
                String busPhone = (String) contactInfo.get("busPhone");
                String email = (String) contactInfo.get("email");
                
                Map<String, Object> salonInfo = (Map<String, Object>) map.get("salonInfo");
                
                String address = (String) salonInfo.get("address");
                String street = (String) salonInfo.get("street");
                String company = (String) salonInfo.get("company");
                //String salonBusPhone = (String) salonInfo.get("busPhone");
                //String salonCellPhone = (String) salonInfo.get("cellPhone");
                //String idUSZip = (String) salonInfo.get("idUSZip");
                String postalCode = (String) salonInfo.get("postalCode");
                
                payload.put("GivenName", firstName);
                payload.put("FamilyName", lastName);
                payload.put("CompanyName", company);
                payload.put("ResaleNum", customerId);
                
                Map<String, Object> PrimaryEmailAddr = new HashMap<>();
                PrimaryEmailAddr.put("Address", email);
                payload.put("PrimaryEmailAddr", PrimaryEmailAddr);
                Map<String, Object> PrimaryPhone = new HashMap<>();
                PrimaryPhone.put("FreeFormNumber", cellPhone);
                payload.put("PrimaryPhone", PrimaryPhone);
                Map<String, Object> BillAddr = new HashMap<>();
                BillAddr.put("PostalCode", postalCode);
                BillAddr.put("City", address);
                BillAddr.put("Line1", street);
                payload.put("BillAddr", BillAddr);

                json = new ObjectMapper().writeValueAsString(payload);
            }
            //String id = (String) paymentJson.get("Id");
            //payload.put("PaymentId", id);
            //json = new ObjectMapper().writeValueAsString(payload);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "{0}", ex);
        }
        return json;
    }
    
    public static Response doNotifyCompleteCustomerQB(String customerId, String qbCustomerId) {
        BufferedReader reader;
        String response = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(apiHost + "/lxerp/api/qbservice/handlecustomercompleted/" + customerId + "/" + qbCustomerId);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("x-access-token", accessToken);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
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
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return Response.status(connection.getResponseCode()).entity(response).build();
            }
        } catch (IOException | JSONException e) {
            LOG.log(Level.INFO, "Exception in doGet{0}", e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return Response.ok(response).build();
    }

    public static Response doNotifyCompletePaymentQB(String paymentId) {
        BufferedReader reader;
        String response = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(apiHost + "/lxerp/api/qbservice/handlepaymentcompleted/" + paymentId);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("x-access-token", accessToken);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
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
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return Response.status(connection.getResponseCode()).entity(response).build();
            }
        } catch (IOException | JSONException e) {
            LOG.log(Level.INFO, "Exception in doGet{0}", e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return Response.ok(response).build();
    }
}
