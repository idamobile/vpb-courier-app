package com.idamobile.vpb.courier.network.images;

import lombok.*;

import java.io.File;

@Data
public class ImageInfo {
    private final File file;
    private int typeId;
    private long totalBytes;
    private long uploadedBytes;
    private boolean processing;
    private boolean uploading;
}