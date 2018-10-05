package com.rocketchain.proto;


/**
 * A transaction stored for an output ownership.
 */
public class WalletTransaction implements Transcodable {

    // Only returned for confirmed transactions.
    // The hash of the block on the local best block chain which includes this transaction, encoded as hex in RPC byte order
    // P1
    private Hash blockHash; // "00000000bd0ed80435fc9fe3269da69bb0730ebb454d0a29128a870ea1a37929",
    // Only returned for confirmed transactions.
    // The block height of the block on the local best block chain which includes this transaction
    // P1
    private long blockIndex; // 11,
    // Only returned for confirmed transactions.
    // The block header time (Unix epoch time) of the block on the local best block chain which includes this transaction
    // P1
    private long blockTime; // 1411051649,
    // The TXID of the transaction, encoded as hex in RPC byte order. Not returned for move category payments
    private Hash transactionId; // "99845fd840ad2cc4d6f93fafb8b072d188821f55d9298772415175c456f3077d",
    // An array containing the TXIDs of other transactions that spend the same inputs (UTXOs) as this transaction.
    // Array may be empty. Not returned for move category payments
    // walletconflicts item : The TXID of a conflicting transaction, encoded as hex in RPC byte order
    // P2
    //    walletconflicts   : List<Hash>,            // : [],
    // A Unix epoch time when the transaction was added to the wallet
    private long addedTime; // 1418695703,
    // A Unix epoch time when the transaction was detected by the local node,
    // or the time of the block on the local best block chain that included the transaction.
    // Not returned for move category payments
    // P2
    //    timereceived      : Long?,          // 1418925580

    // An additional field for sorting transactions by recency.
    // Some(transactionIndex) if the transaction is in a block on the best blockchain.
    // None if the block is in the mempool.
    private int transactionIndex;

    // The transaction related with this wallet transaction.
    private Transaction transaction;

    public WalletTransaction(Hash blockHash, long blockIndex, long blockTime, Hash transactionId, long addedTime,
                             int transactionIndex, Transaction transaction) {
        this.blockHash = blockHash;
        this.blockIndex = blockIndex;
        this.blockTime = blockTime;
        this.transactionId = transactionId;
        this.addedTime = addedTime;
        this.transactionIndex = transactionIndex;
        this.transaction = transaction;
    }

    public Hash getBlockHash() {
        return blockHash;
    }

    public long getBlockIndex() {
        return blockIndex;
    }

    public long getBlockTime() {
        return blockTime;
    }

    public Hash getTransactionId() {
        return transactionId;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
