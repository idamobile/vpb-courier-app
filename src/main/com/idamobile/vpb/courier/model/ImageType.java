package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

@Data
@Mapper(protoClass = Services.ImageType.class)
public class ImageType {
    @Field private int id;
    @Field private String description;
}
