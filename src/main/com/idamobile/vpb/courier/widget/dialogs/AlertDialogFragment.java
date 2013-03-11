package com.idamobile.vpb.courier.widget.dialogs;

import android.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.idamobile.vpb.courier.util.Versions;
import com.idamobile.vpb.courier.widget.adapters.AdaptersCarousel;

public class AlertDialogFragment extends DialogFragment {

    public static final String ATTR_TITLE = "title";
    public static final String ATTR_MESSAGE = "message";
    public static final String ATTR_POS_BUTTON_TEXT = "posButton";
    public static final String ATTR_NEUT_BUTTON_TEXT = "neutButton";
    public static final String ATTR_NEG_BUTTON_TEXT = "negButton";
    public static final String ATTR_WIDTH = "width";
    public static final String ATTR_HEIGHT = "height";
    public static final String ATTR_HAS_LIST_VIEW = "listview";

    private DialogInterface.OnClickListener posClickListener;
    private DialogInterface.OnClickListener neutClickListener;
    private DialogInterface.OnClickListener negClickListener;
    private DialogInterface.OnCancelListener cancelListener;

    private DialogInterface.OnClickListener onItemClickListener;
    private AdaptersCarousel adapterWrapper = new AdaptersCarousel();

    private DialogInterface.OnClickListener posButtonClick = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (posClickListener != null) {
                posClickListener.onClick(dialog, which);
            }
        }
    };
    private DialogInterface.OnClickListener neutButtonClick = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (neutClickListener != null) {
                neutClickListener.onClick(dialog, which);
            }
        }
    };
    private DialogInterface.OnClickListener negButtonClick = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (negClickListener != null) {
                negClickListener.onClick(dialog, which);
            }
        }
    };


    private DialogInterface.OnClickListener onItemClick = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(dialog, which);
            }
        }
    };

    public static AlertDialogFragment newInstance(CharSequence title, CharSequence message, CharSequence posButtonText,
                                                  CharSequence neutralButtonText, CharSequence negButtonText) {
        return newInstance(title, message, posButtonText, neutralButtonText, negButtonText, null, null, false);
    }

    public static AlertDialogFragment newInstance(CharSequence title, CharSequence message, CharSequence posButtonText,
            CharSequence neutralButtonText, CharSequence negButtonText, Integer widht, Integer height, boolean hasListView) {
        Bundle args = getBundle(title, message, posButtonText, neutralButtonText, negButtonText, widht, height, hasListView);

        AlertDialogFragment result = new AlertDialogFragment();
        result.setArguments(args);
        return result;
    }

    protected static Bundle getBundle(CharSequence title, CharSequence message, CharSequence posButtonText,
                                    CharSequence neutralButtonText, CharSequence negButtonText, Integer widht,
                                    Integer height, boolean hasListView) {
        Bundle args = new Bundle();
        args.putCharSequence(ATTR_TITLE, title);
        args.putCharSequence(ATTR_MESSAGE, message);
        args.putCharSequence(ATTR_POS_BUTTON_TEXT, posButtonText);
        args.putCharSequence(ATTR_NEUT_BUTTON_TEXT, neutralButtonText);
        args.putCharSequence(ATTR_NEG_BUTTON_TEXT, negButtonText);
        args.putBoolean(ATTR_HAS_LIST_VIEW, hasListView);
        if (widht != null) {
            args.putInt(ATTR_WIDTH, widht);
        }
        if (height != null) {
            args.putInt(ATTR_HEIGHT, height);
        }
        return args;
    }

    public void setPosClickListener(DialogInterface.OnClickListener posClickListener) {
        this.posClickListener = posClickListener;
    }

    public void setNeutClickListener(DialogInterface.OnClickListener neutClickListener) {
        this.neutClickListener = neutClickListener;
    }

    public void setNegClickListener(DialogInterface.OnClickListener negClickListener) {
        this.negClickListener = negClickListener;
    }

    public void setCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        if (cancelListener != null) {
            setCancelable(true);
        }
    }

    public void setOnItemClickListener(DialogInterface.OnClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setListAdapter(BaseAdapter adapter) {
        if (adapterWrapper.getAdaptersCount() > 0) {
            throw new IllegalStateException("adapter was already set");
        }
        if (adapter == null) {
            throw new NullPointerException("adapter is null");
        }
        if (!getArguments().getBoolean(ATTR_HAS_LIST_VIEW, false)) {
            throw new IllegalStateException("to set adapter you must instantiate fragment with hasListView=true");
        }
        this.adapterWrapper.addAdapter(adapter);
    }

    public boolean isListAdapterSet() {
        return this.adapterWrapper.getAdaptersCount() > 0;
    }

    @Override
    public AlertDialog getDialog() {
        return (AlertDialog) super.getDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = buildDialog(savedInstanceState);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            if (getArguments().containsKey(ATTR_WIDTH) && getArguments().containsKey(ATTR_HEIGHT)) {
                int width = getArguments().getInt(ATTR_WIDTH);
                int height = getArguments().getInt(ATTR_HEIGHT);
                WindowManager.LayoutParams attributes = new WindowManager.LayoutParams();
                attributes.copyFrom(getDialog().getWindow().getAttributes());
                attributes.width = width;
                attributes.height = height;
                getDialog().getWindow().setAttributes(attributes);
            }
        }
    }

    protected AlertDialog.Builder buildDialog(Bundle savedInstanceState) {
        CharSequence title = getArguments().getCharSequence(ATTR_TITLE);
        CharSequence mes = getArguments().getCharSequence(ATTR_MESSAGE);
        CharSequence pos = getArguments().getCharSequence(ATTR_POS_BUTTON_TEXT);
        CharSequence neut = getArguments().getCharSequence(ATTR_NEUT_BUTTON_TEXT);
        CharSequence neg = getArguments().getCharSequence(ATTR_NEG_BUTTON_TEXT);
        boolean hasListView = getArguments().getBoolean(ATTR_HAS_LIST_VIEW, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_SEARCH;
            }
        });
        if (title != null) {
            builder.setTitle(title);
        }
        if (mes != null) {
            builder.setMessage(mes);
        }
        if (pos != null) {
            builder.setPositiveButton(pos, posButtonClick);
        }
        if (neut != null) {
            builder.setNeutralButton(neut, neutButtonClick);
        }
        if (neg != null) {
            builder.setNegativeButton(neg, negButtonClick);
        }

        if (hasListView) {
            ListView list = new ListView(getActivity());
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemClick.onClick(getDialog(), position);
                }
            });
            list.setCacheColorHint(getActivity().getResources().getColor(R.color.transparent));
            list.setAdapter(adapterWrapper);
            if (!Versions.hasHoneycombApi()) {
                list.setBackgroundResource(R.color.white);
            }
            builder.setView(list);
        }
        return builder;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (cancelListener != null) {
            cancelListener.onCancel(dialog);
        }
    }

}