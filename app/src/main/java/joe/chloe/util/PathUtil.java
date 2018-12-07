package joe.chloe.util;

import android.os.Environment;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SDCardUtils;

import java.io.File;

import joe.chloe.ChloeApp;

/**
 */

public class PathUtil {

    public static String getBaseFolder() {
        String baseFolder = ChloeApp.getApp().getFilesDir().getAbsolutePath();
        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = ChloeApp.getApp().getExternalFilesDir(null).getAbsolutePath();
            }
        }
        return baseFolder;
    }

    public static String getSD(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    //获取Video文件的Path
    public static String getPath(String path, String fileName) {
        String p = getBaseFolder().concat("/").concat(path);
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getFinalPath(fileName);
        }
        return p.concat("/").concat( fileName);
    }

    public static String getPath(String path) {
        String p = getBaseFolder().concat("/").concat(path);
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder();
        }
        return p ;
    }
    public static String getDefaultRecordFilePath(){
        return PathUtil.getPath("record", System.currentTimeMillis() + "_rec.mp4");
    }

    public static String getThumbnailPath(){
        return getPath("record",TimeUtil.getARandomRange().concat(".jpg"));
    }

    public static String getRecordPath(){
        return getPath("record");
    }

    public static String getDefaultFilePath(){
        return getPath("record",getDefaultRandomFilename());
    }

    public static String getRecordsTxtFilePath(){
        return getPath("record","records.txt");
    }

    public static String getTrimmedFilePath(){
        return getPath("record","trimmed.mp4");
    }

    public static String getDefaultRandomFilename(){
        return TimeUtil.getARandomRange().concat(".mp4");
    }
    public static String getDefaultRandomTxtFilename(){
        return TimeUtil.getARandomRange().concat(".txt");
    }

    public static String getFinalPath(String filename){
        return getBaseFolder().concat("/").concat(filename);
    }


    public static String saveFileToSd(String videoPath){
        String moviePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
        File dirPathFolder = new File(moviePath);
        if (!dirPathFolder.exists()) {
            dirPathFolder.mkdirs();
        }
        if (!(dirPathFolder.isDirectory()&&dirPathFolder.exists())){
            moviePath = SDCardUtils.getSDCardPathByEnvironment() + "/DCIM/Camera";
        }
        moviePath = moviePath.concat("/").concat(TimeUtil.getARandomRange()).concat(".mp4");
//                LogUtils.i(TAG, "  copy path :" + moviePath);
        boolean isCopyed =  FileUtils.copyFile(videoPath, moviePath);
        if (isCopyed){
            return moviePath;
        }else {
            return "";
        }
    }
}
