package com.idamobile.vpb.courier.widget.login;

import android.content.Context;
import android.graphics.Rect;
import android.text.*;
import android.text.InputFilter.LengthFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author colriot
 * @since Aug 14, 2012
 *
 */
public class PinDigitEditText extends EditText {

    private PinWidget parent;
    private boolean innerTextChange;

    public PinDigitEditText(Context context) {
        super(context);
        init();
    }

    public PinDigitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinDigitEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        InputFilter[] oldFilters = getFilters();
        InputFilter[] filters = new InputFilter[(oldFilters != null ? oldFilters.length : 0) + 1];
        if (oldFilters != null) {
            System.arraycopy(oldFilters, 0, filters, 0, filters.length - 1);
        }
        filters[filters.length - 1] = createNumericFilter();
        setFilters(filters);
        setMaxLines(1);
        setCursorVisible(false);
        setGravity(Gravity.CENTER);
        setInputType(InputType.TYPE_CLASS_PHONE);
        setTransformationMethod(new PasswordTransformationMethod());
        setMaxLength(1);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!innerTextChange) {
                    if (s.length() == 1) {
                        goForwardOrSubmit();
                    }
                }
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN ||
                        (keyCode < KeyEvent.KEYCODE_0 || keyCode > KeyEvent.KEYCODE_9)
                        && keyCode != KeyEvent.KEYCODE_DEL) {
                    return false;
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    goBack();
                }
                return false;
            }
        });

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (getNextFocusDownId() != NO_ID) {
                    goForwardOrSubmit();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private InputFilter createNumericFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                if (source instanceof SpannableStringBuilder) {
                    SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder)source;
                    for (int i = end - 1; i >= start; i--) {
                        char currentChar = source.charAt(i);
                        if (!Character.isDigit(currentChar)) {
                            sourceAsSpannableBuilder.delete(i, i+1);
                        }
                    }
                    return source;
                } else {
                    StringBuilder filteredStringBuilder = new StringBuilder();
                    for (int i = 0; i < end; i++) {
                        char currentChar = source.charAt(i);
                        if (Character.isDigit(currentChar)) {
                            filteredStringBuilder.append(currentChar);
                        }
                    }
                    return filteredStringBuilder.toString();
                }
            }
        };
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            setTextInner(null);
        }
    }

    private void setTextInner(CharSequence text) {
        innerTextChange = true;
        setText(text);
        innerTextChange = false;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new InputConnectionWrapper(super.onCreateInputConnection(outAttrs), true) {
            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                boolean result = super.deleteSurroundingText(beforeLength, afterLength);
                goBack();
                return result;
            }
        };
    }

    protected void goForwardOrSubmit() {
        if (!goForward()) {
            if (parent != null) {
                parent.submit();
            }
        }
    }

    private boolean goForward() {
        return goTo(getNextFocusDownId());
    }

    private boolean goBack() {
        return goTo(getNextFocusUpId());
    }

    private boolean goTo(int id) {
        if (id != NO_ID) {
            View view = ((ViewGroup) getParent()).findViewById(id);
            if (view != null) {
                view.requestFocus();
                return true;
            }
        }
        return false;
    }

    public void setMaxLength(int length) {
        LengthFilter lengthFilter = new LengthFilter(length);

        InputFilter[] curFilters = getFilters();
        if (curFilters != null) {
            for (int idx = 0; idx < curFilters.length; idx++) {
                if (curFilters[idx] instanceof LengthFilter) {
                    curFilters[idx] = lengthFilter;
                    setFilters(curFilters);
                    return;
                }
            }

            // since the length filter was not part of the list, but
            // there are filters, then add the length filter
            InputFilter newFilters[] = new InputFilter[curFilters.length + 1];
            System.arraycopy(curFilters, 0, newFilters, 0, curFilters.length);
            newFilters[curFilters.length] = lengthFilter;
            setFilters(newFilters);
        } else {
            setFilters(new InputFilter[] { lengthFilter });
        }
    }

    void setParent(PinWidget pinWidget) {
        parent = pinWidget;
    }
}
