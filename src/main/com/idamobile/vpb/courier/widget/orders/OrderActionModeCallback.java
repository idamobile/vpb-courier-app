package com.idamobile.vpb.courier.widget.orders;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.util.Versions;

public class OrderActionModeCallback implements ActionMode.Callback {

    private OrderActions orderActions;
    private Order order;

    public OrderActionModeCallback(OrderActions actions, Order order) {
        this.orderActions = actions;
        this.order = order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.order_item_context_menu, menu);
        if (!Versions.isApiLevelAvailable(14)) {
            menu.findItem(R.id.call_item).setIcon(R.drawable.ic_call);
            menu.findItem(R.id.navigate_item).setIcon(R.drawable.ic_navigate);
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call_item:
                orderActions.callClient(order);
                return true;
            case R.id.navigate_item:
                orderActions.navigateToClient(order);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}
