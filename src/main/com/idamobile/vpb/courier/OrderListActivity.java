package com.idamobile.vpb.courier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ListView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.orders.GetOrdersResponse;
import com.idamobile.vpb.courier.presenters.CourierNamePresenter;
import com.idamobile.vpb.courier.security.SecuredActivity;
import com.idamobile.vpb.courier.widget.adapters.SectionListAdapter;
import com.idamobile.vpb.courier.widget.adapters.SimpleIndexer;
import com.idamobile.vpb.courier.widget.orders.OrderComparator;
import com.idamobile.vpb.courier.widget.orders.OrderPresenter;
import com.idamobile.vpb.courier.widget.orders.OrderSectionProvider;

@EActivity(value = R.layout.order_list)
public class OrderListActivity extends SecuredActivity {

    @ViewById(R.id.orders_list) ListView orderList;
    private SectionListAdapter<Order> ordersAdapter;

    private BroadcastReceiver ordersWatcher = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshOrders();
        }
    };

    public OrderListActivity() {
        setShouldFinishIfNotLoggedIn();
        setShouldAttachNotAuthorizedListener();
    }

    @AfterViews
    void setup() {
        CourierNamePresenter.attach(this);

        ordersAdapter = new SectionListAdapter<Order>() {
            @Override
            protected View createNormalView(Order item, ViewGroup parent, LayoutInflater inflater) {
                OrderPresenter presenter = new OrderPresenter(parent, getMediator());
                return presenter.getView();
            }

            @Override
            protected void bindNormalView(View view, Order item) {
                OrderPresenter presenter = (OrderPresenter) view.getTag();
                presenter.setOrder(item);
            }
        };
        ordersAdapter.setItemsComparator(new OrderComparator());
        ordersAdapter.setSectionIndexer(new SimpleIndexer<Order>(new SimpleIndexer.SectionRetriever<Order>() {
            private OrderSectionProvider orderSectionProvider = new OrderSectionProvider(OrderListActivity.this);

            @Override
            public Object getSectionFrom(Order item) {
                return orderSectionProvider.getOrderSection(item);
            }
        }, ordersAdapter));

        orderList.setAdapter(ordersAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMediator().getOrdersManager().registerForOrders(this, ordersWatcher);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ordersWatcher);
    }

    private void refreshOrders() {
        ordersAdapter.replaceAll(getMediator().getOrdersManager().getOrders());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_list_menu, menu);
        super.onCreateOptionsMenu(menu);
        getRefreshButtonController().setRefreshItem(menu.findItem(R.id.refresh));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_images:
                uploadImages();
                return true;

            case R.id.logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefreshClicked() {
        DataHolder<GetOrdersResponse> holder = getMediator().getCache().getHolder(GetOrdersResponse.class);
        if (!holder.isLoading()) {
            showRefreshProgressForHolders(holder);
            Courier courier = getMediator().getLoginManager().getCourier();
            if (courier != null) {
                getMediator().getOrdersManager().requestOrders(courier.getId());
            }
        }
    }

    @Override
    public void onRefreshFinishedWithErrors() {
        DataHolder<GetOrdersResponse> holder = getMediator().getCache().getHolder(GetOrdersResponse.class);
        getResultCodeConverter().showToast(holder.getLastErrorCode());
    }

    private void uploadImages() {

    }

    private void logout() {
        getMediator().getLoginManager().logout();
        getNavigationController().processSignOut();
    }

}