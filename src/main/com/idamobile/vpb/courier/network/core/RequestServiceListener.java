package com.idamobile.vpb.courier.network.core;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import com.idamobile.vpb.courier.util.Logger;

public abstract class RequestServiceListener<T> {

    private static final String TAG = RequestServiceListener.class.getSimpleName();

    private ResultReceiver resultReceiver;
    private Context context;

    public RequestServiceListener(Context context) {
        this.context = context;
        this.resultReceiver = createReceiver();
    }

    private ResultReceiver createReceiver() {
        return new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                switch (resultCode) {
                case RequestService.RESULT_FINISHED:
                    Logger.debug(TAG, "received result from service: RESULT_FINISHED");
                    @SuppressWarnings("unchecked")
                    ResponseDTO<T> response = (ResponseDTO<T>) resultData
                            .getSerializable(RequestService.RESULT_EXTRA_RESPONSE_DTO);
                    onFinished(response);
                    break;
                case RequestService.RESULT_CANCELLED:
                    Logger.debug(TAG, "received result from service: RESULT_CANCELLED");
                    onCancelled();
                    break;
                default:
                    Logger.warn(TAG, "received unknown result from service: " + resultCode);
                    break;
                }
            }
        };
    }

    protected abstract void onCancelled();

    protected abstract void onFinished(ResponseDTO<T> response);

    public void executeWithListener(Request<T> request) {
        RequestService.execute(context, request, resultReceiver);
    }
}