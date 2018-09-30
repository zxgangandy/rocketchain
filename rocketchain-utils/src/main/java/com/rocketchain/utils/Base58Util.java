package com.rocketchain.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Base58Util {

    private static final char[] alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    public static String encode(byte[] input) {
        if (input.length == 0) return "";
        else {
            BigInteger big = new BigInteger(1, input);
            StringBuilder builder = new StringBuilder();

            encode1(builder, big);

            List<Byte> list = new ArrayList<>();
            for(int i=0;i < input.length;i++) {
                if (input[i] == 0) {
                    list.add(input[i]);
                }
            }

            list.stream().forEach(item->builder.append(item.intValue()));
            return builder.reverse().toString();
        }
    }

    private static void encode1(StringBuilder builder, BigInteger current) {
        if (current == BigInteger.ZERO) {
            return;
        }else {
            BigInteger[] result = current.divideAndRemainder(BigInteger.valueOf(58L));
            BigInteger x = result[0];
            BigInteger remainder = result[1];
            builder.append(alphabet[remainder.intValue()]);
            encode1(builder, x);
        }
    }


    public static byte[] decode(String input)  {
        val zeroes = input.takeWhile{it == '1'}.map{0.toByte()}.toByteArray()
        val trim  = input.dropWhile{it== '1'}.toList()
        val decoded = trim.fold(BigInteger.ZERO, {a, b -> a.multiply(BigInteger.valueOf(58L)).add(BigInteger.valueOf(alphabetValue[b] ?: throw NoSuchElementException()))})
        val result = if (trim.isEmpty()) zeroes else zeroes + decoded.toByteArray().dropWhile{it.toInt() == 0}.toByteArray() // BigInteger.toByteArray may add a leading 0x00
        return result
    }
}
