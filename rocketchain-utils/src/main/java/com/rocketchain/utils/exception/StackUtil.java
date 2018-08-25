package com.rocketchain.utils.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackUtil {
    public static String getStackTrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        e.printStackTrace(writer);
        return stringWriter.toString();
    }


}
