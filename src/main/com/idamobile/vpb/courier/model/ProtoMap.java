package com.idamobile.vpb.courier.model;

import android.text.TextUtils;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Mapper(protoClass = Services.Map.class)
@Data
public class ProtoMap implements Serializable {
    @Field
    private List<ProtoMapEntry> entries = new ArrayList<ProtoMapEntry>();

    public String get(String key) {
        for (ProtoMapEntry entry : entries) {
            if (TextUtils.equals(entry.getKey(), key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void put(ProtoMapEntry entry) {
        entries.add(entry);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }
}
