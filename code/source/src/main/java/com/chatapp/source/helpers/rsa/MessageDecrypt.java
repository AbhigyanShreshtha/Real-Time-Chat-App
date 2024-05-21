package com.chatapp.source.helpers.rsa;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.util.Base64;

public class MessageDecrypt {

    public static String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes, "UTF-8");
    }
}
