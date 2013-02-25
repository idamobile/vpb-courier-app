package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.util.PluralHelper;
import com.idamobile.vpb.courier.util.SpannableUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderTimeFormatter {

    private Context context;
    private DateFormat format = new SimpleDateFormat("H:mm");

    public OrderTimeFormatter(Context context) {
        this.context = context;
    }

    public CharSequence formatSimpleOrderTime(Order order) {
        long orderStartTime = order.getMeetTimeFrom();
        long orderEndTime = order.getMeetTimeTo();
        String fromTime = formatTime(orderStartTime);
        String endTime = formatTime(orderEndTime);
        return context.getString(R.string.order_time_simple_format, fromTime, endTime);
    }

    public CharSequence formatOrderTime(Order order) {
        long curTime = System.currentTimeMillis();
        long orderStartTime = order.getMeetTimeFrom();
        long orderEndTime = order.getMeetTimeTo();

        long timeToStart = orderStartTime - curTime;
        long timeToEnd = orderEndTime - curTime;

        int color = R.color.order_time_normal;
        String fromTime = formatTime(orderStartTime);
        String endTime = formatTime(orderEndTime);
        final String text;
        if (timeToStart < 0) {
            color = R.color.order_time_warn;
            if (timeToEnd <= 60 * 1000) {
                color = R.color.order_time_missed;
                text = context.getString(R.string.order_time_missed_format, fromTime, endTime);
            } else {
                text = context.getString(R.string.order_time_remaining_format, fromTime, endTime,
                        formatTimeDifference(timeToEnd));
            }
        } else {
            text = context.getString(R.string.order_time_normal_format, fromTime, endTime,
                    formatTimeDifference(timeToStart));
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableUtils.append(builder, text, new ForegroundColorSpan(context.getResources().getColor(color)));
        int index = text.indexOf("\n");
        if (index > 0) {
            builder.setSpan(new RelativeSizeSpan(0.6f), index, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    private String formatTime(long time) {
        return format.format(new Date(time));
    }

    private String formatTimeDifference(long time) {
        if (time > 2 * 60 * 60 * 1000) {
            return context.getString(R.string.time_diff_more_then_2h);
        } else if (time > 90 * 60 * 1000) {
            return context.getString(R.string.time_diff_more_then_1_5h);
        } else if (time > 60 * 60 * 1000) {
            return context.getString(R.string.time_diff_more_then_1h);
        } else {
            int minutes = (int) ((time + 59 * 1000) / (60 * 1000));
            final String minutesWord;
            switch (PluralHelper.getForm(minutes)) {
                case ONE:
                    minutesWord = context.getString(R.string.one_minute);
                    break;
                case FEW:
                    minutesWord = context.getString(R.string.few_minutes);
                    break;
                default:
                    minutesWord = context.getString(R.string.many_minutes);
            }
            return context.getString(R.string.minutes_format, minutes, minutesWord);
        }
    }

}
