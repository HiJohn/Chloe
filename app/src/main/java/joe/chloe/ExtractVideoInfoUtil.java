package joe.chloe;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class ExtractVideoInfoUtil {

    private ExtractVideoInfoUtil() {

    }


    /**
     * 获取视频某一帧,不一定是关键帧，不耗时
     *
     * @param timeMs 毫秒
     */
    public static Bitmap extractFrame(String file,long timeMs) {
        MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();
        mMetadataRetriever.setDataSource(file);
        String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long fileLength = Long.valueOf(duration);
        //第一个参数是传入时间，只能是us(微秒)
        //OPTION_CLOSEST ,在给定的时间，检索最近一个帧,这个帧不一定是关键帧。
        //OPTION_CLOSEST_SYNC   在给定的时间，检索最近一个同步与数据源相关联的的帧（关键帧）
        //OPTION_NEXT_SYNC 在给定时间之后检索一个同步与数据源相关联的关键帧。
        //OPTION_PREVIOUS_SYNC  顾名思义，同上
//        Bitmap bitmap = mMetadataRetriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
        Bitmap bitmap = null;
        for (long i = timeMs; i < fileLength; i += 1000) {
            bitmap = mMetadataRetriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                break;
            }
        }
        return bitmap;
    }


    public static VideoInfo getVideoInfoFromPath(String filePath){
        LogUtils.i(" get video info , filePath :"+filePath);

        VideoInfo videoInfo = new VideoInfo();
        MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();
        mMetadataRetriever.setDataSource(filePath);

        String rotation = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String width = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String height = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String mimeType = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        String bitRate = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        LogUtils.i(" get video info from path : rotation :"+rotation);

        videoInfo.path=filePath;
        videoInfo.cutName();
        videoInfo.mime_type = mimeType;
        videoInfo.bitRate = bitRate;
        try {
            if (!TextUtils.isEmpty(rotation)){
                videoInfo.rotation = Integer.parseInt(rotation);
            }
            videoInfo.width = Integer.parseInt(width);
            videoInfo.height = Integer.parseInt(height);
            videoInfo.duration = Integer.parseInt(duration);
        }catch (Exception e){
            e.printStackTrace();
        }


        return videoInfo;
    }




    public static void getVideoInfoList(Context context, VideoInfoCallback callback) {
        HashSet<VideoInfo> videoItemHashSet = new HashSet<>();
        MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();
        VideoInfo videoInfo;
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        try {
            if (cursor==null){
                callback.onFailure();
                return ;
            }
            cursor.moveToFirst();
            do{
                videoInfo = new VideoInfo();
                videoInfo.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                mMetadataRetriever.setDataSource(videoInfo.path);

                videoInfo.name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
//                videoInfo.mime_type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
//                videoInfo.width = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
//                videoInfo.height = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
//                videoInfo.duration = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                String rotation = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                String width = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String mimeType = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                String bitRate = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);


                videoInfo.mime_type = mimeType;
                videoInfo.bitRate = bitRate;

                try {
                    videoInfo.width = Integer.parseInt(width);
                    videoInfo.height = Integer.parseInt(height);
                    videoInfo.duration = Integer.parseInt(duration);
                    videoInfo.rotation = Integer.parseInt(rotation);
                }catch (Exception e){
                    e.printStackTrace();
                }

                videoItemHashSet.add(videoInfo);
            }while(cursor.moveToNext());

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
            return;
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        callback.onSuccess(new ArrayList<>(videoItemHashSet));
    }


    public static ArrayList<VideoInfo> getVideoInfos(Context context){
        HashSet<VideoInfo> videoItemHashSet = new HashSet<>();
        VideoInfo videoInfo;
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        try {

            if (cursor==null){
                return null;
            }
            cursor.moveToFirst();

            do{
                videoInfo = new VideoInfo();
                videoInfo.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                videoInfo.name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                videoInfo.mime_type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                videoInfo.width = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
                videoInfo.height = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
                videoInfo.duration = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                videoItemHashSet.add(videoInfo);
            }while(cursor.moveToNext());

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return new ArrayList<>(videoItemHashSet);
    }


    public static ArrayList<VideoInfo> getVideoInfoList(Context context) {
        HashSet<VideoInfo> videoItemHashSet = new HashSet<>();
        MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();
        VideoInfo videoInfo;
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        try {
            if (cursor==null){
                return null;
            }
            cursor.moveToFirst();
            do{
                videoInfo = new VideoInfo();
                videoInfo.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                mMetadataRetriever.setDataSource(videoInfo.path);

                videoInfo.name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
//                videoInfo.mime_type = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
//                videoInfo.width = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
//                videoInfo.height = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
//                videoInfo.duration = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                String rotation = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                String width = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String duration = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String mimeType = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                String bitRate = mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);


                videoInfo.mime_type = mimeType;
                videoInfo.bitRate = bitRate;

                try {
                    videoInfo.width = Integer.parseInt(width);
                    videoInfo.height = Integer.parseInt(height);
                    videoInfo.duration = Integer.parseInt(duration);
                    videoInfo.rotation = Integer.parseInt(rotation);
                }catch (Exception e){
                    e.printStackTrace();
                }

                videoItemHashSet.add(videoInfo);
            }while(cursor.moveToNext());

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
        return new ArrayList<>(videoItemHashSet);
    }


    public static String saveImage(Bitmap bmp, String dirPath) {
        if (bmp == null) {
            return "";
        }
        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "video_thumb_"+System.currentTimeMillis() + "_upload.jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    public static final String POSTFIX = ".jpeg";

    public static String saveImageToSD(Bitmap bmp, String dirPath) {
        if (bmp == null) {
            return "";
        }
        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    public static String saveImageToSDForEdit(Bitmap bmp, String dirPath, String fileName) {
        if (bmp == null) {
            return "";
        }
        File appDir = new File(dirPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static void deleteFile(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; ++i) {
                    deleteFile(files[i]);
                }
            }
        }
        f.delete();
    }
    public interface VideoInfoCallback {
        void onSuccess(ArrayList<VideoInfo> videoInfos);
        void onFailure();
    }


    /**
     * 设置固定的宽度，高度随之变化，使图片不会变形
     *
     * @param bm Bitmap
     * @return Bitmap
     */
    public  static  Bitmap scaleImage(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ScreenUtils.getScreenWidth() * 1.0f / width;
//        float scaleHeight =extractH*1.0f / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap newBm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        if (!bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        return newBm;
    }

}
