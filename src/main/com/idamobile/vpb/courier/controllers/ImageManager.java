package com.idamobile.vpb.courier.controllers;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.images.ImageInfo;
import com.idamobile.vpb.courier.network.images.OrderImages;
import com.idamobile.vpb.courier.security.crypto.OrderImagesFilenameMapper;

import java.util.List;

public class ImageManager {

    private ApplicationMediator mediator;
    private OrderImagesFilenameMapper filenameMapper;

    private Object locker = new Object();
    private LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(1024 * 1024 * 5) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value != null ? value.getByteCount() : 0;
        }
    };

    public ImageManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.filenameMapper = new OrderImagesFilenameMapper(mediator.getContext());
    }

    public OrderImages getImages(Order order) {
        DataHolder<OrderImages> holder = getHolder(order);
        return holder.get();
    }

    public ImageInfo getImageInfo(Order order, ImageType imageType) {
        OrderImages orderImages = getImages(order);
        if (orderImages != null) {
            return orderImages.getInfoByImageType(imageType.getId());
        }
        return null;
    }

    private DataHolder<OrderImages> getHolder(Order order) {
        return mediator.getCache().getHolder(OrderImages.class, String.valueOf(order.getId()));
    }

    public void refreshImages() {
        List<Order> orders = mediator.getOrdersManager().getOrders();
        for (Order order : orders) {
            OrderImages newImages = new OrderImages();
            for (ImageType imageType : order.getImageTypes()) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setFile(filenameMapper.mapToFileName(order, imageType));
                imageInfo.setTypeId(imageType.getId());
                newImages.getImages().add(imageInfo);
            }

            OrderImages oldImages = getImages(order);
            if (oldImages == null) {
                oldImages = newImages;
            } else {
                oldImages.merge(newImages);
            }

            getHolder(order).set(oldImages);
        }
    }

    public void cacheBitmap(String key, Bitmap bitmap) {
        synchronized (locker) {
            imageCache.put(key, bitmap);
        }
    }

    public Bitmap getCachedBitmap(String key) {
        synchronized (locker) {
            Bitmap bitmap = imageCache.get(key);
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            }
            return bitmap;
        }
    }

    public void removeFromCache(String key) {
        synchronized (locker) {
            imageCache.remove(key);
        }
    }

}