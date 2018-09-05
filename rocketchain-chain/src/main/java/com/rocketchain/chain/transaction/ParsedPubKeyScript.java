package com.rocketchain.chain.transaction;

import com.rocketchain.chain.script.ScriptOpList;
import com.rocketchain.proto.LockingScript;
import com.rocketchain.utils.lang.Bytes;
import com.rocketchain.utils.lang.HexUtil;

public class ParsedPubKeyScript implements OutputOwnership {
    private ScriptOpList scriptOps ;

    @Override
    public boolean isValid() {
        // TODO : Check if the scriptOps is one of the pubKeyScript patterns for standard transactions.
        return true;
    }

    @Override
    public LockingScript lockingScript() {
        val serializedScript = ScriptSerializer.serialize(scriptOps.operations);
        return new LockingScript(new Bytes(serializedScript));
    }

    @Override
    public String stringKey() {
        return HexUtil.byteArrayToHexString();
    }
}
