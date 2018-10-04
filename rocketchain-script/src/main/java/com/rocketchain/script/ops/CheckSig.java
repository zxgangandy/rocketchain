package com.rocketchain.script.ops;

import com.google.common.collect.Lists;
import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.script.TransactionSignature;
import com.rocketchain.crypto.ECKey;
import com.rocketchain.crypto.Hash256;
import com.rocketchain.proto.Script;
import com.rocketchain.utils.Config;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;

import java.io.IOException;
import java.util.List;

public class CheckSig implements Crypto {
    @Override
    public OpCode opCode() {
        return null;
    }

    @Override
    public void execute(ScriptEnvironment env) {

    }

    public void checkSig(Script script , ScriptEnvironment env ) {
        assert(env.getTransaction() != null);
        assert(env.getTransactionInputIndex() != null);

        // At least we need to have two items on the stack.
        //   1. a public key.
        //   2. a signature
        if (env.getStack().size() < 2) {
            // TODO : Write a test for this branch.
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:CheckSig");
        }

        ScriptValue publicKey = env.getStack().pop();
        ScriptValue rawSignature = env.getStack().pop();

        // Check if the signature format is valid.
        // BUGBUG : See if we always have to check the signature format.
        if (!ECKey.ECDSASignature.isEncodingCanonical(rawSignature.getValue())) {
            throw new ScriptEvalException(ErrorCode.InvalidSignatureFormat, "ScriptOp:CheckSig");
        }

        ECKey.ECDSASignature signature  = null;
        try {
            signature = ECKey.ECDSASignature.decodeFromDER(rawSignature.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] scriptData = TransactionSignature.getScriptForCheckSig(script.getData().getArray(), env.getSigCheckOffset(), Lists.newArrayList(rawSignature) );

        // use only the low 5 bits from the last byte of the signature to get the hash mode.
        // TODO : The 0x1f constant is from TransactionSignature.sigHashMode of BitcoinJ. Investigate if it is necessary.
        //val howToHash : Int = rawSigature.value.last & 0x1f
        int length = rawSignature.getValue().length;
        int howToHash = rawSignature.getValue()[length-1];

        Hash256 hashOfInput  = TransactionSignature.calculateHash(env.getTransaction(), env.getTransactionInputIndex(), scriptData, howToHash);

        if (ECKey.verify(hashOfInput.getValue().getArray(), signature, publicKey.getValue())) {
            pushTrue(env);
        } else {
            pushFalse(env);
        }
    }

    public void checkMultiSig(Script script , ScriptEnvironment env ) {
        assert(env.getTransaction() != null);
        assert(env.getTransactionInputIndex() != null);

        // At least we need to have 5 items on the stack.
        //   1. the number of public keys
        //   2. at least a public key.
        //   3. the number of signatures.
        //   4. at least a signature
        //   5. dummy
        if (env.getStack().size() < 5) {
            // TODO : Write a test for this branch.
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:CheckMultiSig, the total number of stack items");
        }

        ////////////////////////////////////////////////////////////////////////////////
        // Step 1 : Get the public key count
        int publicKeyCount = env.getStack().popInt().intValue();
        if (publicKeyCount < 0 || publicKeyCount > Config.MAX_PUBLIC_KEYS_FOR_MULTSIG)
            throw new ScriptEvalException(ErrorCode.TooManyPublicKeys, "ScriptOp:CheckMultiSig, the number of public keys");

        // Now, we need to have at least publicKeyCount + 3 items on the stack.
        //   1. publicKeyCount public keys.
        //   2. the number of signatures.
        //   3. at least a signature
        //   4. dummy
        if (env.getStack().size() < publicKeyCount + 3) {
            // TODO : Write a test for this branch.
            throw new  ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:CheckMultiSig, the remaining number of stack items after getting the public key count");
        }

        ////////////////////////////////////////////////////////////////////////////////
        // Step 2 : Get the public keys
        List<ScriptValue> publicKeys = Lists.newArrayList(env.getStack().pop() );

        ////////////////////////////////////////////////////////////////////////////////
        // Step 3 : Get the signature count
        int signatureCount = env.getStack().popInt().intValue();
        if (signatureCount < 0 || signatureCount > publicKeyCount)
            throw new ScriptEvalException(ErrorCode.TooManyPublicKeys, "ScriptOp:CheckMultiSig, the public key count");

        // Now, we need to have at least signatureKeyCount + 1 items on the stack.
        //   1. signatureCount signatures
        //   2. dummy
        if (env.getStack().size() < signatureCount + 1) {
            // TODO : Write a test for this branch.
            throw new ScriptEvalException(ErrorCode.NotEnoughInput, "ScriptOp:CheckMultiSig, the signature count");
        }

        ////////////////////////////////////////////////////////////////////////////////
        // Step 4 : Get the signatures
        List<ScriptValue> signatures = Lists.newArrayList(env.getStack().pop() );

        // The reference implementation had a bug to pop one more item from the stack.
        env.getStack().pop();


        ////////////////////////////////////////////////////////////////////////////////
        // Step 5 : Scrub scriptData to get rid of signatures from it.
        byte[] scriptData  = TransactionSignature.getScriptForCheckSig(script.getData().getArray(), env.getSigCheckOffset(), signatures );

        boolean isValid = true;
        int consumedPublicKeyCount = 0;
        int consumedSignatureCount = 0;

        ////////////////////////////////////////////////////////////////////////////////
        // Step 6 : For each signature, try to match it with public keys.
        for (ScriptValue rawSignature  : signatures ) {

            ////////////////////////////////////////////////////////////////////////////////
            // Step 6.1 : Check the signature format.
            // BUGBUG : See if we always have to check the signature format.
            if (!ECKey.ECDSASignature.isEncodingCanonical(rawSignature.getValue())) {
                throw new ScriptEvalException(ErrorCode.InvalidSignatureFormat, "ScriptOp:CheckMultiSig, invalid raw signature format.");
            }


            ////////////////////////////////////////////////////////////////////////////////
            // Step 6.2 : Get the hash value from the spending transaction.
            // use only the low 5 bits from the last byte of the signature to get the hash mode.
            // TODO : The 0x1f constant is from TransactionSignature.sigHashMode of BitcoinJ. Investigate if it is necessary.
            //val howToHash : Int = rawSigature.value.last & 0x1f
            int length = rawSignature.getValue().length;
            int howToHash  = rawSignature.getValue()[length-1];

            Hash256 hashOfInput  = TransactionSignature.calculateHash(env.getTransaction(), env.getTransactionInputIndex(), scriptData, howToHash);

            // Step 6.3 : Try to match the signature with a public key
            ECKey.ECDSASignature signature  = null;
            try {
                signature = ECKey.ECDSASignature.decodeFromDER(rawSignature.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean signatureVerified = false;
            // Loop until we successfully verify the signature.
            while(consumedPublicKeyCount < publicKeyCount && !signatureVerified) {
                ScriptValue publicKey = publicKeys.get(consumedPublicKeyCount);
                if (ECKey.verify(hashOfInput.getValue().getArray(), signature, publicKey.getValue())) {
                    signatureVerified = true;
                    consumedSignatureCount +=1;
                }
                consumedPublicKeyCount += 1;
            }

            int signaturesLeft = signatureCount - consumedSignatureCount;
            int publicKeysLeft = publicKeyCount - consumedPublicKeyCount;
            if (signaturesLeft > publicKeysLeft) {
                isValid = false;
            }
        }

        if (isValid) {
           pushTrue(env);
        } else {
            pushFalse(env);
        }
    }
}
