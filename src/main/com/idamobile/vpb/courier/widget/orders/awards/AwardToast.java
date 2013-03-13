package com.idamobile.vpb.courier.widget.orders.awards;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.idamobile.vpb.courier.R;

public class AwardToast {

    public static Toast show(Context context, int imageId, CharSequence text) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.award_toast, null);

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(imageId);
        TextView textView = (TextView) layout.findViewById(R.id.text);
        textView.setText(text);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        return toast;
    }

}
