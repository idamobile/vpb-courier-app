package com.idamobile.vpb.courier.security.crypto;

import android.content.Context;
import com.idamobile.vpb.courier.util.Files;

import java.io.File;

public class OrderImagesFilenameMapper {

    public static final String UPLOADED_POSTFIX = "-uploaded";
    private Context context;

    public OrderImagesFilenameMapper(Context context) {
        this.context = context;
    }

    private File mapToFileName(int courierId, int orderId, int imageTypeId, String postfix) {
        String name = "image-courier-" + courierId + "-order-" +orderId + "-imagetype-" + imageTypeId;
        if (postfix != null) {
            name += postfix;
        }
        String imageName = Hashs.getSHA1(name);
        return new File(Files.getImagesDir(context), imageName);
    }

    public File mapToFileName(int courierId, int orderId, int imageTypeId) {
        return mapToFileName(courierId, orderId, imageTypeId, null);
    }

    public File mapToUplodedFileName(int courierId, int orderId, int imageTypeId) {
        return mapToFileName(courierId, orderId, imageTypeId, UPLOADED_POSTFIX);
    }

}