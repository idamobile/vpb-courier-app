package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.model.Order;
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

    public CharSequence formatOrderTime(Order order) {
        long curTime = System.currentTimeMillis();
        long orderStartTime = order.getMeetTimeFrom();
        long orderEndTime = order.getMeetTimeTo();

        long timeToStart = orderStartTime - curTime;
        long timeToEnd = orderEndTime - curTime;

        int color = R.color.order_time_normal;
        String fromTime = formatTime(orderStartTime);
        String endTime = formatTime(orderEndTime);
        final CharSequence text;
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
        } else if (time > 45 * 60 * 1000) {
            return context.getString(R.string.time_diff_more_then_45min);
        } else if (time > 30 * 60 * 1000) {
            return context.getString(R.string.time_diff_more_then_30min);
        } else {
            int minutes = (int) (time / (60 * 1000));
            if (minutes == 1 || minutes % 10 == 1) {
                return context.getString(R.string.time_diff_less_then_n_min_one, minutes);
            } else {
                return context.getString(R.string.time_diff_less_then_n_min_many, minutes);
            }
        }
    }

}
