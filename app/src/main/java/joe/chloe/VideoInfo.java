package joe.chloe;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 视频的信息bean
 */

public class VideoInfo implements Parcelable{


    public String path;//路径
    public String name = "";//文件名
    public String mime_type;//mime_type
    public int rotation;//旋转角度
    public int width;//宽
    public int height;//高
    public String bitRate;//比特率
    public float frameRate;//帧率
    public int frameInterval;//关键帧间隔
    public int duration;//时长

    public int expWidth;//期望宽度
    public int expHeight;//期望高度
    public int cutPoint;//剪切的开始点
    public int cutDuration;//剪切的时长


    public VideoInfo(){}

    protected VideoInfo(Parcel in) {
        path = in.readString();
        name = in.readString();
        mime_type = in.readString();
        rotation = in.readInt();
        width = in.readInt();
        height = in.readInt();
        bitRate = in.readString();
        frameRate = in.readFloat();
        frameInterval = in.readInt();
        duration = in.readInt();
        expWidth = in.readInt();
        expHeight = in.readInt();
        cutPoint = in.readInt();
        cutDuration = in.readInt();
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(mime_type);
        dest.writeInt(rotation);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(bitRate);
        dest.writeFloat(frameRate);
        dest.writeInt(frameInterval);
        dest.writeInt(duration);
        dest.writeInt(expWidth);
        dest.writeInt(expHeight);
        dest.writeInt(cutPoint);
        dest.writeInt(cutDuration);
    }

    // 最小15秒，最大50秒
    public boolean isDurationMatch(){
        if (duration>=15*1000&&duration<=50*1000){
            return true;
        }
        return false;
    }

    public void cutName(){
        int index = path.lastIndexOf("/");
        name = path.substring(index+1,path.length());
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", mime_type='" + mime_type + '\'' +
                ", rotation=" + rotation +
                ", width=" + width +
                ", height=" + height +
                ", bitRate=" + bitRate +
                ", frameRate=" + frameRate +
                ", frameInterval=" + frameInterval +
                ", duration=" + duration +
                ", expWidth=" + expWidth +
                ", expHeight=" + expHeight +
                ", cutPoint=" + cutPoint +
                ", cutDuration=" + cutDuration +
                '}';
    }
}
