package com.idamobile.vpb.courier.widget.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.idamobile.vpb.courier.model.CancellationReason;

import java.util.ArrayList;
import java.util.List;

public class OrderCloseReasonsAdapter extends BaseAdapter {

    private List<CancellationReason> items = new ArrayList<CancellationReason>();

    @Override
    public int getCount() {
        return items.size();
    }

    public void setItems(CancellationReason ... reasons) {
        items.clear();
        for (CancellationReason reason : reasons) {
            items.add(reason);
        }
        notifyDataSetChanged();
    }

    @Override
    public CancellationReason getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).code;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(android.R.layout.select_dialog_item, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setText(getItem(position).strResId);
        return convertView;
    }
}
