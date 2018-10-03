package com.rocketchain.proto;

import java.util.List;

public class Transaction implements ProtocolMessage{
    private int version;
    private List<TransactionInput> inputs;
    private List<TransactionOutput> outputs;
    private long lockTime;
    public Transaction(int version, List<TransactionInput> inputs, List<TransactionOutput> outputs, long lockTime) {
        this.version = version;
        this.inputs = inputs;
        this.outputs = outputs;
        this.lockTime = lockTime;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setInputs(List<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(List<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

    public void setLockTime(long lockTime) {
        this.lockTime = lockTime;
    }

    public int getVersion() {
        return version;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public long getLockTime() {
        return lockTime;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "version=" + version +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", lockTime=" + lockTime +
                '}';
    }
}
