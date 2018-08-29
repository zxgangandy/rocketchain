package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.NetworkAddress;
import com.rocketchain.proto.NetworkAddressWithTimestamp;

public class NetworkAddressWithTimestampCodec implements Codec<NetworkAddressWithTimestamp> {
    @Override
    public NetworkAddressWithTimestamp transcode(CodecInputOutputStream io, NetworkAddressWithTimestamp obj) {

        Long timestamp = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getTimestamp());
        NetworkAddress address = new NetworkAddressCodec().transcode(io, obj == null ? null : obj.getAddress());

        if (io.getInput()) {
            return new NetworkAddressWithTimestamp(timestamp, address);
        }

        return null;
    }
}
