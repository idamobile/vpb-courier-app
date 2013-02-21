package com.idamobile.vpb.courier.model;

import android.text.TextUtils;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;

@Mapper(protoClass = Services.OrderProtobufDTO.class)
@Data
public class Order implements Serializable {
    @Field private int id;
    @Field private OrderStatus status;
    @Field private String clientFirstName;
    @Field private String clientSecondName;
    @Field(optional = true) private String clientMiddleName;
    @Field private String clientPhone;
    @Field private String clientAddress;
    @Field private String subway;
    @Field private long meetTimeFrom;
    @Field private long meetTimeTo;
    @Field private String orderType;
    @Field(optional = true) private String uploadImagesUrl;
    @Field(optional = true) private ProtoMap attributes = new ProtoMap();

    public String getFullName() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(clientSecondName)
                .append(" ")
                .append(clientFirstName);
        if (!TextUtils.isEmpty(clientMiddleName)) {
            buffer.append(" ").append(clientMiddleName);
        }
        return buffer.toString();
    }
}