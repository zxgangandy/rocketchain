package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.OptionalCodec;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.Transaction;
import com.rocketchain.proto.WalletTransaction;
import com.rocketchain.utils.lang.Option;

public class WalletTransactionCodec implements Codec<WalletTransaction> {

    private OptionalCodec<Hash> OptionalHashCodec  = Codecs.optional(new HashCodec());
    private OptionalCodec<Long> OptionalInt64Codec = Codecs.optional(Codecs.Int64);
    private OptionalCodec<Integer> OptionalInt32Codec = Codecs.optional(Codecs.Int32);


    @Override
    public WalletTransaction transcode(CodecInputOutputStream io, WalletTransaction obj) {

        Option<Hash> blockHash = OptionalHashCodec.transcode(io, obj == null ? null :Option.from(obj.getBlockHash()));
        Option<Long> blockIndex = OptionalInt64Codec.transcode(io, obj == null ? null :Option.from(obj.getBlockIndex()));
        Option<Long> blockTime = OptionalInt64Codec.transcode(io, obj == null ? null :Option.from(obj.getBlockTime()));
        Option<Hash> transactionId = OptionalHashCodec.transcode(io, obj == null ? null :Option.from(obj.getTransactionId()));
        Long addedTime = Codecs.Int64.transcode(io, obj == null ? null :obj.getAddedTime());
        Option<Integer> transactionIndex = OptionalInt32Codec.transcode(io, obj == null ? null :Option.from(obj.getTransactionIndex()));
        Transaction transaction = new TransactionCodec().transcode(io, obj == null ? null :obj.getTransaction());

        if (io.getInput()) {
            return new WalletTransaction(
                    blockHash.toNullable(),
                    blockIndex.toNullable(),
                    blockTime.toNullable(),
                    transactionId.toNullable(),
                    addedTime,
                    transactionIndex.toNullable(),
                    transaction);
        }
        return null;
    }
}
