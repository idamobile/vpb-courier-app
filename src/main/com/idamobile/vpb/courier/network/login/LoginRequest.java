package com.idamobile.vpb.courier.network.login;

import com.idamobile.vpb.courier.network.core.AbstractProtoRequest;
import com.idamobile.vpb.courier.network.core.Hosts;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Mapper(protoClass = Services.LoginRequestProtobufDTO.class)
@ToString
public class LoginRequest extends AbstractProtoRequest<LoginResponse> {
    @Field
    private @Getter @Setter String login;

    @Field
    private @Getter @Setter String passwordHash;

    public LoginRequest() {
        super(Hosts.LOGIN_URL);
    }

    @Override
    protected Class<LoginResponse> getResultClass() {
        return LoginResponse.class;
    }

    @Override
    protected Class<?> getRequestProtoClass() {
        return Services.LoginRequestProtobufDTO.class;
    }

    @Override
    protected Class<?> getResultProtoClass() {
        return Services.LoginResponseProtobufDTO.class;
    }
}