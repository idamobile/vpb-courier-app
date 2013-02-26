package com.idamobile.vpb.courier;

import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.navigation.ExtrasBuilder;
import com.idamobile.vpb.courier.security.SecuredActivity;
import com.idamobile.vpb.courier.util.Versions;
import com.idamobile.vpb.courier.widget.orders.OrderDetailsFragment;

@EActivity(value = R.layout.order_details_activity)
public class OrderDetailsActivity extends SecuredActivity {

    @FragmentById(R.id.order_details_fragment) OrderDetailsFragment orderDetailsFragment;

    public OrderDetailsActivity() {
        setShouldFinishIfNotLoggedIn();
        setShouldAttachNotAuthorizedListener();
    }

    @AfterViews
    void setup() {
        int orderId = getIntent().getIntExtra(ExtrasBuilder.EXTRA_ORDER_ID, -1);
        Order order = getMediator().getOrdersManager().getOrder(orderId);
        orderDetailsFragment.setOrder(order);

        if (Versions.hasHoneycombApi()) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
