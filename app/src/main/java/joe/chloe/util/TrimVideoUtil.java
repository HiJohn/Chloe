package joe.chloe.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;

import com.blankj.utilcode.util.ScreenUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Describe:
 */
public class TrimVideoUtil {

    private static final String TAG ="TrimVideoUtil";
    public static final long MIN_SHOOT_DURATION = 15*1000L;// 最小剪辑时间15s
    public static final int VIDEO_MAX_TIME = 30;// 30秒
    public static final long MAX_SHOOT_DURATION = VIDEO_MAX_TIME * 1000L;//视频最多剪切多长时间30s
    public static final int MAX_COUNT_RANGE = 10;  //seekBar的区域内一共有多少张图片
    private static final int SCREEN_WIDTH_FULL = ScreenUtils.getScreenWidth();
//    public static final int RECYCLER_VIEW_PADDING = SizeUtils.dp2px(4);
    public static final int RECYCLER_VIEW_PADDING = 0;
    public static final int VIDEO_FRAMES_WIDTH = SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2;
//    private static final int THUMB_WIDTH = (SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2) / VIDEO_MAX_TIME;
    private static final int THUMB_WIDTH = SCREEN_WIDTH_FULL / 8;
//    private static final int THUMB_HEIGHT = SizeUtils.dp2px(50);
    private static final int THUMB_HEIGHT = THUMB_WIDTH*16/9;





    /**
     * 默认提取十张缩略图
     */
    public static void shootVideoThumbDefault(final String filePath,final TaskCallback<Bitmap, Long> callback){

        final long totalThumbsCount = 10;

        TaskExecutor.executeParallel(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(filePath);

                    // Retrieve media data use microsecond
                    long startPosition = 0;
                    String duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long endPosition = Long.valueOf(duration);
                    long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
                    for (long i = 0; i < totalThumbsCount; ++i) {
                        long frameTime = startPosition + interval * i;
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        try {
                            bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_WIDTH, THUMB_HEIGHT, false);
                        } catch (final Throwable t) {
                            t.printStackTrace();
                        }
                        callback.onResult(bitmap, frameTime*1000);
                    }
                    mediaMetadataRetriever.release();
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    callback.onError(e);
                }
            }
        });

    }



    public static void backgroundShootVideoThumb(final String videoUri, final int totalThumbsCount, final long startPosition,
                                                 final long endPosition, final TaskCallback<Bitmap, Long> callback) {

        TaskExecutor.executeParallel(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(videoUri);
                    // Retrieve media data use microsecond
                    long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
                    for (long i = 0; i < totalThumbsCount; ++i) {
                        long frameTime = startPosition + interval * i;
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        try {
                            bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_WIDTH, THUMB_HEIGHT, false);
                        } catch (final Throwable t) {
                            t.printStackTrace();
                        }
                        callback.onResult(bitmap, frameTime);
                    }
                    mediaMetadataRetriever.release();
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    callback.onError(e);
                }
            }
        });

    }

    public static void backgroundShootVideoThumb(final Context context, final Uri videoUri, final int totalThumbsCount, final long startPosition,
                                                 final long endPosition, final TaskCallback<Bitmap, Long> callback) {

        TaskExecutor.executeParallel(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(context, videoUri);
                    // Retrieve media data use microsecond
                    long interval = (endPosition - startPosition) / (totalThumbsCount - 1);
                    for (long i = 0; i < totalThumbsCount; ++i) {
                        long frameTime = startPosition + interval * i;
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(frameTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//                        try {
//                            bitmap = Bitmap.createScaledBitmap(bitmap, THUMB_WIDTH, THUMB_HEIGHT, false);
//                        } catch (final Throwable t) {
//                            t.printStackTrace();
//                        }
                        callback.onResult(bitmap, frameTime);
                    }
                    mediaMetadataRetriever.release();
                } catch (final Throwable e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    callback.onError(e);
                }
            }
        });

    }

    @SuppressLint("CheckResult")
    public static void loadAllVideoFiles(final Context mContext, final ExtractVideoInfoUtil.VideoInfoCallback callback) {

        TaskExecutor.executeParallel(new Runnable() {
            @Override
            public void run() {
                ExtractVideoInfoUtil.getVideoInfoList(mContext, callback);
            }
        });

    }

    public static String getVideoFilePath(String url) {
        if (TextUtils.isEmpty(url) || url.length() < 5) return "";
        if (url.substring(0, 4).equalsIgnoreCase("http")) {

        } else {
            url = "file://" + url;
        }
        return url;
    }


    public static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00";
        } else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) return "99:59:59";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }



    public static void saveBitmapToSd(Bitmap bitmap,String path){
        File file = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90,fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }


    public interface TaskCallback<T, K> {
        void onResult(T t, K k);
        void onError(Throwable e);
    }

}
