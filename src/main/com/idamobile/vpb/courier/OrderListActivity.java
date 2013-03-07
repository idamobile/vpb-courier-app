package com.idamobile.vpb.courier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.idamobile.vpb.courier.controllers.OrdersManager;
import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.navigation.ExtrasBuilder;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.orders.GetOrdersResponse;
import com.idamobile.vpb.courier.presenters.CourierNamePresenter;
import com.idamobile.vpb.courier.security.SecuredActivity;
import com.idamobile.vpb.courier.util.Versions;
import com.idamobile.vpb.courier.widget.adapters.SectionListAdapter;
import com.idamobile.vpb.courier.widget.adapters.SimpleIndexer;
import com.idamobile.vpb.courier.widget.orders.*;
import com.idamobile.vpb.courier.widget.orders.images.OrdersImageUploader;

@EActivity(value = R.layout.order_list_activity)
public class OrderListActivity extends SecuredActivity {

    @ViewById(R.id.orders_list) ListView orderList;
    @ViewById(R.id.progress) View progress;
    @ViewById(android.R.id.empty) View emptyView;

    private SectionListAdapter<Order> ordersAdapter;

    private OrdersImageUploader imageUploader;

    private ActionMode orderActionMode;
    private OrderActionModeCallback orderActionModeCallback;
    private OrderActions orderActions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderActions = new OrderActions(this);

        imageUploader = new OrdersImageUploader(this, savedInstanceState);
        imageUploader.setImageStatusChangedListener(new OrdersImageUploader.OnImageStatusChangedListener() {
            @Override
            public void onImagesChanged() {
                refreshOrders();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        imageUploader.saveState(outState);
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
                presenter.setDividerVisible(!ordersAdapter.isLastInSection(ordersAdapter.getIndexOf(item)));
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

        orderList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = orderList.getItemAtPosition(position);
                if (item instanceof Order) {
                    return createOrdersActionMode((Order) item);
                } else {
                    return false;
                }
            }
        });
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = ordersAdapter.getItem(position);
                if (item instanceof Order) {
                    int orderId = ((Order) item).getId();
                    Bundle extras = ExtrasBuilder.orderDetailsBundle(orderId);
                    getNavigationController().getOrderDetails().start(extras);
                }
            }
        });
        orderList.setEmptyView(emptyView);

        if (!Versions.hasHoneycombApi()) {
            registerForContextMenu(orderList);
        }
    }

    private boolean createOrdersActionMode(Order order) {
        if (Versions.isApiLevelAvailable(11)) {
            if (orderActionMode != null) {
                orderActionMode.finish();
            }
            if (orderActionModeCallback == null) {
                orderActionModeCallback = new OrderActionModeCallback(orderActions, order) {
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        super.onDestroyActionMode(mode);
                        orderActionMode = null;
                    }
                };
            }
            orderActionModeCallback.setOrder(order);
            orderActionMode = startActionMode(orderActionModeCallback);
            orderActionMode.setTitle(order.getFullName());
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMediator().getOrdersManager().registerForOrders(this, ordersWatcher);
        refreshOrders();
        imageUploader.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(ordersWatcher);
        imageUploader.onPause();
    }

    private void refreshOrders() {
        OrdersManager ordersManager = getMediator().getOrdersManager();
        DataHolder<GetOrdersResponse> holder = ordersManager.getOrdersHolder();
        if (holder.isEmpty()) {
            progress.setVisibility(View.VISIBLE);
            orderList.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            orderList.setVisibility(View.VISIBLE);
            ordersAdapter.replaceAll(ordersManager.getOrders());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.order_item_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Object itemObj = orderList.getItemAtPosition(info.position);
        switch (item.getItemId()) {
            case R.id.call_item:
                if (itemObj instanceof Order) {
                    orderActions.callClient((Order) itemObj);
                }
                return true;
            case R.id.navigate_item:
                if (itemObj instanceof Order) {
                    orderActions.navigateToClient((Order) itemObj);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        imageUploader.upload();
    }

    private void logout() {
        getMediator().getLoginManager().logout();
        getNavigationController().processSignOut();
    }

}