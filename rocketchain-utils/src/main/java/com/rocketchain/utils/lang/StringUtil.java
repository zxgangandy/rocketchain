package com.rocketchain.utils.lang;

public class StringUtil {

    public static String getBrief(String string, int maxLength) {
        return string.length() > maxLength ? string.substring(0, maxLength) : string;
    }

    public static boolean isEmpty(String src) {
        return src == null || src.length() == 0;
    }
}
