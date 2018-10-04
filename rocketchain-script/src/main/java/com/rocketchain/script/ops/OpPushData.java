package com.rocketchain.script.ops;

import com.google.common.collect.ImmutableMap;
import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.proto.Script;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class OpPushData implements Constant {

    private int lengthBytes;
    private ScriptValue inputValue ;

    public OpPushData(int lengthBytes) {
        this(lengthBytes, null);
    }

    public OpPushData(int lengthBytes, ScriptValue inputValue) {
        this.lengthBytes = lengthBytes;
        this.inputValue = inputValue;
    }

    public int getLengthBytes() {
        return lengthBytes;
    }

    public ScriptValue getInputValue() {
        return inputValue;
    }

    @Override
    public OpCode opCode() {
        Map<Integer, Integer> opCodeMap = ImmutableMap.of( 1 ,0x4c).of( 2 , 0x4d).of( 4 , 0x4e);
        Integer opCodeOption = opCodeMap.get(lengthBytes);
        return new OpCode( opCodeOption.shortValue());
    }

    @Override
    public void execute(ScriptEnvironment env) {
// inputValue field is set by calling copyInputFrom while the script parser runs.
        // If it is null, it means that there is an internal error.
        if (inputValue  == null) throw new AssertionError();
        env.getStack().push( inputValue );
    }


    /** create an OpPushData object by copying the input data from the raw script.
     * This is called by script parser before execution.
     *
     * @param script The raw script before it is parsed.
     * @param offset The offset where the byte count is read.
     * @return The number of bytes consumed to copy the input value.
     */
    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        int byteCount = getByteCount(script.getData().getArray(), offset, lengthBytes);
        ScriptValue value = ScriptValue.valueOf(script.getData().getArray(), offset + lengthBytes, byteCount);

        return new MutablePair<>( new OpPushData(lengthBytes, value), lengthBytes + byteCount);
    }

    /** Get how much bytes we need to read from script to get the inputValue to push.
     *
     * @param rawScript The raw script before it is parsed.
     * @param offset The offset where the byte count is read.
     * @param length The length of bytes to read to get the byte count.
     * @return The byte count read form the raw script.
     */
    protected int getByteCount(byte[] rawScript , int offset , int length)  {
        if (offset + length > rawScript.length) {
            throw new ScriptEvalException(ErrorCode.NotEnoughScriptData, "ScriptOp:OpPushData");
        }

        long result = 0L;

        if (length ==1) {
            result = (rawScript[offset] & 0xFF);
        } else if (length == 2) {
            result = (rawScript[offset] & 0xFF) +
                    ((rawScript[offset+1] & 0xFF) << 8);
        } else if (length == 4) {
            result = (rawScript[offset] & 0xFF) +
                    ((rawScript[offset+1] & 0xFF)<< 8) +
                    ((rawScript[offset+2] & 0xFF)<< 16) +
                    ((rawScript[offset+3] & 0xFF)<< 24);
        } else {
            assert(false);
        }
        assert(result < Integer.MAX_VALUE);
        return (int)result;
    }
}
