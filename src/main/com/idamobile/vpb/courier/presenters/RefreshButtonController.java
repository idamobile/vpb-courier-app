package com.idamobile.vpb.courier.presenters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.MenuItem;
import android.view.animation.Animation;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.util.Versions;

import java.util.HashSet;
import java.util.Set;

public class RefreshButtonController {

    public interface RefreshButtonListener {
        void onRefreshClicked();
        void onRefreshFinishedWithErrors();
    }

    private MenuItem refreshItem;

    private Set<String> queuedHolderBroadcastActionsForRefresh = new HashSet<String>();
    private DataHolder<?>[] currentRefreshingHolders;
    private BroadcastReceiver broadcastForHolders;

    private RefreshButtonListener refreshButtonListener;
    private final Activity activity;

    public RefreshButtonController(Activity activity, RefreshButtonListener refreshButtonListener) {
        this.activity = activity;
        this.refreshButtonListener = refreshButtonListener;
    }

    public void setRefreshItem(MenuItem refreshItem) {
        this.refreshItem = refreshItem;
    }

    public boolean dispatchOnOptionsItemSelected(MenuItem item) {
        if (refreshItem != null && item.getItemId() == refreshItem.getItemId()) {
            onRefreshClicked();
            return true;
        } else {
            return false;
        }
    }

    public void dispatchOnResume() {
        //after resume state of queue is unknown, so cleanup it
        queuedHolderBroadcastActionsForRefresh.clear();
        processRefreshButtonHolderStatus();
        if (currentRefreshingHolders != null) {
            registerBroadcastForRefreshButtonHolders();
        }
    }

    public void dispatchOnPause() {
        unregisterBroadcastForRefreshButtonHolders();
        hideRefreshAnimation();
    }

    private void onRefreshClicked() {
        if (refreshButtonListener != null) {
            refreshButtonListener.onRefreshClicked();
        }
    }

    private void notifyRefreshFinishedWithErrors() {
        if (refreshButtonListener != null) {
            refreshButtonListener.onRefreshFinishedWithErrors();
        }
    }

    public void showRefreshProgressForHolders(DataHolder<?> ... holders) {
        forgotAboutHolderForRefreshButton();
        this.currentRefreshingHolders = holders;
        for (DataHolder<?> holder : holders) {
            if (!holder.isLoading()) {
                queuedHolderBroadcastActionsForRefresh.add(holder.getBroadcastAction());
            }
        }
        registerBroadcastForRefreshButtonHolders();
    }

    private void unregisterBroadcastForRefreshButtonHolders() {
        if (broadcastForHolders != null) {
            activity.unregisterReceiver(broadcastForHolders);
            broadcastForHolders = null;
        }
    }

    private void registerBroadcastForRefreshButtonHolders() {
        unregisterBroadcastForRefreshButtonHolders();
        showRefreshAnimation();
        broadcastForHolders = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processRefreshButtonHolderStatus();
            }
        };
        for (DataHolder<?> holder : currentRefreshingHolders) {
            activity.registerReceiver(broadcastForHolders, new IntentFilter(holder.getBroadcastAction()));
        }
    }

    private void processRefreshButtonHolderStatus() {
        if (currentRefreshingHolders != null) {
            boolean loading = false;
            boolean hasError = false;
            for (DataHolder<?> holder : currentRefreshingHolders) {
                if (holder.isLoading()) {
                    loading = true;
                    queuedHolderBroadcastActionsForRefresh.remove(holder.getBroadcastAction());
                } else if (holder.isLastUpdateWithErrors()) {
                    hasError = true;
                }
            }
            if (!loading && queuedHolderBroadcastActionsForRefresh.isEmpty()) {
                if (hasError) {
                    notifyRefreshFinishedWithErrors();
                }
                forgotAboutHolderForRefreshButton();
            }
        }
    }

    public void forgotAboutHolderForRefreshButton() {
        unregisterBroadcastForRefreshButtonHolders();
        currentRefreshingHolders = null;
        queuedHolderBroadcastActionsForRefresh.clear();
        hideRefreshAnimation();
    }

    protected void showRefreshAnimation() {
        if (Versions.hasHoneycombApi()) {
            if (refreshItem != null && refreshItem.getActionView() == null) {
                refreshItem.setActionView(R.layout.action_bar_proress_view);
            }
        } else {
            activity.setProgressBarIndeterminateVisibility(true);
        }
    }

    protected void hideRefreshAnimation() {
        if (Versions.hasHoneycombApi()) {
            if (refreshItem != null && refreshItem.getActionView() != null) {
                Animation animation = refreshItem.getActionView().getAnimation();
                if (animation != null && !animation.hasEnded()) {
                    animation.setRepeatCount(0);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        } else {
            activity.setProgressBarIndeterminateVisibility(false);
        }
    }

}
