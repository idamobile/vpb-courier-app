package com.idamobile.vpb.courier.widget.dialogs;

import com.idamobile.vpb.courier.network.core.Request;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.core.ResponseDTO;
import com.idamobile.vpb.courier.util.Logger;

public class DialogRequestListener<T> implements RequestWatcherCallbacks.RequestListener<T> {

	private static final String TAG = DialogRequestListener.class.getSimpleName();
	
	private DialogFactory dialogFactory;

	public DialogRequestListener(DialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	public void onStarted(Request<T> request) {
		Logger.debug(TAG, "request started, showing dialog");
		showDialog();
	}

	@Override
	public void onSuccess(Request<T> request, ResponseDTO<T> result) {
		
	}

	@Override
	public void onError(Request<T> request, ResponseDTO<T> result) {
		
	}

	@Override
	public void onFinished(Request<T> request) {
		Logger.debug(TAG, "request finished, closing dialog");
		hideDialog();
	}
	
	public void showDialog() {
		dialogFactory.showDialog();
	}

	public void hideDialog() {
		dialogFactory.hideDialog();
	}
	
}
