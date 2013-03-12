package com.idamobile.vpb.courier.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderNote implements Serializable {
    private final int orderId;
    private String note;
}
