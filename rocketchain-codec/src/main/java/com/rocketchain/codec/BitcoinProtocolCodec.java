package com.rocketchain.codec;

import com.rocketchain.proto.ProtocolMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class BitcoinProtocolCodec {

    private NetworkProtocol protocol;

    public BitcoinProtocolCodec(NetworkProtocol protocol) {
        this.protocol = protocol;
    }

    public void encode(ProtocolMessage message, ByteBuf byteBuf) {
        BitcoinMessageEnvelope envelope = BitcoinMessageEnvelope.build(protocol, message);

        CodecInputOutputStream io = new CodecInputOutputStream(byteBuf, false);
        new BitcoinMessageEnvelopeCodec().transcode(io, envelope);
    }

    /**
     * Decode bits and add decoded messages to the given vector.
     *
     * @param encodedByteBuf The data to decode.
     * @param messages       The messages decoded from the given BitVector. The BitVector may have multiple messages, with or without an incomplete message. However, the BitVector itself may not have enough data to construct a message.
     * @return BitVector If we do not have enough data to construct a message, return the data as BitVector instead of constructing a message.
     */
    public void decode(ByteBuf encodedByteBuf, List<Object> messages) {
        while (new BitcoinMessageEnvelopeCodec().decodable(encodedByteBuf)) {
//      val io = CodecInputOutputStream(encodedByteBuf, isInput = false)
            BitcoinMessageEnvelope envelope = new BitcoinMessageEnvelopeCodec().decode(encodedByteBuf);

            BitcoinMessageEnvelope.verify(envelope);
            ProtocolMessage protocolMessage = protocol.decode(envelope.getPayload(), envelope.getCommand());

            messages.add(protocolMessage);
        }
    }
}
