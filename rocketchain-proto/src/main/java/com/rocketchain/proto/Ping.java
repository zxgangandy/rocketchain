package com.rocketchain.proto;


import java.math.BigInteger;

/** Ping ; The ping message is sent primarily to confirm that the TCP/IP connection is still valid.
 * An error in transmission is presumed to be a closed connection and the address is removed as a current peer.
 *
 * Field Size,  Description,  Data type,  Comments
 * ================================================
 *          8,        nonce,   uint64_t,  random nonce
 */
public class Ping implements ProtocolMessage {
    private BigInteger nonce;

    public Ping(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    @Override
    public String toString() {
        return "Ping(" +
                nonce.longValue() +
                ')';
    }
}
