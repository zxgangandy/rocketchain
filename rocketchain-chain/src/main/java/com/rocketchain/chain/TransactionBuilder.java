package com.rocketchain.chain;

import com.google.common.collect.Lists;
import com.rocketchain.chain.script.ScriptSerializer;
import com.rocketchain.chain.script.op.OpPush;
import com.rocketchain.chain.script.op.OpReturn;
import com.rocketchain.chain.script.op.ScriptOp;
import com.rocketchain.chain.transaction.*;
import com.rocketchain.proto.*;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.GeneralException;
import com.rocketchain.utils.lang.Bytes;

import java.math.BigDecimal;
import java.util.List;


/**
 * Build a transaction by using inputs and outputs provided.
 * Note that the builder does not check if this is a double spending transaction.
 * IOW, it does not check if the outputs pointed by inputs are already spent.
 */
public class TransactionBuilder {

    /**
     * The outputs spent by inputs. The order of spendingOutputs matches inputs.
     */
    List<TransactionOutput> spendingOutputs = Lists.newArrayList();

    /**
     * The inputs of the transaction
     */
    List<TransactionInput> inputs = Lists.newArrayList();

    /**
     * The outputs of the transaction
     */
    List<TransactionOutput> newOutputs = Lists.newArrayList();

    /**
     * Add a generation transaction input.
     *
     * @param coinbaseData   The coinbase data to embed into the generation transaction input.
     * @param sequenceNumber The sequence number in the generation transaction input.
     */
    public TransactionBuilder addGenerationInput(CoinbaseData coinbaseData, Long sequenceNumber) {
        // TODO : Need to move to a singleton to avoid writing the same code over and over.
        Hash allZeroHash = Hash.ALL_ZERO;
        // TODO : Need to make sure that the output index is serialized correctly for the generation transaction
        long outputIndex = 0xFFFFFFFFL;

        inputs.add(new GenerationTransactionInput(allZeroHash, outputIndex, coinbaseData, sequenceNumber));
        return this;
    }

    /**
     * Add a normal transaction input.
     *
     * @param coinsView             The read-only view of coins in the blockchain. Need it to verify the sum of input amounts >= sum of output amounts.
     * @param outPoint              The out point which points to the output we want to spent.
     * @param unlockingScriptOption The unlocking script if any.
     *                              If None is passed, we will put an empty unlocking script,
     *                              and TransactionSigner will add the unlocking script with public keys and signatures.
     *                              If Some(script) is passed, we will use the given script.
     * @param sequenceNumberOption  The sequence number.
     *                              If None is passed we will use the default value zero.
     *                              If Some(sequence) is passed, we will use the given value.
     */
    public TransactionBuilder addInput(KeyValueDatabase db, CoinsView coinsView, OutPoint outPoint,
                                       UnlockingScript unlockingScriptOption, Long sequenceNumberOption) {
        // TODO : Check if the sequenceNumberOption.get is the maximum of unsigned integer.

        if (sequenceNumberOption == null) {
            sequenceNumberOption = 0L;
        }

        if (unlockingScriptOption == null) {
            unlockingScriptOption = new UnlockingScript(new Bytes(new byte[]{}));
        }

        TransactionInput input = new NormalTransactionInput(
                new Hash(outPoint.getTransactionHash().getValue()),
                outPoint.getOutputIndex(),
                unlockingScriptOption,
                sequenceNumberOption);

        inputs.add(input);

        spendingOutputs.add(coinsView.getTransactionOutput(db, input.getOutPoint()));
        return this;
    }

    /**
     * Add a transaction output with a public key hash.
     *
     * @param amount        The amount of coins .
     * @param publicKeyHash The public key hash to put into the locking script.
     */
    public TransactionBuilder addOutput(CoinAmount amount, Hash publicKeyHash) {
        ParsedPubKeyScript pubKeyScript = ParsedPubKeyScript.from(publicKeyHash.getValue().getArray());
        TransactionOutput output = new TransactionOutput(amount.coinUnits(), pubKeyScript.lockingScript());
        newOutputs.add(output);
        return this;
    }

    /**
     * Add a transaction output with an output ownership.
     *
     * @param amount          The amount of coins.
     * @param outputOwnership The output ownership that owns the output.
     */
    public TransactionBuilder addOutput(CoinAmount amount, OutputOwnership outputOwnership) {
        TransactionOutput output = new TransactionOutput(amount.coinUnits(), outputOwnership.lockingScript());
        newOutputs.add(output);
        return this;
    }

    /**
     * Add an output whose locking script only contains the given bytes prefixed with OP_RETURN.
     * <p>
     * Used by the block signer to create a transaction that contains the block hash to sign.
     *
     * @param data
     * @return
     */
    public TransactionBuilder addOutput(byte[] data) {
        List<ScriptOp> lockingScriptOps = Lists.newArrayList(new OpReturn(), OpPush.from(data));
        byte[] lockingScriptData = ScriptSerializer.serialize(lockingScriptOps);
        TransactionOutput output = new TransactionOutput(0L, new LockingScript(new Bytes(lockingScriptData)));
        newOutputs.add(output);
        return this;
    }

    public CoinAmount calculateFee(List<TransactionOutput> spendingOutputs, List<TransactionOutput> newOutputs) {
        long fee = spendingOutputs.stream()
                .map(item -> item.getValue())
                .reduce(0L, (element1, element2) -> element1 + element2)
                - newOutputs.stream()
                .map(item -> item.getValue())
                .reduce(0L, (element1, element2) -> element1 + element2);

        return CoinAmount.from(fee);
    }

    /**
     * Check if the current status of the builder is valid.
     */
    public void checkValidity() {
        // Step 1 : Check if we have at least one input.
        if (inputs.size() == 0)
            throw new GeneralException(ErrorCode.NotEnoughTransactionInput);

        // Step 2 : Check if we have at least one output.
        if (newOutputs.size() == 0)
            throw new GeneralException(ErrorCode.NotEnoughTransactionOutput);

        // Step 3 : Check if we have other inputs when we have a generation input.
        if (inputs.get(0).isCoinBaseInput()) {
            if (inputs.size() != 1)
                throw new GeneralException(ErrorCode.GenerationInputWithOtherInputs);
        }

        for (int i = 1; i < inputs.size(); i++) {
            if (inputs.get(i).isCoinBaseInput())
                throw new GeneralException(ErrorCode.GenerationInputWithOtherInputs);
        }

        // Step 4 : Check if sum of input values is greater than or equal to the sum of output values.
        if (!inputs.get(0).isCoinBaseInput()) {
            if (calculateFee(spendingOutputs, newOutputs).getValue().compareTo(BigDecimal.valueOf(0)) < -1) {
                throw new GeneralException(ErrorCode.NotEnoughInputAmounts);
            }
        }
    }

    /**
     * Get the built transaction.
     *
     * @param lockTime The lock time of the transaction.
     * @param version  The version of the transaction.
     * @return The built transaction.
     */
    public Transaction build(Long lockTime, Integer version) {
        checkValidity();

        return new Transaction(
                version,
                inputs,
                newOutputs,
                lockTime
        );
    }


    /**
     * create a transaction builder.
     *
     * @return The transaction builder.
     */
    public static TransactionBuilder newBuilder() {
        return new TransactionBuilder();
    }

    public static Transaction newGenerationTransaction(CoinbaseData coinbaseData, CoinAddress minerAddress) {
        return TransactionBuilder.newBuilder()
                .addGenerationInput(coinbaseData, null)
                .addOutput(new CoinAmount(50), minerAddress)
                .build(null, null);
    }

}
