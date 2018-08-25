package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.NormalTransactionInput;
import com.rocketchain.proto.UnlockingScript;

public class NormalTransactionInputCodec implements Codec<NormalTransactionInput> {
    @Override
    public NormalTransactionInput transcode(CodecInputOutputStream io, NormalTransactionInput obj) {
        Hash outputTransactionHash = new HashCodec().transcode(io, obj == null ? null : obj.getOutputTransactionHash());
        Long outputIndex = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getOutputIndex());
        UnlockingScript unlockingScript = new UnlockingScriptCodec().transcode(io, obj == null ? null : obj.getUnlockingScript());
        Long sequenceNumber = Codecs.UInt32L.transcode(io, obj == null ? null : obj.getSequenceNumber());

        if (io.getInput()) {
            return new NormalTransactionInput(
                    outputTransactionHash,
                    outputIndex,
                    unlockingScript,
                    sequenceNumber);
        }
        return null;
    }
}
