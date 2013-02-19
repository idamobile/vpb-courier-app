package com.idamobile.vpb.courier.network.login;

import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;

@Mapper(protoClass = Services.LoginResponseProtobufDTO.Result.class, isEnum = true)
public enum LoginResult {
    OK(1),
    WRONG_CREDENTIALS(2),
    BLOCKED_ACCOUNT(3);

    @Field public final int code;

    private LoginResult(int code) {
        this.code = code;
    }
}
