package com.idamobile.vpb.courier.network;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestWatcherCallbacks<T> implements LoaderCallbacks<ResponseDTO<T>> {

    public interface RequestListener<T> {
        public abstract void onStarted(Request<T> request);

        public abstract void onSuccess(Request<T> request, ResponseDTO<T> result);

        public abstract void onError(Request<T> request, ResponseDTO<T> result);

        public abstract void onFinished(Request<T> request);

    }

    public static class SimpleRequestListener<T> implements RequestListener<T> {
        @Override
        public void onStarted(Request<T> request) {
        }

        @Override
        public void onSuccess(Request<T> request, ResponseDTO<T> result) {

        }

        @Override
        public void onError(Request<T> request, ResponseDTO<T> result) {

        }

        @Override
        public void onFinished(Request<T> request) {

        }
    }

    private LoaderManager loaderManager;
    private Request<T> request;
    private Context context;
    private Set<RequestListener<T>> listeners;
    private Handler handler;

    public RequestWatcherCallbacks(FragmentActivity fragmentActivity) {
        this(fragmentActivity, fragmentActivity.getSupportLoaderManager());
    }

    public RequestWatcherCallbacks(Context context, LoaderManager loaderManager) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.listeners = new HashSet<RequestListener<T>>();
        this.handler = new Handler();
    }

    public RequestWatcherCallbacks<T> registerListener(
            RequestListener<T> listener) {
        listeners.add(listener);
        return this;
    }

    public void unregisterListener(
            RequestListener<T> listener) {
        listeners.remove(listener);
    }

    public void execute(Request<T> request) {
        int loaderId = getLoaderId(request);
        execute(request, loaderId);
    }

    public int getLoaderId(Request<T> request) {
        int val = request.getRequestUuid().hashCode();
        if (val == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        }
        return Math.abs(val);
    }

    public void execute(Request<T> request, int loaderId) {
        this.request = request;
        loaderManager.restartLoader(loaderId, null, this);
    }

    public void cancel(boolean interrupt) {
        if (request != null) {
            RequestService.cancel(context, request, interrupt);
        }
    }

    public void getResultOrExecute(Request<T> request) {
        int loaderId = getLoaderId(request);
        getResultOrExecute(request, loaderId);
    }

    public void getResultOrExecute(Request<T> request, int loaderId) {
        this.request = request;
        loaderManager.initLoader(loaderId, null, this);
    }

    @Override
    public Loader<ResponseDTO<T>> onCreateLoader(int id, Bundle args) {
        onStarted();
        return new RequestWatcher<T>(context, request);
    }

    @Override
    public void onLoadFinished(Loader<ResponseDTO<T>> loader, ResponseDTO<T> result) {
        if (result == null || result.getResultCode() != ResponseDTO.ResultCode.SUCCESS) {
            onError(result);
        } else {
            onSuccess(result);
        }
        onFinished();
    }

    @Override
    public void onLoaderReset(Loader<ResponseDTO<T>> loader) {
        onFinished();
    }

    private void onStarted() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<RequestListener<T>> listeners = new ArrayList<RequestListener<T>>(
                        RequestWatcherCallbacks.this.listeners);
                for (RequestListener<T> listener : listeners) {
                    listener.onStarted(request);
                }
            }
        });
    }

    private void onFinished() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<RequestListener<T>> listeners = new ArrayList<RequestListener<T>>(
                        RequestWatcherCallbacks.this.listeners);
                for (RequestListener<T> listener : listeners) {
                    listener.onFinished(request);
                }
            }
        });
    }

    private void onSuccess(final ResponseDTO<T> result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<RequestListener<T>> listeners = new ArrayList<RequestListener<T>>(
                        RequestWatcherCallbacks.this.listeners);
                for (RequestListener<T> listener : listeners) {
                    listener.onSuccess(request, result);
                }
            }
        });
    }

    private void onError(final ResponseDTO<T> result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                List<RequestListener<T>> listeners = new ArrayList<RequestListener<T>>(
                        RequestWatcherCallbacks.this.listeners);
                for (RequestListener<T> listener : listeners) {
                    listener.onError(request, result);
                }
            }
        });
    }
}
