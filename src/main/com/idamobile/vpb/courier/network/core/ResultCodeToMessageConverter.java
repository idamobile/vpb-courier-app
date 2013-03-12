package com.idamobile.vpb.courier.network.core;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;
import com.idamobile.vpb.courier.R;

public class ResultCodeToMessageConverter {

    private Context context;

    public ResultCodeToMessageConverter(Context context) {
        this.context = context;
    }

    public CharSequence convert(ResponseDTO.ResultCode resultCode) {
        if (resultCode == null || resultCode == ResponseDTO.ResultCode.CANCELLED) {
            return null;
        } else if (resultCode == ResponseDTO.ResultCode.SUCCESS) {
            return null;
        } else if (resultCode == ResponseDTO.ResultCode.UNKNOWN_ERROR) {
            return context.getString(R.string.response_unknown_error);
        } else if (resultCode == ResponseDTO.ResultCode.NETWORK_ERROR
                && !NetworkUtils.hasInternet(context)) {
            return context.getText(R.string.response_internet_error_msg);
        } else {
            return context.getString(R.string.response_error_msg_format, resultCode.name());
        }
    }

    public Toast showToast(ResponseDTO.ResultCode resultCode) {
        CharSequence text = convert(resultCode);
        if (!TextUtils.isEmpty(text)) {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return toast;
        } else {
            return null;
        }
    }

}
