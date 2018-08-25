package com.rocketchain.codec;

import com.rocketchain.crypto.Hash256;
import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.proto.BlockHeader;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.utils.lang.Bytes;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.ArrayUtils;

public class HashUtil {


    public static Hash hashBlockHeader(BlockHeader blockHeader) {
        CodecInputOutputStream io = new CodecInputOutputStream(Unpooled.buffer(), false);
        new BlockHeaderCodec().transcode(io, blockHeader);

        // Run SHA256 twice and reverse bytes.
        assert (io.getByteBuf().hasArray());
        Hash256 hash = HashFunctions.hash256(ByteBufUtil.getBytes(io.getByteBuf()));
        byte[] temp = hash.getValue().getArray();
        ArrayUtils.reverse(temp);
        return new Hash(new Bytes(temp));
    }


    public static Hash hashTransaction(Transaction transaction) {
        CodecInputOutputStream io = new CodecInputOutputStream(Unpooled.buffer(), false);
        new TransactionCodec().transcode(io, transaction);

        // Run SHA256 twice and reverse bytes.
        assert (io.getByteBuf().hasArray());
        Hash256 hash = HashFunctions.hash256(ByteBufUtil.getBytes(io.getByteBuf()));

        // BUGBUG : Rethink if using ByteBuf in Hash is a correct apporach.
        byte[] temp = hash.getValue().getArray();
        ArrayUtils.reverse(temp);
        return new Hash(new Bytes(temp));
    }
}
