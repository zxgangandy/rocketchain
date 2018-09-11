package com.rocketchain.chain.transaction;

import java.math.BigDecimal;

public class CoinAmount {


    /**
     * How many units does a coin have?
     */
    private static final BigDecimal ONE_COIN_IN_UNITS = java.math.BigDecimal.valueOf(100000000L);

    private BigDecimal value;

    /**
     * The amount of coin.
     *
     * @param value The amount of coin value.
     */
    public CoinAmount(BigDecimal value) {
        this.value = value;
    }

    public CoinAmount(long value) {
        this(BigDecimal.valueOf(value));
    }

    /**
     * Return the coin amount in coin units.
     *
     * @return The coin units calculated from the CoinAmount. In Bitcoin, the units of coin is satoshi.
     */
    public long coinUnits() {
        // BUGBUG : Change from toLongExact to toLong. is it ok?
        return (value.multiply(CoinAmount.ONE_COIN_IN_UNITS)).longValue();
    }


    /**
     * Get CoinAmount from the units of coin. In Bitcoin, the units of coin is satoshi.
     *
     * @param coinUnits
     * @return
     */
    public static CoinAmount from(long coinUnits) {
        return new CoinAmount(java.math.BigDecimal.valueOf(coinUnits).divide(ONE_COIN_IN_UNITS));
    }

    public BigDecimal getValue() {
        return value;
    }
}
