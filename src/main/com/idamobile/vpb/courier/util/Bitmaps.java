package com.idamobile.vpb.courier.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class Bitmaps {

    public static final String TAG = Bitmaps.class.getSimpleName();

    public interface StreamProvider {
        InputStream openStrem() throws IOException;
    }

    public static class Size {
        public final int width;
        public final int height;
        
        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static Bitmap loadScaledImage(StreamProvider streamProvider, int maxWidht, int maxHeight) {
        InputStream in = null;
        try {
            int inWidth = 0;
            int inHeight = 0;

            in = streamProvider.openStrem();

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
            in = streamProvider.openStrem();
            return BitmapFactory.decodeStream(in, null, options);
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

    private static int log2(int x) {
        int pow = 0;
        if(x >= (1 << 16)) { x >>= 16; pow +=  16;}
        if(x >= (1 << 8 )) { x >>=  8; pow +=   8;}
        if(x >= (1 << 4 )) { x >>=  4; pow +=   4;}
        if(x >= (1 << 2 )) { x >>=  2; pow +=   2;}
        if(x >= (1 << 1 )) { x >>=  1; pow +=   1;}
        return pow;
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
