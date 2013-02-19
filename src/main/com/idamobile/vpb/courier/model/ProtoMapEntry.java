package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

@Mapper(protoClass = Services.Entry.class)
@Data
public class ProtoMapEntry {
    @Field private String key;
    @Field(optional = true) private String value;
}
