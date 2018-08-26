package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.NetworkAddress;
import com.rocketchain.proto.Version;

import java.math.BigInteger;

public class VersionCodec extends ProtocolMessageCodec<Version> {
    public VersionCodec() {
        command = "version";
        clazz = Version.class;
    }

    @Override
    public Version transcode(CodecInputOutputStream io, Version obj) {
        Integer version = Codecs.Int32L.transcode(io, obj == null ? null : obj.getVersion());
        BigInteger services = Codecs.UInt64L.transcode(io, obj == null ? null : obj.getServices());
        Long timestamp = Codecs.Int64L.transcode(io, obj == null ? null : obj.getTimestamp());
        NetworkAddress destAddress = new NetworkAddressCodec().transcode(io, obj == null ? null : obj.getDestAddress());
        NetworkAddress sourceAddress = new NetworkAddressCodec().transcode(io, obj == null ? null : obj.getSourceAddress());
        BigInteger nonce = Codecs.UInt64L.transcode(io, obj == null ? null : obj.getNonce());
        String userAgent = Codecs.VariableString.transcode(io, obj == null ? null : obj.getUserAgent());
        Long startHeight = Codecs.Int64.transcode(io, obj == null ? null : obj.getStartHeight());
        Boolean relay = Codecs.Boolean.transcode(io, obj == null ? null : obj.getRelay());

        if (io.getInput()) {
            return new Version(
                    version,
                    services,
                    timestamp,
                    destAddress,
                    sourceAddress,
                    nonce,
                    userAgent,
                    startHeight,
                    relay);
        }

        return null;
    }
}
