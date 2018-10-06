package com.rocketchain.codec.primitive;

import com.rocketchain.codec.Codec;
import com.rocketchain.codec.CodecInputOutputStream;

import java.util.Map;

public class PolymorphicCodec<typeT, valueT> implements Codec<valueT> {

    private Codec<typeT> typeIndicatorCodec;
    private Map<String, typeT> typeClassNameToTypeIndicatorMap;
    private Map<typeT, Codec<valueT>> typeIndicatorToCodecMap;

    public PolymorphicCodec(Codec<typeT> typeIndicatorCodec, Map<String, typeT> typeClassNameToTypeIndicatorMap,
                            Map<typeT, Codec<valueT>> typeIndicatorToCodecMap) {
        this.typeIndicatorCodec = typeIndicatorCodec;
        this.typeClassNameToTypeIndicatorMap = typeClassNameToTypeIndicatorMap;
        this.typeIndicatorToCodecMap = typeIndicatorToCodecMap;
    }

    @Override
    public valueT transcode(CodecInputOutputStream io, valueT obj) {
        if (io.getInput()) {
            typeT typeIndicator = typeIndicatorCodec.transcode(io, null);
            return typeIndicatorToCodecMap.get(typeIndicator).transcode(io, null);
        } else {
            String className = obj.getClass().getSimpleName();
            typeT typeIndicator = typeClassNameToTypeIndicatorMap.get(className);
            typeIndicatorCodec.transcode(io, typeIndicator);
            typeIndicatorToCodecMap.get(typeIndicator).transcode(io, obj);
            return null;
        }
    }
}
