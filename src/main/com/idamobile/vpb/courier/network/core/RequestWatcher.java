package com.idamobile.vpb.courier.network.core;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class RequestWatcher<T> extends AsyncTaskLoader<ResponseDTO<T>> {

    private Request<T> request;
    private ResponseDTO<T> response;
    private RequestBlockingListener<T> blockingListener;
    private boolean shouldCancelOnReset;
    private boolean interruptOnCancel;

    public RequestWatcher(Context context, Request<T> request) {
        super(context);
        this.request = request;
        this.blockingListener = new RequestBlockingListener<T>(context.getApplicationContext());
    }

    public void setShouldCancelOnReset(boolean shouldCancelOnReset) {
        this.shouldCancelOnReset = shouldCancelOnReset;
    }

    public void setInterruptOnCancel(boolean interruptOnCancel) {
        this.interruptOnCancel = interruptOnCancel;
    }

    @Override
    public ResponseDTO<T> loadInBackground() {
        if (request != null) {
            if (blockingListener.isExecuting()) {
                return blockingListener.attach();
            } else if (blockingListener.isFinished()) {
                return blockingListener.getResult();
            } else {
                return blockingListener.execute(request);
            }
        } else {
            return ResponseDTO.newFailureResponse(ResponseDTO.ResultCode.CANCELLED, "canceled");
        }
    }

    @Override
    protected void onStartLoading() {
        if (response != null) {
            deliverResult(response);
        }
        if (takeContentChanged() || response == null) {
            forceLoad();
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(ResponseDTO<T> result) {
        if (isReset()) {
            return;
        }

        response = result;

        if (isStarted()) {
            super.deliverResult(response);
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (shouldCancelOnReset) {
            blockingListener.cancel(request, interruptOnCancel);
        }
    }

}
