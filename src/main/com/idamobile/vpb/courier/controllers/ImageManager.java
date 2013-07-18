package com.idamobile.vpb.courier.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.RequestGroup;
import com.idamobile.vpb.courier.network.core.RequestService;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.images.*;
import com.idamobile.vpb.courier.security.crypto.OrderImagesFilenameMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageManager {

    private ApplicationMediator mediator;
    private OrderImagesFilenameMapper filenameMapper;

    private final Object cacheLocker = new Object();
    private final Object imagesLocker = new Object();

    private LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(1024 * 1024 * 5) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value != null ? (value.getRowBytes() * value.getHeight()) : 0;
        }
    };

    public ImageManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.filenameMapper = new OrderImagesFilenameMapper(mediator.getContext());
    }

    public OrderImages getImages(Order order) {
        return getImages(order.getId());
    }

    public OrderImages getImages(int orderId) {
        synchronized (imagesLocker) {
            DataHolder<OrderImages> holder = getHolder(orderId);
            return holder.get();
        }
    }

    public ImageInfo getImageInfo(Order order, ImageType imageType) {
        return getImageInfo(order.getId(), imageType.getId());
    }

    public ImageInfo getImageInfo(int orderId, int imageTypeId) {
        synchronized (imagesLocker) {
            OrderImages orderImages = getImages(orderId);
            if (orderImages != null) {
                return orderImages.getInfoByImageType(imageTypeId);
            }
            return null;
        }
    }

    private DataHolder<OrderImages> getHolder(Order order) {
        return getHolder(order.getId());
    }

    private DataHolder<OrderImages> getHolder(int orderId) {
        return mediator.getCache().getHolder(OrderImages.class, String.valueOf(orderId));
    }

    public void registerReceiver(Context context, BroadcastReceiver receiver, Order order) {
        context.registerReceiver(receiver, new IntentFilter(getHolder(order).getBroadcastAction()));
    }

    public void refreshImages() {
        synchronized (imagesLocker) {
            int courierId = mediator.getLoginManager().getCourier().getId();
            List<Order> orders = mediator.getOrdersManager().getOrders();
            for (Order order : orders) {
                OrderImages newImages = new OrderImages(this, order.getId());
                for (ImageType imageType : order.getImageTypes()) {
                    ImageInfo imageInfo = new ImageInfo(
                            filenameMapper.mapToFileName(courierId, order.getId(), imageType.getId()));
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
    }

    public void startUploadImages(RequestWatcherCallbacks<RequestGroup.ModelCollection> callbacks) {
        LoginManager loginManager = mediator.getLoginManager();
        int courierId = loginManager.getCourier().getId();

        UploadImagesRequestGroup requestGroup = new UploadImagesRequestGroup();
        for (Order order : mediator.getOrdersManager().getOrders()) {
            if (order.getStatus() == OrderStatus.STATUS_ACTIVATED) {
                for (ImageType imageType : order.getImageTypes()) {
                    if (!isUploaded(order.getId(), imageType.getId())) {
                        requestGroup.addRequest(new UploadImageRequest(courierId, order.getId(), imageType.getId()));
                    }
                }
            }
        }
        if (callbacks != null) {
            callbacks.execute(requestGroup);
        } else {
            RequestService.execute(mediator.getContext(), requestGroup);
        }
    }

    public boolean hasImagesToUpload() {
        for (Order order : mediator.getOrdersManager().getOrders()) {
            if (order.getStatus() == OrderStatus.STATUS_ACTIVATED) {
                for (ImageType imageType : order.getImageTypes()) {
                    if (!isUploaded(order.getId(), imageType.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isUploaded(int orderId, int imageId) {
        int courierId = mediator.getLoginManager().getCourier().getId();
        File file = filenameMapper.mapToUplodedFileName(courierId, orderId, imageId);
        return file.exists();
    }

    public boolean markUploaded(int orderId, int imageId) throws IOException {
        int courierId = mediator.getLoginManager().getCourier().getId();
        File file = filenameMapper.mapToUplodedFileName(courierId, orderId, imageId);
        return file.exists() || file.createNewFile();
    }

    public boolean orderHasAllPictures(Order order) {
        OrderImages orderImages = getImages(order);
        if (orderImages != null) {
            for (ImageType imageType : order.getImageTypes()) {
                ImageInfo imageInfo = orderImages.getInfoByImageType(imageType.getId());
                if (imageType.isRequiredImg() && !imageInfo.getFile().exists()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void notifyImageInfoChanged(int orderId) {
        synchronized (imagesLocker) {
            DataHolder<OrderImages> holder = getHolder(orderId);
            holder.notifyChanged();
        }
    }

    public void cacheBitmap(String key, Bitmap bitmap) {
        synchronized (cacheLocker) {
            imageCache.put(key, bitmap);
        }
    }

    public Bitmap getCachedBitmap(String key) {
        synchronized (cacheLocker) {
            Bitmap bitmap = imageCache.get(key);
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            }
            return bitmap;
        }
    }

    public void removeFromCache(String key) {
        synchronized (cacheLocker) {
            imageCache.remove(key);
        }
    }
}