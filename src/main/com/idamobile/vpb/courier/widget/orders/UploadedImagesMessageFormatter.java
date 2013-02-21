package com.idamobile.vpb.courier.widget.orders;

import android.content.Context;
import com.idamobile.vpb.courier.R;

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
            boolean useOneForUploaded = numOfUploadedImages == 1 || numOfUploadedImages % 10 == 1;
            String uploadedWord = getStringWithUppercaseLetter(useOneForUploaded ? R.string.one_uploaded : R.string.many_uploaded);
            boolean useOneForPhotos = totalImages == 1 || totalImages % 10 == 1;
            String photosWord = context.getString(useOneForPhotos ? R.string.many_of_photos : R.string.many_photos);
            return context.getString(R.string.uploaded_n_of_m_photos_format,
                    uploadedWord, numOfUploadedImages, totalImages, photosWord);
        }
    }

    private String getStringWithUppercaseLetter(int resId) {
        String res = context.getString(resId);
        return res.substring(0,1).toUpperCase() + res.substring(1);
    }
}
