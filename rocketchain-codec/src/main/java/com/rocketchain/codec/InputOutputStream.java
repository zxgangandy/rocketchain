package com.rocketchain.codec;

import io.netty.buffer.ByteBuf;


/** A stream that has either input or output stream.
 * If it has an input stream, it reads data from the stream and returns the read value.
 * If it has an output stream, it write the argument data to the stream and returns the argument unchanged.
 *
 */
public class InputOutputStream {
    private ByteBuf byteBuf;
    private Boolean isInput;

    public InputOutputStream(ByteBuf byteBuf, Boolean isInput) {
        this.byteBuf = byteBuf;
        this.isInput = isInput;
    }

    public ByteBuf fixedBytes(int length, ByteBuf bytes) {
        assert(length >= 0);

        if (isInput) {
            return byteBuf.readBytes(length);
        } else {
            assert(bytes != null);
            return byteBuf.writeBytes(bytes, length);
        }
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public Boolean getInput() {
        return isInput;
    }
}
