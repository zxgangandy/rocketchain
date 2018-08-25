package com.rocketchain.storage.index;

import com.rocketchain.codec.HashCodec;
import com.rocketchain.codec.TransactionDescriptorCodec;
import com.rocketchain.proto.Hash;
import com.rocketchain.proto.TransactionDescriptor;
import com.rocketchain.storage.DB;

public interface TransactionDescriptorIndex {

    /**
     * Get the descriptor of a transaction by hash
     *
     * TODO : Add a test case
     *
     * @param txHash The transaction hash.
     * @return Some(descriptor) if the transaction exists; None otherwise.
     */
    default TransactionDescriptor getTransactionDescriptor(KeyValueDatabase db , Hash txHash ) {
        //logger.trace(s"getTransactionDescriptor : ${txHash}")
        return db.getObject(new HashCodec(), new TransactionDescriptorCodec(), DB.TRANSACTION, txHash);
    }

    /**
     * Put the descriptor of a transaction with hash of it
     *
     * TODO : Add a test case
     *
     * @param txHash The transaction hash.
     * @param transactionDescriptor The descriptor of the transaction.
     */
    default void putTransactionDescriptor(KeyValueDatabase db , Hash txHash , TransactionDescriptor transactionDescriptor )  {
        //logger.trace(s"putTransactionDescriptor : ${txHash}")
        db.putObject(new HashCodec(), new TransactionDescriptorCodec(), DB.TRANSACTION, txHash, transactionDescriptor);
    }

    /**
     * Del the descriptor of a transaction by hash.
     *
     * TODO : Add a test case
     *
     * @param txHash The transaction hash
     */
    default void delTransactionDescriptor(KeyValueDatabase db , Hash txHash )  {
        //logger.trace(s"delTransactionDescriptor : ${txHash}")
        db.delObject(new HashCodec(), DB.TRANSACTION, txHash);
    }

}
