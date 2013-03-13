package com.idamobile.vpb.courier.widget.orders.awards;

import android.content.Context;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.CancellationReason;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AwardMessages {

    private static final int[] POSITIVE_IMAGES = {
            R.drawable.pos_11886,
            R.drawable.pos_1198,
            R.drawable.pos_12043,
            R.drawable.pos_1297,
            R.drawable.pos_1317,
            R.drawable.pos_1811,
            R.drawable.pos_2042,
            R.drawable.pos_2477,
            R.drawable.pos_3359,
            R.drawable.pos_5331,
            R.drawable.pos_883,
            R.drawable.pos_9666,
            R.drawable.pos_9993,
    };

    private static final int[] FINAL_ORDER_POSITIVE_IMAGES = {
            R.drawable.pos_11537,
            R.drawable.pos_12085,
            R.drawable.pos_634,
    };

    private static final int[] NEGATIVE_IMAGES = {
            R.drawable.neg_12459,
            R.drawable.neg_2327,
            R.drawable.neg_320,
            R.drawable.neg_5174,
            R.drawable.neg_881
    };

    private static final int CLIENT_CRY = R.drawable.neg_1760;
    private static final int CLIENT_SLEEP = R.drawable.neg_1216;

    private final Map<CancellationReason, int[]> NEGATIVE_TEXTS;
    private final String[] POSITIVE_TEXTS;
    private final int[] FINAL_ORDER_POSITIVE_TEXTS;

    private final Context context;
    private final Random random;

    public AwardMessages(Context context) {
        this.context = context;
        this.random = new Random();

        this.POSITIVE_TEXTS = context.getResources().getStringArray(R.array.award_positive);
        this.FINAL_ORDER_POSITIVE_TEXTS = new int[] {
                R.string.text_1_final_order_completed,
                R.string.text_2_final_order_completed,
                R.string.text_3_final_order_completed,
                R.string.text_4_final_order_completed,
                R.string.text_5_final_order_completed,
                R.string.text_6_final_order_completed,
        };
        this.NEGATIVE_TEXTS = new HashMap<CancellationReason, int[]>();

        NEGATIVE_TEXTS.put(CancellationReason.AGREEMENT_CAN_NOT_BE_SIGNED, new int[]{
                R.string.text_1_agreement_can_not_be_signed,
        });
        NEGATIVE_TEXTS.put(CancellationReason.CLIENT_REJECTED_ORDER, new int[]{
                R.string.text_1_client_rejected_order,
                R.string.text_2_client_rejected_order,
                R.string.text_client_fault,
        });
        NEGATIVE_TEXTS.put(CancellationReason.CLIENT_FORGOT_PASSPORT, new int[]{
                R.string.text_1_client_forgot_passport,
                R.string.text_2_client_forgot_passport,
        });
        NEGATIVE_TEXTS.put(CancellationReason.CLIENT_DID_NOT_ANSWER_THE_PHONE, new int[]{
                R.string.text_1_client_did_not_answer_the_phone,
                R.string.text_2_client_did_not_answer_the_phone,
                R.string.text_3_client_did_not_answer_the_phone,
                R.string.text_client_fault,
                R.string.text_client_would_cry,
        });
        NEGATIVE_TEXTS.put(CancellationReason.CLIENT_MISSED_MEETING, new int[]{
                R.string.text_1_client_missed_meeting,
                R.string.text_2_client_missed_meeting,
                R.string.text_client_fault,
                R.string.text_client_would_cry,
        });
        NEGATIVE_TEXTS.put(CancellationReason.COURIER_MISSED_MEETING, new int[]{
                R.string.text_1_courier_missed_meeting,
                R.string.text_2_courier_missed_meeting,
                R.string.text_3_courier_missed_meeting,
                R.string.text_4_courier_missed_meeting,
        });
    }

    public Message getMessageForLastOrder() {
        int imageId = FINAL_ORDER_POSITIVE_IMAGES[random.nextInt(FINAL_ORDER_POSITIVE_IMAGES.length)];
        CharSequence message = context.getText(
                FINAL_ORDER_POSITIVE_TEXTS[random.nextInt(FINAL_ORDER_POSITIVE_TEXTS.length)]);
        return new Message(imageId, message);
    }

    public Message getPositiveMessage() {
        int imageId = POSITIVE_IMAGES[random.nextInt(POSITIVE_IMAGES.length)];
        CharSequence message = POSITIVE_TEXTS[random.nextInt(POSITIVE_TEXTS.length)];
        return new Message(imageId, message);
    }

    public Message getNegativeMessage(CancellationReason reason) {
        int imageId = NEGATIVE_IMAGES[random.nextInt(NEGATIVE_IMAGES.length)];

        int[] strArray = NEGATIVE_TEXTS.get(reason);
        int messageId = strArray[random.nextInt(strArray.length)];
        if (messageId == R.string.text_client_would_cry) {
            imageId = CLIENT_CRY;
        } else if (messageId == R.string.text_2_client_missed_meeting) {
            imageId = CLIENT_SLEEP;
        }

        return new Message(imageId, context.getString(messageId));
    }

    @Data
    public static class Message {
        private final int imageResId;
        private final CharSequence message;
    }

}