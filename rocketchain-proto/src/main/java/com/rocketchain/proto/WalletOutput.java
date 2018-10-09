package com.rocketchain.proto;

public class WalletOutput implements Transcodable{
    // Some(block height) of the block on the local best block chain which includes this transaction. None otherwise.
    // BUGBUG : Change name to blockIndex
    private Long blockindex ;          // 11,
    // Whether this output is in the generation transaction.
    private boolean coinbase ;
    // Whether this coin was spent or not.
    private boolean spent;
    // The transaction output
    private TransactionOutput transactionOutput ;

    public WalletOutput(Long blockindex, boolean coinbase, boolean spent, TransactionOutput transactionOutput) {
        this.blockindex = blockindex;
        this.coinbase = coinbase;
        this.spent = spent;
        this.transactionOutput = transactionOutput;
    }

    public void setBlockindex(Long blockindex) {
        this.blockindex = blockindex;
    }

    public void setCoinbase(boolean coinbase) {
        this.coinbase = coinbase;
    }

    public void setTransactionOutput(TransactionOutput transactionOutput) {
        this.transactionOutput = transactionOutput;
    }

    public Long getBlockindex() {
        return blockindex;
    }

    public boolean isCoinbase() {
        return coinbase;
    }

    public boolean isSpent() {
        return spent;
    }

    public TransactionOutput getTransactionOutput() {
        return transactionOutput;
    }

    public void setSpent(boolean spent) {
        this.spent = spent;
    }
}
