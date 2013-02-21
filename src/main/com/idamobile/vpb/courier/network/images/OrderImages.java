package com.idamobile.vpb.courier.network.images;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderImages {
    private List<ImageInfo> images = new ArrayList<ImageInfo>();

    public int getCount() {
        return images.size();
    }

    public int getUploadedCount() {
        int count = 0;
        for (ImageInfo info : images) {
            if (info.isUploaded()) {
                count++;
            }
        }
        return count;
    }

    public boolean isAllUploaded() {
        return getUploadedCount() == getCount();
    }
}
