package com.rocketchain.utils.lang;

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
}
