package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;

@Data
@Mapper(protoClass = Services.ImageTypeProtobufDTO.class)
public class ImageType implements Serializable {
    @Field private int id;
    @Field private String description;
    @Field private boolean requiredImg;
}
