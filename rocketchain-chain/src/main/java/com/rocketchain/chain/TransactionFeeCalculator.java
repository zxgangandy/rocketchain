package com.rocketchain.chain;


import com.rocketchain.chain.transaction.CoinAmount;
import com.rocketchain.chain.transaction.CoinsView;
import com.rocketchain.proto.OutPoint;
import com.rocketchain.proto.Transaction;
import com.rocketchain.storage.index.KeyValueDatabase;

import java.math.BigDecimal;

/**
 * Calculate the transaction fee.
 */
public class TransactionFeeCalculator {
    /**
     * Calculate fee. Sum(input values) = Sum(output values)
     *
     * @param coinsView The coins view to get the UTXO.
     * @param tx        The transaction to calculate the fee.
     * @return The amount of the fee.
     */
    public static CoinAmount fee(KeyValueDatabase db, CoinsView coinsView, Transaction tx) {

        long totalInputAmount = tx.getInputs().stream()
                .map(input -> coinsView.getTransactionOutput(db, new OutPoint(input.getOutputTransactionHash(),
                        (int) input.getOutputIndex())).getValue())
                .reduce(0L, (element1, element2) -> element1 + element2);


        long totalOutputAmount = tx.getOutputs().stream()
                .map(ouput -> ouput.getValue()).reduce(0L, (element1, element2) -> element1 + element2);

        long feeAmount = totalInputAmount - totalOutputAmount;
        return new CoinAmount(BigDecimal.valueOf(feeAmount));
    }
}
