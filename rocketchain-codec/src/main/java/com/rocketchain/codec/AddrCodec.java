package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.VariableListCodec;
import com.rocketchain.proto.Addr;
import com.rocketchain.proto.NetworkAddressWithTimestamp;

import java.util.List;

public class AddrCodec extends ProtocolMessageCodec<Addr> {

    private VariableListCodec<NetworkAddressWithTimestamp> networkAddressWithTimestampListCodec =
            Codecs.variableListOf(new NetworkAddressWithTimestampCodec());

    public AddrCodec() {
        command = "addr";
        clazz = Addr.class;
    }

    @Override
    public Addr transcode(CodecInputOutputStream io, Addr obj) {
        List<NetworkAddressWithTimestamp> addresses = networkAddressWithTimestampListCodec.
                transcode(io, obj == null ? null : obj.getAddresses());

        if (io.getInput()) {
            return new Addr(addresses);
        }
        return null;
    }
}
