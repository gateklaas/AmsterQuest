package vu.group6.amsterquest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;

public class CameraUtils {
    public static final int REQUEST_IMAGE_CAPTURE = 1000;
    public static final int REQUEST_PERMISSION = 3000;

    public static final Uri CAMERA_TMP = Uri.fromFile(new File("/sdcard/tmp.jpg"));

    public static void dispatchTakePictureIntent(Activity activity) {
        if (!checkPermissions(activity))
            throw new Error("Camera permissions not set");

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, CAMERA_TMP);
        activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public static Uri getUri(int resultCode) {
        return resultCode == Activity.RESULT_OK ? CAMERA_TMP : null;
    }

    public static File getFile(int resultCode) {
        return resultCode == Activity.RESULT_OK ? new File(CAMERA_TMP.getPath()) : null;
    }

    public static Bitmap getBitmap(Activity activity, int resultCode) throws FileNotFoundException {
        return resultCode == Activity.RESULT_OK ? BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(CAMERA_TMP)) : null;
    }

    public static boolean checkPermissions(Activity activity) {
        String permissions[] = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean grantCamera = ContextCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean grantExternal = ContextCompat.checkSelfPermission(activity, permissions[1]) == PackageManager.PERMISSION_GRANTED;

        if (!grantCamera && !grantExternal) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION);
        } else if (!grantCamera) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[0]}, REQUEST_PERMISSION);
        } else if (!grantExternal) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[1]}, REQUEST_PERMISSION);
        }

        return grantCamera && grantExternal;
    }
}
