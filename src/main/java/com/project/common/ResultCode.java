package com.project.common;

/**
 *  response enum
 */
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200),
    /**
     * 失败
     */
    FAIL(400),

    /**
     * 找不到
     */
    NOT_FOUND(404),

    /**
     * 内部错误
     */
    INTERNAL_ERROR(500);

    public int code;

    ResultCode(int code) {
        this.code = code;
    }
}
