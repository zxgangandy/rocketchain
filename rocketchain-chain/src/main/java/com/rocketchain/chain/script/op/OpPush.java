package com.rocketchain.chain.script.op;

import com.rocketchain.chain.script.ScriptEnvironment;
import com.rocketchain.chain.script.ScriptValue;
import com.rocketchain.proto.Script;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class OpPush implements Constant {

    private int byteCount;
    private ScriptValue inputValue;

    /**
     * 1-75(0x01-0x4b) : Push the next N bytes onto the stack, where N is 1 to 75 bytes
     */
    public OpPush(int byteCount, ScriptValue inputValue) {
        this.byteCount = byteCount;
        this.inputValue = inputValue;
    }

    // 0x00 is the base value for opCode. The actual op code is calculated by adding byteCount.
    @Override
    public OpCode opCode() {
        return opCodeFromBase(0, byteCount);
    }

    @Override
    public void execute(ScriptEnvironment env) {
    // inputValue field is set by calling copyInputFrom while the script parser runs.
        // If it is null, it means that there is an internal error.
        if (inputValue == null) throw new AssertionError();
        env.getStack().push(inputValue);
    }


    /** create an OpPush object by copying the input data from the raw script.
     * This is called by script parser before execution.
     *
     * @param script The raw script before it is parsed.
     * @param offset The offset where the input is read.
     * @return The number of bytes consumed to copy the input value.
     */
    @Override
    public Pair<ScriptOp, Integer> create(Script script, int offset) {
        //println(s"Script.length : ${script.data.length}, offset : $offset, byteCount : $byteCount")
        ScriptValue value = ScriptValue.valueOf(script.getData().getArray(), offset, byteCount);

        return new MutablePair<>( new OpPush(byteCount, value), byteCount);
    }

    /** Serialize the script operation into an array buffer.
     *
     * @param buffer The array buffer where the script is serialized.
     */
    @Override
    public void serialize(List<Byte> buffer) {
        buffer.add(opCode().getCode().byteValue());
        if (inputValue == null) throw new AssertionError();
        // BUGBUG : Optimize : Can we avoid calling inputValue.value.toList()?

        Byte[] bytes = ArrayUtils.toObject(inputValue.getValue());
        buffer.addAll(Arrays.asList(bytes));
    }

    private final  static  int  MAX_SIZE = 75;
    public static OpPush from(byte[] value )  {
        assert(value.length <= MAX_SIZE);
        return new OpPush(value.length, ScriptValue.valueOf(value));
    }
}
