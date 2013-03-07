package com.idamobile.vpb.courier.network.core;

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

    private final String requestKey;
    private LoaderManager loaderManager;
    private Request<T> request;
    private Context context;
    private Set<RequestListener<T>> listeners;
    private Handler handler;

    private boolean firstInit;
    private Runnable firstInitTask = new Runnable() {
        @Override
        public void run() {
            if (request != null) {
                firstInit = true;
                getResultOrExecute(request);
            }
        }
    };

    public RequestWatcherCallbacks(FragmentActivity fragmentActivity, String tag) {
        this(fragmentActivity, tag, null);
    }

    public RequestWatcherCallbacks(FragmentActivity fragmentActivity, String tag, Bundle savedInstanceState) {
        this(fragmentActivity, fragmentActivity.getSupportLoaderManager(), tag, savedInstanceState);
    }

    public RequestWatcherCallbacks(Context context, LoaderManager loaderManager, String tag, Bundle savedInstanceState) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.requestKey = "watcher-callbacks-for-" + tag;
        this.listeners = new HashSet<RequestListener<T>>();
        this.handler = new Handler();

        restoreState(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            request = (Request<T>) savedInstanceState.getSerializable(requestKey);
            if (request != null) {
                firstInit = true;
                handler.post(firstInitTask);
            }
        }
    }

    public RequestWatcherCallbacks<T> registerListener(RequestListener<T> listener) {
        listeners.add(listener);
        return this;
    }

    public void unregisterListener(RequestListener<T> listener) {
        listeners.remove(listener);
    }

    public void execute(Request<T> request) {
        int loaderId = getLoaderId(request);
        execute(request, loaderId);

        if (firstInit) {
            handler.removeCallbacks(firstInitTask);
            firstInit = false;
        }
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
        if (!firstInit) {
            onStarted();
        }
        Request<T> req = firstInit ? null : request;
        firstInit = false;
        return new RequestWatcher<T>(context, req);
    }

    @Override
    public void onLoadFinished(Loader<ResponseDTO<T>> loader, ResponseDTO<T> result) {
        firstInit = false;
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

    public void saveInstanceState(Bundle outState) {
        if (request != null) {
            outState.putSerializable(requestKey, request);
        }
    }
}
