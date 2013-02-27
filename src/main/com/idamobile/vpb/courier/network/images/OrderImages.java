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
        List<ImageInfo> resultImages = new ArrayList<ImageInfo>(newImages.getImages().size());
        for (ImageInfo info : newImages.getImages()) {
            ImageInfo oldInfo = getInfoByImageType(info.getTypeId());
            if (oldInfo != null) {
                resultImages.add(oldInfo);
            } else {
                resultImages.add(info);
            }
        }

        images.clear();
        images.addAll(resultImages);
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
