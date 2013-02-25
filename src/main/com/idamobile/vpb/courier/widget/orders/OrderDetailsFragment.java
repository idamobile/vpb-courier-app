package com.idamobile.vpb.courier.widget.orders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.ProtoMapEntry;
import com.idamobile.vpb.courier.navigation.ExtrasBuilder;

@EFragment(value = R.layout.order_details_fragment)
public class OrderDetailsFragment extends Fragment {
    @ViewById(R.id.client_name) TextView nameView;
    @ViewById(R.id.client_status) TextView statusView;
    @ViewById(R.id.client_order_type) TextView orderTypeView;
    @ViewById(R.id.client_address) TextView addressView;
    @ViewById(R.id.client_metro) TextView metroView;
    @ViewById(R.id.navigate_button) View navigateButton;
    @ViewById(R.id.client_phone) TextView phoneView;
    @ViewById(R.id.call_button) View callButton;
    @ViewById(R.id.client_time) TextView timeView;
    @ViewById(R.id.additional_params_section) ViewGroup additionalParamsSection;
    @ViewById(R.id.client_additional) TextView additionalParamsView;

    private BroadcastReceiver ordersWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshOrder();
            refreshContent();
        }
    };

    private Order order;
    private OrderTimeFormatter orderTimeFormatter;
    private OrderActions orderActions;
    private ApplicationMediator mediator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderTimeFormatter = new OrderTimeFormatter(getActivity());
        orderActions = new OrderActions(getActivity());
        mediator = CoreApplication.getMediator(getActivity());
        restoreOrder(savedInstanceState);
    }

    private void restoreOrder(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int orderId = savedInstanceState.getInt(ExtrasBuilder.EXTRA_ORDER_ID, -1);
            if (orderId >= 0) {
                order = mediator.getOrdersManager().getOrder(orderId);
            }
        }
    }

    private void saveOrder(Bundle outState) {
        if (order != null) {
            outState.putInt(ExtrasBuilder.EXTRA_ORDER_ID, order.getId());
        }
    }

    private void refreshOrder() {
        if (order != null) {
            order = mediator.getOrdersManager().getOrder(order.getId());
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        if (this.order != order) {
            this.order = order;
            refreshContent();
        }
    }

    private void refreshContent() {
        if (order != null) {
            getView().setVisibility(View.VISIBLE);

            getActivity().setTitle(
                    getActivity().getString(R.string.order_details_activity_label_format, order.getId()));
            nameView.setText(order.getFullName());
            CharSequence orderTime = orderTimeFormatter.formatSimpleOrderTime(order);
            switch (order.getStatus()) {
                case STATUS_NEW:
                    statusView.setText(getActivity().getString(R.string.order_status_new_format, orderTime));
                    break;
                case STATUS_DOCUMENTS_SUBMITTED:
                    statusView.setText(R.string.order_status_submitted);
                    break;
                case STATUS_DOCUMENTS_NOT_SUBMITTED:
                    statusView.setText(R.string.order_status_not_submitted);
                    break;
                default:
                    throw new IllegalStateException("Unknown order status: " + order.getStatus());
            }
            orderTypeView.setText(order.getOrderType());
            addressView.setText(order.getClientAddress());
            metroView.setText(order.getSubway());
            navigateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderActions.navigateToClient(order);
                }
            });
            phoneView.setText(order.getClientPhone());
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderActions.callClient(order);
                }
            });
            timeView.setText(orderTime);
            if (order.getAttributes() == null || order.getAttributes().isEmpty()) {
                additionalParamsSection.setVisibility(View.GONE);
            } else {
                additionalParamsSection.setVisibility(View.VISIBLE);
                StringBuilder builder = new StringBuilder();
                for (ProtoMapEntry entry : order.getAttributes().getEntries()) {
                    if (!TextUtils.isEmpty(entry.getValue())) {
                        if (builder.length() > 0) {
                            builder.append("\n");
                        }
                        builder.append(entry.getKey()).append(": ").append(entry.getValue());
                    }
                }
                additionalParamsView.setText(builder);
            }
        } else {
            getActivity().setTitle(getActivity().getString(R.string.order_not_found));
            getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContent();
        mediator.getOrdersManager().registerForOrders(getActivity(), ordersWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(ordersWatcher);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveOrder(outState);
    }
}
