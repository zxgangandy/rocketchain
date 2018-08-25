package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.BlockHeight;

public class BlockHeightCodec implements Codec<BlockHeight> {
    @Override
    public BlockHeight transcode(CodecInputOutputStream io, BlockHeight obj) {
        long height = Codecs.Int64.transcode(io, obj.getHeight());
        if (io.getInput()) {
            return new BlockHeight(height);
        }
        return null;
    }
}
