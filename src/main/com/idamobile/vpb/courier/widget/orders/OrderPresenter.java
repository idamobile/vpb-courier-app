package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.images.OrderImages;

public class OrderPresenter {

    private View view;

    private TextView addressView;
    private TextView timeView;
    private TextView metroView;
    private TextView nameView;
    private TextView uploadedImagesView;
    private View divider;

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
        uploadedImagesView = (TextView) view.findViewById(R.id.photos_uploaded);
        divider = view.findViewById(R.id.divider);

        view.setTag(this);
    }

    public View getView() {
        return view;
    }

    public void setOrder(Order order) {
        this.order = order;
        refreshContent();
    }

    public void setDividerVisible(boolean visible) {
        divider.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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

                    setLeftCompoundDrawable(-1, addressView);

                    metroView.setText(order.getSubway());
                    timeView.setText(timeFormatter.formatOrderTime(order));

                    long curTime = System.currentTimeMillis();
                    long orderEndTime = order.getMeetTimeTo();
                    long timeToEnd = orderEndTime - curTime;
                    if (timeToEnd < 30 * 60 * 1000) {
                        Drawable hurryIcon = context.getResources().getDrawable(R.drawable.ic_hurry_2486);
                        hurryIcon.setBounds(0, 0, hurryIcon.getIntrinsicWidth(), hurryIcon.getIntrinsicHeight());
                        timeView.setCompoundDrawables(null, null, hurryIcon, null);
                    } else {
                        timeView.setCompoundDrawables(null, null, null, null);
                    }
                    break;
                case STATUS_DOCUMENTS_SUBMITTED:
                    uploadedImagesView.setVisibility(View.GONE);
                    metroView.setVisibility(View.GONE);
                    timeView.setVisibility(View.GONE);

                    setLeftCompoundDrawable(R.drawable.status_ok, addressView);
                    break;
                case STATUS_ACTIVATED:
                    uploadedImagesView.setVisibility(View.VISIBLE);
                    metroView.setVisibility(View.GONE);
                    timeView.setVisibility(View.GONE);

                    setLeftCompoundDrawable(R.drawable.status_activated, addressView);

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

                    setLeftCompoundDrawable(R.drawable.status_fail, addressView);
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
    }

    private void setLeftCompoundDrawable(int resId, TextView view) {
        if (resId > 0) {
            Drawable statusDrawable = context.getResources().getDrawable(resId);
            statusDrawable.setBounds(0, 0, statusDrawable.getIntrinsicWidth(), statusDrawable.getMinimumHeight());
            view.setCompoundDrawables(statusDrawable, null, null, null);
        } else {
            view.setCompoundDrawables(null, null, null, null);
        }
    }

    private void refreshTime() {
        if (order != null) {
            timeView.setText(timeFormatter.formatOrderTime(order));
        } else {
            timeView.setText(null);
        }
    }
}
