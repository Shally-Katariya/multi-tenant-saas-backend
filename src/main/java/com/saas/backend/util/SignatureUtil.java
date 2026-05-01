package com.saas.backend.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SignatureUtil {

    public static String generateHmac(String payload, String secret) {

        try {
            // 🔥 Create HMAC SHA256 algorithm
            Mac mac = Mac.getInstance("HmacSHA256");

            // 🔥 Convert secret into key
            SecretKeySpec keySpec =
                    new SecretKeySpec(secret.getBytes(), "HmacSHA256");

            mac.init(keySpec);

            // 🔥 Generate hash
            byte[] rawHmac = mac.doFinal(payload.getBytes());

            // 🔥 Convert to readable string
            return Base64.getEncoder().encodeToString(rawHmac);

        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
}