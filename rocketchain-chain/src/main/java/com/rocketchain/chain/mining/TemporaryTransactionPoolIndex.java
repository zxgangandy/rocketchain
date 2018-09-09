package com.rocketchain.chain.mining;

import com.rocketchain.storage.DB;
import com.rocketchain.storage.index.TransactionPoolIndex;

public class TemporaryTransactionPoolIndex implements TransactionPoolIndex {

    @Override
    public byte getTxPoolPrefix() {
        return DB.TEMP_TRANSACTION_POOL;
    }
}
