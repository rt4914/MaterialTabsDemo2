package com.arvind.linebalancing.serverutilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AppHelper {

    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getFileDataFromUrl(Context context, String filePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath,bmOptions);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static  byte[] getFileDataFromDrawable(Context context, Uri uri) {
        // Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            InputStream iStream =   context.getContentResolver().openInputStream(uri);
            int bufferSize = 2048;
            byte[] buffer = new byte[bufferSize];

            // we need to know how may bytes were read to write them to the byteBuffer
            int len = 0;
            if (iStream != null) {
                while ((len = iStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}