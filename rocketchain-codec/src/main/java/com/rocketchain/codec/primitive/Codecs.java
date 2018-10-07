package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.InPointCodec;
import com.rocketchain.codec.ProvideCodec;
import com.rocketchain.codec.VariableStringCodec;
import com.rocketchain.proto.InPoint;
import com.rocketchain.utils.lang.Option;

import java.nio.charset.Charset;
import java.util.Map;

public class Codecs {
    public static BooleanCodec Boolean = new BooleanCodec();

    public static ByteCodec Byte = new ByteCodec();

    public static UInt16Codec UInt16 = new UInt16Codec();

    public static Int32LCodec Int32L = new Int32LCodec();
    public static UInt32LCodec UInt32L = new UInt32LCodec();
    public static Int32Codec Int32 = new Int32Codec();

    public static Int64Codec Int64 = new Int64Codec();
    public static Int64LCodec Int64L = new Int64LCodec();
    public static UInt64LCodec UInt64L = new UInt64LCodec();

    public static VariableIntCodec VariableInt = new VariableIntCodec();

    public static VariableStringCodec VariableString = variableString(VariableInt);

    public static VariableListCodec<Option<InPoint>> OptionalInPointListCodec = variableListOf(optional(new InPointCodec()));


    public static VariableByteBufCodec variableByteBuf(Codec<Long> lengthCodec) {
        return new VariableByteBufCodec(lengthCodec);
    }

    public static VariableByteBufCodec VariableByteBuf = variableByteBuf(VariableInt);

    public static CStringCodec CString = new CStringCodec(Charset.forName("UTF-8"));

    public static <T> CStringPrefixedCodec<T> cstringPrefixed(Codec<T> valueCodec) {
        return new CStringPrefixedCodec<T>(valueCodec);
    }

    public static FixedReversedByteArrayCodec fixedReversedByteArray(int length) {
        return new FixedReversedByteArrayCodec(length);
    }

    public static FixedByteArrayCodec fixedByteArray(int length) {
        return new FixedByteArrayCodec(length);
    }

    public static <T> OptionalCodec<T> optional(Codec<Boolean> flagCodec, Codec<T> valueCodec) {
        return new OptionalCodec(flagCodec, valueCodec);
    }

    public static <T> OptionalCodec<T> optional(Codec<T> valueCodec) {
        return optional(Boolean, valueCodec);
    }

    public static <T> VariableListCodec<T> variableListOf(Codec<Long> lengthCodec, Codec<T> valueCodec) {
        return new VariableListCodec<T>(lengthCodec, valueCodec);
    }

    public static <T> VariableListCodec<T> variableListOf(Codec<T> valueCodec) {
        return variableListOf(VariableInt, valueCodec);
    }

    public static VariableStringCodec variableString(Codec<Long> lengthCodec) {
        return new VariableStringCodec(lengthCodec);
    }

    public static <T> ProvideCodec<T> provide(T objectSample ) {
        return new ProvideCodec<T>(objectSample);
    }

    public static <valueT> Codec<valueT> polymorphicCodec(Codec typeIndicatorCodec ,
                                                                 Map typeClassNameToTypeIndicatorMap,
                                                                 Map typeIndicatorToCodecMap) {
        return new PolymorphicCodec(typeIndicatorCodec, typeClassNameToTypeIndicatorMap, typeIndicatorToCodecMap);
    }
}
