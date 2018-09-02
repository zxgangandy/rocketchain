package com.rocketchain.proto;

import java.math.BigInteger;

/** Pong ; The pong message is sent in response to a ping message.
 * In modern protocol versions, a pong response is generated using a nonce included in the ping.

 * Field Size,  Description,  Data type,  Comments
 * ================================================
 *          8,        nonce,   uint64_t,  random nonce

 */
public class Pong {
    private BigInteger nonce;

    public Pong(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getNonce() {
        return nonce;
    }
}
