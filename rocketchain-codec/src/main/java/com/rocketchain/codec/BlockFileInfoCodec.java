package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.BlockFileInfo;

public class BlockFileInfoCodec implements Codec<BlockFileInfo> {
    @Override
    public BlockFileInfo transcode(CodecInputOutputStream io, BlockFileInfo obj) {
        Integer blockCount = Codecs.Int32L.transcode(io, obj == null ? null : obj.getBlockCount());
        Long fileSize = Codecs.Int64L.transcode(io, obj == null ? null : obj.getFileSize());
        Long firstBlockHeight = Codecs.Int64L.transcode(io, obj == null ? null : obj.getFirstBlockHeight());
        Long lastBlockHeight = Codecs.Int64L.transcode(io, obj == null ? null : obj.getLastBlockHeight());
        Long firstBlockTimestamp = Codecs.Int64L.transcode(io, obj == null ? null : obj.getFirstBlockTimestamp());
        Long lastBlockTimestamp = Codecs.Int64L.transcode(io, obj == null ? null : obj.getLastBlockTimestamp());

        if (io.getInput()) {
            return new BlockFileInfo(
                    blockCount,
                    fileSize,
                    firstBlockHeight,
                    lastBlockHeight,
                    firstBlockTimestamp,
                    lastBlockTimestamp
            );
        }

        return null;
    }
}
