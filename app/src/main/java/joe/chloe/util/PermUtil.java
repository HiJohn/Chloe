package joe.chloe.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by takashi on 2017/1/27.
 */
public class PermUtil {

    public static final String SD_READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String SD_WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static final String CAMERA_PERM = Manifest.permission.CAMERA;
    public static final String AUDIO_PERM = Manifest.permission.RECORD_AUDIO;
    public static final String RECORD_VIDEO_GROUP[] = {SD_READ_PERM,CAMERA_PERM,AUDIO_PERM};

    public static final int GRANTED = PackageManager.PERMISSION_GRANTED;
    public static final int DENIED = PackageManager.PERMISSION_DENIED;

    public static final int SD_REQ = 9111;
    public static final int RECORD_REQ = 9112;
    private static final String TAG = "PermUtil";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isGranted(Context context, String permission) {

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isDenied(Context context, String permission) {

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED;
    }


    public static void requestPermissions(Activity activity, int reqCode, String... perms) {
        ActivityCompat.requestPermissions(activity, perms, reqCode);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isSdGranted(Context context) {
        return isGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isSdDenied(Context context) {
        return isDenied(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static void requestRecordPermissions(Activity activity){
        requestPermissions(activity,RECORD_REQ,RECORD_VIDEO_GROUP);
    }

    public static boolean hasRecordPermissions(Activity activity){
        return ContextCompat.checkSelfPermission(activity,CAMERA_PERM)==GRANTED&&
                ContextCompat.checkSelfPermission(activity,SD_WRITE_PERM)==GRANTED&&
                ContextCompat.checkSelfPermission(activity,AUDIO_PERM)==GRANTED;
    }

    public static void requestSDPermissions(Activity activity, int reqCode) {
        ActivityCompat.requestPermissions(activity, new String[]{SD_READ_PERM, SD_WRITE_PERM}, reqCode);
    }
    public static void requestSDPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{SD_READ_PERM, SD_WRITE_PERM}, SD_REQ);
    }

    public static boolean isScreenOrientPhoneLocked(Context context) {

        int str = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
//        Logs.i(" rotation :" + str);
        return str != 1;


    }

    public static boolean isVersionM(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
    }


    public static final int  RC_PERMISSION_REQUEST = 9222;
    public static boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean hasRecordAudioPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean hasWriteStoragePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    public static void requestCameraPermission(Activity activity, boolean requestWritePermission) {

        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA) || (requestWritePermission &&
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.RECORD_AUDIO));
        if (showRationale) {
            Toast.makeText(activity,
                    "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
        } else {

            // No explanation needed, we can request the permission.

            String permissions[] = requestWritePermission ? new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}: new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO};
            ActivityCompat.requestPermissions(activity,permissions,RC_PERMISSION_REQUEST);
        }
    }

    public static void requestWriteStoragePermission(Activity activity) {
        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (showRationale) {
            Toast.makeText(activity,
                    "Writing to external storage permission is needed to run this application",
                    Toast.LENGTH_LONG).show();
        } else {

            // No explanation needed, we can request the permission.

            String permissions[] =  new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};

            ActivityCompat.requestPermissions(activity,permissions,RC_PERMISSION_REQUEST);
        }
    }

    /** Launch Application Setting to grant permission. */
    public static void launchPermissionSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

}
