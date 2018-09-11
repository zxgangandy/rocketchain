package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.OptionalCodec;
import com.rocketchain.proto.TransactionOutput;
import com.rocketchain.proto.WalletOutput;
import com.rocketchain.utils.lang.Option;

public class WalletOutputCodec implements Codec<WalletOutput> {

    private OptionalCodec<Long> OptionalInt64Codec = Codecs.optional(Codecs.Int64);

    @Override
    public WalletOutput transcode(CodecInputOutputStream io, WalletOutput obj) {

        Option<Long> blockindex = OptionalInt64Codec.transcode(io, obj == null ? null : Option.from(obj.getBlockindex()));
        Boolean coinbase = Codecs.Boolean.transcode(io, obj == null ? null : obj.isCoinbase());
        Boolean spent = Codecs.Boolean.transcode(io, obj == null ? null : obj.isSpent());
        TransactionOutput transactionOutput = new TransactionOutputCodec().transcode(io, obj == null ? null : obj.getTransactionOutput());

        if (io.getInput()) {
            return new WalletOutput(blockindex.toNullable(), coinbase, spent, transactionOutput);
        }

        return null;
    }
}
