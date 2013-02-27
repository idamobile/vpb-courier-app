package com.idamobile.vpb.courier.model;

import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.protobuf.Services;
import com.shaubert.protomapper.annotations.Field;
import com.shaubert.protomapper.annotations.Mapper;

@Mapper(protoClass = Services.CancelOrderRequestProtobufDTO.CancellationReason.class, isEnum = true)
public enum CancellationReason {

    CLIENT_DID_NOT_ANSWER_THE_PHONE(1, R.string.client_did_not_answer_the_phone),
    CLIENT_MISSED_MEETING(2, R.string.client_missed_meeting),
    COURIER_MISSED_MEETING(3, R.string.courier_missed_meeting),
    CLIENT_FORGOT_PASSPORT(4, R.string.client_forgot_passport),
    CLIENT_REJECTED_ORDER(5, R.string.client_rejected_order),
    AGREEMENT_CAN_NOT_BE_SIGNED(6, R.string.agreement_can_not_be_signed);

    @Field public final int code;
    public final int strResId;

    private CancellationReason(int code, int strResId) {
        this.code = code;
        this.strResId = strResId;
    }

}