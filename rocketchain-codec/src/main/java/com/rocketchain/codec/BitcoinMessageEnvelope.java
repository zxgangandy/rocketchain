package com.rocketchain.codec;

import com.rocketchain.crypto.Hash256;
import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.proto.Checksum;
import com.rocketchain.proto.Magic;
import com.rocketchain.proto.ProtocolMessage;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ProtocolCodecException;
import com.rocketchain.utils.lang.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.Arrays;


public class BitcoinMessageEnvelope {

    private Magic magic;
    private String command;
    private int length;
    private Checksum checksum;
    private ByteBuf payload;

    public BitcoinMessageEnvelope(Magic magic, String command, int length, Checksum checksum, ByteBuf payload) {
        this.magic = magic;
        this.command = command;
        this.length = length;
        this.checksum = checksum;
        this.payload = payload;
    }

    /**
     * Calculate checksum from a range of a byte array.
     *
     * @param buffer The byte array to check.
     * @param offset The start offset of the buffer to calculate the checksum.
     * @param length The length of bytes starting from the offset to calculate the checksum.
     */
    public static Checksum checksum(byte[] buffer, int offset, int length) {
        // OPTIMIZE : Directly calculate hash from the BitVector
        Hash256 hash = new HashFunctions().hash256(buffer, offset, length);

        byte[] value = Arrays.copyOfRange(hash.getValue().getArray(), 0, Checksum.VALUE_SIZE);
        return new Checksum(new Bytes(value));
    }

    public static BitcoinMessageEnvelope build(NetworkProtocol protocol, ProtocolMessage message) {
        ByteBuf byteBuf = Unpooled.buffer();
        protocol.encode(byteBuf, message);

        byte[] payload = ByteBufUtil.getBytes(byteBuf);

        return new BitcoinMessageEnvelope(
                BitcoinConfiguration.config.getMagic(),
                protocol.getCommand(message),
                payload.length,
                checksum(payload, 0, payload.length),
                byteBuf
        );
    }

    public static boolean isMagicValid(Magic magic) {
        return magic.equals(BitcoinConfiguration.config.getMagic());
    }

    public static void verify(BitcoinMessageEnvelope envelope) {

        if (!isMagicValid(envelope.getMagic())) {
            throw new ProtocolCodecException(ErrorCode.IncorrectMagicValue);
        }

        if (envelope.length != envelope.getPayload().readableBytes()) {
            throw new ProtocolCodecException(ErrorCode.PayloadLengthMismatch);
        }

        // BUGBUG : Try to avoid byte array copy.
        byte[] payloadBytes = ByteBufUtil.getBytes(envelope.getPayload());
        if (!envelope.getChecksum().equals(checksum(payloadBytes, 0, payloadBytes.length))) {
            throw new ProtocolCodecException(ErrorCode.PayloadChecksumMismatch);
        }
    }

    public Magic getMagic() {
        return magic;
    }

    public String getCommand() {
        return command;
    }

    public int getLength() {
        return length;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public ByteBuf getPayload() {
        return payload;
    }
}
