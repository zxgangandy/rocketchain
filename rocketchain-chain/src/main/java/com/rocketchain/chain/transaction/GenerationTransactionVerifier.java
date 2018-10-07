package com.rocketchain.chain.transaction;

import com.rocketchain.proto.GenerationTransactionInput;
import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.storage.index.KeyValueDatabase;

public class GenerationTransactionVerifier {
    private KeyValueDatabase db ;
    private GenerationTransactionInput transaction ;

    public GenerationTransactionVerifier(KeyValueDatabase db, GenerationTransactionInput transaction) {
        this.db = db;
        this.transaction = transaction;
    }

    public KeyValueDatabase getDb() {
        return db;
    }

    public GenerationTransactionInput getTransaction() {
        return transaction;
    }


    /** Verify that 100 blocks are created after the generation transaction was created.
     * Generation transactions do not reference any UTXO, as it creates UTXO from the scratch.
     * So, we don't have to verify the locking script and unlocking script, but we need to make sure that at least 100 blocks are created
     * after the block where this generation transaction exists.
     *
     * @param env For a generation transaction, this is null, because we don't need to execute any script.
     * @param chainView A blockchain view that can get the transaction output pointed by an out point.
     //* @throws TransactionVerificationException if the verification failed.
     */
    public void verify(ScriptEnvironment env , BlockchainView chainView ) {
        //assert(env == null)
        //assert(chainView == null)
        // Do nothing.
        // TODO : Verify that 100 blocks are created after the generation transaction was created.
    }
}
