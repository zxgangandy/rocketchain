package com.rocketchain.script;

import com.google.common.collect.ImmutableMap;
import com.rocketchain.script.ops.*;

import java.util.Map;

public class ScriptOperations {
    private static Map<Short, ScriptOp> SCRIPT_OPS = ImmutableMap
            .of(0x00 , new Op0())
    /*
      new OpPush(1) ~ new OpPush(75) was generated with this code.
      Reason : I don't want my source complicated by writing a for loop
      that puts these operations onto a mutable map, and then merge with
      the immutable SCRIPT_OPS map to produce another immutable map.

      for (i : Int <- 1 to 75 ) {
         println(s"0x${Integer.toHexString(i)}, new OpPush($i)),")
      }
    */
            .of((short)0x01,new OpPush(1))
            .of(0x02,new OpPush(2))
            .of(0x03,new OpPush(3))
            .of(0x04,new OpPush(4))
            .of(0x05,new OpPush(5))
            .of(0x06,new OpPush(6))
            .of(0x07,new OpPush(7))
            .of(0x08,new OpPush(8))
            .of(0x09,new OpPush(9))
            .of(0x0a,new OpPush(10))
            .of(0x0b,new OpPush(11))
            .of(0x0c,new OpPush(12))
            .of(0x0d,new OpPush(13))
            .of(0x0e,new OpPush(14))
            .of(0x0f,new OpPush(15))
            .of(0x10,new OpPush(16))
            .of(0x11,new OpPush(17))
            .of(0x12,new OpPush(18))
            .of(0x13,new OpPush(19))
            .of(0x14,new OpPush(20))
            .of(0x15,new OpPush(21))
            .of(0x16,new OpPush(22))
            .of(0x17,new OpPush(23))
            .of(0x18,new OpPush(24))
            .of(0x19,new OpPush(25))
            .of(0x1a,new OpPush(26))
            .of(0x1b,new OpPush(27))
            .of(0x1c,new OpPush(28))
            .of(0x1d,new OpPush(29))
            .of(0x1e,new OpPush(30))
            .of(0x1f,new OpPush(31))
            .of(0x20,new OpPush(32))
            .of(0x21,new OpPush(33))
            .of(0x22,new OpPush(34))
            .of(0x23,new OpPush(35))
            .of(0x24,new OpPush(36))
            .of(0x25,new OpPush(37))
            .of(0x26,new OpPush(38))
            .of(0x27, new OpPush(39))
            .of(0x28,new OpPush(40))
            .of(0x29,new OpPush(41))
            .of(0x2a,new OpPush(42))
            .of(0x2b,new OpPush(43))
            .of(0x2c,new OpPush(44))
            .of(0x2d,new OpPush(45))
                    .of(0x2e,new OpPush(46))
                    .of(0x2f,new OpPush(47))
                    .of(0x30,new OpPush(48))
                    .of(0x31,new OpPush(49))
                    .of(0x32,new OpPush(50))
                    .of(0x33,new OpPush(51))
                    .of(0x34,new OpPush(52))
                    .of(0x35,new OpPush(53))
                    .of(0x36,new OpPush(54))
                    .of(0x37,new OpPush(55))
                    .of(0x38,new OpPush(56))
                    .of(0x39,new OpPush(57))
                    .of(0x3a,new OpPush(58))
                    .of(0x3b,new OpPush(59))
                    .of(0x3c,new OpPush(60))
                    .of(0x3d,new OpPush(61))
                    .of(0x3e,new OpPush(62))
                    .of(0x3f,new OpPush(63))
                    .of(0x40,new OpPush(64))
                    .of(0x41,new OpPush(65))
                    .of(0x42,new OpPush(66))
                    .of(0x43,new OpPush(67))
                    .of(0x44,new OpPush(68))
                    .of(0x45,new OpPush(69))
                    .of(0x46,new OpPush(70))
                    .of(0x47,new OpPush(71))
                    .of(0x48,new OpPush(72))
                    .of(0x49,new OpPush(73))
                    .of(0x4a,new OpPush(74))
                    .of(0x4b,new OpPush(75))
                    .of(0x4c,new OpPushData(1))
                    .of(0x4d,new OpPushData(2))
                    .of(0x4e,new OpPushData(4))
                    .of(0x4f,new Op1Negate())
                    .of(0x51, new Op1())
                    .of(0x52,new OpNum(2))
                    .of(0x53,new OpNum(3))
                    .of(0x54,new OpNum(4))
                    .of(0x55,new OpNum(5))
                    .of(0x56,new OpNum(6))
                    .of(0x57,new OpNum(7))
                    .of(0x58,new OpNum(8))
                    .of(0x59,new OpNum(9))
                    .of(0x5a,new OpNum(10))
                    .of(0x5b,new OpNum(11))
                    .of(0x5c,new OpNum(12))
                    .of(0x5d,new OpNum(13))
                    .of(0x5e,new OpNum(14))
                    .of(0x5f,new OpNum(15))
                    .of(0x60,new OpNum(16))
                    .of(0x61, new OpNop())
                    .of(0x63, new OpIf())
                    .of(0x64, new OpNotIf())
                    .of(0x67,new OpElse())
                    .of(0x68,new OpEndIf())
                    .of(0x69,new OpVerify())
                    .of(0x6a, new OpReturn())
                    .of(0x6b,new OpToAltStack())
                    .of(0x6c,new OpFromAltStack())
                    .of( 0x73,new OpIfDup())
                    .of(0x74,new OpDepth())
                    .of(0x75,new OpDrop())
                    .of(0x76, new OpDup())
                    .of(0x77,new OpNip())
                    .of(0x78,new OpOver())
                    .of( 0x79,new OpPick())
                    .of(0x7a,new OpRoll())
                    .of( 0x7b,new OpRot())
                    .of(  0x7c,new OpSwap())
                    .of(   0x7d,new OpTuck())
                    .of(   0x6d,new Op2Drop())
                    .of(  0x6e,new Op2Dup())
                    .of(  0x6f,new Op3Dup())
                    .of(0x70,new Op2Over())
                    .of(0x71,new Op2Rot())
                    .of(0x72,new Op2Swap())
                    .of(0x7e,new OpCat())
                    .of(0x7f,new OpSubstr())
                    .of(0x80,new OpLeft())
                    .of(0x81,new OpRight())
                    .of(0x82,new OpSize())
                    .of(0x83,new OpInvert())
                    .of(0x84,new OpAnd())
                    .of(0x85,new OpOr())
                    .of(0x86,new OpXor())
                    .of(0x87, new OpEqual())
                    .of(0x88, new OpEqualVerify())
                    .of(0x8b,new Op1Add())
                    .of(0x8c, new Op2Mul())
                    .of(0x8e,new Op2Div())
                    .of(0x8f,new OpNegate())
                    .of(0x90,new OpAbs())
                    .of(0x91,new OpNot())
                    .of(0x92,new Op0NotEqual())
                    .of(0x93,new OpAdd())
                    .of(0x94,new OpSub())
                    .of(0x95,new OpMul())
                    .of(0x96,new OpDiv())
                    .of(0x97,new OpMod())
                    .of(0x98,new OpLShift())
                    .of(0x99,new OpRShift())
                    .of(0x9a,new OpBoolAnd())
                    .of(0x9b,new OpBoolOr())
                    .of(0x9c,new OpNumEqual())
                    .of(0x9d,new OpNumEqualVerify())
                    .of(0x9e,new OpNumNotEqual())
                    .of(0x9f,new OpLessThan())
                    .of(0xa0,new OpGreaterThan())
                    .of(0xa1,new OpLessThanOrEqual())
                    .of(0xa2,new OpGreaterThanOrEqual())
                    .of(0xa3,new OpMin())
                    .of(0xa4,new OpMax())
                    .of(0xa5,new OpWithin())
                    .of(0xa6, new OpRIPEMD160())
                    .of(0xa7,new OpSHA1())
                    .of(0xa8,new OpSHA256())
                    .of(0xa9, new OpHash160())
                    .of(0xab,new OpCodeSparator())
                    .of(0xac,new OpCheckSig())
                    .of(0xad,new OpCheckSigVerify())
                    .of(0xae,new OpCheckMultiSig())
                    .of(0xaf,new OpCheckMultiSigVerify())
                    .of(0xf9,new OpSmallData())
                    .of(0xfa,new OpSmallInteger())
                    .of(0xfd,new OpPubKeyHash())
                    .of(0xfe,new OpPubKey())
                    .of(0xff,new OpInvalidOpCode())
                    .of(0x50,new OpReserved())
                    .of(0x62,new OpVer())
                    .of(0x65,new OpVerIf())
                    .of(0x66,new OpVerNotIf())
                    .of(0x89,new OpReserved1())
                    .of(0x8a,new OpReserved2())
                    .of(0xb0,new OpNopN(1))
                    .of(0xb1,new OpNopN(2))
                    .of(0xb2,new OpNopN(3))
                    .of(0xb3,new OpNopN(4))
                    .of(0xb4,new OpNopN(5))
                    .of(0xb5,new OpNopN(6))
                    .of(0xb6,new OpNopN(7))
                    .of(0xb7,new OpNopN(8))
                    .of(0xb8,new OpNopN(9))
                    .of(0xb9,new OpNopN(10);



    /** Return the ScriptOp object that implements a specific operation code of the script.
     *
     * @param opCode The op code of a script word.
     * @return
     */
    public ScriptOp get(short opCode )  {
        return SCRIPT_OPS.get(opCode);
    }
}
