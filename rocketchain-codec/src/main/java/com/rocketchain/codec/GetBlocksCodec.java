package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.VariableListCodec;
import com.rocketchain.proto.GetBlocks;
import com.rocketchain.proto.Hash;

import java.util.List;

public class GetBlocksCodec extends ProtocolMessageCodec<GetBlocks> {
    private VariableListCodec<Hash> HashListCodec = Codecs.variableListOf(new HashCodec());

    public GetBlocksCodec() {
        command = "getblocks";
        clazz = GetBlocks.class;
    }

    @Override
    public GetBlocks transcode(CodecInputOutputStream io, GetBlocks obj) {
        Long version = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getVersion());
        List<Hash> blockLocatorHashes = HashListCodec.transcode(io, obj == null ? null : obj.getBlockLocatorHashes());
        Hash hashStop = new HashCodec().transcode(io, obj == null ? null : obj.getHashStop());

        if (io.getInput()) {
            return new GetBlocks(version, blockLocatorHashes, hashStop);
        }
        return null;
    }
}
