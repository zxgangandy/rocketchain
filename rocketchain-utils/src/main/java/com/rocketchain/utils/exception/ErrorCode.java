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

    // Protocol Decode errors
    IncorrectMagicValue(0x40, "incorrect_magic_value"),
    DecodeFailure(0x41, "decode_failure"),
    PayloadLengthMismatch(0x42, "payload_length_mismatch"),
    PayloadChecksumMismatch(0x43, "payload_checksum_mismatch");

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
