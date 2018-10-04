package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.proto.Script;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptParseException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class OpCodeSparator implements Crypto {

    private int sigCheckOffset;

    public OpCodeSparator() {
        this.sigCheckOffset = 0;
    }

    public OpCodeSparator(int sigCheckOffset) {
        this.sigCheckOffset = sigCheckOffset;
    }

    public int getSigCheckOffset() {
        return sigCheckOffset;
    }

    @Override
    public OpCode opCode() {
        return new OpCode((short)0xab);
    }

    @Override
    public void execute(ScriptEnvironment env) {
        assert(sigCheckOffset > 0);
        env.setSigCheckOffset(sigCheckOffset);
    }

    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        // The offset is the next byte of the OpCodeSparator OP code in the raw script.
        int sigCheckOffset = offset;

        if (sigCheckOffset >= script.size()) {
            throw new ScriptParseException(ErrorCode.NoDataAfterCodeSparator);
        }

        return new MutablePair<>(new OpCodeSparator(sigCheckOffset), 0);
    }
}
