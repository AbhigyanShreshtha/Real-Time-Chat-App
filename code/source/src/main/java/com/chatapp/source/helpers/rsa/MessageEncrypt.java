package com.chatapp.source.helpers.rsa;

import javax.crypto.Cipher;
import java.security.PublicKey;
import java.util.Base64;

public class MessageEncrypt {

    public static String encrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
