package com.lexor.qbsa.service.security;

import com.intuit.ipp.services.WebhooksService;
import com.intuit.ipp.util.Config;
import com.lexor.qbsa.service.qbo.WebhooksServiceFactory;
import com.lexor.qbsa.util.ConfigHelper;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for encrypting/decrypting data as well as authenticate
 *
 */
public class SecurityService {

    private static final String VERIFIER_KEY = "webhooks.verifier.token";
    private static final String ENCRYPTION_KEY = "encryption.key";

    WebhooksServiceFactory webhooksServiceFactory;

    private SecretKeySpec secretKey;

    @PostConstruct
    public void init() {
        webhooksServiceFactory = new WebhooksServiceFactory();
        try {
            secretKey = new SecretKeySpec(getEncryptionKey().getBytes("UTF-8"), "AES");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SecurityService.class.getName()).log(Level.WARNING, "Error during initializing secretkeyspec ", ex.getCause());
        }
    }

    /**
     * Validates the payload with the intuit-signature hash
     *
     * @param signature
     * @param payload
     * @return valid or not
     */
    public boolean isRequestValid(String signature, String payload) {

        // set custom config
        Config.setProperty(Config.WEBHOOKS_VERIFIER_TOKEN, getVerifierKey());

        // create webhooks service
        WebhooksService service = webhooksServiceFactory.getWebhooksService();
        return service.verifyPayload(signature, payload);
    }

    /**
     * Verified key to validate webhooks payload
     *
     * @return
     */
    public String getVerifierKey() {
        return ConfigHelper.properties.getProperty(VERIFIER_KEY);
    }

    /**
     * Encryption key
     *
     * @return
     */
    public String getEncryptionKey() {
        return ConfigHelper.properties.getProperty(ENCRYPTION_KEY);
    }

    /**
     * @param plainText
     * @return
     * @throws Exception
     */
    public String encrypt(String plainText) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] byteCipherText = aesCipher.doFinal(plainText.getBytes());
        return bytesToHex(byteCipherText);
    }

    /**
     * @param byteCipherText
     * @return
     * @throws Exception
     */
    public String decrypt(String byteCipherText) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] bytePlainText = aesCipher.doFinal(hexToBytes(byteCipherText));
        return new String(bytePlainText);

    }

    private String bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }

    private byte[] hexToBytes(String hash) {
        return DatatypeConverter.parseHexBinary(hash);
    }

}
