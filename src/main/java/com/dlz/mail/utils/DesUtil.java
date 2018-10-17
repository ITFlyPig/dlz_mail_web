package com.dlz.mail.utils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DesUtil {
    private static DesUtil ourInstance = new DesUtil();

    public static DesUtil getInstance() {
        return ourInstance;
    }
    private Cipher ecipher,dcipher;


    private DesUtil(){
        DESKeySpec dks;
        try {
            //Constants.EncryptDecryptKEY是我一个常量类中的字符串而已，它就是加密解密的密钥。请自行替换。
            dks = new DESKeySpec(Constant.EncryptDecryptKEY.getBytes());
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            SecretKey desKey = skf.generateSecret(dks);
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, desKey);
            dcipher.init(Cipher.DECRYPT_MODE, desKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }

    public String encrypt(String str) throws Exception {
        // Encode the string into bytes using utf-8
        byte[] utf8 = str.getBytes("UTF8");
        // Encrypt
        byte[] enc = ecipher.doFinal(utf8);
        // Encode bytes to base64 to get a string
        return new sun.misc.BASE64Encoder().encode(enc);
    }

    public String decrypt(String str) throws Exception {
        // Decode base64 to get bytes
        byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
        byte[] utf8 = dcipher.doFinal(dec);
        // Decode using utf-8
        return new String(utf8, "UTF8");
    }
}
