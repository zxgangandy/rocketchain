package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.InPoint;
import com.rocketchain.proto.Transaction;
import com.rocketchain.proto.TransactionPoolEntry;
import com.rocketchain.utils.lang.Option;

import java.util.List;
import java.util.stream.Collectors;

import static com.rocketchain.codec.primitive.Codecs.OptionalInPointListCodec;

public class TransactionPoolEntryCodec implements Codec<TransactionPoolEntry> {
    @Override
    public TransactionPoolEntry transcode(CodecInputOutputStream io, TransactionPoolEntry obj) {
        Transaction transaction = new TransactionCodec().transcode(io, obj == null ? null : obj.getTransaction());
        List<Option<InPoint>> outputsSpentBy = OptionalInPointListCodec.transcode(io,
                obj == null ? null : obj.getOutputsSpentBy().stream()
                        .map(item -> Option.from(item))
                        .collect(Collectors.toList()));
        long createdAtNanos = Codecs.Int64.transcode(io, obj == null ? null : obj.getCreatedAtNanos());

        if (io.getInput()) {
            return new TransactionPoolEntry(
                    transaction,
                    obj == null ? null : outputsSpentBy.stream()
                            .map(item -> item.toNullable())
                            .collect(Collectors.toList()),
                    createdAtNanos);
        }
        return null;
    }
}
