package com.rocketchain.utils.exception;

public class ExceptionUtil {
    public static String cause(Throwable throwable) {
        if (throwable == null) {
            return "";
        } else {
            return "{Exception : " + throwable.getMessage() + ", stack : " + StackUtil.getStackTrace(throwable) + "}";
        }
    }
}
