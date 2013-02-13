package com.idamobile.vpb.courier.network;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import static com.idamobile.vpb.courier.network.ResponseDTO.ResultCode;

public class HttpStatusCodeProcessor {

    public ResultCode parse(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();
        switch (code) {
        case HttpStatus.SC_OK:
            return ResultCode.SUCCESS;

        case HttpStatus.SC_UNAUTHORIZED:
        case HttpStatus.SC_FORBIDDEN:
            return ResultCode.NOT_AUTHORIZED;

        case HttpStatus.SC_SERVICE_UNAVAILABLE:
        case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            return ResultCode.SERVER_ERROR;

        case HttpStatus.SC_GONE:
            return ResultCode.ENTITY_NOT_FOUND;

        case HttpStatus.SC_BAD_REQUEST:
            return ResultCode.BAD_REQUEST;

        case HttpStatus.SC_PRECONDITION_FAILED:
            return ResultCode.PRECONDITION_FAILED;

        default:
            // 2xx -> OK
            if (code / 100 == 2) {
                return ResultCode.SUCCESS;
            } else {
                return ResultCode.UNKNOWN_ERROR;
            }
        }
    }

}
