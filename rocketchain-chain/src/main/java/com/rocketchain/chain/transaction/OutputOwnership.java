package com.rocketchain.chain.transaction;

import com.rocketchain.proto.LockingScript;

/** An entity that describes the ownership of a coin.
 * For example, a coin address can be a description of ownership of a coin.
 * Used by wallet's importAddress.
 */
public interface OutputOwnership {
    /** Check if the ownership is valid.
     * Ex> The format of a coin address is valid.
     * Ex> The script operations of the public key script is one of allowed patterns.
     *
     * @return true if the ownership is valid. false otherwise.
     */
    boolean isValid();

    /** The locking script that this output ownership can unlock.
     *
     * @return The locking script.
     */
    LockingScript lockingScript();

    /** A string key used for prefixed objects in the wallet database.
     *
     * @return The string to be used as a string key in the wallet database.
     */
    String stringKey() ;
}
