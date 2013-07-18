package com.idamobile.vpb.courier.widget.orders.images;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.network.images.ImageInfo;
import com.idamobile.vpb.courier.security.crypto.CryptoStreamProvider;
import com.idamobile.vpb.courier.util.*;
import lombok.Cleanup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class OrderImageView {

    public static final String TAG = OrderImageView.class.getSimpleName();

    private Order order;
    private ImageType image;

    public interface OrderImageImageCallbacks {
        void onTakeImageClicked(Order order, ImageType image);
        void onRemoveImageClicked(Order order, ImageType image);
    }

    private View view;
    private ImageView imageView;
    private View removeView;
    private View progressView;
    private TextView descriptionView;

    private OrderImageImageCallbacks imageCallbacks;
    private ApplicationMediator mediator;

    private boolean lastLoadingOfImageFailed;

    public OrderImageView(ViewGroup parent, ApplicationMediator mediator) {
        this.mediator = mediator;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        view = inflater.inflate(R.layout.order_image_item, parent, false);
        imageView = (ImageView) view.findViewById(R.id.image_placeholder);
        removeView = view.findViewById(R.id.remove_icon);
        progressView = view.findViewById(R.id.image_progress);
        descriptionView = (TextView) view.findViewById(R.id.image_description);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClicked();
            }
        });
    }

    public void setImageCallbacks(OrderImageImageCallbacks imageCallbacks) {
        this.imageCallbacks = imageCallbacks;
    }

    private void onClicked() {
        if (order != null && imageView != null) {
            ImageInfo info = getImageInfo();
            if (info != null) {
                if (order.getStatus() != OrderStatus.STATUS_ACTIVATED) {
                    if (!mediator.getImageManager().isUploaded(order.getId(), info.getTypeId())) {
                        if (info.getFile().exists()) {
                            removePictureClicked();
                        } else {
                            takePictureClicked();
                        }
                    }
                }
            }
        }
    }

    private void removePictureClicked() {
        if (imageCallbacks != null) {
            imageCallbacks.onRemoveImageClicked(order, image);
            lastLoadingOfImageFailed = false;
        }
    }

    private void takePictureClicked() {
        if (imageCallbacks != null) {
            imageCallbacks.onTakeImageClicked(order, image);
            lastLoadingOfImageFailed = false;
        }
    }

    public View getView() {
        return view;
    }

    public void setImage(Order order, ImageType image) {
        this.order = order;
        this.image = image;
        lastLoadingOfImageFailed = false;
        refresh();
    }

    public void refresh() {
        ImageInfo imageInfo = getImageInfo();
        if (imageInfo != null) {
            progressView.setVisibility(imageInfo.isProcessing() ? View.VISIBLE : View.GONE);
            imageView.setVisibility(imageInfo.isProcessing() ? View.GONE : View.VISIBLE);
            boolean uploaded = mediator.getImageManager().isUploaded(order.getId(), imageInfo.getTypeId());
            if (imageInfo.getFile().exists()) {
                removeView.setVisibility(order.getStatus() == OrderStatus.STATUS_ACTIVATED ? View.GONE : View.VISIBLE);
                if (!uploaded) {
                    if (!imageInfo.isProcessing() && !lastLoadingOfImageFailed) {
                        loadImage(imageInfo.getFile());
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_uploaded);
                }
            } else {
                removeView.setVisibility(View.GONE);
                if (!uploaded) {
                    if (image.isRequiredImg()) {
                        imageView.setImageResource(R.drawable.ic_take_picture_required);
                    } else {
                        imageView.setImageResource(R.drawable.ic_take_picture);
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_uploaded);
                }
                mediator.getImageManager().removeFromCache(imageInfo.getFile().getAbsolutePath());
            }
            descriptionView.setText(image.getDescription());
        } else {
            progressView.setVisibility(View.GONE);
            imageView.setImageDrawable(null);
            descriptionView.setText(null);
        }
    }

    private void loadImage(File file) {
        final String path = file.getAbsolutePath();
        Bitmap bitmap = mediator.getImageManager().getCachedBitmap(path);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected void onPreExecute() {
                    ImageInfo imageInfo = getImageInfo();
                    if (imageInfo != null) {
                        imageInfo.setProcessing(true);
                        imageView.setVisibility(View.GONE);
                        progressView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    int size = imageView.getWidth();
                    if (size <= 0) {
                        size = imageView.getLayoutParams().width;
                        if (size <= 0) {
                            size = Sizes.dpToPx(64, imageView.getContext());
                        }
                    }
                    size *= 2;

                    Files.InputStreamProvider streamProvider =
                            new CryptoStreamProvider(mediator.getLoginManager(), new File(path));
                    int orientation = 0;
                    try {
                        @Cleanup InputStream inputStream = streamProvider.openInputStream();
                        orientation = Exif.getOrientation(inputStream);
                    } catch (IOException e) {
                        Logger.debug(TAG, "failed to get orientation", e);
                    }
                    return Bitmaps.loadScaledImage(streamProvider, size, size, orientation);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    ImageInfo imageInfo = getImageInfo();
                    if (imageInfo != null) {
                        getImageInfo().setProcessing(false);
                    }
                    if (bitmap != null) {
                        mediator.getImageManager().cacheBitmap(path, bitmap);
                    } else {
                        lastLoadingOfImageFailed = true;
                    }
                    refresh();
                }
            }.execute();
        }
    }

    private ImageInfo getImageInfo() {
        if (order != null && image != null) {
            return mediator.getImageManager().getImageInfo(order, image);
        }
        return null;
    }

}
