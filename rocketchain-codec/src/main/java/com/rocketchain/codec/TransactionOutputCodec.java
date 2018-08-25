package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.LockingScript;
import com.rocketchain.proto.TransactionOutput;

public class TransactionOutputCodec implements Codec<TransactionOutput> {
    @Override
    public TransactionOutput transcode(CodecInputOutputStream io, TransactionOutput obj) {
        Long value = Codecs.Int64L.transcode(io, obj == null ? null : obj.getValue());
        LockingScript lockingScript = new LockingScriptCodec().transcode(io, obj == null ? null : obj.getLockingScript());

        if (io.getInput()) {
            return new TransactionOutput(value, lockingScript);
        }
        return null;
    }
}
