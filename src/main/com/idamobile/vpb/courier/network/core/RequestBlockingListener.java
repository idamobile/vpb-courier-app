package com.idamobile.vpb.courier.network.core;

import android.content.Context;
import com.idamobile.vpb.courier.util.Logger;

public class RequestBlockingListener<T> {

	private static final String TAG = RequestBlockingListener.class.getSimpleName();
	
	private RequestServiceListener<T> serviceListener;
	private ResponseDTO<T> result;
	private boolean cancelled;
	private boolean interrupted;
	private Context context;

    private boolean executing;
    private volatile boolean finished = false;
	private final Object locker = new Object();
	
	public RequestBlockingListener(Context context) {
		this.context = context;
		this.serviceListener = createServiceListener();
	}

	private RequestServiceListener<T> createServiceListener() {
		return new RequestServiceListener<T>(context) {
			@Override
			protected void onCancelled() {
				cancelled = true;
				reportFinish();
			}

			@Override
			protected void onFinished(ResponseDTO<T> response) {
				result = response; 
				reportFinish();
			}
		};
	}

	private void reportFinish() {
		synchronized (locker) {
            finished = true;
            executing = false;
			locker.notifyAll();
		}
	}

    public boolean isFinished() {
        return finished;
    }

    public boolean isExecuting() {
        return executing;
    }

    public ResponseDTO<T> attach() {
        synchronized (locker) {
            try {
                Logger.debug(TAG, "waiting for response...");
                while (!finished) {
                    locker.wait();
                }
                Logger.debug(TAG, "response received");
            } catch (InterruptedException e) {
                interrupted = true;
                Logger.warn(TAG, "execution is interrupted", e);
            }
        }
        if (cancelled) {
            result = ResponseDTO.newFailureResponse(ResponseDTO.ResultCode.CANCELLED, "Request is cancelled");
        }
        return result;
    }

    public ResponseDTO<T> execute(Request<T> request) {
		serviceListener.executeWithListener(request);
		synchronized (locker) {
            finished = false;
            executing = true;
		}
        return attach();
	}

	public ResponseDTO<T> getResult() {
		return result;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public boolean isInterrupted() {
		return interrupted;
	}

	public void cancel(Request<T> request, boolean interrupt) {
        RequestService.cancel(context, request, interrupt);
	}
}
