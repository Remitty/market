package com.brian.market.doba.helper;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA public key/private key/signature tool
 */
public class RsaUtil {
    /**
     * Encryption algorithm RSA
     */
    public static final String RSA_ALGORITHM = "RSA";

    /**
     * Signature rules
     */
    public static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";

    /**
     * Generate digital signature
     *
     * @param appKey
     * @param privateKey
     * @param timestamp  Millisecond timestamp
     * @return
     * @throws Exception
     */
    public static String sign(String appKey, String privateKey, long timestamp) throws Exception {
        String contentForSign = String.format("appKey=%s&signType=rsa2&timestamp=%s", appKey, String.valueOf(timestamp));
        byte[] keyBytes = Base64.decode(privateKey, Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        System.out.println("privateK:   "+privateK);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(contentForSign.getBytes());
        return new String(Base64.encodeToString(signature.sign(), Base64.NO_WRAP));
    }

    /**
     * Verify digital signature
     *
     * @param data      Encrypted data
     * @param publicKey Public Key
     * @param sign      Digital signature
     * @return
     */
    public static boolean verify(byte[] data, String publicKey, String sign) {
        byte[] keyBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicK = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicK);
            signature.update(data);
            return signature.verify(Base64.decode(sign, Base64.DEFAULT));
        } catch (Exception e) {
        }
        return false;
    }

}
