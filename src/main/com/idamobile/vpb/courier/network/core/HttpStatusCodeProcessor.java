package com.idamobile.vpb.courier.network.core;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

public class HttpStatusCodeProcessor {

    public ResponseDTO.ResultCode parse(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        switch (code) {
        case HttpStatus.SC_OK:
            return ResponseDTO.ResultCode.SUCCESS;

        case HttpStatus.SC_UNAUTHORIZED:
        case HttpStatus.SC_FORBIDDEN:
            return ResponseDTO.ResultCode.NOT_AUTHORIZED;

        case HttpStatus.SC_SERVICE_UNAVAILABLE:
        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            return ResponseDTO.ResultCode.SERVER_ERROR;

        case HttpStatus.SC_GONE:
            return ResponseDTO.ResultCode.ENTITY_NOT_FOUND;

        case HttpStatus.SC_BAD_REQUEST:
            return ResponseDTO.ResultCode.BAD_REQUEST;

        case HttpStatus.SC_PRECONDITION_FAILED:
            return ResponseDTO.ResultCode.PRECONDITION_FAILED;

        default:
            // 2xx -> OK
            if (code / 100 == 2) {
                return ResponseDTO.ResultCode.SUCCESS;
            } else {
                return ResponseDTO.ResultCode.UNKNOWN_ERROR;
            }
        }
    }

}
