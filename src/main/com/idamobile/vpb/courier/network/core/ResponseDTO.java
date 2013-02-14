package com.idamobile.vpb.courier.network.core;

import java.io.Serializable;

/**
 * 
 * @author zjor
 * @since Jun 4, 2012
 *
 */
public class ResponseDTO<T> implements Serializable {

    public enum ResultCode {
        SUCCESS,

        SERVER_ERROR,
        NETWORK_ERROR,
        NOT_AUTHORIZED,
        ENTITY_NOT_FOUND,
        BAD_REQUEST,
        PRECONDITION_FAILED,
        UNKNOWN_ERROR,

        PROTOCOL_ERROR,
        CANCELLED
    }

    private boolean success;

    private ResultCode resultCode;
    private String errorMessage;

    private T data;

    public ResponseDTO() {}

    public ResponseDTO(T data, boolean success, ResultCode resultCode, String errorMessage) {
        this.success = success;
        this.resultCode = resultCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public static <T> ResponseDTO<T> newSuccessfulResponse(T data) {
        return new ResponseDTO<T>(data, true, ResultCode.SUCCESS, null);
    }

    public static <T> ResponseDTO<T> newFailureResponse(ResultCode resultCode, String errorMessage) {
        return new ResponseDTO<T>(null, false, resultCode, errorMessage);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return String.format("{success: %b; code: %s; data: %s; error: %s}", success, resultCode, data, errorMessage);
    }

}
