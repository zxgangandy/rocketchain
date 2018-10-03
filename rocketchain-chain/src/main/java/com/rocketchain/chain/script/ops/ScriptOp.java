package com.rocketchain.chain.script.ops;

import com.rocketchain.chain.script.ScriptEnvironment;
import com.rocketchain.chain.script.ScriptValue;
import com.rocketchain.chain.script.op.OpCode;
import com.rocketchain.proto.Script;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptEvalException;
import com.rocketchain.utils.lang.Utils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.util.List;

public interface ScriptOp {
    OpCode opCode() ;

    /** Execute the script operation using the given script execution environment.
     *
     * @param env The script execution environment.
     */
    void execute(ScriptEnvironment env ) ;


    /**
     * Copy a region of the raw script byte array, and create a ScriptOp by copying the data in the region.
     * For example, OP_PUSHDATA1(0x4c) reads one byte from the offset to get the number of bytes to copy from the raw Script.
     * And then, it copies that number of bytes from the raw Script and creates OpPushData with the copied data.

     * OpPushData will use the copied data when executed.
     *
     * For most of operations, this function simply returns the same object without copying any data.
     * Ex> OP_ADD just uses stack, without copying data from the raw script.
     *
     * This method is called while a raw script is parsed. After the script is parsed,
     * An instance of ScriptOp'subclass will have a ScriptValue field which has the copied data.
     *
     * Also, OP_CODESEPARATOR overrides this method to get the offset(=programCounter) parameter to store it
     * in the script execution environment.
     * The stored program counter will be used to find out the fence on the raw script
     * for checking signature by OP_CHECKSIG and OP_CHECKMULTISIG.
     *
     * @param script The raw script before it is parsed.
     * @param offset The offset where the input is read.
     * @return The number of bytes consumed to copy the input value.
     */
    default Pair<ScriptOp, Integer> create(Script script , int offset ) {
        return new MutablePair<>(this, 0);
    }

    /**
     * Calculate an OP code from a base OP code and an index.
     * Ex> OpPush, 1-75(0x01-0x4b), returns 1 for OpPush(1), and OpPush has the baseOpCode 0.
     * @param baseOpCode The base OP code where the index is added to calculate the OP code.
     * @param index The index from the base OP code.
     * @return The calculated OP code.
     */
    default OpCode opCodeFromBase(int baseOpCode, int index )  {
        int result = baseOpCode + index;

        return new OpCode((short)result);
    }

    /** Verify if the top value of the stack is true. Halt script execution if false.
     *
     * @param env The script execution environment.
     * @throws ScriptEvalException if the top value of the stack was not true. code : ErrorCode.InvalidTransaction
     */
    default void verify(ScriptEnvironment env)  {
        ScriptValue top = env.getStack().pop();

        if (!Utils.castToBool(top.getValue())) {
            throw new ScriptEvalException(ErrorCode.InvalidTransaction, "ScriptOp:${this.javaClass.getName()}");
        }
    }

    /** Push a false value on top of the stack.
     *
     * @param env The script execution environment.
     */
    default void pushFalse(ScriptEnvironment env)  {
        env.getStack().push(ScriptValue.valueOf(new byte[]{0}));
    }


    /** Push a true value on top of the stack.
     *
     * @param env The script execution environment.
     */
    default void pushTrue(ScriptEnvironment env)  {
        env.getStack().pushInt( BigInteger.valueOf(1));
    }

    /** Serialize the script operation into an array buffer.
     *
     * @param buffer The array buffer where the script is serialized.
     */
    default void serialize(List<Byte> buffer) {
        buffer.add(Byte.valueOf(opCode().getCode().byteValue()) );
    }

//    fun unaryOperation(env : ScriptEnvironment, mutate : (ScriptValue) -> (ScriptValue) ): Unit {
//        val value1 =  env.stack.pop()
//
//        val result = mutate( value1 )
//
//        env.stack.push( result )
//    }
//
//    fun binaryOperation(env : ScriptEnvironment, mutate : (ScriptValue, ScriptValue) -> (ScriptValue) ): Unit {
//        val value2 = env.stack.pop()
//        val value1 =  env.stack.pop()
//
//        val result = mutate( value1, value2 )
//
//        env.stack.push( result )
//    }
//
//    fun ternaryOperation(env : ScriptEnvironment, mutate : (ScriptValue, ScriptValue, ScriptValue) -> (ScriptValue) ): Unit {
//        val value3 = env.stack.pop()
//        val value2 = env.stack.pop()
//        val value1 =  env.stack.pop()
//
//        val result = mutate( value1, value2, value3 )
//
//        env.stack.push( result )
//    }
}
