package com.easyconnect.util;

import com.easyconnect.enums.NavicatVerEnum;
import com.easyconnect.factory.NavicatCipherFactory;
import com.easyconnect.navicat.NavicatChiper;

/**
 * DecodeNcx 解密navicat导出的密码
 *
 * <p>
 * navicat 11采用BF(blowfish)-ECB方式，对应mode为ECB
 * navicat 12以上采用AES-128-CBC方式，对应mode为CBC
 *
 * @author lzy
 * @date 2022/01/10 15:13
 */
public class DecodeNcx {

    private static String mode;

    public DecodeNcx(String mode) {
        DecodeNcx.mode = mode;
    }

    /**
     * 根据mode进行解密
     *
     * @param str
     * @return String
     */
    public String decode(String str) {
        if (StringUtil.isEmpty(str)) {
            return "";
        }
        NavicatChiper chiper = NavicatCipherFactory.get(mode);
        return chiper.decryptString(str);
    }

    public static void main(String[] args) {
        DecodeNcx decodeNcx = new DecodeNcx(NavicatVerEnum.native11.name());
        System.out.println(decodeNcx.decode("575D3920F7"));
    }
}
