package com.lexor.qbsa.util;

import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.InvalidRequestException;
import com.intuit.oauth2.exception.OAuthException;
import com.lexor.qbsa.service.qbo.OAuth2PlatformClientFactory;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.json.JSONException;

public class Utility {

    private static final Logger LOG = Logger.getLogger(Utility.class.getName());

    public static int PRETTY_PRINT_INDENT_FACTOR = 4;
    public static String bearerToken = "";
    public static String refreshToken = "";
    public static LocalDateTime lastRefresh = LocalDateTime.now().minusMinutes(65);

    public static String realmId = "";
//    public static String clientId = ConfigHelper.properties.getProperty("OAuth2AppClientId");
//    public static String clientSecret = ConfigHelper.properties.getProperty("OAuth2AppClientSecret");
    public static String callBackUrl = "";
    public static String apiHost = "";

    public static void init() {
        realmId = ConfigHelper.properties.getProperty("RealmId");
        callBackUrl = ConfigHelper.properties.getProperty("OAuth2AppRedirectUri");
        apiHost = ConfigHelper.properties.getProperty("IntuitAccountingAPIHost");
    }

    private static boolean checkAuth(String response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> map = mapper.readValue(response, Map.class);
            if (map.containsKey("fault")) {
                Map<String, Object> fault = (Map<String, Object>)map.get("fault");
                if (fault.containsKey("type") && "AUTHENTICATION".equals(fault.get("type"))) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Method to request bearerToken from Quickbooks for testing@return String
     * response
     *
     * @return
     */
    public static String useBearerToken() {
        BufferedReader reader;
        String response = "";
        try {
            URL url;
            url = new URL(apiHost + "/v3/company/" + realmId + "/companyinfo/" + realmId + "?minorversion=55");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("This is the response");

            String line;
            StringWriter out = new StringWriter(connection.getContentLength() > 0 ? connection.getContentLength() : 2048);
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            response = out.toString();
            System.out.println(response);
        } catch (IOException | JSONException e) {
            System.out.println(e.toString());
        }
        return response;
    }

    /**
     * Method to send GET request to Quickbooks 1. Set up connection 2. send to
     * Quickbooks 3. Return Quickbooks response back to the originator
     *
     * @param params
     * @return Response
     */
    public static Response doGet(String params) {
        doRenew();
        BufferedReader reader;
        String response = "";
        boolean refresh = false;
        HttpsURLConnection connection = null;
        try {
            URL url;
            while (true) {
                url = new URL(apiHost + "/v3/company/" + realmId + "/" + params + "?minorversion=55");
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
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
                if (connection.getResponseCode() == 400) {
                    return Response.status(400).entity(response).build();
                }
                if (!checkAuth(response)) {
                    if (refresh) {
                        break;
                    }
                    try {
                        Utility.renew();
                    } catch (URISyntaxException | OAuthException ex) {
                        LOG.log(Level.SEVERE, "{0}", ex);
                    } finally {
                        refresh = true;
                    }
                } else {
                    break;
                }
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

    public static boolean doRenew() {
        if (lastRefresh.compareTo(LocalDateTime.now().minusMinutes(60)) < 0) {
            try {
                renew();
            } catch (URISyntaxException | OAuthException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Method to send POST request to Quickbooks 1. Set up connection 2. send to
     * Quickbooks 3. Return Quickbooks response back to the originator
     *
     * @param apiEndpoint
     * @param postData
     * @return Response
     */
    public static Response doPost(String apiEndpoint, String postData) {

        doRenew();

        String response = "";
        BufferedReader in;
        String line;
        HttpsURLConnection connection = null;
        boolean refresh = false;
        try {
            URL url;
            while (true) {
                url = new URL(apiHost + "/v3/company/" + realmId + "/" + apiEndpoint + "?minorversion=55");
                connection = (HttpsURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Authorization", "Bearer " + bearerToken);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json;");

                try (OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream())) {
                    osw.write(String.format(postData));
                    osw.flush();
                    osw.close();
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

                if (connection.getResponseCode() == 400) {
                    return Response.status(400).entity(response).build();
                }
                if (!checkAuth(response)) {
                    if (refresh) {
                        break;
                    }
                    try {
                        Utility.renew();
                    } catch (URISyntaxException | OAuthException ex) {
                        LOG.log(Level.SEVERE, "{0}", ex);
                    } finally {
                        refresh = true;
                    }
                } else {
                    break;
                }
            }

        } catch (IOException e) {
            LOG.severe(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return Response.ok(response).build();

    }

    /**
     * Method to send authentication request to Quickbooks 1.Prepare the request
     * 2.Open callback URL
     *
     * @param session
     * @return Response
     * @throws java.net.URISyntaxException
     */
    public static Response auth(HttpSession session, String target) throws URISyntaxException {

        //Prepare the config
        OAuth2Config oauth2Config = OAuth2PlatformClientFactory.oauth2Config;

        //Generate the CSRF token
        String csrf = oauth2Config.generateCSRFToken();

        session.setAttribute("csrfToken", csrf);
        session.setAttribute("target", target);

        //Prepare scopes
        List<Scope> scopes = new ArrayList<>();
        scopes.add(Scope.Accounting);
        scopes.add(Scope.Payments);
        scopes.add(Scope.OpenIdAll);

        String url;
        //Get the authorization URL
        try {
            String s = oauth2Config.getIntuitAuthorizationEndpoint();
            url = oauth2Config.prepareUrl(scopes, callBackUrl, csrf); //redirectUri - pass the callback url

//            url = url.replace("null", "https://appcenter.intuit.com/app/connect/oauth2");
            //Open callbackURL
            return Response.seeOther(new URI(url)).build();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to renew Quickbooks access tokens 1.renew 2.set new access token
     *
     * @throws java.net.URISyntaxException
     * @throws com.intuit.oauth2.exception.OAuthException
     */
    public static void renew() throws URISyntaxException, OAuthException {
        //Prepare OAuth2PlatformClient
        OAuth2PlatformClient client = OAuth2PlatformClientFactory.client;

        //Call refresh endpoint
        BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken); //set refresh token

        lastRefresh = LocalDateTime.now();
        bearerToken = bearerTokenResponse.getAccessToken();
    }

    /**
     * Method to get tokens from Quickbook auth response 1.Use Quickbooks client
     * library to get all tokens.2. Set tokens
     *
     * @param code
     * @throws com.intuit.oauth2.exception.OAuthException
     */
    public static void getToken(String code, HttpSession session) throws OAuthException {

        OAuth2PlatformClient client = OAuth2PlatformClientFactory.client;

        BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(code, callBackUrl);

        Utility.lastRefresh = LocalDateTime.now();
        Utility.bearerToken = bearerTokenResponse.getAccessToken();
        Utility.refreshToken = bearerTokenResponse.getRefreshToken();
        session.setAttribute("bearerToken", bearerToken);
        session.setAttribute("refreshToken", refreshToken);
        LOG.log(Level.INFO, "Access token: {0}", bearerTokenResponse.getAccessToken());
        LOG.log(Level.INFO, "Refresh token: {0}", bearerTokenResponse.getRefreshToken());

    }

    /**
     * Method to use Sendgrid to send notification emails
     *
     * @param payload
     */
    public static void sendNotificationEmails(String payload) {
        List<String> subscribedEmails = Arrays.asList(ConfigHelper.properties.getProperty("sendgrid.emails").split(","));
        subscribedEmails.forEach(email -> sendEmail(email, payload));
    }

    /**
     * Method to use Sendgrid to send email
     *
     * @param email
     * @param payload
     */
    public static void sendEmail(String email, String payload) {

        Email from = new Email("info@codemate.vn");
        String subject = "Sending with SendGrid is Fun";
        Email to = new Email(email);
        Content content = new Content("text/plain", payload);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(ConfigHelper.properties.getProperty("sendgrid.api.key"));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            com.sendgrid.Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error while emailing for test to: {0}", email);
        }

    }

}
