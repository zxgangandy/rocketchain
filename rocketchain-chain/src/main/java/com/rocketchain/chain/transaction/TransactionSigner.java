package com.rocketchain.chain.transaction;

import com.google.common.collect.Lists;
import com.rocketchain.crypto.ECKey;
import com.rocketchain.crypto.Hash256;
import com.rocketchain.proto.*;
import com.rocketchain.script.ScriptSerializer;
import com.rocketchain.script.TransactionSignature;
import com.rocketchain.script.ops.OpPush;
import com.rocketchain.script.ops.ScriptOp;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.TransactionSignException;
import com.rocketchain.utils.exception.UnsupportedFeature;
import com.rocketchain.utils.lang.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TransactionSigner {

    private KeyValueDatabase db ;

    public TransactionSigner(KeyValueDatabase db) {
        this.db = db;
    }

    /** Sign an input of a transaction with the list of given private keys.
     *
     * @param transaction The transaction to sign
     * @param inputIndex The input we want to sign. it is an index to transaction.inputs.
     * @param privateKeys An array holding private keys.
     * @param lockingScript The locking script that the given input unlocks. From the ouput pointed by the out point in the input.
     * @param chainView A blockchain view that can get the transaction output pointed by an out point.
     * @return The signed input.
     */
    protected TransactionInput signInput(Transaction transaction, int inputIndex, NormalTransactionInput normalTxInput,
                                         List<PrivateKey> privateKeys, LockingScript lockingScript, SigHash sigHash,
                                         BlockchainView chainView) {
        // We already checked if the number of private keys is 1. Multisig is not supported yet.
        assert(privateKeys.size() == 1);

        PrivateKey keyToUse = privateKeys.get(0);

        byte[] scriptData  =  TransactionSignature.getScriptForCheckSig(
                lockingScript.getData().getArray(),  // The locking script data.
                0, // The data to sign starts from offset 0
                Lists.newArrayList()); // We have no signatures to scrub from the locking script.

        int howToHash ;
        if (sigHash == SigHash.ALL) {
            howToHash =  1;
        } else {
            // We already checked that sigHash is SigHash.ALL. Currently it is the only supported hash type.
            assert (false);
            howToHash = 0;
        }

        Hash256 hashOfInput  = TransactionSignature.calculateHash(transaction, inputIndex, scriptData, howToHash);

        ECKey.ECDSASignature signature  =  ECKey.doSign(hashOfInput.getValue().getArray(), keyToUse.getValue());

        // Encode the signature to DER format, and append the hash type.
        byte[] encodedSignature = new byte[0];
        try {
            encodedSignature = com.google.common.primitives.Bytes.concat(signature.encodeToDER(), new byte[]{(byte)howToHash});
        } catch (IOException e) {
            e.printStackTrace();
        }

        PublicKey publickKey = PublicKey.from(keyToUse);
        // If we use compressed version, the transaction verification fails. Need investigation.
        byte[] encodedPublicKey = publickKey.encode();

        List<ScriptOp> unlockingScriptOps = Lists.newArrayList(
                OpPush.from(encodedSignature), // Signature.
                OpPush.from(encodedPublicKey)  // Public Key.
        );

        UnlockingScript unlockingScriptWithSignature = new UnlockingScript(new Bytes(ScriptSerializer.serialize(unlockingScriptOps)) );
        normalTxInput.setUnlockingScript(unlockingScriptWithSignature);
        return normalTxInput;
    }

    /** For an input, try to find matching private keys to sign, and sign the input.
     *
     * @param transaction The transaction to sign
     * @param inputIndex The input we want to sign. it is an index to transaction.inputs.
     * @param privateKeys An array holding private keys.
     * @param sigHash The type of signature hash to use for all of the signatures performed.
     * @param chainView A blockchain view that can get the transaction output pointed by an out point.
     * @return Some(transaction) if the given input was signed and the signature was verified. None otherwise.
     */
    protected Transaction tryToSignInput(Transaction transaction, int inputIndex , List<PrivateKey> privateKeys , SigHash sigHash , BlockchainView chainView )  {
        TransactionInput inputToSign = transaction.getInputs().get(inputIndex);

        if (inputToSign instanceof NormalTransactionInput) {
            LockingScript lockingScript  = new NormalTransactionVerifier(db, (NormalTransactionInput)inputToSign, transaction, inputIndex).getLockingScript(chainView);
            List<CoinAddress> addresses  = LockingScriptAnalyzer.extractAddresses(lockingScript);

            // TODO : Sign an input with multiple public key hashes.
            if (addresses.size() > 1) {
                throw new TransactionSignException(ErrorCode.UnsupportedFeature, "Multisig is not supported yet. Input Index : " + inputIndex);
            }
            if (addresses.isEmpty()) {
                throw new TransactionSignException(ErrorCode.UnsupportedFeature, "Unsupported locking script for the transaction input. Input Index : " + inputIndex);
            }

            CoinAddress address = addresses.get(0);

            List<PrivateKey> keysToUse = privateKeys.stream().filter(key ->{
                PublicKey publicKey = PublicKey.from(key);

            if ( Arrays.equals( publicKey.getHash().getValue().getArray(), address.getPublicKeyHash().getArray()) ) { // If a public key hash matches, we can sign the transaction.
                return true;
            } else {
                return false;
            }}).collect(Collectors.toList());

//            val keysToUse = privateKeys.filter { key ->
//                    val publicKey = PublicKey.from(key)
//
//                if ( Arrays.equals( publicKey.getHash().value.array, address.publicKeyHash.array) ) { // If a public key hash matches, we can sign the transaction.
//                    true
//                } else {
//                    false
//                }
//            }

            // Need to remove this assertion after implementing the multisig.
            assert(keysToUse.size() <= 1);

            if (keysToUse.isEmpty()) { // We don't have the private key to sign the input.
                return null;
            } else {
                // Get the signed input to create a transaction that has the signed input instead of the original one.
                TransactionInput signedInput = signInput(transaction, inputIndex, (NormalTransactionInput) inputToSign,
                        keysToUse, lockingScript, sigHash, chainView);

//                int i = 0;
//                List<TransactionInput> inputs = Lists.newArrayList();
//                for (TransactionInput input : transaction.getInputs()) {
//                    if (i == inputIndex) {
//                        inputs.add(signedInput);
//                    } else {
//                        inputs.add(input);
//                    }
//                    i++;
//                }

                List<TransactionInput> inputs = IntStream.range(0, transaction.getInputs().size())
                        .mapToObj(index -> (index == inputIndex) ? signedInput : transaction.getInputs().get(index))
                        .collect(Collectors.toList());

                transaction.setInputs(inputs);

                Transaction transactionWithSignedInput = transaction;

                // TODO : Uncomment.
                //TransactionVerifier(transactionWithSignedInput).verifyInput(inputIndex, blockIndex)

                return transactionWithSignedInput;
            }
        } else{
            throw new TransactionSignException( ErrorCode.InvalidTransactionInput, "An input to sign should be a normal transaction input, not a generation transaction input. Input Index : " + inputIndex);
        }
    }

    /** Signs a transaction from the first input. This is a recursive function with the base case at the end of the inputs.
     *
     * @param transaction The transaction to sign
     * @param privateKeys An array holding private keys.
     * @param sigHash The type of signature hash to use for all of the signatures performed.
     * @param chainView A blockchain view that can get the transaction output pointed by an out point.
     * @return The transaction with the newly signed input updated.
     */

    protected Transaction signInputsFrom(Transaction transaction , int inputIndex , List<PrivateKey> privateKeys , SigHash sigHash , BlockchainView chainView )  {

        // TODO : List.length is costly. Optimize it by passing an input itself by dropping one item at head for each invocation of this method.
        if (inputIndex >= transaction.getInputs().size()) { // The base case. We try to sign all inputs.
            return transaction;
        } else {
            // Do our best to sign an input
            Transaction transaction1 = tryToSignInput(transaction, inputIndex, privateKeys, sigHash, chainView);
            Transaction newTransaction =  transaction1 == null ?  transaction : transaction1;

            // Recursively call the signInputsFrom by increasing the input index.
            return signInputsFrom(newTransaction, inputIndex + 1, privateKeys, sigHash, chainView );
        }
    }

    /** Merge signatures of the original transaction and newly signed transaction.
     *
     * @param beforeSigning The original transaction.
     * @param afterSigning The newly signed transaction.
     * @param chainView A blockchain view that can get the transaction output pointed by an out point.
     * @return The transaction with merged inputs.
     */
    protected SignedTransaction mergeSignatures(Transaction beforeSigning , Transaction afterSigning , BlockchainView chainView ) {
        assert(beforeSigning.getInputs().size() == afterSigning.getInputs().size());

        boolean allInputsSigned = true;

        // Preserve signatures in the original transaction if any.
        List<TransactionInput> mergedInputs = Lists.newArrayList();
        int beforeSigningLen = beforeSigning.getInputs().size();
        int afterSigningLen = afterSigning.getInputs().size();
        int length = Math.min(beforeSigningLen, afterSigningLen);
        for (int i = 0; i < length; i++) {
            TransactionInput orgInput = beforeSigning.getInputs().get(i);
            TransactionInput newInput = afterSigning.getInputs().get(i);

            if (orgInput instanceof NormalTransactionInput && newInput instanceof NormalTransactionInput) {
                if (((NormalTransactionInput) orgInput).getUnlockingScript().getData().getArray().length != 0) {
                    mergedInputs.add(orgInput);
                } else {
                    if (((NormalTransactionInput) newInput).getUnlockingScript().getData().getArray().length != 0) {
                        allInputsSigned = false;
                    }
                    mergedInputs.add(newInput);
                }
            } else {
                // We already checked if all inputs are normal transaction inputs.
                throw new AssertionError();
            }
        }

        // Get the final transaction by copying the merged inputs.
        beforeSigning.setInputs(mergedInputs);
        Transaction finalTransaction = beforeSigning;

        // Make sure that the transaction verification passes if all inputs are signed.
        if (allInputsSigned) {
            new TransactionVerifier(db, finalTransaction).verify(chainView);
        }

        return new SignedTransaction(finalTransaction, allInputsSigned);
    }

    /** Sign a transaction.
     *
     * @param transaction The transaction to sign.
     * @param chainView A blockchain view that can get the transaction output pointed by an out point.
     * @param dependencies  Unspent transaction output details. The previous outputs being spent by this transaction.
     * @param privateKeys An array holding private keys.
     * @param sigHash The type of signature hash to use for all of the signatures performed.
     * @return true if all inputs are signed, false otherwise.
     */
    public SignedTransaction sign(Transaction transaction   ,
                                  BlockchainView chainView     ,
                                  List<UnspentTransactionOutput> dependencies  ,
                                  List<PrivateKey> privateKeys   ,
                                  SigHash  sigHash
    )  {
        // Only ALL SigHash type is supported for now.
        if (sigHash != SigHash.ALL) {
            throw new UnsupportedFeature(ErrorCode.UnsupportedHashType);
        }
        // dependencies parameter is not supported yet.
        if ( ! dependencies.isEmpty() ) {
            throw new  UnsupportedFeature(ErrorCode.UnsupportedFeature);
        }

        // TODO : Make the error code compatible with Bitcoin.
        if (transaction.getInputs().get(0).isCoinBaseInput()) {
            throw new TransactionSignException( ErrorCode.UnableToSignCoinbaseTransaction );
        }

        Transaction signedTransaction = signInputsFrom(transaction, 0/*input index*/, privateKeys, sigHash, chainView);

        SignedTransaction signedResult  = mergeSignatures(transaction, signedTransaction, chainView);
        return signedResult;
    }
}
