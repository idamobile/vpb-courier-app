package com.idamobile.vpb.courier.network.login;

import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;

@Mapper(protoClass = Services.LoginResponseProtobufDTO.class)
public @Data class LoginResponse implements Serializable {
    private @Field LoginResult loginResult;
    private @Field(optional = true) String keyHash;
    private @Field(optional = true) Courier courierInfo;
}