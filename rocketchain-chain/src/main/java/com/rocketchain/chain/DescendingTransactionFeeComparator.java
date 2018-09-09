package com.rocketchain.chain;

import java.util.Comparator;

public class DescendingTransactionFeeComparator implements Comparator<TransactionWithFee> {
    @Override
    public int compare(TransactionWithFee x, TransactionWithFee y) {
        return - (x.getFee().getValue().subtract(y.getFee().getValue())).intValue();
    }
}
