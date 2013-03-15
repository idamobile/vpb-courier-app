package com.idamobile.vpb.courier.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.model.*;
import com.idamobile.vpb.courier.network.core.DataHolder;
import com.idamobile.vpb.courier.network.core.RequestService;
import com.idamobile.vpb.courier.network.core.RequestWatcherCallbacks;
import com.idamobile.vpb.courier.network.core.ResponseDTO;
import com.idamobile.vpb.courier.network.login.LoginResponse;
import com.idamobile.vpb.courier.network.orders.*;
import com.idamobile.vpb.courier.security.crypto.CryptoStreamProvider;
import com.idamobile.vpb.courier.security.crypto.OrderNoteFilenameMapper;
import com.idamobile.vpb.courier.util.Files;
import com.idamobile.vpb.courier.util.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrdersManager {

    public static final String TAG = OrdersManager.class.getSimpleName();

    private ApplicationMediator mediator;
    private OrderNoteFilenameMapper orderNoteFilenameMapper;

    public OrdersManager(ApplicationMediator mediator) {
        this.mediator = mediator;
        this.orderNoteFilenameMapper = new OrderNoteFilenameMapper(mediator.getContext());
    }

    public void requestOrders(int courierId) {
        requestOrders(courierId, null);
    }

    public void requestOrders(int courierId,
                              RequestWatcherCallbacks<GetOrdersResponse> callback) {
        GetOrdersRequest request = new GetOrdersRequest();
        request.setCourierId(courierId);
        request.setUpdateModelCallback(new GetOrdersResponseCallback());
        if (callback != null) {
            callback.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void requestSetOrderCompleted(int orderId, boolean resident, boolean hasMarks,
                                         RequestWatcherCallbacks<UpdateOrderResponse> callbacks) {
        CompleteOrderRequest request = new CompleteOrderRequest();
        request.setUpdateModelCallback(new UpdateOrderCallback());
        request.setHasCorrections(hasMarks);
        request.setResident(resident);
        request.setOrderId(orderId);
        if (callbacks != null) {
            callbacks.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void requestSetOrderRejected(int orderId, boolean metWithClient, CancellationReason reason,
                                         RequestWatcherCallbacks<UpdateOrderResponse> callbacks) {
        CancelOrderRequest request = new CancelOrderRequest();
        request.setUpdateModelCallback(new UpdateOrderCallback());
        request.setReason(reason);
        request.setMetWithClient(metWithClient);
        request.setOrderId(orderId);
        if (callbacks != null) {
            callbacks.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void requestActivateCard(int orderId, RequestWatcherCallbacks<UpdateOrderResponse> callbacks) {
        ActivateCardRequest request = new ActivateCardRequest();
        request.setOrderId(orderId);
        request.setUpdateModelCallback(new UpdateOrderCallback());
        if (callbacks != null) {
            callbacks.execute(request);
        } else {
            RequestService.execute(mediator.getContext(), request);
        }
    }

    public void registerForOrders(Context context, BroadcastReceiver receiver) {
        context.registerReceiver(receiver, new IntentFilter(getOrdersHolder().getBroadcastAction()));
    }

    public List<Order> getOrders() {
        List<Order> result = new ArrayList<Order>();
        DataHolder<GetOrdersResponse> holder = getOrdersHolder();
        if (!holder.isEmpty()) {
            result.addAll(holder.get().getOrders());
        }
        return result;
    }

    public Order getOrder(int id) {
        List<Order> orders = getOrders();
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    public DataHolder<GetOrdersResponse> getOrdersHolder() {
        return mediator.getCache().getHolder(GetOrdersResponse.class);
    }

    public OrderNote loadNoteFor(int orderId) {
        DataHolder<OrderNote> noteDataHolder = getNoteDataHolder(orderId);
        ResponseDTO.ResultCode resultCode = ResponseDTO.ResultCode.SUCCESS;
        File noteFile = getOrderNoteFile(orderId);
        OrderNote orderNote = new OrderNote(orderId);
        if (noteFile.exists()) {
            try {
                Files.InputStreamProvider provider = new CryptoStreamProvider(mediator.getLoginManager(), noteFile);
                orderNote.setNote(Files.readAllLines(provider));
            } catch (IOException e) {
                Logger.debug(TAG, "unable to load note", e);
                resultCode = ResponseDTO.ResultCode.UNKNOWN_ERROR;
            }
        }
        noteDataHolder.set(orderNote);
        noteDataHolder.markLoaded(resultCode);
        return orderNote;
    }

    public boolean saveNote(OrderNote note) {
        DataHolder<OrderNote> noteDataHolder = getNoteDataHolder(note.getOrderId());
        noteDataHolder.set(note);

        File noteFile = getOrderNoteFile(note.getOrderId());
        Files.OutputStreamProvider provider = new CryptoStreamProvider(mediator.getLoginManager(), noteFile);
        try {
            Files.saveAllLines(provider, note.getNote());
            return true;
        } catch (IOException e) {
            Logger.debug(TAG, "unable to save note", e);
            return false;
        }
    }

    public DataHolder<OrderNote> getNoteDataHolder(int orderId) {
        return mediator.getCache().getHolder(OrderNote.class, "order-id-" + orderId);
    }

    private File getOrderNoteFile(int orderId) {
        Courier courier = mediator.getLoginManager().getCourier();
        return orderNoteFilenameMapper.mapToFileName(courier.getId(), orderId);
    }

    public int getTotalCompletedOrders() {
        LoginResponse loginResponse = mediator.getCache().getHolder(LoginResponse.class).get();
        Courier courier = loginResponse != null ? loginResponse.getCourierInfo() : null;
        int totalCount = courier != null ? courier.getCompletedOrders() : 0;
        long lastCourierInfoUpdateTime = mediator.getLoginManager().getLastLoginTime();
        for (Order order : getOrders()) {
            if (order.getStatus() == OrderStatus.STATUS_ACTIVATED
                    || order.getStatus() == OrderStatus.STATUS_DOCUMENTS_SUBMITTED) {
                if (order.getStatusUpdateTime() > lastCourierInfoUpdateTime) {
                    totalCount++;
                }
            }
        }
        return totalCount;
    }

    public int getCompletedTodayOrfersCount() {
        int todayCount = 0;
        for (Order order : getOrders()) {
            if (order.getStatus() == OrderStatus.STATUS_ACTIVATED
                    || order.getStatus() == OrderStatus.STATUS_DOCUMENTS_SUBMITTED) {
                todayCount++;
            }
        }
        return todayCount;
    }
}
