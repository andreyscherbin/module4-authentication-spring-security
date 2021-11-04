package com.epam.esm.entity;

public final class ErrorCode {

    public static final int GIFT_CERTIFICATE_ERROR_CODE = 1;
    public static final int TAG_ERROR_CODE = 2;
    public static final int USER_ERROR_CODE = 3;
    public static final int ORDER_ERROR_CODE = 4;
    public static final int PARAM_ERROR = 5;
    public static final int SERVER_ERROR = 0;
    public static final int BAD_INPUT = 6;
    public static final int BAD_TOKEN = 7;
    public static final int ACCESS_DENIED = 8;
    public static final int BAD_CREDENTIALS = 9;

    private ErrorCode() {
    }
}
