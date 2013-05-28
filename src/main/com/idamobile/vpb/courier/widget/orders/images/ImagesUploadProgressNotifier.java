package com.idamobile.vpb.courier.widget.orders.images;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.controllers.ImageManager;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.images.ImageInfo;
import com.idamobile.vpb.courier.network.images.OrderImages;
import com.idamobile.vpb.courier.util.Bitmaps;
import com.idamobile.vpb.courier.util.Versions;

import java.util.List;

public class ImagesUploadProgressNotifier {

    public static final String TAG = ImagesUploadProgressNotifier.class.getSimpleName();

    public static final int UPLOAD_PROGRESS_NOTIFICATION_ID = 4310;
    public static final int PENDING_INTENT_REQUEST_CODE = 4320;

    private NotificationManager notificationManager;
    private ApplicationMediator mediator;
    private Context context;

    private final Object locker = new Object();
    private volatile boolean imagesWatcherRegistered;

    private BroadcastReceiver imagesWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            post();
        }
    };
    private NotificationCompat.Builder builder;

    public ImagesUploadProgressNotifier(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.context = mediator.getContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void register() {
        synchronized (locker) {
            if (imagesWatcherRegistered) {
                context.unregisterReceiver(imagesWatcher);
                imagesWatcherRegistered = false;
            }
            List<Order> orders = mediator.getOrdersManager().getOrders();
            for (Order order : orders) {
                if (order.getStatus() == OrderStatus.STATUS_ACTIVATED) {
                    mediator.getImageManager().registerReceiver(context, imagesWatcher, order);
                    imagesWatcherRegistered = true;
                }
            }
        }
    }

    private void post() {
        synchronized (locker) {
            if (imagesWatcherRegistered) {
                Intent notificationIntent = new Intent();
                PendingIntent contentIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (builder == null) {
                    builder = new NotificationCompat.Builder(mediator.getContext());
                    builder.setAutoCancel(false)
                            .setSmallIcon(R.drawable.ic_stat_upload)
                            .setContentIntent(contentIntent)
                            .setOnlyAlertOnce(true)
                            .setDefaults(Notification.DEFAULT_LIGHTS);
                }
                setContent(builder);

                if (Versions.hasHoneycombApi()) {
                    int maxWidht = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
                    int maxHeight = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
                    Bitmap largeIcon = Bitmaps.resizeBitmap(
                            BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_upload),
                            maxWidht, maxHeight);
                    builder.setLargeIcon(largeIcon);
                }

                Notification notification = builder.build();
                notificationManager.notify(UPLOAD_PROGRESS_NOTIFICATION_ID, notification);
            } else {
                cancel();
            }
        }
    }

    private void setContent(NotificationCompat.Builder builder) {
        boolean uploadingFound = false;
        int totalImages = 0;
        for (Order order : mediator.getOrdersManager().getOrders()) {
            if (order.getStatus() != OrderStatus.STATUS_ACTIVATED) {
                continue;
            }

            ImageManager imageManager = mediator.getImageManager();
            OrderImages orderImages = imageManager.getImages(order);
            if (orderImages != null) {
                for (ImageInfo imageInfo : orderImages.getImages()) {
                    if (!uploadingFound && imageInfo.isUploading()) {
                        int progress = imageInfo.getTotalBytes() > 0
                                ? (int) (imageInfo.getUploadedBytes() * 100 / imageInfo.getTotalBytes()) : 0;
                        String message = context.getString(
                                R.string.upload_progress_notification_message_format, progress);
                        builder.setContentText(message);
                        if (imageInfo.getTotalBytes() == 0) {
                            builder.setProgress(0, 0, true);
                        } else {
                            builder.setProgress(100, progress, false);
                        }
                        uploadingFound = true;
                    }

                    if (!imageManager.isUploaded(order.getId(), imageInfo.getTypeId())) {
                        totalImages++;
                    }
                }
            }
        }

        String title = context.getString(R.string.upload_progress_notification_title_format, totalImages);
        builder.setContentTitle(title);
        if (!uploadingFound) {
            builder.setProgress(0, 0, true);
            builder.setContentText(context.getString(R.string.upload_progress_dialog_message_connecting));
        }
    }

    public void cancel() {
        synchronized (locker) {
            if (imagesWatcherRegistered) {
                context.unregisterReceiver(imagesWatcher);
                imagesWatcherRegistered = false;
            }
            notificationManager.cancel(UPLOAD_PROGRESS_NOTIFICATION_ID);
        }
    }
}