package com.idamobile.vpb.courier.network.images;

import com.idamobile.vpb.courier.network.core.RequestGroup;
import com.idamobile.vpb.courier.network.core.ResponseDTO;

public class UploadImageExecutionPolicy implements RequestGroup.ExecutionPolicy {

    @Override
    public boolean shouldContinue(ResponseDTO responseDTO) {
        if (responseDTO.isSuccess()) {
            return true;
        } else {
            switch (responseDTO.getResultCode()) {
                case UNKNOWN_ERROR:
                case CANCELLED:
                    return true;
                default:
                    return false;
            }
        }
    }

}
