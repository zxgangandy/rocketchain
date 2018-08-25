package com.rocketchain.proto;

import java.util.List;

public class Block implements ProtocolMessage {
    private BlockHeader header;
    private List<Transaction> transactions;
    public Block(BlockHeader header, List<Transaction> transactions) {
        this.header = header;
        this.transactions = transactions;
    }

    public BlockHeader getHeader() {
        return header;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }


    @Override
    public String toString() {
        return "Block{" +
                "header=" + header +
                ", transactions=" + transactions +
                '}';
    }
}
