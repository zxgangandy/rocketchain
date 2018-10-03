package com.rocketchain.chain.script;

import com.rocketchain.proto.Transaction;

public class ScriptEnvironment {
    private Transaction transaction ;
    private Integer transactionInputIndex ;

    public ScriptEnvironment() {
        this(null, null);
    }

    public ScriptEnvironment(Transaction transaction, Integer transactionInputIndex) {
        this.transaction = transaction;
        this.transactionInputIndex = transactionInputIndex;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Integer getTransactionInputIndex() {
        return transactionInputIndex;
    }

    // BUGBUG : if OP_CHECKSIG or OP_CHECKMULTISIG runs without OP_CODESEPARATOR,
    //          can we keep signatureOffset as zero?
    // The offset in the raw script where the data for checking signature starts.
    private int sigCheckOffset;

    /** Set the offset of raw script where the data for checking signature starts.
     *
     * @param offset the offset of raw script
     */
    public void setSigCheckOffset(int offset ) {
        sigCheckOffset = offset;
    }

    /** Get the offset of raw script where the data for checking signature starts.
     *
     * @return The offset.
     */
    public int getSigCheckOffset() {
        return sigCheckOffset;
    }

    private ScriptStack stack = new  ScriptStack();
    // The altStack is necessary to support OP_TOALTSTACK and OP_FROMALTSTACK,
    // which moves items on top of the stack and the alternative stack.
    private ScriptStack altStack = new  ScriptStack();

    public ScriptStack getStack() {
        return stack;
    }

    public ScriptStack getAltStack() {
        return altStack;
    }
}
