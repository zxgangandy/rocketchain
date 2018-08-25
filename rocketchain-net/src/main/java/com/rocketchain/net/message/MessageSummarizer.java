package com.rocketchain.net.message;

import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.utils.lang.StringUtil;


public class MessageSummarizer {

    public static String summarize(ProtocolMessage message) {
        return StringUtil.getBrief(message.toString(), 256);
    }
}
