package com.rocketchain.chain.mining;

import com.rocketchain.storage.DB;
import com.rocketchain.storage.index.TransactionTimeIndex;

public class TemporaryTransactionTimeIndex implements TransactionTimeIndex {

    @Override
    public byte getTxTimePrefix() {
        return DB.TEMP_TRANSACTION_TIME;
    }
}
