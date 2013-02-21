package com.idamobile.vpb.courier.network.login;

import android.content.Context;
import android.content.Intent;
import com.idamobile.vpb.courier.network.core.ResponseDTO;
import com.idamobile.vpb.courier.network.core.ResponseDTOPreProcessor;

public class NotAuthorizedResponseProcessor implements ResponseDTOPreProcessor {

    public static final String NOT_AUTHORIZED_BROADCAST_ACTION
            = "com.idamobile.vpb.courier.network.login.NotAuthorizedResponseProcessor.NOT_AUTHORIZED_BROADCAST_ACTION";

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