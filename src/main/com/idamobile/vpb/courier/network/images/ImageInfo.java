package com.idamobile.vpb.courier.network.images;

import lombok.Data;

import java.io.File;

@Data
public class ImageInfo {
    private int typeId;
    private int totalBytes;
    private int uploadedBytes;
    private File file;
    private boolean processing;
    private boolean uploading;

    public boolean isUploaded() {
        return uploadedBytes == totalBytes;
    }
}
