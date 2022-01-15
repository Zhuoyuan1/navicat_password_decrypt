package com.easyconnect.navicat;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Navicat11及以下密码加密解密
 *
 * @author lzy
 * @date 2021/01/14 15:58
 */
public class Navicat11Cipher extends NavicatChiper {
    public static final String DefaultUserKey = "3DC5CA39";
    private static byte[] _IV;

    private static SecretKeySpec _Key;
    private static Cipher _Encryptor;
    private static Cipher _Decryptor;

    private static void initKey(String UserKey) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            byte[] userkey_data = UserKey.getBytes(StandardCharsets.UTF_8);
            sha1.update(userkey_data, 0, userkey_data.length);
            _Key = new SecretKeySpec(sha1.digest(), "Blowfish");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initChiperEncrypt() {
        try {
            // Must use NoPadding
            _Encryptor = javax.crypto.Cipher.getInstance("Blowfish/ECB/NoPadding");
            _Encryptor.init(javax.crypto.Cipher.ENCRYPT_MODE, _Key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initChiperDecrypt() {
        try {
            // Must use NoPadding
            _Decryptor = javax.crypto.Cipher.getInstance("Blowfish/ECB/NoPadding");
            _Decryptor.init(javax.crypto.Cipher.DECRYPT_MODE, _Key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initIV() {
        try {
            byte[] initVec = DatatypeConverter.parseHexBinary("FFFFFFFFFFFFFFFF");
            _IV = _Encryptor.doFinal(initVec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xorBytes(byte[] a, byte[] b) {
        for (int i = 0; i < a.length; i++) {
            int aVal = a[i] & 0xff; // convert byte to integer
            int bVal = b[i] & 0xff;
            a[i] = (byte) (aVal ^ bVal); // xor aVal and bVal and typecast to byte
        }
    }

    private void xorBytes(byte[] a, byte[] b, int l) {
        for (int i = 0; i < l; i++) {
            int aVal = a[i] & 0xff; // convert byte to integer
            int bVal = b[i] & 0xff;
            a[i] = (byte) (aVal ^ bVal); // xor aVal and bVal and typecast to byte
        }
    }

    static {
        initKey(DefaultUserKey);
        initChiperEncrypt();
        initChiperDecrypt();
        initIV();
    }

    private byte[] Encrypt(byte[] inData) {
        try {
            byte[] CV = Arrays.copyOf(_IV, _IV.length);
            byte[] ret = new byte[inData.length];

            int blocks_len = inData.length / 8;
            int left_len = inData.length % 8;

            for (int i = 0; i < blocks_len; i++) {
                byte[] temp = Arrays.copyOfRange(inData, i * 8, (i * 8) + 8);

                xorBytes(temp, CV);
                temp = _Encryptor.doFinal(temp);
                xorBytes(CV, temp);

                System.arraycopy(temp, 0, ret, i * 8, 8);
            }

            if (left_len != 0) {
                CV = _Encryptor.doFinal(CV);
                byte[] temp = Arrays.copyOfRange(inData, blocks_len * 8, (blocks_len * 8) + left_len);
                xorBytes(temp, CV, left_len);
                System.arraycopy(temp, 0, ret, blocks_len * 8, temp.length);
            }

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String encryptString(String inputString) {
        try {
            byte[] inData = inputString.getBytes(StandardCharsets.UTF_8);
            byte[] outData = Encrypt(inData);
            return DatatypeConverter.printHexBinary(outData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private byte[] Decrypt(byte[] inData) {
        try {
            byte[] CV = Arrays.copyOf(_IV, _IV.length);
            byte[] ret = new byte[inData.length];

            int blocks_len = inData.length / 8;
            int left_len = inData.length % 8;

            for (int i = 0; i < blocks_len; i++) {
                byte[] temp = Arrays.copyOfRange(inData, i * 8, (i * 8) + 8);

                temp = _Decryptor.doFinal(temp);
                xorBytes(temp, CV);
                System.arraycopy(temp, 0, ret, i * 8, 8);
                for (int j = 0; j < CV.length; j++) {
                    CV[j] = (byte) (CV[j] ^ inData[i * 8 + j]);
                }
            }

            if (left_len != 0) {
                CV = _Encryptor.doFinal(CV);
                byte[] temp = Arrays.copyOfRange(inData, blocks_len * 8, (blocks_len * 8) + left_len);

                xorBytes(temp, CV, left_len);
                for (int j = 0; j < temp.length; j++) {
                    ret[blocks_len * 8 + j] = temp[j];
                }
            }

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decryptString(String hexString) {
        try {
            byte[] inData = DatatypeConverter.parseHexBinary(hexString);
            byte[] outData = Decrypt(inData);
            return new String(outData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
