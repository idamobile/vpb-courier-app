package com.idamobile.vpb.courier.widget.courier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.controllers.AwardManager;
import com.idamobile.vpb.courier.model.Courier;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.network.login.LoginResponse;

import java.text.NumberFormat;
import java.util.List;

public class CourierInfoViewPresenter {

    public static final int AWARD_OFFSET = 6;
    public static final int AWARDS_IN_A_ROW = 5;

    private FragmentActivity activity;
    private ApplicationMediator mediator;
    private ListView listView;

    private View view;
    private TextView courierNameView;
    private ImageView awardsImageView;
    private TextView completedTotalView;
    private TextView completedTodayView;

    private boolean registered;
    private BroadcastReceiver refreshListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    private NumberFormat numberFormat;
    private int ordersCount;
    private Drawable medalsDrawable;

    private AwardManager.MedalDispenser medalDispenser;

    public CourierInfoViewPresenter(FragmentActivity activity, ListView listView) {
        this.activity = activity;
        this.listView = listView;
        this.mediator = CoreApplication.getMediator(activity);
        this.medalDispenser = mediator.getAwardManager().getMedalDispenser();

        this.numberFormat = NumberFormat.getIntegerInstance();

        createView();
    }

    private void createView() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        view = inflater.inflate(R.layout.courier_info_view, null);

        courierNameView = (TextView) view.findViewById(R.id.courier_name);
        awardsImageView = (ImageView) view.findViewById(R.id.awards_image);
        completedTotalView = (TextView) view.findViewById(R.id.courier_completed_orders);
        completedTodayView = (TextView) view.findViewById(R.id.courier_completed_orders_today);
    }

    public View getView() {
        return view;
    }

    public void resume() {
        if (!registered) {
            registered = true;
            mediator.getLoginManager().registerForLogin(activity, refreshListener);
            mediator.getOrdersManager().registerForOrders(activity, refreshListener);
        }
        refresh();
    }

    public void pause() {
        if (registered) {
            activity.unregisterReceiver(refreshListener);
            registered = false;
        }
    }

    private void refresh() {
        LoginResponse loginResponse = mediator.getCache().getHolder(LoginResponse.class).get();
        Courier courier = loginResponse != null ? loginResponse.getCourierInfo() : null;
        List<Order> orders = mediator.getOrdersManager().getOrders();
        if (courier != null && !orders.isEmpty()) {
            view.setVisibility(View.VISIBLE);
            courierNameView.setText(courier.getFirstName() + " " + courier.getLastName());

            int totalCount = mediator.getOrdersManager().getTotalCompletedOrders();
            completedTotalView.setText(numberFormat.format(totalCount));
            int todayCount = mediator.getOrdersManager().getCompletedTodayOrfersCount();
            completedTodayView.setText(numberFormat.format(todayCount));

            if (totalCount != this.ordersCount) {
                this.ordersCount = totalCount;
                medalsDrawable = createMedals(totalCount);
            }
            awardsImageView.setImageDrawable(medalsDrawable);
        } else {
            view.setVisibility(View.GONE);
        }

        refreshListView();
    }

    private void refreshListView() {
        ListAdapter adapter = listView.getAdapter();
        if (adapter != null) {
            if (adapter instanceof WrapperListAdapter) {
                adapter = ((WrapperListAdapter) adapter).getWrappedAdapter();
            }
            if (adapter instanceof BaseAdapter) {
                ((BaseAdapter) adapter).notifyDataSetChanged();
            }
        }
    }

    private Drawable createMedals(int ordersCount) {
        List<AwardManager.Medal> medals = medalDispenser.getMedals(ordersCount);
        if (medals.isEmpty()) {
            return null;
        }
        int MEDAL_SIZE = medals.get(0).getMedalBitmap().getWidth();

        int rowsCount = (medals.size() / AWARDS_IN_A_ROW);
        if (medals.size() != rowsCount * AWARDS_IN_A_ROW) {
            rowsCount++;
        }

        Bitmap result = Bitmap.createBitmap(AWARDS_IN_A_ROW * MEDAL_SIZE + (AWARDS_IN_A_ROW - 1) * AWARD_OFFSET,
                MEDAL_SIZE * rowsCount + AWARD_OFFSET * (rowsCount - 1), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        for (int row = 0; row < rowsCount; row++) {
            int countInARow = Math.min(medals.size(), AWARDS_IN_A_ROW);
            int offsetLeft = result.getWidth() - countInARow * MEDAL_SIZE - (countInARow - 1) * AWARD_OFFSET;
            for (int i = 0; i < countInARow; i++) {
                AwardManager.Medal medal = medals.remove(0);
                canvas.drawBitmap(medal.getMedalBitmap(), offsetLeft + i * (AWARD_OFFSET + MEDAL_SIZE),
                        row * (MEDAL_SIZE + AWARD_OFFSET), null);
            }
        }

        return new BitmapDrawable(activity.getResources(), result);
    }
}
