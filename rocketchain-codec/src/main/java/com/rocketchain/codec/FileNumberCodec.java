package com.rocketchain.codec;

import com.rocketchain.codec.primitive.Codecs;
import com.rocketchain.proto.FileNumber;

public class FileNumberCodec implements Codec<FileNumber> {
    @Override
    public FileNumber transcode(CodecInputOutputStream io, FileNumber obj) {
        Integer fileNumber = Codecs.Int32L.transcode(io, obj == null ? null : obj.getFileNumber());

        if (io.getInput()) {
            return new FileNumber(fileNumber);
        }
        return null;
    }
}
