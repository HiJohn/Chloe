package joe.chloe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private int thumbIds[] = {R.id.thumb1, R.id.thumb2, R.id.thumb3, R.id.thumb4,
            R.id.thumb5, R.id.thumb6, R.id.thumb7, R.id.thumb8};


    private static final String TAG = "MainActivity";
    private static final int CHOOSE_VIDEO = 0x001;
    private static final int CHOOSE_AUDIO = 0x002;
    private static final long TIMEOUT_USEC = 1000L;

    public static final int MSG_PLAY = 0x010;
    public static final int MSG_SHOW_IMG = 0x011;

    private String audioPath = "";
    private String videoPath = "";
    private VideoInfo videoInfo = null;

    private int seekedPosition = 0;
    int bitmapCount = 0;
    private MediaController mController;




    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {


            switch (msg.what) {
                case MSG_PLAY:

                    if (videoView != null && videoInfo != null) {
                        seekBar.setMax(videoInfo.duration);
                        seekBar.setProgress(0);
                        videoView.setVideoPath(videoPath);
                        videoView.seekTo(0);
//                        playRepeat();
                        loadThumb(videoInfo.duration);
//                        videoView.start();
                    }


                    break;

                case MSG_SHOW_IMG:
                    bitmapCount ++;
                    thumb = findViewById(thumbIds[bitmapCount-1]);
                    thumb.setImageBitmap(bitmapss.pop());
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
        seekBar = findViewById(R.id.sv_seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (videoInfo != null) {
                    videoView.seekTo(progress);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (disposable!=null&&!disposable.isDisposed()){
                    disposable.dispose();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekedPosition = seekBar.getProgress();
                if (videoInfo!=null){

//                    playRepeat();
                }
            }
        });
//        mController = new MediaController(this);
//        videoView.setMediaController(mController);
//        mController.setMediaPlayer(videoView);


    }

    Disposable disposable;

    private void playRepeat() {
        disposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .repeat()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.i(TAG,"   interval doOnComplete");
//                        videoView.pause();
                    }
                })
                .doAfterNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtils.i(TAG,"   interval doAfterNext");
                        videoView.pause();
                    }
                })
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtils.i(TAG,"   interval doOnNext");
//                        videoView.seekTo(seekedPosition);
//                        videoView.start();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtils.i(TAG,"   interval subscribe");
                        videoView.seekTo(seekedPosition);
                        videoView.start();
                    }
                });


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

    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private LinkedList<Bitmap> bitmapss = new LinkedList<>();

    private void loadThumb(long duration) {

        TrimVideoUtil.backgroundShootVideoThumb(videoPath, 8, 0, duration, new TrimVideoUtil.TaskCallback<Bitmap, Long>() {
            @Override
            public void onResult(final Bitmap bitmap, Long aLong) {

                if (bitmap != null) {
                    bitmapss.push(bitmap);
                }
                mHandler.sendEmptyMessage(MSG_SHOW_IMG);



            }

            @Override
            public void onError(Throwable e) {
                LogUtils.i(TAG," backgroundShootVideoThumb  onError  ");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CHOOSE_VIDEO:
                videoPath = UriUtil.getPath(this, data.getData());

                TaskExecutor.executeParallel(new Runnable() {
                    @Override
                    public void run() {
                        videoInfo = ExtractVideoInfoUtil.getVideoInfoFromPath(videoPath);
                        mHandler.sendEmptyMessage(MSG_PLAY);
                    }
                });


                break;
            case CHOOSE_AUDIO:
                audioPath = UriUtil.getPath(this, data.getData());

                break;
        }
    }
}
