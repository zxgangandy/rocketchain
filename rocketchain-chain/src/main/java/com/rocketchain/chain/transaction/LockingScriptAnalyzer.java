package com.rocketchain.chain.transaction;

import com.google.common.collect.Lists;

import com.rocketchain.script.ScriptOpList;
import com.rocketchain.script.ScriptParser;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.script.ops.*;
import com.rocketchain.crypto.Hash160;
import com.rocketchain.crypto.HashFunctions;
import com.rocketchain.proto.LockingScript;

import java.util.List;

/**
 * Analyze the locking script attached to UTXOs.
 */
public class LockingScriptAnalyzer {
    /**
     * Extract addresses from a parsed script operations.
     * <p>
     * A locking script has only one address if it is P2PK or P2PKH.
     * A locking script can have multiple addresses if it is using either checkmultisig without p2sh or checkmultisig with p2sh.
     *
     * @param scriptOps The parsed script operations.
     * @return The list of extracted addresses.
     */
    public static List<CoinAddress> extractAddresses(ScriptOpList scriptOps) {
        int opCount = scriptOps.getOperations().size();

        if (opCount == 2) {
            ScriptOp opPush = scriptOps.getOperations().get(0);
            ScriptOp opCheckSig = scriptOps.getOperations().get(1);
            if (opPush instanceof OpPush &&
                    opCheckSig instanceof OpCheckSig) {
                ScriptValue encodedPublicKey = ((OpPush) opPush).getInputValue();
                PublicKey publicKey = PublicKey.from(encodedPublicKey.getValue());
                byte[] uncompressedPublicKey = publicKey.encode();
                Hash160 publicKeyHash = HashFunctions.hash160(uncompressedPublicKey);
                return Lists.newArrayList(CoinAddress.from(publicKeyHash.getValue().getArray()));
            } else {
                return Lists.newArrayList();
            }
        } else if (opCount == 5) {
            ScriptOp opDup = scriptOps.getOperations().get(0);
            ScriptOp opHash160 = scriptOps.getOperations().get(1);
            ScriptOp opPush = scriptOps.getOperations().get(2);
            ScriptOp opEqualVerify = scriptOps.getOperations().get(3);
            ScriptOp opCheckSig = scriptOps.getOperations().get(4);

            if (opDup instanceof OpDup &&
                    opHash160 instanceof OpHash160 &&
                    opPush instanceof OpPush && ((OpPush) opPush).getByteCount() == 20 &&
                    opEqualVerify instanceof OpEqualVerify &&
                    opCheckSig instanceof OpCheckSig) {
                ScriptValue publicKeyHash = ((OpPush) opPush).getInputValue();
                return Lists.newArrayList(CoinAddress.from(publicKeyHash.getValue()));
            } else {
                return Lists.newArrayList();
            }
        } else {
            return Lists.newArrayList();
        }
    }

    /**
     * Extract addresses from a locking script.
     *
     * @param lockingScript The locking script where we extract addreses.
     */
    public static List<CoinAddress> extractAddresses(LockingScript lockingScript) {
        ScriptOpList scriptOperations = ScriptParser.parse(lockingScript);
        return extractAddresses(scriptOperations);
    }

    /**
     * Extract output ownership from a locking script.
     *
     * @param lockingScript The locking script where we extract an output ownership.
     * @return The extracted output ownership.
     */
    public OutputOwnership extractOutputOwnership(LockingScript lockingScript) {
        // Step 1 : parse the script operations
        ScriptOpList scriptOperations = ScriptParser.parse(lockingScript);

        // Step 2 : try to extract coin addresses from it.
        List<CoinAddress> addresses = extractAddresses(scriptOperations);

        if (addresses.isEmpty()) {
            // Step 2 : construct a pared public key script as an output ownership.
            //
            return new ParsedPubKeyScript(scriptOperations);
        } else {
            // TODO : BUGBUG : We are using the first coin address only. is this ok?
            return addresses.get(0);
        }
    }


    /**
     * Extract all possible output ownerships from a locking script matching with known patterns of script operations for the locking script.
     *
     * @param lockingScript The locking script to analyze
     * @return The list of all possible output ownerships.
     */
    public static List<CoinAddress> extractPossibleOutputOwnerships(LockingScript lockingScript) {
        // Step 1 : parse the script operations
        ScriptOpList scriptOperations = ScriptParser.parse(lockingScript);

        // Step 2 : try to extract coin addresses from it.
        List<CoinAddress> addresses = extractAddresses(scriptOperations);

        // TODO : Need to return ParsedPubKeyScript.
        // ParsedPubKeyScript is not supported for an output ownership. Check Wallet.importOutputOwnership
        return addresses; //::: listOf(ParsedPubKeyScript(scriptOperations))
    }
}
