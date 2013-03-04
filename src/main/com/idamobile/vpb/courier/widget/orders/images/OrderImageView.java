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
import com.idamobile.vpb.courier.util.Bitmaps;
import com.idamobile.vpb.courier.util.CryptoUtil;
import com.idamobile.vpb.courier.util.Sizes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OrderImageView {

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
                if (info.getFile().exists()) {
                    if (order.getStatus() != OrderStatus.STATUS_ACTIVATED) {
                        removePictureClicked();
                    }
                } else {
                    takePictureClicked();
                }
            }
        }
    }

    private void removePictureClicked() {
        if (imageCallbacks != null) {
            imageCallbacks.onRemoveImageClicked(order, image);
        }
    }

    private void takePictureClicked() {
        if (imageCallbacks != null) {
            imageCallbacks.onTakeImageClicked(order, image);
        }
    }

    public View getView() {
        return view;
    }

    public void setImage(Order order, ImageType image) {
        this.order = order;
        this.image = image;
        refresh();
    }

    public void refresh() {
        ImageInfo imageInfo = getImageInfo();
        if (imageInfo != null) {
            progressView.setVisibility(imageInfo.isProcessing() ? View.VISIBLE : View.GONE);
            imageView.setVisibility(imageInfo.isProcessing() ? View.GONE : View.VISIBLE);
            if (imageInfo.getFile().exists()) {
                removeView.setVisibility(order.getStatus() == OrderStatus.STATUS_ACTIVATED ? View.GONE : View.VISIBLE);
                if (!imageInfo.isProcessing()) {
                    loadImage(imageInfo.getFile());
                }
            } else {
                removeView.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.ic_take_picture);
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

                    return Bitmaps.loadScaledImage(new Bitmaps.StreamProvider() {
                        @Override
                        public InputStream openStrem() throws IOException {
                            InputStream inputStream = new FileInputStream(path);
                            try {
                                return CryptoUtil.getUncryptInputStream(inputStream,
                                        mediator.getLoginManager().getSecretKey());
                            } catch (Exception e) {
                                throw new IOException(e.getMessage());
                            }
                        }
                    }, size, size);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    ImageInfo imageInfo = getImageInfo();
                    if (imageInfo != null) {
                        getImageInfo().setProcessing(false);
                    }
                    if (bitmap != null) {
                        mediator.getImageManager().cacheBitmap(path, bitmap);
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
