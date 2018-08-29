package com.rocketchain.codec;

import com.google.common.collect.ImmutableList;
import com.rocketchain.proto.ProtocolMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BitcoinProtocol implements NetworkProtocol {

    private static List<ProtocolMessageCodec> codes = ImmutableList.of(new VersionCodec(), new VerackCodec());

    private Map<String, ProtocolMessageCodec> codecMapByCommand = codes.stream().
            collect(Collectors.toMap(ProtocolMessageCodec::getCommand, item -> item));

    private Map<Class, ProtocolMessageCodec> codecMapByClass = codes.stream().
            collect(Collectors.toMap(ProtocolMessageCodec::getClazz, item -> item));

    @Override
    public String getCommand(ProtocolMessage message) {
        ProtocolMessageCodec codec = codecMapByClass.get(message.getClass());
        return codec.command;
    }

    @Override
    public void encode(ByteBuf writeBuf, ProtocolMessage message) {
        // Force to type case the codec to transcode ProtocolMessage.
        // **b0c1** provided this code.
        ProtocolMessageCodec codec = codecMapByClass.get(message.getClass());
        codec.transcode(new CodecInputOutputStream(writeBuf,   false), message);
    }

    @Override
    public ProtocolMessage decode(ByteBuf readBuf, String command) {
        ProtocolMessageCodec codec = codecMapByCommand.get(command);
        Object message = codec.transcode(new CodecInputOutputStream(readBuf,  true), null);
        return (ProtocolMessage) message;
    }
}
