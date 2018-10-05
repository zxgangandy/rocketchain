package com.rocketchain.script.ops;

import com.rocketchain.script.ScriptEnvironment;
import com.rocketchain.script.ScriptValue;
import com.rocketchain.utils.TernaryFunction;

import java.math.BigInteger;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public interface Arithmetic extends ScriptOp {
    default void unaryIntOperation(ScriptEnvironment env, UnaryOperator<Long> mutate) {
        unaryOperation(env, (value1) -> {
            BigInteger intValue1 = ScriptValue.decodeStackInt(value1.getValue());

            Long intResult = mutate.apply(intValue1.longValue());
            return ScriptValue.valueOf(intResult);
        });
    }

    default void binaryIntOperation(ScriptEnvironment env, BinaryOperator<Long> mutate) {
        binaryOperation(env, (value1, value2) -> {
            BigInteger intValue1 = ScriptValue.decodeStackInt(value1.getValue());
            BigInteger intValue2 = ScriptValue.decodeStackInt(value2.getValue());

            Long intResult = mutate.apply(intValue1.longValue(), intValue2.longValue());
            return ScriptValue.valueOf(intResult);
        });
    }


    default void ternaryIntOperation(ScriptEnvironment env, TernaryFunction<Long> mutate) {
        ternaryOperation(env, (value1, value2, value3) -> {
            BigInteger intValue1 = ScriptValue.decodeStackInt(value1.getValue());
            BigInteger intValue2 = ScriptValue.decodeStackInt(value2.getValue());
            BigInteger intValue3 = ScriptValue.decodeStackInt(value3.getValue());

            Long intResult = mutate.apply(intValue1.longValue(), intValue2.longValue(), intValue3.longValue());
            return ScriptValue.valueOf(intResult);
        });
    }
}
