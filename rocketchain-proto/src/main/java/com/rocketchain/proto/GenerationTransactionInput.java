package com.rocketchain.proto;

public class GenerationTransactionInput extends TransactionInput {
    private CoinbaseData coinbaseData ;
    private long sequenceNumber;

    public GenerationTransactionInput(Hash outputTransactionHash ,
                                      // BUGBUG : Change to Int
                                      long outputIndex,
                                      CoinbaseData coinbaseData ,
                                      long sequenceNumber ) {
        super(outputTransactionHash, outputIndex);

        this.coinbaseData = coinbaseData;
        this.sequenceNumber = sequenceNumber;
    }

    public GenerationTransactionInput(Hash outputTransactionHash, long outputIndex) {
        super(outputTransactionHash, outputIndex);
    }

    public CoinbaseData getCoinbaseData() {
        return coinbaseData;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public String toString() {
        return "GenerationTransactionInput{" +
                "coinbaseData=" + coinbaseData +
                ", sequenceNumber=" + sequenceNumber +
                '}';
    }
}
