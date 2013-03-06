package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import com.idamobile.vpb.courier.R;
import com.idamobile.vpb.courier.util.PluralHelper;

public class UploadedImagesMessageFormatter {
    private Context context;

    public UploadedImagesMessageFormatter(Context context) {
        this.context = context;
    }

    public CharSequence format(int numOfUploadedImages, int totalImages) {
        if (numOfUploadedImages == totalImages) {
            return context.getText(R.string.all_photos_uploaded);
        } else if (numOfUploadedImages == 0) {
            String photosWord = getStringWithUppercaseLetter(totalImages == 1 ? R.string.one_photo : R.string.many_photos);
            String uploadedWord = context.getString(totalImages == 1 ? R.string.one_uploaded : R.string.many_uploaded);
            return context.getString(R.string.photos_not_uploaded_format, photosWord, uploadedWord);
        } else {
            final String uploadedWord;
            switch (PluralHelper.getForm(numOfUploadedImages)) {
                case ONE:
                    uploadedWord = getStringWithUppercaseLetter(R.string.one_uploaded);
                    break;
                default:
                    uploadedWord = getStringWithUppercaseLetter(R.string.many_uploaded);
                    break;
            }
            final String photosWord;
            switch (PluralHelper.getForm(totalImages)) {
                case ONE:
                    photosWord = context.getString(R.string.many_photos);
                    break;
                default:
                    photosWord = context.getString(R.string.many_of_photos);
                    break;
            }
            return context.getString(R.string.uploaded_n_of_m_photos_format,
                    uploadedWord, numOfUploadedImages, totalImages, photosWord);
        }
    }

    private String getStringWithUppercaseLetter(int resId) {
        String res = context.getString(resId);
        return res.substring(0,1).toUpperCase() + res.substring(1);
    }
}
