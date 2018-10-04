package com.rocketchain.script;

import com.google.common.collect.Lists;
import com.rocketchain.script.ops.ScriptOp;
import com.rocketchain.proto.Script;
import com.rocketchain.utils.exception.ErrorCode;
import com.rocketchain.utils.exception.ScriptParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ScriptParser {
    private Logger logger = LoggerFactory.getLogger(ScriptParser.class);

    private ScriptOpList scriptOpList ;
    private ScriptOp foundFenceOp ;
    private int bytesConsumed;

    /**
     * Parse byte array and produce list of script operations.
     *
     * @param rawScript The raw bytes of script that we did not parse yet.
     */

    /** Parse a given raw script in a byte array to get the list of ScriptOp(s)
     *
     * @param script The input script.
     * @return The list of ScriptOp(s).
     */
    public static ScriptOpList parse(Script script )  {
        ParseResult parseResult = parseUntil(script, 0, null);
        return parseResult.getScriptOpList();
    }

    /** An internal version of parse method. ScriptOp.create can call this function.
     * The parse function is a recursive function.
     * ScriptParser.parse -> ScriptOp.create -> ScriptParser.parse ...
     *
     * When the parse function calls ScriptOp.create, some of sub classes of ScriptOp such as OP_IF and OP_NOTIF.
     * Script operation case classes implementing OP_IF and OP_NOTIF are OpIf and OpNotIf.
     * These implements the create method to call parse function again and parses until it meets OP_ENDIF
     * to produce OpCond, which is a pseudo operation.
     *
     * See OpCond for the details.
     *
     * @param script The input script.
     * @param offset The offset of the script to start parsing.
     * @param fenceScriptOps Parsing continues until we meet any of the operations in fenceScriptOps.
     *                       If no fence operation is passed, parse the script until the end of script.
     *
     * Throw a parse exception with UnexpectedEndOfScript code if we did not meet any of fenceScriptOps
     * and we reached at the end of the script.
     * @return The list of ScriptOp(s).
     */
    public static ParseResult parseUntil(Script script , int offset ,  ScriptOp fenceScriptOps )   {
        List<ScriptOp> operations = Lists.newArrayList();
        int programCounter = offset;
        ScriptOp fenceOp  = null;
        // BUGBUG : Improve readability of this code.

        while ( (programCounter < script.size()) && // Loop until we meet the end of script and
                fenceOp == null) {               // we did not meet any fence operation.
            // Read the script op code
            val opCode = (script.get(programCounter).toInt() and 0xFF).toShort();
            // Move the cursor forward
            programCounter += 1;

            // Get the script operation that matches the op code.
            val scriptOp = ScriptOperations.get(opCode);
            if (scriptOp != null) {
                val scriptOpTemplate = scriptOp;

                // A script operation can consume bytes in the script chunk.
                // Copy a chunk of bytes of
                val (scriptOp, bytesConsumed) = scriptOpTemplate.create(script, programCounter);
                // Move the cursor to the next script operation we want to execute.

                // See if the scriptOp exists in the fenceScriptOps list.
                // For example, while parsing OP_IF/OP_NOTIF, we need to parse until we meet either OP_ELSE or OP_ENDIF.
                if (scriptOp !is ScriptOpWithoutCode) { // BUGBUG : Improve this code without using runtime type checking.
                    fenceOp = fenceScriptOps.find{ scriptOp.opCode() == it.opCode() };
                }

                // Append to the list of operations only if it is not a fence operation.
                if (fenceOp == null) {
                    operations.add(scriptOp);
                }

                programCounter += bytesConsumed;
            } else {
                // Encountered an invalid OP code. This could be an attack, so dump the raw script onto log.
                logger.warn("InvalidScriptOperation. " +
                        "code : ${HexUtil.prettyHex(ByteArray(1, {opCode.toByte()}))}" +
                        "programCounter : $programCounter" );

                throw new ScriptParseException(ErrorCode.InvalidScriptOperation);
            }
        }

        // Throw an exception if we did not meet any fence operation
        // even though the caller of this method passed list of fence operations.
        if (fenceScriptOps.size > 0) {
            if (fenceOp == null) {
                throw new ScriptParseException(ErrorCode.UnexpectedEndOfScript);
            }
        }

        return new  ParseResult( ScriptOpList(operations.toList()), fenceOp, programCounter - offset );
    }
}
