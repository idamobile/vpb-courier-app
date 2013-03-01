package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.network.core.DefaultLoaderCallback;
import com.idamobile.vpb.courier.network.core.Request;

public class GetOrdersResponseCallback extends DefaultLoaderCallback<GetOrdersResponse> {

    public GetOrdersResponseCallback() {
        super(GetOrdersResponse.class);
    }

    @Override
    protected void onSuccess(Request<GetOrdersResponse> request, GetOrdersResponse data, ApplicationMediator mediator) {
        super.onSuccess(request, data, mediator);
        mediator.getImageManager().refreshImages();
    }
}
