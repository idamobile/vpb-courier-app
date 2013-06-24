package com.idamobile.vpb.courier.model;

import android.text.TextUtils;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @Field private OrderType orderType;
    @Field(optional = true) private ProtoMap attributes = new ProtoMap();
    @Field private List<ImageType> imageTypes = new ArrayList<ImageType>();

    private long statusUpdateTime;

    public String getFullName() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(clientSecondName)
                .append(" ")
                .append(clientFirstName);
        if (!TextUtils.isEmpty(clientMiddleName)) {
            buffer.append(" ").append(clientMiddleName);
        }
        return buffer.toString();
    }

    public boolean hasImageType(int id) {
        return getImageType(id) != null;
    }

    public ImageType getImageType(int id) {
        for (ImageType type : imageTypes) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}