package navicat;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

/**
 * Navicat12及以上密码加密解密
 *
 * @author lzy
 * @date 2021/01/14 15:58
 */
public class Navicat12Cipher extends NavicatChiper {
    private static SecretKeySpec _AesKey;
    private static IvParameterSpec _AesIV;

    static {
        _AesKey = new SecretKeySpec("libcckeylibcckey".getBytes(StandardCharsets.UTF_8), "AES");
        _AesIV = new IvParameterSpec("libcciv libcciv ".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String encryptString(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, _AesKey, _AesIV);
            byte[] ret = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(ret);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String decryptString(String ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, _AesKey, _AesIV);
            byte[] ret = cipher.doFinal(DatatypeConverter.parseHexBinary(ciphertext));
            return new String(ret, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
