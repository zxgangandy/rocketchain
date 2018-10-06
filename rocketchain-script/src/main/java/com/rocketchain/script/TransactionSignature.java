package com.rocketchain.script;

import com.google.common.collect.Lists;
import com.rocketchain.codec.TransactionCodec;
import com.rocketchain.script.ops.OpCodeSparator;
import com.rocketchain.codec.CodecInputOutputStream;
import com.rocketchain.crypto.Hash256;
import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.proto.*;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.TransactionVerificationException;
import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.Utils;
import com.rocketchain.utils.net.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;


public class TransactionSignature {

    /** Get the script for verifying if a signature is valid.
     * Also it gets rid of signatures from the given script.
     *
     * @param rawScript The script where we want to remove the signature.
     * @param startOffset Copy bytes from this offset in rawScript to get the script for check sign
     * @param rawSignatures The signatures we are going to remove. To support OP_CHECKMULTISIG, it accepts multiple signatures.
     * @return The script for verifying if a signature is valid.
     */
    public static byte[] getScriptForCheckSig(byte[] rawScript, int startOffset, List<ScriptValue> rawSignatures )  {
        // Step 1 : Copy the region of the raw script starting from startOffset
        byte[] scriptFromStartOffset = null;
        if (startOffset>0)
            scriptFromStartOffset = Arrays.copyOfRange(rawScript, startOffset, rawScript.length);
        else
            scriptFromStartOffset = rawScript; // In most cases, startOffset is 0. Do not copy anything.

        // Step 2 : Remove the signatures from the script if any.
        byte[] signatureRemoved  = scriptFromStartOffset;
        for (ScriptValue rawSignature :   rawSignatures) {
            signatureRemoved = Utils.removeAllInstancesOf(signatureRemoved, rawSignature.value);
        }

        // Step 3 : Remove OP_CODESEPARATOR if any.
        return Utils.removeAllInstancesOfOp(signatureRemoved, new OpCodeSparator().opCode().getCode());
    }

    /** Calculate hash value for a given transaction input, and part of script that unlocks the UTXO attached to the input.
     * Why use part of script instead of all script bytes?
     *
     * 1. We need to use bytes after the OP_CODESEPARATOR in the script.
     * 2. We need to get rid of all signature data from the script.
     * 3. We need to get rid of OP_CODESEPARATOR OP code from the script.
     *
     * @param transactionInputIndex The index of the transaction input to get the hash.
     * @param scriptData A part of unlocking script for the UTXO attached to the given transaction input.
     * @param howToHash Decides how to calculate the hash value from this transaction and the given script.
     *                  The value should be one of values in Transaction.SigHash
     * @return The calculated hash value.
     */
    public static Hash256 calculateHash(Transaction transaction , int transactionInputIndex , byte[] scriptData , int howToHash )  {
        // Step 1 : Check if the transactionInputIndex is valid.
        if (transactionInputIndex < 0 || transactionInputIndex >= transaction.getInputs().size()) {
            throw new TransactionVerificationException(ErrorCode.InvalidInputIndex, "calculateHash: invalid transaction input");
        }

        // Step 2 : For each hash type, mutate the transaction.
        Transaction alteredTransaction = alter(transaction, transactionInputIndex, scriptData, howToHash);

        // Step 3 : calculate hash of the transaction.
        return calculateHash(alteredTransaction, howToHash);
    }

    /** Alter transaction inputs to calculate hash value used for signing/verifying a signature.
     *
     * See CTransactionSignatureSerializer of the Bitcoin core implementation for the details.
     *
     * @param transaction The transaction to alter.
     * @param transactionInputIndex See hashForSignature
     * @param scriptData See hashForSignature
     * @param howToHash See hashForSignature
     */
    private static Transaction alter(Transaction transaction , int transactionInputIndex , byte[] scriptData , int howToHash ) {
        int currentInputIndex = -1;


        List<TransactionInput> newInputs = Lists.newArrayList();

        for (TransactionInput input : transaction.getInputs()) {
            currentInputIndex += 1;

            if (input instanceof NormalTransactionInput) {
                UnlockingScript newUnlockingScript;
                if (currentInputIndex == transactionInputIndex) {
                    newUnlockingScript = new UnlockingScript(new Bytes(scriptData));
                } else {
                    newUnlockingScript = new UnlockingScript(new Bytes(new byte[]{}));
                }

                ((NormalTransactionInput) input).setUnlockingScript(newUnlockingScript);
            }
            // No need to change the generation transaction
            else {
                assert (input instanceof GenerationTransactionInput);
            }

            newInputs.add(input);
        }

        transaction.setInputs(newInputs);
        return transaction;
    }

    /** Calculate hash value of this transaction for signing/validating a signature.
     *
     * @param transaction The transaction to calculate hash for signature of it.
     * @param howToHash The hash type. A value of Transaction.SigHash.
     * @return The calcuated hash.
     */
    private static Hash256 calculateHash(Transaction transaction, int howToHash)  {

        ByteBuf writeBuffer = Unpooled.buffer();
        CodecInputOutputStream io = new CodecInputOutputStream(writeBuffer,  false);

        // Step 1 : Serialize the transaction
        new TransactionCodec().transcode(io, transaction);
        // Step 2 : Write hash type
        ByteUtil.writeUnsignedIntLE(writeBuffer, (0x000000ff & howToHash));

        // Step 3 : Calculate hash
        return HashFunctions.hash256(ByteBufUtil.getBytes(writeBuffer));
    }
}
