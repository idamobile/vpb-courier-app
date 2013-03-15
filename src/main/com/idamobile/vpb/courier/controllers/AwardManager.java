package com.idamobile.vpb.courier.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.CancellationReason;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.model.OrderStatus;
import com.idamobile.vpb.courier.util.PrimitivesUtils;
import com.idamobile.vpb.courier.widget.orders.awards.AwardMessages;
import com.idamobile.vpb.courier.widget.orders.awards.AwardToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AwardManager {

    private ApplicationMediator mediator;
    private AwardMessages awardMessages;
    private MedalDispenser medalDispenser;

    public AwardManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.medalDispenser = new MedalDispenser(mediator.getContext());
        this.awardMessages = new AwardMessages(mediator.getContext(), medalDispenser);
    }

    public void onOrderCompleted() {
        if (isAllOrdersCompleted()) {
            showToast(awardMessages.getMessageForLastOrder());
        } else {
            showToast(awardMessages.getPositiveMessage(mediator.getOrdersManager().getTotalCompletedOrders()));
        }
    }

    private boolean isAllOrdersCompleted() {
        List<Order> orders = mediator.getOrdersManager().getOrders();
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.STATUS_NEW) {
                return false;
            }
        }
        return true;
    }

    public void onOrderCancelled(CancellationReason reason, boolean metWithClient) {
        showToast(awardMessages.getNegativeMessage(reason));
    }

    private void showToast(AwardMessages.Message message) {
        AwardToast.show(mediator.getContext(), message.getImageResId(), message.getMessage());
    }

    public MedalDispenser getMedalDispenser() {
        return medalDispenser;
    }

    public static class Medal implements Comparable<Medal> {
        private final int ordersCount;
        private Bitmap medalBitmap;

        private Medal(int ordersCount, int resId, Context context) {
            this.ordersCount = ordersCount;
            this.medalBitmap = ((BitmapDrawable) context.getResources().getDrawable(resId)).getBitmap();
        }

        public int getOrdersCount() {
            return ordersCount;
        }

        public Bitmap getMedalBitmap() {
            return medalBitmap;
        }

        @Override
        public int compareTo(Medal another) {
            return PrimitivesUtils.compare(another.ordersCount, ordersCount);
        }
    }

    public static class MedalDispenser {
        private List<Medal> medals;

        private MedalDispenser(Context context) {
            this.medals = new ArrayList<Medal>();

            medals.add(new Medal(10, R.drawable.medal_10, context));
            medals.add(new Medal(50, R.drawable.medal_50, context));
            medals.add(new Medal(100, R.drawable.medal_100, context));
            medals.add(new Medal(250, R.drawable.medal_250, context));
            medals.add(new Medal(500, R.drawable.medal_500, context));
            medals.add(new Medal(750, R.drawable.medal_750, context));
            medals.add(new Medal(1000, R.drawable.medal_1k, context));
            medals.add(new Medal(2500, R.drawable.medal_2500, context));
            medals.add(new Medal(5000, R.drawable.medal_5k, context));

            Collections.sort(medals);
        }

        public List<Medal> getMedals(int ordersCount) {
            List<Medal> result = new ArrayList<Medal>();
            int remainingOrders = ordersCount;
            for (Medal medal : medals) {
                int count = remainingOrders / medal.getOrdersCount();
                for (int i = 0; i < count; i++) {
                    result.add(medal);
                }
                remainingOrders -= count * medal.getOrdersCount();
            }
            return result;
        }

        public boolean isNewAchievementUnlocked(int ordersCount) {
            for (Medal medal : medals) {
                if (medal.getOrdersCount() == ordersCount) {
                    return true;
                }
            }
            return false;
        }
    }


}
