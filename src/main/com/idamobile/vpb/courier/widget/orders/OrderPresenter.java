package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.images.OrderImages;
import com.idamobile.vpb.courier.util.Intents;

public class OrderPresenter {

    private View view;

    private TextView addressView;
    private TextView timeView;
    private TextView metroView;
    private TextView nameView;
    private TextView uploadedImagesView;

    private ViewGroup buttonsGroup;
    private View callButton;
    private View metroButton;
    private View navigateButton;

    private Order order;
    private OrderTimeFormatter timeFormatter;
    private UploadedImagesMessageFormatter uploadedImagesFormatter;

    private Context context;
    private ApplicationMediator mediator;

    public static OrderPresenter get(View convertView, ViewGroup parent, ApplicationMediator mediator) {
        if (convertView.getTag() == null) {
            return new OrderPresenter(parent, mediator);
        } else if  (convertView.getTag() instanceof OrderPresenter) {
            return (OrderPresenter) convertView.getTag();
        } else {
            throw new IllegalArgumentException("this view has unknown tag:" + convertView.getTag());
        }
    }

    public OrderPresenter(ViewGroup parent, ApplicationMediator mediator) {
        this.mediator = mediator;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        this.context = parent.getContext();
        this.view = inflater.inflate(R.layout.order_list_item, parent, false);
        this.timeFormatter = new OrderTimeFormatter(context);
        this.uploadedImagesFormatter = new UploadedImagesMessageFormatter(context);

        addressView = (TextView) view.findViewById(R.id.client_address);
        timeView = (TextView) view.findViewById(R.id.client_time);
        metroView = (TextView) view.findViewById(R.id.client_metro);
        nameView = (TextView) view.findViewById(R.id.client_name);
        uploadedImagesView = (TextView) view.findViewById(R.id.upload_images);

        buttonsGroup = (ViewGroup) view.findViewById(R.id.action_buttons);
        callButton = view.findViewById(R.id.call_button);
        metroButton = view.findViewById(R.id.metro_button);
        navigateButton = view.findViewById(R.id.navigate_button);

        setupButtons();

        view.setTag(this);
    }

    private void setupButtons() {
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Intents.createCallIntent(order.getClientPhone());
                if (!Intents.startActivityIfExists(intent, context)) {
                    showActivityNotFoundError(R.string.call_activity_failed);
                }
            }
        });

        metroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Intents.createUndergroundIntent(order.getSubway());
                if (!Intents.startActivityIfExists(intent, context)) {
                    showActivityNotFoundError(R.string.map_activity_failed);
                }
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Intents.createRouteIntent(order.getClientAddress());
                if (!Intents.startActivityIfExists(intent, context)) {
                    showActivityNotFoundError(R.string.map_activity_failed);
                }
            }
        });
    }

    private void showActivityNotFoundError(int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    public View getView() {
        return view;
    }

    public void setOrder(Order order) {
        this.order = order;
        refreshContent();
    }

    public void refreshContent() {
        if (order != null) {
            addressView.setText(order.getClientAddress());
            nameView.setText(order.getFullName());
            switch (order.getStatus()) {
                case STATUS_NEW:
                    uploadedImagesView.setVisibility(View.GONE);
                    metroView.setVisibility(View.VISIBLE);
                    timeView.setVisibility(View.VISIBLE);

                    addressView.setCompoundDrawables(null, null, null, null);

                    metroView.setText(order.getSubway());
                    timeView.setText(timeFormatter.formatOrderTime(order));
                    break;
                case STATUS_DOCUMENTS_SUBMITTED:
                    uploadedImagesView.setVisibility(View.VISIBLE);
                    metroView.setVisibility(View.GONE);
                    timeView.setVisibility(View.GONE);

                    addressView.setCompoundDrawables(context.getResources().getDrawable(R.drawable.status_ok),
                            null, null, null);

                    OrderImages images = mediator.getImageManager().getImages(order);
                    if (images == null) {
                        uploadedImagesView.setText(uploadedImagesFormatter.format(0, 0));
                    } else {
                        uploadedImagesView.setText(
                                uploadedImagesFormatter.format(images.getUploadedCount(), images.getCount()));
                    }
                    break;
                case STATUS_DOCUMENTS_NOT_SUBMITTED:
                    uploadedImagesView.setVisibility(View.GONE);
                    metroView.setVisibility(View.GONE);
                    timeView.setVisibility(View.GONE);

                    addressView.setCompoundDrawables(context.getResources().getDrawable(R.drawable.status_fail),
                            null, null, null);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown order status: " + order.getStatus());
            }
        } else {
            addressView.setText(null);
            metroView.setText(null);
            nameView.setText(null);
            timeView.setText(null);
            uploadedImagesView.setText(null);
        }
        metroButton.setEnabled(order != null);
        callButton.setEnabled(order != null);
        navigateButton.setEnabled(order != null);
    }

    private void refreshTime() {
        if (order != null) {
            timeView.setText(timeFormatter.formatOrderTime(order));
        } else {
            timeView.setText(null);
        }
    }
}
