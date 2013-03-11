package com.idamobile.vpb.courier.network.images;

import com.idamobile.vpb.courier.ApplicationMediator;
import com.idamobile.vpb.courier.controllers.ImageManager;
import com.idamobile.vpb.courier.controllers.LoginManager;
import com.idamobile.vpb.courier.network.core.AbstractRequest;
import com.idamobile.vpb.courier.network.core.Hosts;
import com.idamobile.vpb.courier.network.core.ResponseDTO;
import com.idamobile.vpb.courier.security.crypto.CryptoUtil;
import com.idamobile.vpb.courier.util.Logger;
import com.idamobile.vpb.courier.util.Streams;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.protocol.HttpContext;

import java.io.*;

public class UploadImageRequest extends AbstractRequest<Void> {

    public static final String TAG = UploadImageRequest.class.getSimpleName();

    private int orderId;
    private int imageId;

    private transient InputStream inputStream;

    private transient LoginManager loginManager;
    private transient ImageManager imageManager;

    public UploadImageRequest(int courierId, int orderId, int imageId) {
        super(Hosts.getUploadImageUrl(courierId, orderId, imageId));
        this.orderId = orderId;
        this.imageId = imageId;
    }

    private ImageInfo getImageInfo() {
        return imageManager.getImageInfo(orderId, imageId);
    }

    @Override
    protected HttpEntity createHttpPostOrPutEntity() throws Exception {
        ImageInfo imageInfo = getImageInfo();
        if (imageInfo == null || !imageInfo.getFile().exists()) {
            throw new FileNotFoundException("image file doesn't exists: "
                    + (imageInfo == null ? "null" : imageInfo.getFile()));
        }
        File file = imageInfo.getFile();
        inputStream = getImageStream(file);
        InputStreamEntity entity = new InputStreamEntity(inputStream, -1);
        entity.setContentType("binary/octet-stream");
        entity.setChunked(true);
        return entity;
    }

    private InputStream getImageStream(File file) throws Exception{
        FileInputStream fileInputStream = new FileInputStream(file);
        Streams.ProgressListener progressListener = new Streams.ProgressListener() {
            @Override
            public void onStreamProgress(long readBytes, long total) {
                reportProgress(readBytes, total);
            }
        };
        InputStream inputStreamWithProgress =
                Streams.addProgressListener(fileInputStream, file.length(), progressListener);
        try {
            return CryptoUtil.getUncryptInputStream(inputStreamWithProgress, loginManager.getSecretKey());
        } catch (Exception ex) {
            try {
                fileInputStream.close();
            } catch (Exception ignored) {
            }
            throw ex;
        }
    }

    private void reportProgress(long readBytes, long total) {
        ImageInfo imageInfo = imageManager.getImageInfo(orderId, imageId);
        if (imageInfo != null) {
            imageInfo.setUploadedBytes(readBytes);
            imageInfo.setTotalBytes(total);
            imageManager.notifyImageInfoChanged(orderId);
        }
    }

    @Override
    public ResponseDTO<Void> execute(ApplicationMediator mediator, HttpClient httpClient, HttpContext httpContext) {
        this.loginManager = mediator.getLoginManager();
        this.imageManager = mediator.getImageManager();

        markImageUploading(true);
        ResponseDTO<Void> responseDTO = super.execute(mediator, httpClient, httpContext);
        markImageUploading(false);

        closeInputStream();
        if (responseDTO.isSuccess()) {
            if (!markUploadedAndCleanUp()) {
                responseDTO = ResponseDTO.newFailureResponse(ResponseDTO.ResultCode.UNKNOWN_ERROR, "IO operation failed");
            }
        }

        return responseDTO;
    }

    private void closeInputStream() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    private boolean markUploadedAndCleanUp() {
        ImageInfo imageInfo = getImageInfo();
        if (imageInfo != null) {
            try {
                if (imageManager.markUploaded(orderId, imageInfo.getTypeId())) {
                    //noinspection ResultOfMethodCallIgnored
                    imageInfo.getFile().delete();
                    return true;
                }
            } catch (IOException e) {
                Logger.debug(TAG, "IO operation failed", e);
            }
        }
        return false;
    }

    private void markImageUploading(boolean uploading) {
        ImageInfo imageInfo = imageManager.getImageInfo(orderId, imageId);
        if (imageInfo != null) {
            imageInfo.setUploading(uploading);
            if (uploading) {
                imageInfo.setTotalBytes(imageInfo.getFile().length());
                imageInfo.setUploadedBytes(0);
            }
            imageManager.notifyImageInfoChanged(orderId);
        }
    }

    @Override
    public void cancel(boolean interrupt) {
        super.cancel(interrupt);
        if (interrupt) {
            closeInputStream();
        }
    }

    @Override
    protected Void parseResponseEntity(InputStream inputStream) throws IOException {
        return null;
    }

}
