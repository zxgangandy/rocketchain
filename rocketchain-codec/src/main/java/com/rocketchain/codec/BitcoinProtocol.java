package com.rocketchain.codec;

import com.google.common.collect.ImmutableList;
import com.rocketchain.proto.ProtocolMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class BitcoinProtocol implements NetworkProtocol {

    private static List<ProtocolMessageCodec> codes = ImmutableList.of(new VersionCodec());

    @Override
    public String getCommand(ProtocolMessage message) {
        return null;
    }

    @Override
    public void encode(ByteBuf writeBuf, ProtocolMessage message) {

    }

    @Override
    public ProtocolMessage decode(ByteBuf readBuf, String command) {
        return null;
    }
}
