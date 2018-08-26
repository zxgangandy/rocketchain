package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.IPv6Address;
import com.rocketchain.proto.NetworkAddress;

import java.math.BigInteger;

public class NetworkAddressCodec implements Codec<NetworkAddress> {
    @Override
    public NetworkAddress transcode(CodecInputOutputStream io, NetworkAddress obj) {

        BigInteger services = Codecs.UInt64L.transcode(io,  obj == null ? null : obj.getServices());
        IPv6Address ipv6     = new IPv6AddressCodec().transcode(io,  obj == null ? null : obj.getIpv6());
        // Note, port is encoded with big endian, not little endian
        Integer port     = Codecs.UInt16.transcode(io,  obj == null ? null : obj.getPort());

        if (io.getInput()) {
            return new NetworkAddress(services, ipv6, port);
        }
        return null;
    }
}
