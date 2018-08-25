package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.Hash;

public class BlockHeaderCodec implements Codec<BlockHeader> {

    @Override
    public BlockHeader transcode(CodecInputOutputStream io, BlockHeader obj) {
        Integer version = Codecs.Int32L.transcode(io, obj == null ? null : obj.getVersion());
        Hash hashPrevBlock = new HashCodec().transcode(io, obj == null ? null : obj.getHashPrevBlock());
        Hash hashMerkleRoot = new HashCodec().transcode(io, obj == null ? null : obj.getHashMerkleRoot());
        Long timestamp = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getTimestamp());
        Long target = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getTarget());
        Long nonce = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getNonce());

        if (io.getInput()) {
            return new BlockHeader(version, hashPrevBlock, hashMerkleRoot, timestamp, target, nonce);
        }
        return null;
    }
}
