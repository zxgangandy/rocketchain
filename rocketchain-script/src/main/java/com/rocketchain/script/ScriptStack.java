package com.rocketchain.script;

import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.List;

/**
 * Script Execution Stack. It holds data that Bitcoin Scripts push and pop.
 * Ex> OP_ADD pops two integer values from the stack, and pushes the result, which is an integer value that adds the two integers on to the stack.
 */
public class ScriptStack {
    /**
     * Use an array buffer to implement a script stack.
     * We can not use mutable.Stack, because it lacks a method that remove an n-th element.
     * <p>
     * The bottom of the stack is the first element in this array.
     * The top of the stack is the last element of this array.
     */
    private List<ScriptValue> array = Lists.newArrayList();

    /**
     * Convert the stack index to the array index on the array field.
     * - Stack index 0 means top of the stack and it maps to array.length-1
     * - Stack index array.length-1 means the bottom of the stack and it maps to 0
     *
     * @param stackIndex
     * @return
     */
    public int toArrayIndex(int stackIndex) {
        return array.size() - 1 - stackIndex;
    }

    /**
     * Push a ScriptValue on to the top of the stack.
     *
     * @param value
     */
    public void push(ScriptValue value) {
        // The top of the stack is the end of the array.
        // Just append the element to the end of the array.
        array.add(value);
    }

    /**
     * Pop a ScriptValue from the top of the stack.
     *
     * @return
     */
    public ScriptValue pop() {
        // The top of the stack is the end of the array.
        // Get rid of the last element of the array.
        ScriptValue popped = array.remove(toArrayIndex(0));
        return popped;
    }

    /**
     * Get the top element without popping it.
     *
     * @return The top element.
     */
    public ScriptValue top() {
        return this.get(0);
    }

    /**
     * Retrieve n-th element from stack, where top of stack has index 0.
     *
     * @param index The index from the top of the stack.
     * @return The n-th element.
     */
    public ScriptValue get(int index) {
        return array.get(toArrayIndex(index));
    }

    /**
     * Remove the N-th element on the stack.
     * - The top element : N = 0
     * - The element right below the top element : N = 1
     */
    public ScriptValue remove(int index) {
        ScriptValue removedValue = array.remove(toArrayIndex(index));
        return removedValue;
    }

    /**
     * Inserts elements at a given index into this stack.
     *
     * @param index The index where the new element will exist after the insertion.
     * @param value The value to insert into this stack.
     */
    // TODO : Write a unit test for every edge cases for this method.
    public void insert(int index, ScriptValue value) {
        array.add(toArrayIndex(index), value);
    }

    /**
     * Get the number of elements in the stack.
     *
     * @return The number of elements.
     */
    public int size() {
        return array.size();
    }

    /**
     * Check if the stack is empty.
     *
     * @return true if the stack is empty. false otherwise.
     */
    public boolean isEmpty() {
        return array.isEmpty();
    }

    /**
     * Check if the stack is not empty.
     *
     * @return true if the stack is not empty. false otherwise.
     */
    public boolean isNotEmpty() {
        return !array.isEmpty();
    }

    /**
     * Push a big integer value on the top of the stack.
     *
     * @param value The value to push
     */
    public void pushInt(BigInteger value) {
        ScriptValue scriptValue = ScriptValue.valueOf(value);
        push(scriptValue);
    }

    /**
     * Pop an integer value from the top of the stack.
     *
     * @return The popped value.
     */
    public BigInteger popInt() {
        ScriptValue scriptValue = pop();
        BigInteger value = ScriptValue.decodeStackInt(scriptValue.value);
        return value;
    }

/*
  override fun toString() : String {
    s"ScriptStack<${array.mkString(",")}}>"
  }
*/
}
