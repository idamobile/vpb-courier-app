package com.idamobile.vpb.courier.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

public class Intents {

    public static Intent createCallIntent(String phoneNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
    }

    public static Intent createRouteIntent(String address) {
        StringBuilder builder = new StringBuilder();
        String[] parts = address.split(" ");
        for (String part : parts) {
            if (!TextUtils.isEmpty(part)) {
                if (builder.length() > 0) {
                    builder.append("+");
                }
                builder.append(part);
            }
        }
        return new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + builder.toString()));
    }

    public static Intent createUndergroundIntent(String subwayStation) {
        subwayStation = subwayStation.replaceAll("[Мм]\\.", "");
        return createRouteIntent("Москва метро " + subwayStation);
    }

    public static Intent takePictureIntent(File output) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (output != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        }
        return takePictureIntent;
    }

    public static Intent browserIntent(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        intent.setData(Uri.parse(url));
        return intent;
    }

    public static boolean startActivityIfExists(Intent intent, Context context) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException ex) {
            return false;
        }
    }
}
