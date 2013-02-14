package com.idamobile.vpb.courier.network.login;

import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.protobuf.ServicesDTO;
import lombok.Data;

public @Data
class LoginResponse {

    public static enum Result {
        OK(1),
        WRONG_CREDENTIALS(2),
        BLOCKED_ACCOUNT(3);

        private final int code;

        private Result(int code) {
            this.code = code;
        }

        public Result parse(ServicesDTO.LoginResponseProtobufDTO.Result result) {
            for (Result res : values()) {
                if (res.code == result.getNumber()) {
                    return res;
                }
            }
            throw new IllegalArgumentException("Unknown login response: " + result);
        }
    }

    private Result loginResult;
    private Courier courierInfo;

}
