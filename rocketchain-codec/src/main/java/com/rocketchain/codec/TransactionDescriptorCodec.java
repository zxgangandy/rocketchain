package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.VariableListCodec;
import com.rocketchain.proto.FileRecordLocator;
import com.rocketchain.proto.InPoint;
import com.rocketchain.proto.TransactionDescriptor;
import com.rocketchain.utils.lang.Option;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionDescriptorCodec implements Codec<TransactionDescriptor> {

    VariableListCodec<Option<InPoint>> OptionalInPointListCodec = Codecs.variableListOf(Codecs.optional(new InPointCodec()));

    @Override
    public TransactionDescriptor transcode(CodecInputOutputStream io, TransactionDescriptor obj) {
        FileRecordLocator transactionLocator = new FileRecordLocatorCodec().transcode(io, obj.getTransactionLocator());
        Long blockHeight = Codecs.Int64.transcode(io, obj.getBlockHeight());
        List<Option<InPoint>> outputsSpentBy = OptionalInPointListCodec.transcode(io, obj.getOutputsSpentBy()
                .stream()
                .map(Option::from)
                .collect(Collectors.toList()));

        if (io.getInput()) {
            return new TransactionDescriptor(
                    transactionLocator,
                    blockHeight,
                    outputsSpentBy
                            .stream()
                            .map(Option::toNullable)
                            .collect(Collectors.toList()));
        }
        return null;
    }
}
