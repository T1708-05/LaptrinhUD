package com.example.btap7;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Utility class để lấy đường dẫn thật từ Uri
 * Tương thích với Android 10+ (Scoped Storage)
 */
public class RealPathUtil {

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        // SDK >= 19 (KitKat)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            realPath = getRealPathFromURI_API19(context, fileUri);
        } else {
            // SDK < 19
            realPath = getRealPathFromURI_API11to18(context, fileUri);
        }
        return realPath;
    }

    /**
     * Lấy real path cho Android API 19+
     */
    private static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String[] splits = wholeID.split(":");
            String id = splits.length > 1 ? splits[1] : wholeID;

            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column,
                    sel,
                    new String[]{id},
                    null
            );

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    null
            );
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
                cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }

        return filePath;
    }

    /**
     * Lấy real path cho Android API 11-18
     */
    private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        Cursor cursor = context.getContentResolver().query(
                contentUri,
                proj,
                null,
                null,
                null
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }

        return result;
    }
}
