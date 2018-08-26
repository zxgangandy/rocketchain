package com.rocketchain.codec;

import com.rocketchain.proto.ProtocolMessage;
import io.netty.buffer.ByteBuf;

public interface NetworkProtocol {

    String getCommand(ProtocolMessage message);

    // BUGBUG : Interface change  encode(message : ProtocolMessage) : ByteBuf -> encode(writeBuf : ByteBuf, message : ProtocolMessage)
    void encode(ByteBuf writeBuf, ProtocolMessage message);

    // BUGBUG : Interface change  decode(command:String, byteBuf:ByteBuf) : ProtocolMessage -> decode(readBuf: ByteBuf, command:String) : ProtocolMessage
    ProtocolMessage decode(ByteBuf readBuf, String command);
}
