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

    public void merge(OrderImages newImages) {
        for (ImageInfo info : newImages.getImages()) {
            ImageInfo oldInfo = getInfoByImageType(info.getTypeId());
            if (oldInfo != null) {
                info.setTotalBytes(oldInfo.getTotalBytes());
                info.setUploadedBytes(oldInfo.getUploadedBytes());
            }
        }

        images.clear();
        images.addAll(newImages.getImages());
    }

    public ImageInfo getInfoByImageType(int typeId) {
        for (ImageInfo info : images) {
            if (info.getTypeId() == typeId) {
                return info;
            }
        }
        return null;
    }

}
