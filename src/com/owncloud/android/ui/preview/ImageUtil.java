package com.owncloud.android.ui.preview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
/**
 * star
 */
public class ImageUtil {
    public static Bitmap loadImage(String imageFilePath) {
        if (imageFilePath == null || imageFilePath.length() < 1)
            return null;
        File file = new File(imageFilePath);
        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = (int) (file.length() / (1*1024*1024) + 1);   //ÏÞÖÆÔÚ1M 
        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
        return resizeBmp;
    }
    
    public static Bitmap loadOriginalImage(String imageFilePath) {
        if (imageFilePath == null || imageFilePath.length() < 1)
            return null;
        File file = new File(imageFilePath);
        Bitmap resizeBmp = null;
        resizeBmp = BitmapFactory.decodeFile(file.getPath());
        return resizeBmp;
    }

    public static byte[] getBitmapArray(Bitmap bmp) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (IOException e) {
        }
        return result;
    }

    public static Bitmap resizeBitmap(Bitmap bmp, int max_size) {
        int cur_size = bmp.getHeight() * bmp.getWidth();
        if (cur_size < max_size) {
            return bmp;
        }
        double quality = Math.sqrt(((double) max_size) / cur_size);
        int dstWidth = (int) (quality * bmp.getWidth());
        int dstHeight = (int) (quality * bmp.getHeight());
        return Bitmap.createScaledBitmap(bmp, dstWidth, dstHeight, true);
    }
}
