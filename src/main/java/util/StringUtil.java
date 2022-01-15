package com.easyconnect.util;

import com.mysql.cj.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * StringUtil
 *
 * @author lzy
 * @date 2021/11/1 15:24
 **/
public class StringUtil {

    public static String StringEqual(String str) {
        if (StringUtils.isNullOrEmpty(str)) {
            return "--";
        }
        return str;
    }

    public static String join(Collection collection, String split) {
        StringBuilder stringBuffer = new StringBuilder();
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); stringBuffer.append((String) iterator.next())) {
            if (stringBuffer.length() != 0) {
                stringBuffer.append(split);
            }
        }
        return stringBuffer.toString();
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
