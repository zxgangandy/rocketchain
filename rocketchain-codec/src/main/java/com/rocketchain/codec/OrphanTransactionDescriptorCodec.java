package com.rocketchain.codec;

import com.rocketchain.proto.OrphanTransactionDescriptor;
import com.rocketchain.proto.Transaction;

public class OrphanTransactionDescriptorCodec implements Codec<OrphanTransactionDescriptor> {
    @Override
    public OrphanTransactionDescriptor transcode(CodecInputOutputStream io, OrphanTransactionDescriptor obj) {

        Transaction transaction = new TransactionCodec().transcode(io, obj == null ? null : obj.getTransaction());
        if (io.getInput()) {
            return new OrphanTransactionDescriptor(transaction);
        }
        return null;
    }
}
