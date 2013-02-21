package com.idamobile.vpb.courier.network.images;

import lombok.Data;

@Data
public class ImageInfo {
    private int totalBytes;
    private int uploadedBytes;
    private String filename;

    public boolean isUploaded() {
        return uploadedBytes == totalBytes;
    }
}
