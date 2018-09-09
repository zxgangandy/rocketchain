package com.rocketchain.chain.mining;

import com.rocketchain.chain.transaction.CoinsView;
import com.rocketchain.proto.OutPoint;
import com.rocketchain.proto.TransactionOutput;
import com.rocketchain.storage.index.KeyValueDatabase;

public class TemporaryCoinsView implements CoinsView {

    private CoinsView coinsView ;


    TemporaryTransactionPoolIndex tempTranasctionPoolIndex = new  TemporaryTransactionPoolIndex();
    TemporaryTransactionTimeIndex tempTranasctionTimeIndex = new  TemporaryTransactionTimeIndex();


    public TemporaryCoinsView(CoinsView coinsView) {
        this.coinsView = coinsView;
    }

    /** Return a transaction output specified by a give out point.
     *
     * @param outPoint The outpoint that points to the transaction output.
     * @return The transaction output we found.
     */
    @Override
    public TransactionOutput getTransactionOutput(KeyValueDatabase db, OutPoint outPoint) {
        // Find from the temporary transaction pool index first, and then find from the transactions in a block.
        TransactionOutput output = tempTranasctionPoolIndex.getTransactionFromPool(db, outPoint.getTransactionHash())
                .getTransaction().getOutputs().get(outPoint.getOutputIndex());
        // This is called by TransactionPriorityQueue, which already checked if the transaction is attachable.
        return output == null ? coinsView.getTransactionOutput(db, outPoint) : output;

    }

    public CoinsView getCoinsView() {
        return coinsView;
    }

    public TemporaryTransactionPoolIndex getTempTranasctionPoolIndex() {
        return tempTranasctionPoolIndex;
    }

    public TemporaryTransactionTimeIndex getTempTranasctionTimeIndex() {
        return tempTranasctionTimeIndex;
    }
}
