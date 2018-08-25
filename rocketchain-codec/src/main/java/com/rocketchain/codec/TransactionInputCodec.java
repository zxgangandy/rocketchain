package com.rocketchain.codec;

import com.rocketchain.proto.*;

public class TransactionInputCodec implements Codec<TransactionInput> {
    @Override
    public TransactionInput transcode(CodecInputOutputStream io, TransactionInput obj) {
        if (io.getInput()) {
            NormalTransactionInput readNormalTxInput = new NormalTransactionInputCodec().transcode(io, null);
            return normalTxToGenerationOrNormal(readNormalTxInput);
        } else {
            NormalTransactionInput normalTxInput = generationOrNormalToNormalTx(obj);
            return new NormalTransactionInputCodec().transcode(io, normalTxInput);
        }
    }

    private TransactionInput normalTxToGenerationOrNormal(NormalTransactionInput normalTxInput) {
        if (normalTxInput.isCoinBaseInput()) {
            // Generation Transaction
            // Convert to GenerationTransactionInput
            return new GenerationTransactionInput(
                    normalTxInput.getOutputTransactionHash(),
                    normalTxInput.getOutputIndex(),
                    new CoinbaseData(normalTxInput.getUnlockingScript().getData()),
                    normalTxInput.getSequenceNumber()
            );
        } else {
            return normalTxInput;
        }
    }

    private NormalTransactionInput generationOrNormalToNormalTx(TransactionInput txInput) {
        if (txInput instanceof GenerationTransactionInput) {
            return new NormalTransactionInput(
                    txInput.getOutputTransactionHash(),
                    txInput.getOutputIndex(),
                    new UnlockingScript(((GenerationTransactionInput) txInput).getCoinbaseData().getData()),
                    ((GenerationTransactionInput) txInput).getSequenceNumber());
        } else if (txInput instanceof NormalTransactionInput) {
            return (NormalTransactionInput) txInput;
        } else {
            throw new AssertionError();
        }
    }
}
