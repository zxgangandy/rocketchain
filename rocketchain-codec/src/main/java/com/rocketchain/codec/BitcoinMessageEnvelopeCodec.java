package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.UInt32LCodec;
import com.rocketchain.proto.Checksum;
import com.rocketchain.proto.Magic;
import com.rocketchain.utils.lang.ArrayUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class BitcoinMessageEnvelopeCodec implements Codec<BitcoinMessageEnvelope> {
    private final static int COMMAND_SIZE = 12;

    private final static int COMMAND_LENGTH = 12;
    private UInt32LCodec PayloadLengthCodec = Codecs.UInt32L;

    private int PAYLOAD_LENGTH_SIZE = PayloadLengthCodec.encode(0L).length;
    private long MIN_ENVELOPE_BYTES = envelopSize(0);
    private int PAYLOAD_LENGTH_OFFSET = Magic.VALUE_SIZE + COMMAND_LENGTH;

    public long envelopSize(long payloadLength) {
        return Magic.VALUE_SIZE +
                COMMAND_LENGTH + // command
                PAYLOAD_LENGTH_SIZE + // length
                Checksum.VALUE_SIZE +
                payloadLength;
    }

    @Override
    public BitcoinMessageEnvelope transcode(CodecInputOutputStream io, BitcoinMessageEnvelope obj) {
        Magic magic = new MagicCodec().transcode(io, obj == null ? null : obj.getMagic());
        byte[] encodeCommand = obj == null ? null : encodeCommand(obj.getCommand());
        byte[] command = Codecs.fixedByteArray(COMMAND_LENGTH).transcode(io, encodeCommand);
        Long length = PayloadLengthCodec.transcode(io, obj == null ? null : Long.valueOf(obj.getLength()));
        Checksum checksum = new ChecksumCodec().transcode(io, obj == null ? null : obj.getChecksum());

        // To avoid read index from being changed for the bytes ByteBuf, wrap it before passing to writeBytes
        ByteBuf wrappedPayload = (obj == null) ? null : Unpooled.wrappedBuffer(obj.getPayload());

        ByteBuf payload = io.fixedBytes((obj == null) ? length.intValue() : obj.getLength(), wrappedPayload);

        if (io.getInput()) {
            return new BitcoinMessageEnvelope(magic, decodeCommand(command), length.intValue(), checksum, payload);
        }
        return null;
    }


    private String decodeCommand(byte[] zeroPaddedCommand) {
        assert (zeroPaddedCommand.length == COMMAND_SIZE);

        // command is a 0 padded string. Get rid of trailing 0 values.
        byte[] command = ArrayUtil.unpad(zeroPaddedCommand, (byte) 0);

        // BUGBUG : Dirty code. make it clean.
        return new String(command, StandardCharsets.US_ASCII);
    }

    private byte[] encodeCommand(String command) {
        assert (command.length() <= COMMAND_SIZE);

        // BUGBUG : Dirty code. make it clean.
        byte[] bytes = command.getBytes(StandardCharsets.US_ASCII);
        // Pad the array with 0, to make the size to 12 bytes.
        return ArrayUtil.pad(bytes, COMMAND_SIZE, (byte) 0);
    }

    private long getPayloadLength(ByteBuf encodedByteBuf) {
        ByteBuf destBuffer = Unpooled.buffer();
        encodedByteBuf.getBytes(encodedByteBuf.readerIndex() + PAYLOAD_LENGTH_OFFSET, destBuffer, PAYLOAD_LENGTH_SIZE);

        Long payloadLength = PayloadLengthCodec.decode(destBuffer);
        return payloadLength;
    }

    private Magic getMagic(ByteBuf encodedByteBuf) {
        ByteBuf destBuffer = Unpooled.buffer();
        encodedByteBuf.getBytes(encodedByteBuf.readerIndex(), destBuffer, Magic.VALUE_SIZE);

        Magic magic = new MagicCodec().decode(destBuffer);
        return magic;
    }

    public boolean decodable(ByteBuf encodedByteBuf) {
        if (encodedByteBuf.readableBytes() < MIN_ENVELOPE_BYTES) {
            return false;
        }

        if (BitcoinMessageEnvelope.isMagicValid(getMagic(encodedByteBuf))) {
            Long payloadLength = getPayloadLength(encodedByteBuf);

            return encodedByteBuf.readableBytes() >= envelopSize(payloadLength);
        } else {
            return false;
        }
    }
}
