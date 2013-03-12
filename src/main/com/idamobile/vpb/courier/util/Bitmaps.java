package com.idamobile.vpb.courier.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class Bitmaps {

    public static final String TAG = Bitmaps.class.getSimpleName();

    public static class Size {
        public final int width;
        public final int height;
        
        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static Bitmap loadScaledImage(Files.InputStreamProvider streamProvider, int maxWidht, int maxHeight, int orientation) {
        InputStream in = null;
        try {
            int inWidth = 0;
            int inHeight = 0;

            in = streamProvider.openInputStream();

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            options = new BitmapFactory.Options();
            if (inWidth > maxWidht || inHeight > maxHeight) {
                // calc rought re-size (this is no exact resize)
                float scale = Math.max(inWidth/maxWidht, inHeight/maxHeight);
                int pow = log2((int) Math.ceil(scale));
                options.inSampleSize = (int)Math.pow(2, pow);
            }
            in = streamProvider.openInputStream();
            Bitmap tmp = BitmapFactory.decodeStream(in, null, options);
            Bitmap result = rotateBitmap(tmp, orientation);
            if (tmp != result) {
                tmp.recycle();
            }
            return result;
        } catch (IOException e) {
            Logger.warn(TAG, "error loading image", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        if (orientation == 0 || bitmap == null) {
            return bitmap;
        }
        Matrix mtx = new Matrix();
        mtx.setRotate((float)orientation, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, false);
    }

    private static int log2(int x) {
        int pow = 0;
        if(x >= (1 << 16)) { x >>= 16; pow +=  16;}
        if(x >= (1 << 8 )) { x >>=  8; pow +=   8;}
        if(x >= (1 << 4 )) { x >>=  4; pow +=   4;}
        if(x >= (1 << 2 )) { x >>=  2; pow +=   2;}
        if(x >= (1 << 1 )) { x >>=  1; pow +=   1;}
        return pow;
    }

    public static int getExifOrientation(String filename) {
        ExifInterface exif;
        int orientation = 0;
        try {
            exif = new ExifInterface(filename);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        } catch (IOException e) {
            Logger.debug(TAG, "failed to get orientation", e);
            e.printStackTrace();
        }
        return orientation;
    }

    public static int getBitmapRotation(int exifOrientation) {
        int rotation = 0;
        switch (exifOrientation) {
            case 3:
                rotation = 180;
                break;
            case 6:
                rotation = 90;
                break;
            case 8:
                rotation = 270;
                break;
        }

        return rotation;
    }

    public static Size getScaledSize(int maxWidht, int maxHeight, int width, int height) {
        if (width > maxWidht || height > maxHeight) {
            float scale = Math.max(width/maxWidht, height/maxHeight);
            float mul = (int)Math.pow(2, Math.ceil(scale));
            return new Size((int)(width / mul), (int)(height / mul));
        } else {
            return new Size(width, height);
        }
    }
    
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidht, int maxHeight) {
    	double aspect = Math.min((double) maxWidht / bitmap.getWidth(),
    			(double) maxHeight / bitmap.getHeight());
    	int widht = (int) (bitmap.getWidth() * aspect);
    	int height = (int) (bitmap.getHeight() * aspect);
    	return Bitmap.createScaledBitmap(bitmap, widht, height, false);
    }
}
