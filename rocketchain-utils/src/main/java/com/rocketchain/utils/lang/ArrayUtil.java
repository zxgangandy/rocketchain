package com.rocketchain.utils.lang;

import com.google.common.primitives.Bytes;

import java.util.Arrays;

public class ArrayUtil {

    public static int compare(byte[] left, byte[] right) {
        // get the minimum length of the two arrays.
        int minLength;
        if (left.length < right.length) {
            minLength = left.length;
        } else {
            minLength = right.length;
        }
        for (int i = 0; i < minLength; i++) {
            if (left[i] == right[i]) {
                continue;
            }
            return left[i] - right[i];
        }
        // If one array starts with the contents of another array, the logner one is greater.
        if (left.length < right.length) {
            return -1;
        } else if (left.length > right.length) {
            return 1;
        } else {
            return 0;
        }
    }

    public static byte[] reversedArray(byte[] src) {
        int size = src.length;
        byte[] result = new byte[size];
        int lastIndex = size - 1;
        for (int i = 0; i <= lastIndex; i++) {
            result[i] = src[lastIndex - i];
        }
        return result;
    }

    /**
     * pad an array with the given value.
     *
     * @param array        The input array. At the end of this array, the given value is padded.
     * @param targetLength After padding, the length of the array becomes targetLength.
     * @param value        The value to use for padding the input array.
     * @return The newly padded array whose length is targetLength.
     */
    public static byte[] pad(byte[] array, int targetLength, byte value) {
        if (targetLength > array.length) {
            byte[] padArray = new byte[targetLength - array.length];

            Arrays.fill(padArray, 0, padArray.length, value);
            return Bytes.concat(array, padArray);
        } else {
            return array;
        }
    }

    public static byte[] unpad(byte[] array, byte value )  {
        int size = array.length;
        int index = 0;
        for (int i = size - 1; i>=0; i--) {
            if (array[i] != value) {
                index = i;
                break;
            }
        }

        return Arrays.copyOfRange(array, 0, index);
    }


}
