package com.idamobile.vpb.courier.network.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Pair;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.CoreApplication;
import com.idamobile.vpb.courier.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class RequestService extends Service {

    private static final String TAG = RequestService.class.getSimpleName();

    public static final String EXTRA_SERIALIZABLE_REQUEST = "_serializable_request_";
    public static final String EXTRA_CANCEL = "_cancel_";
    public static final String EXTRA_INTERRUPT = "_interrupt_";
    public static final String EXTRA_RESULT_RECEIVER = "_result_receiver_";

    public static final int RESULT_FINISHED = 1;
    public static final int RESULT_CANCELLED = 2;
    public static final String RESULT_EXTRA_RESPONSE_DTO = "_result_response_dto_";

    private boolean redelivery;

    private NetworkManager networkManager;

    private Map<String, Pair<Request<?>, ResultReceiver>> executingRequests = new HashMap<String, Pair<Request<?>, ResultReceiver>>();

    @SuppressWarnings("rawtypes")
    private NetworkManager.RequestListener requestListener = new NetworkManager.RequestListener() {
        @Override
        public void onFinished(Request request, ResponseDTO responseDTO) {
            Logger.debug(TAG, "Request " + request.getRequestUuid() + " is finished");
            finishRequest(request, RESULT_FINISHED, wrapResponseIntoBundle(responseDTO));
        }

        @Override
        public void onCancelled(Request request) {
            Logger.debug(TAG, "Request " + request.getRequestUuid() + " is cancelled");
            finishRequest(request, RESULT_CANCELLED, null);
        }

        private void finishRequest(Request request, int code, Bundle result) {
            Pair<Request<?>, ResultReceiver> pair = executingRequests.remove(request.getRequestUuid());
            if (pair != null && pair.second != null) {
                pair.second.send(code, result);
            }
            tryToStop();
        }

        private Bundle wrapResponseIntoBundle(ResponseDTO responseDTO) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(RESULT_EXTRA_RESPONSE_DTO, responseDTO);
            return bundle;
        }
    };

    /**
     * Control redelivery of intents. If called with true, {@link #onStartCommand(android.content.Intent, int, int)} will return
     * {@link android.app.Service#START_REDELIVER_INTENT} instead of {@link android.app.Service#START_NOT_STICKY}, so that if this service's
     * process is called while it is executing the Intent in {@link #onHandleIntent(android.content.Intent)}, then when later restarted
     * the same Intent will be re-delivered to it, to retry its execution.
     */
    public void setIntentRedelivery(boolean enabled) {
        redelivery = enabled;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.debug(TAG, "execution service is created");
        ApplicationMediator mediator = ((CoreApplication) getApplication()).getMediator();
        networkManager = mediator.getNetworkManager();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return redelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        Request<?> request = getRequestOrThrowIfNotExists(intent);
        if (hasCancelExtra(intent)) {
            cancel(request, shouldInterrupt(intent));
        } else {
            execute(request, getResultReceiver(intent));
        }
    }

    protected void tryToStop() {
        if (executingRequests.isEmpty()) {
            Logger.debug(TAG, "execution queue is empty, stopping service...");
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Logger.debug(TAG, "service is destroyed");
        super.onDestroy();
    }

    private boolean hasCancelExtra(Intent intent) {
        return intent.getBooleanExtra(EXTRA_CANCEL, false);
    }

    private boolean shouldInterrupt(Intent intent) {
        return intent.getBooleanExtra(EXTRA_INTERRUPT, false);
    }

    private Request<?> getRequestOrThrowIfNotExists(Intent intent) {
        Request<?> request =
                (Request<?>) intent.getSerializableExtra(EXTRA_SERIALIZABLE_REQUEST);
        if (request == null) {
            throw new IllegalArgumentException("intent without request");
        }
        return request;
    }

    private ResultReceiver getResultReceiver(Intent intent) {
        return intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
    }

    @SuppressWarnings("unchecked")
    protected void execute(Request<?> request, ResultReceiver receiver) {
        if (networkManager.execute(request, requestListener)) {
            Logger.debug(TAG, "executing new request " + request.getRequestUuid());
            executingRequests.put(request.getRequestUuid(), new Pair<Request<?>, ResultReceiver>(request, receiver));
        } else {
            Logger.warn(TAG, "already executing request " + request.getRequestUuid());
        }
    }

    protected void cancel(Request<?> request, boolean interrupt) {
        Logger.debug(TAG, "cancelling request " + request.getRequestUuid());
        Pair<Request<?>, ResultReceiver> pair = executingRequests.get(request.getRequestUuid());
        if (pair != null) {
            pair.first.cancel(interrupt);
        }
        if (!networkManager.cancel(request, interrupt)) {
            executingRequests.remove(request.getRequestUuid());
        }
    }

    public static void execute(Context context, Request<?> request) {
        execute(context, request, null);
    }

    public static void execute(Context context, Request<?> request, ResultReceiver receiver) {
        context.startService(formatBaseIntent(context, request, receiver));
    }

    public static void cancel(Context context, Request<?> request, boolean interrupt) {
        Intent intent = formatBaseIntent(context, request, null);
        intent.putExtra(EXTRA_CANCEL, true);
        intent.putExtra(EXTRA_INTERRUPT, interrupt);
        context.startService(intent);
    }

    private static Intent formatBaseIntent(Context context, Request<?> request, ResultReceiver receiver) {
        Intent intent = new Intent(context, RequestService.class);
        intent.putExtra(EXTRA_SERIALIZABLE_REQUEST, request);
        intent.putExtra(EXTRA_RESULT_RECEIVER, receiver);
        return intent;
    }

}
