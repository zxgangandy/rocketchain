package com.rocketchain.chain;

import com.google.common.util.concurrent.Striped;
import com.rocketchain.chain.transaction.ChainBlock;
import com.rocketchain.codec.HashUtil;
import com.rocketchain.proto.*;
import com.rocketchain.storage.index.KeyValueDatabase;
import com.rocketchain.storage.index.TransactionDescriptorIndex;
import com.rocketchain.storage.index.TransactionPoolIndex;
import com.rocketchain.storage.index.TransactionTimeIndex;
import com.rocketchain.utils.exception.ChainException;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.lang.HexUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TransactionMagnet {

    private final Logger logger = LoggerFactory.getLogger(TransactionMagnet.class);

    private static final int TxLockCount = 1024;
    private Striped<Lock> mTxLock = Striped.lock(TxLockCount);

    private TransactionDescriptorIndex txDescIndex;
    private TransactionPoolIndex txPoolIndex;
    private TransactionTimeIndex txTimeIndex;

    public TransactionMagnet(TransactionDescriptorIndex txDescIndex, TransactionPoolIndex txPoolIndex, TransactionTimeIndex txTimeIndex) {
        this.txDescIndex = txDescIndex;
        this.txPoolIndex = txPoolIndex;
        this.txTimeIndex = txTimeIndex;
    }


    public TransactionDescriptorIndex getTxDescIndex() {
        return txDescIndex;
    }

    public TransactionPoolIndex getTxPoolIndex() {
        return txPoolIndex;
    }

    public TransactionTimeIndex getTxTimeIndex() {
        return txTimeIndex;
    }


    protected ChainEventListener chainEventListener;

    /**
     * Set an event listener of the blockchain.
     *
     * @param listener The listener that wants to be notified for blocks, invalidated blocks, and transactions comes into and goes out from the transaction pool.
     */
    public void setEventListener(ChainEventListener listener) {
        chainEventListener = listener;
    }


    /**
     * The UTXO pointed by the transaction input is marked as spent by the in-point.
     *
     * @param inPoint          The in-point that points to the input to attach.
     * @param transactionInput The transaction input to attach.
     * @param checkOnly        If true, do not attach the transaction input, but just check if the transaction input can be attached.
     */
    public void attachTransactionInput(KeyValueDatabase db, InPoint inPoint, TransactionInput transactionInput, boolean checkOnly) {
        // Make sure that the transaction input is not a coinbase input. attachBlock already checked if the input was NOT coinbase.
        assert (!transactionInput.isCoinBaseInput());

        // TODO : Step 1. read CTxIndex from disk if not read yet.
        // TODO : Step 2. read the transaction that the outpoint points from disk if not read yet.
        // TODO : Step 3. Increase DoS score if an invalid output index was found in a transaction input.
        // TODO : Step 4. check coinbase maturity for outpoints spent by a transaction.
        // TODO : Step 5. Skip ECDSA signature verification when connecting blocks (fBlock=true) during initial download
        // TODO : Step 6. check value range of each input and sum of inputs.
        // TODO : Step 7. for the transaction output pointed by the input, mark this transaction as the spending transaction of the output. check double spends.
        markOutputSpent(db, transactionInput.getOutPoint(), inPoint, checkOnly);
    }


    /**
     * Detach the transaction from the best blockchain.
     *
     * For outputs, all outputs spent by the transaction is marked as unspent.
     *
     * @param transaction The transaction to detach.
     */
    public void detachTransaction(KeyValueDatabase db , Transaction transaction )  {
        Hash transactionHash = HashUtil.hashTransaction(transaction);

        // Step 1 : Detach each transaction input
        if (transaction.getInputs().get(0).isCoinBaseInput()) {
            // Nothing to do for the coinbase inputs.
        } else {
            detachTransactionInputs(db, transactionHash, transaction);
        }

        // Remove the transaction descriptor otherwise other transactions can spend the UTXO from the detached transaction.
        // The transaction might not be stored in a block on the best blockchain yet. Remove the transaction from the pool too.
        txDescIndex.delTransactionDescriptor(db, transactionHash);

        TransactionPoolEntry txOption  = txPoolIndex.getTransactionFromPool(db, transactionHash);
        if (txOption != null) {
            // BUGBUG : Need to remove these two records atomically
            txTimeIndex.delTransactionTime( db, txOption.getCreatedAtNanos(), transactionHash);
            txPoolIndex.delTransactionFromPool(db, transactionHash);
        }

        //chainEventListener.onRemoveTransaction(db, transactionHash, transaction);
    }


    /**
     * Mark an output spent by the given in-point.
     *
     * @param outPoint  The out-point that points to the output to mark.
     * @param inPoint   The in-point that points to a transaction input that spends to output.
     * @param checkOnly If true, do not update the spending in-point, just check if the output is a valid UTXO.
     */
    protected void markOutputSpent(KeyValueDatabase db, OutPoint outPoint, InPoint inPoint, boolean checkOnly) {
        List<InPoint> outputsSpentBy = getOutputsSpentBy(db, outPoint.getTransactionHash());
        if (outputsSpentBy == null) {
            String message = "An output pointed by an out-point spent by the in-point points to a transaction that does not exist yet.";
            if (!checkOnly)
                logger.warn(message);
            throw new ChainException(ErrorCode.ParentTransactionNotFound, message);
        }

        // TODO : BUGBUG : indexing into a list is slow. Optimize the code.
        if (outPoint.getOutputIndex() < 0 || outputsSpentBy.size() <= outPoint.getOutputIndex()) {
            // TODO : Add DoS score. The outpoint in a transaction input was invalid.
            String message = "An output pointed by an out-point(" + outPoint + ") spent by the in-point(${inPoint}) has invalid transaction output index.";
            if (!checkOnly)
                logger.warn(message);
            throw new ChainException(ErrorCode.InvalidTransactionOutPoint, message);
        }

        InPoint spendingInPointOption = outputsSpentBy.get(outPoint.getOutputIndex());
        if (spendingInPointOption != null) { // The transaction output was already spent.
            if (spendingInPointOption == inPoint) {
                // Already marked as spent by the given in-point.
                // This can happen when a transaction is already attached while it was put into the transaction pool,
                // But tried to attach again while accepting a block that has the (already attached) transaction.
            } else {
                String message = "An output pointed by an out-point(" + outPoint + ") has already been spent by ${spendingInPointOption}. The in-point(${inPoint}) tried to spend it again.";
                if (!checkOnly)
                    logger.warn(message);
                throw new ChainException(ErrorCode.TransactionOutputAlreadySpent, message);
            }
        } else {
            if (checkOnly) {
                // Do not update, just check if the output can be marked as spent.
            } else {
                List<InPoint> nowOutputsSpentBy = IntStream
                        .range(0, outputsSpentBy.size())
                        .filter(i -> i == outPoint.getOutputIndex())
                        .mapToObj(i -> {
                            if (i == outPoint.getOutputIndex()) {
                                return inPoint;
                            } else {
                                return outputsSpentBy.get(i);
                            }
                        })
                        .collect(Collectors.toList());
                putOutputsSpentBy(
                        db,
                        outPoint.getTransactionHash(),
                        nowOutputsSpentBy);
            }
        }
    }


    /**
     * Detach each of transction inputs. Mark outputs spent by the transaction inputs unspent.
     *
     * @param transactionHash The hash of the tranasction that has the inputs.
     * @param transaction The transaction that has the inputs.
     */
    private void detachTransactionInputs(KeyValueDatabase db , Hash transactionHash , Transaction transaction )  {
        int inputIndex = -1;

        List<TransactionInput> inputs = transaction.getInputs();
        for (TransactionInput transactionInput :  inputs) {
            inputIndex += 1;

            // Make sure that the transaction input is not a coinbase input. detachBlock already checked if the input was NOT coinbase.
            assert(!transactionInput.isCoinBaseInput());

            detachTransactionInput(db, new InPoint(transactionHash, inputIndex), transactionInput);
        }
    }

    /**
     * Detach the transaction input from the best blockchain.
     * The output spent by the transaction input is marked as unspent.
     *
     * @param inPoint The in-point that points to the input to attach.
     * @param transactionInput The transaction input to attach.
     */
    private void detachTransactionInput( KeyValueDatabase db , InPoint inPoint , TransactionInput transactionInput )  {
        // Make sure that the transaction input is not a coinbase input. detachBlock already checked if the input was NOT coinbase.
        assert(!transactionInput.isCoinBaseInput());

        markOutputUnspent(db, transactionInput.getOutPoint(), inPoint);
    }


    /**
     * Mark an output unspent. The output should have been marked as spent by the given in-point.
     *
     * @param outPoint The out-point that points to the output to mark.
     * @param inPoint The in-point that points to a transaction input that should have spent the output.
     */
    private void markOutputUnspent(KeyValueDatabase db , OutPoint outPoint , InPoint inPoint )  {
        List<InPoint> outputsSpentBy = getOutputsSpentBy(db, outPoint.getTransactionHash());
        if (outputsSpentBy == null) {
            String message = "An output pointed by an out-point(${outPoint}) spent by the in-point(${inPoint}) points " +
                    "to a transaction that does not exist.";
            logger.warn(message);
            throw new ChainException(ErrorCode.ParentTransactionNotFound, message);
        }

        // TODO : BUGBUG : indexing into a list is slow. Optimize the code.
        if ( outPoint.getOutputIndex() < 0 || outputsSpentBy.size() <= outPoint.getOutputIndex() ) {
            // TODO : Add DoS score. The outpoint in a transaction input was invalid.
            String message = "An output pointed by an out-point(${outPoint}) has invalid transaction output index. " +
                    "The output should have been spent by ${inPoint}";
            logger.warn(message);
            throw new ChainException(ErrorCode.InvalidTransactionOutPoint, message);
        }

        InPoint spendingInPointOption = outputsSpentBy.get(outPoint.getOutputIndex());
        // The output pointed by the out-point should have been spent by the transaction input poined by the given in-point.

        if( spendingInPointOption != inPoint ) { // The transaction output was NOT spent by the transaction input poined by the given in-point.
            String message = "An output pointed by an out-point(${outPoint}) was not spent by the expected transaction " +
                    "input pointed by the in-point(${inPoint}), but spent by ${spendingInPointOption}.";
            logger.warn(message);
            throw new ChainException(ErrorCode.TransactionOutputSpentByUnexpectedInput, message);
        }

        List<InPoint> nowOutputsSpentBy = IntStream
                .range(0, outputsSpentBy.size())
                .filter(i -> i == outPoint.getOutputIndex())
                .mapToObj(i -> {
                    if (i == outPoint.getOutputIndex()) {
                        return null;
                    } else {
                        return outputsSpentBy.get(i);
                    }
                })
                .collect(Collectors.toList());
        putOutputsSpentBy(db, outPoint.getTransactionHash(), nowOutputsSpentBy);
    }


    /**
     * Attach the transaction inputs to the outputs spent by them.
     * Mark outputs spent by the transaction inputs.
     *
     * @param transactionHash The hash of the tranasction that has the inputs.
     * @param transaction     The transaction that has the inputs.
     * @param checkOnly       If true, do not attach the transaction inputs, but just check if the transaction inputs can be attached.
     */
    protected void attachTransactionInputs(KeyValueDatabase db, Hash transactionHash, Transaction transaction, boolean checkOnly) {
        int inputIndex = -1;
        List<TransactionInput> inputs = transaction.getInputs();

        for (TransactionInput transactionInput : inputs) {
            // Make sure that the transaction input is not a coinbase input. attachBlock already checked if the input was NOT coinbase.
            assert (!transactionInput.isCoinBaseInput());
            inputIndex += 1;

            attachTransactionInput(db, new InPoint(transactionHash, inputIndex), transactionInput, checkOnly);
        }
    }

    /**
     * Attach the transaction into the best blockchain.
     * <p>
     * For UTXOs, all outputs spent by the transaction is marked as spent by this transaction.
     *
     * @param transactionHash The hash of the transaction to attach.
     * @param transaction     The transaction to attach.
     * @param checkOnly       If true, do not attach the transaction inputs, but just check if the transaction inputs can be attached.
     * @param txLocatorOption Some(locator) if the transaction is stored in a block on the best blockchain; None if the transaction should be stored in a mempool.
     */
    void attachTransaction(KeyValueDatabase db, Hash transactionHash, Transaction transaction, boolean checkOnly,
                           FileRecordLocator txLocatorOption, ChainBlock chainBlock, Integer transactionIndex) {
        //logger.trace(s"Attach Transaction : ${transactionHash}, stack : ${StackUtil.getCurrentStack}")
        // Step 1 : Attach each transaction input
        if (transaction.getInputs().get(0).isCoinBaseInput()) {
            // Nothing to do for the coinbase inputs.
        } else {
            attachTransactionInputs(db, transactionHash, transaction, checkOnly);
        }

        if (checkOnly) {
            // Do nothing. We just want to check if we can attach the transaction.
        } else {
            // Need to set the transaction locator of the transaction descriptor according to the location of the attached block.
            if (txLocatorOption != null) {
                //logger.trace(s"<Attach Transaction> Put transaction descriptor : ${transactionHash}")
                // If the txLocator is defined, the block height should also be defined.

                txDescIndex.putTransactionDescriptor(
                        db,
                        transactionHash,
                        new TransactionDescriptor(
                                txLocatorOption,
                                chainBlock.getHeight(),
                                Collections.nCopies(transaction.getOutputs().size(), null)));
            } else {
                // Use fine grained lock for the concurrency control of adding a transaction.
                String txLockName = HexUtil.byteArrayToHexString(transactionHash.getValue().getArray());

                Lock txLock = mTxLock.get(txLockName);

                txLock.lock();
                try {
                    if (txPoolIndex.getTransactionFromPool(db, transactionHash) == null) {
                        //logger.trace(s"<Attach Transaction> Put into the pool : ${transactionHash}")
                        long txCreatedAt = System.nanoTime();
                        // Need to put transaction first, and then put transaction time.
                        // Why? We will search by transaction time, and get the transaction object from tx hash we get from the transaction time index.
                        // If we put transaction time first, we may not have transaction even though a transaction time exists.
                        txPoolIndex.putTransactionToPool(
                                db,
                                transactionHash,
                                new TransactionPoolEntry(
                                        transaction,
                                        Collections.nCopies(transaction.getOutputs().size(), null),
                                        txCreatedAt));
                        assert (txPoolIndex.getTransactionFromPool(db, transactionHash) != null);

                        txTimeIndex.putTransactionTime(db, txCreatedAt, transactionHash);
                    }
                } finally {
                    txLock.unlock();
                }
            }

            //chainEventListener.onNewTransaction(db, transactionHash, transaction, chainBlock, transactionIndex);
        }

        // TODO : Step 2 : check if the sum of input values is greater than or equal to the sum of outputs.
        // TODO : Step 3 : make sure if the fee is not negative.
        // TODO : Step 4 : check the minimum transaction fee for each transaction.
    }


    /**
     * Put the list of in-points that are spending the outputs of a transaction
     *
     * @param txHash         The hash of the transaction.
     * @param outputsSpentBy The list of in-points that are spending the outputs of the transaction
     */
    protected void putOutputsSpentBy(KeyValueDatabase db, Hash txHash, List<InPoint> outputsSpentBy) {
        TransactionDescriptor txDescOption = txDescIndex.getTransactionDescriptor(db, txHash);
        TransactionPoolEntry txPoolEntryOption = txPoolIndex.getTransactionFromPool(db, txHash);
        if (txDescOption != null) {

            txDescOption.setOutputsSpentBy(outputsSpentBy);
            txDescIndex.putTransactionDescriptor(db, txHash, txDescOption);
            // Note that txPoolEntryOption can be defined,
            // because the same transaction can be attached at the same time while (1) attaching a block by putBlock (2) attaching a transaction by putTransaction
        } else {
            if (txPoolEntryOption == null) throw new AssertionError();

            txPoolEntryOption.setOutputsSpentBy(outputsSpentBy);
            txPoolIndex.putTransactionToPool(db, txHash, txPoolEntryOption);
        }
    }


    /**
     * Get the list of in-points that are spending the outputs of a transaction
     *
     * @param txHash The hash of the transaction.
     * @return The list of in-points that are spending the outputs of the transaction
     */
    protected List<InPoint> getOutputsSpentBy(KeyValueDatabase db, Hash txHash) {
        //println("desc (${txHash})${txDescIndex.getTransactionDescriptor(db, txHash)}")
        //println("pool (${txHash})${txPoolIndex.getTransactionFromPool(db, txHash)}")

        List<InPoint> list = txDescIndex.getTransactionDescriptor(db, txHash).getOutputsSpentBy();
        return CollectionUtils.isEmpty(list) ? txPoolIndex.getTransactionFromPool(db, txHash).getOutputsSpentBy() : list;
    }


}
