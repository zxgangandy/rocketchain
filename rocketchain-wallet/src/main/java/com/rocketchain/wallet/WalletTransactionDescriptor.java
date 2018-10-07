package com.rocketchain.wallet;

import com.rocketchain.proto.Hash;

import java.math.BigDecimal;

public class WalletTransactionDescriptor {
    private boolean involvesWatchonly;      // true,
    // The account which the payment was credited to or debited from.
    // May be an empty string (“”) for the default account
    private String account;                // "someone else's address2",
    // The address paid in this payment, which may be someone else’s address not belonging to this wallet.
    // May be empty if the address is unknown, such as when paying to a non-standard pubkey script or if this is in the move category
    private String address;        // "n3GNqMveyvaPvUbH469vDRadqpJMPc84JA",
    // Set to one of the following values:
    // • send if sending payment
    // • receive if this wallet received payment in a regular transaction
    // • generate if a matured and spendable coinbase
    // • immature if a coinbase that is not spendable yet
    // • orphan if a coinbase from a block that’s not in the local best block chain
    // • move if an off-block-chain move made with the move RPC
    private String category;               // "receive",
    // A negative bitcoin amount if sending payment;
    // a positive bitcoin amount if receiving payment (including coinbases)
    private java.math.BigDecimal amount; // 0.00050000,
    // ( Since : 0.10.0 )
    // For an output, the output index (vout) for this output in this transaction.
    // For an input, the output index for the output being spent in its transaction.
    // Because inputs list the output indexes from previous transactions,
    // more than one entry in the details array may have the same output index.
    // Not returned for move category payments
    // P1
    private Integer vout;           // 0,
    // If sending payment, the fee paid as a negative bitcoins value.
    // May be 0. Not returned if receiving payment or for move category payments
    private java.math.BigDecimal fee;
    // The number of confirmations the transaction has received.
    // Will be 0 for unconfirmed and -1 for conflicted. Not returned for move category payments
    private Long confirmations;        // 34714,
    // Set to true if the transaction is a coinbase. Not returned for regular transactions or move category payments
    private boolean generated;
    // Only returned for confirmed transactions.
    // The hash of the block on the local best block chain which includes this transaction, encoded as hex in RPC byte order
    // P1
    private Hash blockhash;         // "00000000bd0ed80435fc9fe3269da69bb0730ebb454d0a29128a870ea1a37929",
    // Only returned for confirmed transactions.
    // The block height of the block on the local best block chain which includes this transaction
    // P1
    private Long blockindex;        // 11,
    // Only returned for confirmed transactions.
    // The block header time (Unix epoch time) of the block on the local best block chain which includes this transaction
    // P1
    private Long blocktime;          // 1411051649,
    // The TXID of the transaction, encoded as hex in RPC byte order. Not returned for move category payments
    private Hash txid;         // "99845fd840ad2cc4d6f93fafb8b072d188821f55d9298772415175c456f3077d",
    // An array containing the TXIDs of other transactions that spend the same inputs (UTXOs) as this transaction.
    // Array may be empty. Not returned for move category payments
    // walletconflicts item : The TXID of a conflicting transaction, encoded as hex in RPC byte order
    // P2
    //    walletconflicts   : List<Hash>,            // : <>,
    // A Unix epoch time when the transaction was added to the wallet
    private long time;                  // 1418695703,
    // A Unix epoch time when the transaction was detected by the local node,
    // or the time of the block on the local best block chain that included the transaction.
    // Not returned for move category payments
    // P2
    //timereceived      : Option<Long>             // 1418925580
    // For transaction originating with this wallet, a locally-stored comment added to the transaction.
    // Only returned in regular payments if a comment was added.
    // Always returned in move category payments. May be an empty string
    // P3
    //    comment : Option<String>,
    // For transaction originating with this wallet, a locally-stored comment added to the transaction
    // identifying who the transaction was sent to.
    // Only returned if a comment-to was added. Never returned by move category payments. May be an empty string
    // P3
    //  to : Option<String>,

    // Only returned by move category payments.
    // This is the account the bitcoins were moved from or moved to,
    // as indicated by a negative or positive amount field in this payment
    // P3
    //    otheraccount : Option<String>


    public WalletTransactionDescriptor(boolean involvesWatchonly, String account, String address, String category,
                                       BigDecimal amount, Integer vout, BigDecimal fee, Long confirmations, boolean generated,
                                       Hash blockhash, Long blockindex, Long blocktime, Hash txid, long time) {
        this.involvesWatchonly = involvesWatchonly;
        this.account = account;
        this.address = address;
        this.category = category;
        this.amount = amount;
        this.vout = vout;
        this.fee = fee;
        this.confirmations = confirmations;
        this.generated = generated;
        this.blockhash = blockhash;
        this.blockindex = blockindex;
        this.blocktime = blocktime;
        this.txid = txid;
        this.time = time;
    }


    public void setInvolvesWatchonly(boolean involvesWatchonly) {
        this.involvesWatchonly = involvesWatchonly;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setVout(Integer vout) {
        this.vout = vout;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmations = confirmations;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    public void setBlockhash(Hash blockhash) {
        this.blockhash = blockhash;
    }

    public void setBlockindex(Long blockindex) {
        this.blockindex = blockindex;
    }

    public void setBlocktime(Long blocktime) {
        this.blocktime = blocktime;
    }

    public void setTxid(Hash txid) {
        this.txid = txid;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isInvolvesWatchonly() {
        return involvesWatchonly;
    }

    public String getAccount() {
        return account;
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getVout() {
        return vout;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public Long getConfirmations() {
        return confirmations;
    }

    public boolean isGenerated() {
        return generated;
    }

    public Hash getBlockhash() {
        return blockhash;
    }

    public Long getBlockindex() {
        return blockindex;
    }

    public Long getBlocktime() {
        return blocktime;
    }

    public Hash getTxid() {
        return txid;
    }

    public long getTime() {
        return time;
    }
}
