package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.codec.primitive.VariableListCodec;
import com.rocketchain.proto.Transaction;
import com.rocketchain.proto.TransactionInput;
import com.rocketchain.proto.TransactionOutput;

import java.util.List;

public class TransactionCodec implements Codec<Transaction> {
    private VariableListCodec TransactionInputListCodec = Codecs.variableListOf(new TransactionInputCodec());
    private VariableListCodec TransactionOutputListCodec = Codecs.variableListOf(new TransactionOutputCodec());

    @Override
    public Transaction transcode(CodecInputOutputStream io, Transaction obj) {
        Integer version = Codecs.Int32L.transcode(io, obj == null ? null : obj.getVersion());
        List<TransactionInput> inputs = TransactionInputListCodec.transcode(io, obj == null ? null : obj.getInputs());
        List<TransactionOutput> outputs = TransactionOutputListCodec.transcode(io, obj == null ? null : obj.getOutputs());
        Long lockTime = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getLockTime());

        if (io.getInput()) {
            return new Transaction(version, inputs, outputs, lockTime);
        }
        return null;
    }
}
