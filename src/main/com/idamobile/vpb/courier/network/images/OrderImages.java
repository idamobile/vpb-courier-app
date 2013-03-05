package com.idamobile.vpb.courier.network.images;

import com.idamobile.vpb.courier.controllers.ImageManager;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderImages {

    private final ImageManager manager;
    private List<ImageInfo> images = new ArrayList<ImageInfo>();
    private final int orderId;

    public int getCount() {
        return images.size();
    }

    public synchronized int getUploadedCount() {
        int count = 0;
        for (ImageInfo info : images) {
            if (manager.isUploaded(orderId, info.getTypeId())) {
                count++;
            }
        }
        return count;
    }

    public boolean isAllUploaded() {
        return getUploadedCount() == getCount();
    }

    public synchronized void merge(OrderImages newImages) {
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

    public synchronized ImageInfo getInfoByImageType(int typeId) {
        for (ImageInfo info : images) {
            if (info.getTypeId() == typeId) {
                return info;
            }
        }
        return null;
    }

}
