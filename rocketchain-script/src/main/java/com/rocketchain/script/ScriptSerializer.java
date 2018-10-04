package com.rocketchain.script;

import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.rocketchain.script.ops.ScriptOp;

import java.util.List;

public class ScriptSerializer {
    /** Serialize the script operations into a Byte array.
     * This is also necessary in order to pass them to script parser and executor while we write test cases.
     * The input of the script parser is a byte array. So the serializer will write a list of ScriptOp(s) into ByteArray.
     * @param operations
     */
    public static byte[] serialize(List<ScriptOp> operations)  {
        List<Byte> buffer = Lists.newArrayList();

        for (ScriptOp op : operations ) {
            op.serialize(buffer);
        }

        return Bytes.toArray(buffer);
    }
}
