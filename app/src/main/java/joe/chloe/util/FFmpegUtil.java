package joe.chloe.util;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.coremedia.iso.boxes.Container;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import joe.chloe.model.VideoInfo;

/**
 * 三个功能
 * 压缩裁剪
 * 添加背景音
 * 截取某一帧做封面
 */
public class FFmpegUtil {


    private static final String TAG = "FFmpegUtil";
    private static int TARGET_WIDTH = 540;
    private static int TARGET_HEIGHT = 960;
    private static final float TARGET_SIZE_RATE = 1.7f;

//    private FFmpegUtil(){}


    //*************** writeMinds start ************************


    public static void init(Context context, FFmpegLoadBinaryResponseHandler fFmpegLoadBinaryResponseHandler)
            throws FFmpegNotSupportedException {
        FFmpeg.getInstance(context).loadBinary(fFmpegLoadBinaryResponseHandler);
    }

    /**
     * 使用 writeMinds 库
     *
     * @param context
     * @param videoPath
     * @param audioPath
     * @param outPath
     * @param mvVol
     * @param bgVol
     * @param duration
     * @param responseHandler
     * @throws FFmpegCommandAlreadyRunningException
     */
    public static void addBgmUseCmd(Context context, String videoPath, String audioPath, String outPath,
                                    float mvVol, float bgVol, long duration, FFmpegExecuteResponseHandler responseHandler)
            throws FFmpegCommandAlreadyRunningException {

        List<String> cmdList = new ArrayList<>();
        cmdList.add("-y");
        cmdList.add("-i");
        cmdList.add(videoPath);
        cmdList.add("-i");
        cmdList.add(audioPath);
        cmdList.add("-strict");
        cmdList.add("-2");
        cmdList.add("-filter_complex");
        cmdList.add("[0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + mvVol + "[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + bgVol + "[a1];[a0][a1]amix=inputs=2:duration=first[aout]");
        cmdList.add("-map");
        cmdList.add("[aout]");
        cmdList.add("-ac");
        cmdList.add("2");
        cmdList.add("-c:v");
        cmdList.add("copy");
//        cmdList.add("-c:a");
//        cmdList.add("libmp3lame");
        cmdList.add("-map");
        cmdList.add("0:v:0");
        cmdList.add("-preset");
        cmdList.add("superfast");
        cmdList.add(outPath);

        String cmd = "";
        for (int i = 0; i < cmdList.size(); i++) {
            cmd = cmd.concat(cmdList.get(i).concat(" "));
        }
        String[] cmds = cmdList.toArray(new String[0]);
        LogUtils.i(TAG, "addBgmUseCmd :" + cmd);
        FFmpeg.getInstance(context).execute(cmds, responseHandler);

    }


    /**
     * 如果视频录制出来多于30秒，则切出来
     *
     * @param context
     * @param videoPath
     * @param outPath
     * @param responseHandler
     * @throws FFmpegCommandAlreadyRunningException
     */
    public static void clipVideo(Context context, String videoPath, String outPath,
                                 FFmpegExecuteResponseHandler responseHandler)
            throws FFmpegCommandAlreadyRunningException {
        List<String> cmdList = new ArrayList<>();
        cmdList.add("-y");
        cmdList.add("-ss");
        cmdList.add("0.0");
        cmdList.add("-t");
        cmdList.add("30.0");
        cmdList.add("-accurate_seek");
        cmdList.add("-i");
        cmdList.add(videoPath);

        cmdList.add("-codec");
        cmdList.add("copy");
//        cmdList.add("-avoid_negative_ts");
//        cmdList.add("1");

        cmdList.add("-preset");
        cmdList.add("superfast");
        cmdList.add(outPath);
        String cmd = "";
        for (int i = 0; i < cmdList.size(); i++) {
            cmd = cmd.concat(cmdList.get(i).concat(" "));
        }
        LogUtils.i(TAG, " clipVideo cmd :" + cmd);

        String[] cmds = cmdList.toArray(new String[0]);
        FFmpeg.getInstance(context).execute(cmds, responseHandler);

    }

    /**
     * 只压缩分辨率
     */
    public static void compressResolution(Context context, String videoPath, String outPath, float startS, float durationS,
                                          VideoInfo videoInfo, FFmpegExecuteResponseHandler responseHandler)
            throws FFmpegCommandAlreadyRunningException {
        int width = 0;
        int height = 0;
        int rotation = 0;
        int duration = 0;
        float sizeRate = 0f;
        if (videoInfo == null) {
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(videoPath);
            try {
                width = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                height = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    rotation = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                }
                duration = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//
            } catch (Exception e) {
                e.printStackTrace();
                responseHandler.onFailure("获取视频信息失败");
                return;
            } finally {
                metadataRetriever.release();
            }
        } else {
            width = videoInfo.width;
            height = videoInfo.height;
            rotation = videoInfo.rotation;
            duration = videoInfo.duration;
            LogUtils.i(TAG, " videoInfo :" + videoInfo.toString());
        }
        sizeRate = (float) width / (float) height;

        int targetWidth = width;
        int targetHeight = height;
        //宽高反着的 android api 90度 被认为基准横屏0度，旋转90度，原生拍出的视频 大多宽大于高
        //ffmpeg 转换过的宽高是视觉上的宽高
        if (rotation == 90) {
            //portrait 基本是原生摄像拍出来的,原生相机rotation90的时候，视频属性里的宽度是视觉上的高度
            targetWidth = TARGET_WIDTH;
            targetHeight = (int) (TARGET_WIDTH * sizeRate);
        } else if (rotation == 0 || rotation == 180) {
            //landscape
            if (sizeRate < 1) {// width < height 宽小于高，竖屏
                targetWidth = TARGET_WIDTH;
                targetHeight = (int) (TARGET_WIDTH / sizeRate);
            } else {//宽大于高 横屏
                targetWidth = TARGET_HEIGHT;
                targetHeight = (int) (TARGET_HEIGHT / sizeRate);
            }
        }

        List<String> cmdList = new ArrayList<>();
        cmdList.add("-y");
        cmdList.add("-i");
        cmdList.add(videoPath);
//        cmdList.add("-strict");
//        cmdList.add("-2");
        cmdList.add("-filter_complex");
        cmdList.add("scale=" + targetWidth + ":" + targetHeight + ",setdar=" + targetWidth + "/" + targetHeight);
//        cmdList.add("-c:a");
//        cmdList.add("libmp3lame");
        cmdList.add("-codec");
        cmdList.add("copy");
        cmdList.add("-avoid_negative_ts");
        cmdList.add("1");
        cmdList.add("-preset");
        cmdList.add("superfast");
        cmdList.add(outPath);

        String[] cmds = cmdList.toArray(new String[0]);
//        FFmpegCmd.exec(cmds,duration ,  editorListener);
        String cmd = "";
        for (int i = 0; i < cmdList.size(); i++) {
            cmd = cmd.concat(cmdList.get(i).concat(" "));
        }
        LogUtils.i(TAG, " clipAndCompressVideo cmd :" + cmd);
        FFmpeg.getInstance(context).execute(cmds, responseHandler);

    }


    public static void cropAndCompress(Context context, String videoPath, String outPath, float startS, float durationS,
                                       VideoInfo videoInfo, FFmpegExecuteResponseHandler responseHandler) throws FFmpegCommandAlreadyRunningException {
        int codecRate = 4;

        int width = 0;
        int height = 0;
        int rotation = 0;
        int duration = 0;
        int bitRate = 0;
        String mBitRate = "";
        float sizeRate = 0f;
        if (videoInfo == null) {
            MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
            metadataRetriever.setDataSource(videoPath);
            try {
                width = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                height = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    rotation = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                }
                duration = Integer.valueOf(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                mBitRate = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                if (!TextUtils.isEmpty(mBitRate) && TextUtils.isDigitsOnly(mBitRate)) {
                    bitRate = Integer.parseInt(mBitRate);
                }
                LogUtils.i(TAG, " m bit rate :" + mBitRate);
            } catch (Exception e) {
                e.printStackTrace();
                responseHandler.onFailure("获取视频信息失败");
                return;
            } finally {
                metadataRetriever.release();
            }
        } else {
            width = videoInfo.width;
            height = videoInfo.height;
            rotation = videoInfo.rotation;
            mBitRate = videoInfo.bitRate;
            if (!TextUtils.isEmpty(mBitRate) && TextUtils.isDigitsOnly(mBitRate)) {
                bitRate = Integer.valueOf(mBitRate);
            }
            duration = videoInfo.duration;
            LogUtils.i(TAG, " videoInfo :" + videoInfo.toString());
        }

        sizeRate = (float) width / (float) height;
        boolean isLowerBitrate = bitRate / 1000 / 1000 <= codecRate;
        boolean isSkipCompress = rotation == 90 && (width <= TARGET_HEIGHT || height <= TARGET_WIDTH);
        if ((rotation == 0 || rotation == 180)) {
            if (sizeRate < 1) {
                isSkipCompress = (width <= TARGET_WIDTH || height <= TARGET_HEIGHT);
            } else {
                isSkipCompress = (width <= TARGET_HEIGHT || height <= TARGET_WIDTH);
            }
        }

//        LogUtils.i(TAG,"isSkipCompress : "+isSkipCompress+", is lower bitrate :"+isLowerBitrate);
        int targetWidth = width;
        int targetHeight = height;

        //宽高反着的 android api 90度 被认为基准横屏0度，旋转90度，原生拍出的视频 大多宽大于高
        //ffmpeg 转换过的宽高是视觉上的宽高
        if (rotation == 90) {
            //portrait 基本是原生摄像拍出来的,原生相机rotation90的时候，视频属性里的宽度是视觉上的高度
            targetWidth = TARGET_WIDTH;
            targetHeight = (int) (TARGET_WIDTH * sizeRate);
        } else if (rotation == 0 || rotation == 180) {
            //landscape
            if (sizeRate < 1) {// width < height 宽小于高，竖屏
                targetWidth = TARGET_WIDTH;
                targetHeight = (int) (TARGET_WIDTH / sizeRate);
            } else {//宽大于高 横屏
                targetWidth = TARGET_HEIGHT;
                targetHeight = (int) (TARGET_HEIGHT / sizeRate);
            }

        }
        List<String> cmdList = new ArrayList<>();
        cmdList.add("-y");
        cmdList.add("-ss");
        cmdList.add("" + startS);
        cmdList.add("-t");
        cmdList.add("" + durationS);
        cmdList.add("-accurate_seek");
        cmdList.add("-i");
        cmdList.add(videoPath);
//        cmdList.add("-strict");
//        cmdList.add("-2");
        if (isSkipCompress) {
            cmdList.add("-codec");
            cmdList.add("copy");
            cmdList.add("-avoid_negative_ts");
            cmdList.add("1");
        } else {
            cmdList.add("-filter_complex");
            cmdList.add("scale=" + targetWidth + ":" + targetHeight + ",setdar=" + targetWidth + "/" + targetHeight);
        }

//        cmdList.add("-c:a");
//        cmdList.add("libmp3lame");
//        if (!isLowerBitrate){
//            cmdList.add("-b:v");
//            cmdList.add(String.valueOf(codecRate).concat("M"));
//        }
        cmdList.add("-preset");
        cmdList.add("superfast");
        cmdList.add(outPath);


        String[] cmds = cmdList.toArray(new String[0]);
//        FFmpegCmd.exec(cmds,duration ,  editorListener);
        String cmd = "";
        for (int i = 0; i < cmdList.size(); i++) {
            cmd = cmd.concat(cmdList.get(i).concat(" "));
        }
        LogUtils.i(TAG, " clipAndCompressVideo cmd :" + cmd);
        FFmpeg.getInstance(context).execute(cmds, responseHandler);
    }


    public static void getOneScreenShot(Context context, String videoPath, long frameTime,
                                        String thumbPath, FFmpegExecuteResponseHandler responseHandler)
            throws FFmpegCommandAlreadyRunningException {
        float mTime = Utils.accuracy(frameTime, 1000, 3, RoundingMode.DOWN);
//        LogUtils.i(TAG," frame time :"+frameTime+", mtime :"+mTime);
        List<String> cmdList = new ArrayList<>();
        cmdList.add("-i");
        cmdList.add(videoPath);
        cmdList.add("-ss");
        cmdList.add(String.valueOf(mTime));
        cmdList.add("-vframes");
        cmdList.add("1");
        cmdList.add("-y");
        cmdList.add(thumbPath);

        String cmd = "";
        for (int i = 0; i < cmdList.size(); i++) {
            cmd = cmd.concat(cmdList.get(i).concat(" "));
        }
        LogUtils.i(TAG, " getOneScreenShot cmd :" + cmd);

        String[] cmds = cmdList.toArray(new String[0]);
        FFmpeg.getInstance(context).execute(cmds, responseHandler);
    }


    public static void mergeVideoFiles(Context context, ArrayList<String> videoFiles, String outFile, FFmpegExecuteResponseHandler responseHandler)
            throws FFmpegCommandAlreadyRunningException {



        String txtName = PathUtil.getRecordsTxtFilePath();

        try {
            FileOutputStream fos = new FileOutputStream(txtName, true);
            OutputStreamWriter outWriter = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bufWrite = new BufferedWriter(outWriter);
            for (String filename : videoFiles) {
                bufWrite.write("file".concat(" ").concat("'").concat(filename).concat("'").concat("\n"));
            }
            bufWrite.close();
            outWriter.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            responseHandler.onFailure(e.getMessage());
            return;
        }

        List<String> cmdList = new ArrayList<>();
        cmdList.add("-f");
        cmdList.add("concat");
        cmdList.add("-i");
        cmdList.add(txtName);
        cmdList.add("-c");
        cmdList.add("copy");
        cmdList.add(outFile);

        String cmd = "";
        for (int i = 0; i < cmdList.size(); i++) {
            cmd = cmd.concat(cmdList.get(i).concat(" "));
        }
        LogUtils.i(TAG, " mergeVideoFiles cmd :" + cmd);

        String[] cmds = cmdList.toArray(new String[0]);
        FFmpeg.getInstance(context).execute(cmds, responseHandler);
    }

    public static boolean killIfRunning(Context context) {
        if (FFmpeg.getInstance(context).isFFmpegCommandRunning()) {
            return FFmpeg.getInstance(context).killRunningProcesses();
        }
        return true;
    }


    //******************* writeMinds end  *******************

    public static int[] getResolution(int rotation, int rawWidth, int rawHeight) {
        int ret[] = {rawWidth, rawHeight};
        float sizeRate = (float) rawWidth / (float) rawHeight;
        int targetWidth = 0;
        int targetHeight = 0;
        //宽高反着的 android api 90度 被认为基准横屏0度，旋转90度，原生拍出的视频 大多宽大于高
        //此 epmedia库 的宽高是视觉上的宽高
        if (rotation == 90) {
            //portrait 竖屏，带rotation=90属性的，基本是原生摄像拍出来的
            targetWidth = TARGET_WIDTH;
            targetHeight = (int) (TARGET_WIDTH * sizeRate);
        } else if (rotation == 0 || rotation == 180) {
            //landscape
            if (sizeRate < 1) {// width < height 宽度小于高，竖屏
                targetWidth = TARGET_WIDTH;
                targetHeight = (int) (TARGET_WIDTH / sizeRate);
            } else {//宽度大于高度，横屏
                targetWidth = TARGET_HEIGHT;
                targetHeight = (int) (TARGET_HEIGHT / sizeRate);
            }

        }
        ret[0] = targetWidth;
        ret[1] = targetHeight;
        return ret;
    }

    public static void putFilterTest(String path, String outputPath) {
//        String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";//底片效果
//
    }


    /**
     * 对Mp4文件集合进行追加合并(按照顺序一个一个拼接起来)
     *
     * @param mp4PathList [输入]Mp4文件路径的集合(支持m4a)(不支持wav)
     * @param outPutPath  [输出]结果文件全部名称包含后缀(比如.mp4)
     * @throws IOException 格式不支持等情况抛出异常
     */
    public static void appendMp4List(List<String> mp4PathList, String outPutPath) throws IOException {
        List<Movie> mp4MovieList = new ArrayList<>();// Movie对象集合[输入]
        for (String mp4Path : mp4PathList) {// 将每个文件路径都构建成一个Movie对象
            mp4MovieList.add(MovieCreator.build(mp4Path));
        }

        List<Track> audioTracks = new LinkedList<>();// 音频通道集合
        List<Track> videoTracks = new LinkedList<>();// 视频通道集合

        for (Movie mp4Movie : mp4MovieList) {// 对Movie对象集合进行循环
            for (Track inMovieTrack : mp4Movie.getTracks()) {
                if ("soun".equals(inMovieTrack.getHandler())) {// 从Movie对象中取出音频通道
                    audioTracks.add(inMovieTrack);
                }
                if ("vide".equals(inMovieTrack.getHandler())) {// 从Movie对象中取出视频通道
                    videoTracks.add(inMovieTrack);
                }
            }
        }

        Movie resultMovie = new Movie();// 结果Movie对象[输出]
        if (!audioTracks.isEmpty()) {// 将所有音频通道追加合并
            resultMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[0])));
        }
        if (!videoTracks.isEmpty()) {// 将所有视频通道追加合并
            resultMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[0])));
        }

        Container outContainer = new DefaultMp4Builder().build(resultMovie);// 将结果Movie对象封装进容器
        FileChannel fileChannel = new RandomAccessFile(outPutPath, "rw").getChannel();
        outContainer.writeContainer(fileChannel);// 将容器内容写入磁盘
        fileChannel.close();
    }



}
