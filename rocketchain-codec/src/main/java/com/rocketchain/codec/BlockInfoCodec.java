package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.OptionalCodec;
import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.BlockInfo;
import com.rocketchain.proto.FileRecordLocator;
import com.rocketchain.proto.Hash;
import com.rocketchain.utils.lang.Option;

public class BlockInfoCodec implements Codec<BlockInfo> {

    OptionalCodec optionalHashCodec = Codecs.optional(new HashCodec());
    OptionalCodec optionalFileRecordLocatorCodec = Codecs.optional(new FileRecordLocatorCodec());

    @Override
    public BlockInfo transcode(CodecInputOutputStream io, BlockInfo obj) {
        Long height = Codecs.Int64L.transcode(io, obj == null ? null : obj.getHeight());
        Long chainWork = Codecs.Int64L.transcode(io, obj == null ? null : obj.getChainWork());
        Option<Hash> nextBlockHash = optionalHashCodec.transcode(io, obj == null ? null : Option.from(obj.getNextBlockHash()));
        Integer transactionCount = Codecs.Int32L.transcode(io, obj == null ? null : obj.getTransactionCount());
        Integer status = Codecs.Int32L.transcode(io, obj == null ? null : obj.getStatus());
        BlockHeader blockHeader = new BlockHeaderCodec().transcode(io, obj == null ? null : obj.getBlockHeader());
        Option<FileRecordLocator> blockLocatorOption = optionalFileRecordLocatorCodec.transcode(io,
                obj == null ? null : Option.from(obj.getBlockLocatorOption()));

        if (io.getInput()) {
            return new BlockInfo(
                    height,
                    chainWork,
                    transactionCount,
                    nextBlockHash.toNullable(),
                    status,
                    blockHeader,
                    blockLocatorOption.toNullable()
            );
        }
        return null;
    }
}
