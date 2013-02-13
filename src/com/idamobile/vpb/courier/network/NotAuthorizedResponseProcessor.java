package com.idamobile.vpb.courier.network;

import android.content.Context;
import android.content.Intent;

public class NotAuthorizedResponseProcessor implements ResponseDTOPreProcessor {

    public static final String NOT_AUTHORIZED_BROADCAST_ACTION = "com.idamobile.platform.android.core.network.NotAuthorizedResponseProcessor.NOT_AUTHORIZED_BROADCAST_ACTION";

    private Context context;

    public NotAuthorizedResponseProcessor(Context context) {
        this.context = context;
    }

    @Override
    public void processResponse(ResponseDTO<?> response) {
        if (response.getResultCode() == ResponseDTO.ResultCode.NOT_AUTHORIZED) {
            sendBroadcast();
        }
    }

    private void sendBroadcast() {
        context.sendBroadcast(new Intent(NOT_AUTHORIZED_BROADCAST_ACTION));
    }

}
