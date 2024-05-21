package com.chatapp.source.helpers.aes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DecryptionGCM {

    private static final int GCM_TAG_LENGTH = 16;
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY = "EnterYourKeyHere"; // Replace with your AES key

    public static String decrypt(String encryptedText) throws Exception {
        String[] parts = encryptedText.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] cipherText = Base64.getDecoder().decode(parts[1]);
        byte[] key = Base64.getDecoder().decode(KEY);
        SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv));
        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText);
    }
}