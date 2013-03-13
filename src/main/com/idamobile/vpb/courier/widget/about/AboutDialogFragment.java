package com.idamobile.vpb.courier.widget.about;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.util.Intents;
import com.idamobile.vpb.courier.util.Versions;
import com.idamobile.vpb.courier.widget.dialogs.AlertDialogFragment;

public class AboutDialogFragment extends AlertDialogFragment {

    public static final String NOUN_PROJECT = "Noun Project";

    public static AboutDialogFragment newInstance(Context context) {
        Bundle args = getBundle(context.getString(R.string.about_dialog_title), null,
                context.getText(android.R.string.ok),
                null, null, null, null, false);

        AboutDialogFragment result = new AboutDialogFragment();
        result.setArguments(args);
        return result;
    }

    @Override
    protected AlertDialog.Builder buildDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = super.buildDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.about_dialog_layout, null);
        TextView versionView = (TextView) view.findViewById(R.id.app_version);
        View logo = view.findViewById(R.id.idalogo);
        TextView nounProject = (TextView) view.findViewById(R.id.noun_project_reference);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Intents.browserIntent("http://idamob.ru/");
                Intents.startActivityIfExists(intent, getActivity());
            }
        });
        versionView.setText(getString(R.string.about_dialog_version, getString(R.string.version_name)));

        CharSequence text = nounProject.getText();
        SpannableStringBuilder textBuilder = new SpannableStringBuilder(text);
        String s = text.toString();
        int start = s.indexOf(NOUN_PROJECT);
        textBuilder.setSpan(new URLSpan("http://thenounproject.com/"),
                start, start + NOUN_PROJECT.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        nounProject.setText(textBuilder);
        nounProject.setMovementMethod(LinkMovementMethod.getInstance());

        if (!Versions.hasHoneycombApi()) {
            view.setBackgroundResource(android.R.color.white);
        }

        builder.setView(view);
        return builder;
    }
}
