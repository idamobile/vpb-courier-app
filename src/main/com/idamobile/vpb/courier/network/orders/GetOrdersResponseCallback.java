package com.idamobile.vpb.courier.network.orders;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.network.core.LoaderCallback;
import com.idamobile.vpb.courier.network.core.Request;

public class GetOrdersResponseCallback extends LoaderCallback<GetOrdersResponse> {

    public GetOrdersResponseCallback() {
        super(GetOrdersResponse.class);
    }

    @Override
    protected void onSuccess(Request<GetOrdersResponse> request, GetOrdersResponse data, ApplicationMediator mediator) {
        super.onSuccess(request, data, mediator);
        mediator.getImageManager().refreshImages();
    }
}