package com.idamobile.vpb.courier.security.crypto;

import android.content.Context;
import com.idamobile.vpb.courier.model.ImageType;
import com.idamobile.vpb.courier.model.Order;
import com.idamobile.vpb.courier.util.Files;
import com.idamobile.vpb.courier.util.Hashs;

import java.io.File;

public class OrderImagesFilenameMapper {

    private Context context;

    public OrderImagesFilenameMapper(Context context) {
        this.context = context;
    }

    public File mapToFileName(Order order, ImageType imageType) {
        String imageName = Hashs.getSHA1(String.valueOf(order.getId()) + "-" + imageType.getId());
        return new File(Files.getImagesDir(context), imageName);
    }

}