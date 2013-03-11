package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.util.Intents;

public class OrderActions {

    private Context context;

    public OrderActions(Context context) {
        this.context = context;
    }

    public void navigateToClient(Order order) {
        Intent intent = Intents.createRouteIntent(order.getClientAddress());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!Intents.startActivityIfExists(intent, context)) {
            showActivityNotFoundError(R.string.map_activity_failed);
        }
    }

    public void callClient(Order order) {
        Intent intent = Intents.createCallIntent(order.getClientPhone());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!Intents.startActivityIfExists(intent, context)) {
            showActivityNotFoundError(R.string.call_activity_failed);
        }
    }

    private void showActivityNotFoundError(int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }
}
