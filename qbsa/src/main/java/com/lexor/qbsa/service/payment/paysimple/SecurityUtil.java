package com.lexor.qbsa.service.payment.paysimple;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.joda.time.DateTime;

public class SecurityUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String generatePaySimpleAuthorization(String accessId, String secret) throws SignatureException, UnsupportedEncodingException {
        // get the current time
        String now = DateTime.now().toString();
        byte[] hexBytes;
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM);
            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);
            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(now.getBytes());
            // base64-encode the hmac
            hexBytes = Base64.getEncoder().encode(rawHmac);
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        // build the result
        String res = new StringBuilder("PSSERVER accessid=")
                .append(accessId)
                .append("; timestamp=")
                .append(now)
                .append("; signature=")
                .append(new String(hexBytes, "UTF-8"))
                .toString();
        return res;
    }
}
