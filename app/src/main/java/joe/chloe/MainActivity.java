package joe.chloe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import joe.chloe.drawer.TextureRenderer;
import joe.chloe.exoplayerfilter.ExoPlayerFilterActivity;
import joe.chloe.model.VideoInfo;
import joe.chloe.util.ExtractVideoInfoUtil;
import joe.chloe.util.TaskExecutor;
import joe.chloe.util.TrimVideoUtil;
import joe.chloe.util.UriUtil;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final int CHOOSE_VIDEO = 0x001;
    private static final int CHOOSE_AUDIO = 0x002;
    private static final long TIMEOUT_USEC = 1000L;

    public static final int MSG_PLAY = 0x010;
    public static final int MSG_SHOW_IMG = 0x011;

    private String audioPath = "";
    private String videoPath = "";
    private Uri mVideoUri = null;
    private VideoInfo videoInfo = null;

    private int seekedPosition = 0;
    int bitmapCount = 0;
    private MediaController mController;




    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_PLAY:


                    break;

                case MSG_SHOW_IMG:

                    break;
            }


            return false;
        }
    });


    private ImageView thumb;
    private VideoView videoView;
    private AppCompatSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);

    }


    public void chooseAudioFile(View view) {
//        Intent intent = new Intent();
//        intent.setType("audio/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, CHOOSE_AUDIO);
    }

    public void chooseVideoFile(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CHOOSE_VIDEO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHOOSE_VIDEO:
                mVideoUri = data.getData();
                videoPath = UriUtil.getPath(this, mVideoUri);
//              videoInfo = ExtractVideoInfoUtil.getVideoInfoFromPath(videoPath);

                break;
            case CHOOSE_AUDIO:
                audioPath = UriUtil.getPath(this, data.getData());

                break;
        }
    }

    public void toGlPreview(View view) {

        if (TextUtils.isEmpty(videoPath)){
            ToastUtils.showLong("没有选择视频文件");
            return;
        }

        Intent intent = new Intent(this,GLPreviewActivity.class);
        intent.setData(mVideoUri);
        intent.putExtra("path",videoPath);
        startActivity(intent);

    }

    public void toExoPreview(View view) {
        if (mVideoUri==null){
            ToastUtils.showLong("没有选择视频文件");
            return;
        }
        Intent intent = new Intent(this,ExoPlayerFilterActivity.class);
        intent.setData(mVideoUri);
//        intent.putExtra("uri",mVideoUri);
        startActivity(intent);
    }
}
