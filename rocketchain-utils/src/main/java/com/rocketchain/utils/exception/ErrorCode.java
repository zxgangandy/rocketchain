package com.rocketchain.utils.exception;

public enum ErrorCode {
    InternalError(0x01, "InternalError"),
    UnsupportedFeature(0x02, "UnsupportedFeature"),
    NoMoreKeys(0x0f, "no_more_keys"),

    OutOfFileSpace(0x10, "out_of_file_space"),
    BlockFilePathNotExists(0x11, "out_of_file_space"),
    InvalidFileNumber(0x12, "invalid_file_number"),


    InvalidBlockHeight(0x30, "invalid_block_height"),
    InvalidBlockHeightOnDatabase(0x31, "invalid_block_height_on_database"),
    InvalidTransactionOutPoint(0x32, "invalid_transaction_out_point"),
    TransactionOutputAlreadySpent(0x33, "transaction_output_already_spent"),
    TransactionOutputSpentByUnexpectedInput(0x34, "transaction_output_spent_by_unexpected_input"),
    ParentTransactionNotFound(0x35, "parent_transaction_not_found"),
    DisabledScriptOperation(0x35, "disabled_script_operation"),

    // Protocol Decode errors
    IncorrectMagicValue(0x40, "incorrect_magic_value"),
    DecodeFailure(0x41, "decode_failure"),
    PayloadLengthMismatch(0x42, "payload_length_mismatch"),
    PayloadChecksumMismatch(0x43, "payload_checksum_mismatch"),
    InvalidScriptOperation(0x44, "invalid_script_operation"),

    TooBigScriptInteger(0x50, "too_big_script_integer"),
    InvalidTransaction(0x51, "invalid_transaction"),
    NotEnoughInput(0x52, "not_enough_input"),
    NotEnoughScriptData(0x53, "not_enough_script_data"),
    TooManyPublicKeys(0x54, "too_many_public_keys"),
    // Base58 encoding errors
    InvalidChecksum(0x55, "invalid_checksum"),


    NotEnoughTransactionInput(0x60, "not_enough_transaction_input"),
    NotEnoughTransactionOutput(0x61, "not_enough_transaction_output"),
    NotEnoughInputAmounts(0x62, "not_enough_input_amounts"),
    GenerationInputWithOtherInputs(0x63, "generation_input_with_other_inputs"),
    SpendingOutputNotFound(0x64, "spending_output_not_found"),


    // Transaction verification errors
    InvalidInputIndex(0x70, "invalid_input_index"),
    TopValueFalse(0x71, "top_value_false"),
    ScriptParseFailure(0x72, "script_parse_failure"),
    ScriptEvalFailure(0x73, "script_eval_failure"),
    GeneralFailure(0x74, "general_failure"),
    UnsupportedHashType(0x75, "unsupported_hash_type"),
    NotEnoughStackValues(0x76, "not_enough_stack_values"),


    // Transaction Signer
    UnableToSignCoinbaseTransaction(0x80, "unable_to_sign_coinbase_transaction"),
    InvalidTransactionInput(0x81, "invalid_transaction_input"),

    // Parse errors
    NoDataAfterCodeSparator(0x90, "no_data_after_code_separator"),
    UnexpectedEndOfScript(0x91, "unexpected_end_of_script"),
    InvalidSignatureFormat(0x92, "invalid_signature_format"),

    // RPC errors
    RpcRequestParseFailure(0xa0, "rpc_request_parse_failure"),
    RpcParameterTypeConversionFailure(0xa1, "rpc_parameter_type_conversion_failure"),
    RpcMissingRequiredParameter(0xa2, "rpc_missing_required_parameter"),
    RpcArgumentLessThanMinValue(0xa3, "rpc_argument_less_than_min_value"),
    RpcArgumentGreaterThanMaxValue(0xa4, "rpc_argument_greater_than_max_value"),
    RpcInvalidAddress(0xa5, "invalid_address"),
    RpcInvalidKey(0xa6, "invalid_key"),
    RpcInvalidParameter(0xa7, "rpc_invalid_parameter"),


    // Wallet Exceptions
    OwnershipNotFound(0xb0, "ownership_not_found"),
    WalletOutputNotFound(0xb1, "wallet_output_not_found");

    private int errCode;
    private String desc;

    ErrorCode(int errCode, String desc) {
        this.errCode = errCode;
        this.desc = desc;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getDesc() {
        return desc;
    }

}
