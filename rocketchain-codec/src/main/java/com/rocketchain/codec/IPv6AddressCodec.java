package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.FixedByteArrayCodec;
import com.rocketchain.proto.IPv6Address;
import com.rocketchain.utils.lang.Bytes;

public class IPv6AddressCodec implements Codec<IPv6Address> {

    private FixedByteArrayCodec byteArrayLength16 = Codecs.fixedByteArray(16);

    @Override
    public IPv6Address transcode(CodecInputOutputStream io, IPv6Address obj) {

        Bytes bytesAddr = obj == null ? null : obj.getAddress();
        byte[] byteAddr = bytesAddr == null ? null : bytesAddr.getArray();
        byte[] address = byteArrayLength16.transcode(io, byteAddr);

        if (io.getInput()) {
            return new  IPv6Address(new Bytes(address));
        }
        return null;
    }
}
