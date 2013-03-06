package com.idamobile.vpb.courier.network.images;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.network.core.RequestGroup;
import com.idamobile.vpb.courier.network.core.ResponseDTO;
import com.idamobile.vpb.courier.widget.orders.images.ImagesUploadProgressNotifier;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

public class UploadImagesRequstGroup extends RequestGroup {

    private transient ImagesUploadProgressNotifier progressNotifier;

    public UploadImagesRequstGroup() {
        setExecutionPolicy(new UploadImageExecutionPolicy());
    }

    @Override
    public ResponseDTO<ModelCollection> execute(ApplicationMediator mediator, HttpClient httpClient, HttpContext httpContext) {
        progressNotifier = mediator.getProgressNotifier();
        progressNotifier.register();
        try {
            return super.execute(mediator, httpClient, httpContext);
        } finally {
            progressNotifier.cancel();
        }
    }

    @Override
    public void cancel() {
        if (progressNotifier != null) {
            progressNotifier.cancel();
        }
        super.cancel();
    }
}
