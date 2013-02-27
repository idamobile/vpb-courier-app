package com.idamobile.vpb.courier.widget.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import com.idamobile.vpb.courier.R;

public class OrderFinalOptionsAdapter extends BaseAdapter {

    public static final int RESIDENT_OPTION_INDEX = 0;
    public static final int MARKS_OPTION_INDEX = 1;

    private int[] items = {
            R.string.client_resident_option,
            R.string.marks_option
    };

    private boolean hasMarks;
    private boolean resident = true;

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Boolean getItem(int position) {
        if (position == RESIDENT_OPTION_INDEX) {
            return resident;
        } else if (position == MARKS_OPTION_INDEX) {
            return hasMarks;
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(android.R.layout.select_dialog_multichoice, parent, false);
        }
        CheckedTextView checkedTextView = (CheckedTextView) convertView;
        checkedTextView.setText(items[position]);
        checkedTextView.setChecked(getItem(position));
        return convertView;
    }

    public void toggle(int position) {
        if (position == RESIDENT_OPTION_INDEX) {
            resident = !resident;
        } else if (position == MARKS_OPTION_INDEX) {
            hasMarks = !hasMarks;
        }
        notifyDataSetChanged();
    }

    public void reset() {
        hasMarks = false;
        resident = true;
        notifyDataSetChanged();
    }
}
