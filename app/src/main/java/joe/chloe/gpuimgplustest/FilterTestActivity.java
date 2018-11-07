package joe.chloe.gpuimgplustest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.wysaid.myUtils.MsgUtil;
import org.wysaid.nativePort.CGEFFmpegNativeLibrary;
import org.wysaid.nativePort.CGENativeLibrary;
import org.wysaid.view.VideoPlayerGLSurfaceView;

import java.util.Locale;

import joe.chloe.R;
import joe.chloe.util.BackgroundExecutor;
import joe.chloe.util.UiThreadExecutor;
import joe.chloe.util.UriUtil;

public class FilterTestActivity extends AppCompatActivity {


    public static final String LOG_TAG = "FilterTestActivity";

    public static final String FILTERS[] = {

            "@beautify face 1 480 640", //Beautify
            "@adjust lut late_sunset.png",
            "#unpack @krblend sr hehe.jpg 100 ",
            "@style edge 1 2 @curve RGB(0, 255)(255, 0) @adjust saturation 0 @adjust level 0.33 0.71 0.93 ",
            "@adjust shadowhighlight -200 200 ",
            "@adjust sharpen 10 1.5 ",
            "@adjust colorbalance 0.99 0.52 -0.31 ",
            "@adjust level 0.66 0.23 0.44 ",

    };

    private String mPath ="";

    private String mCurrentConfig = "@adjust lut edgy_amber.png";

    private String outputFile = "";

    private ProgressDialog progressDialog;

    private VideoPlayerGLSurfaceView mPlayerView;

    private VideoPlayerGLSurfaceView.PlayCompletionCallback playCompletionCallback = new VideoPlayerGLSurfaceView.PlayCompletionCallback() {
        @Override
        public void playComplete(MediaPlayer player) {
            player.start();
        }

        @Override
        public boolean playFailed(MediaPlayer player, final int what, final int extra) {
            MsgUtil.toastMsg(FilterTestActivity.this, String.format(Locale.getDefault(),"Error occured! Stop playing, Err code: %d, %d", what, extra));
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_test);
        mPlayerView = findViewById(R.id.filter_preview);
        mPlayerView.setZOrderOnTop(false);
        mPlayerView.setZOrderMediaOverlay(true);

        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("add filter ing ...");
        progressDialog.setCancelable(false);
        outputFile = Environment.getExternalStorageDirectory().getPath().concat("/").
                concat(String.valueOf(System.currentTimeMillis()).concat(".mp4"));
    }

    public void selectVideo(View view) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 11);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            if (data.getData()!=null){
                mPath = UriUtil.getPath(this,data.getData());
                mPlayerView.setVideoUri(data.getData(), new VideoPlayerGLSurfaceView.PlayPreparedCallback() {
                    @Override
                    public void playPrepared(MediaPlayer player) {
                        player.start();
                    }
                }, playCompletionCallback);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_demo, menu);
        return true;
    }

    private boolean shouldFit = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (TextUtils.isEmpty(mPath)){

            return false;
        }

        switch (id){
            case R.id.border:

                break;

            case R.id.fit_screen:
                shouldFit = !shouldFit;
                mPlayerView.setFitFullView(shouldFit);
                break;

            case R.id.filter1:
                mCurrentConfig = FILTERS[0];
                mPlayerView.setFilterWithConfig(mCurrentConfig);
                break;
            case R.id.filter2:
                mCurrentConfig = FILTERS[1];
                mPlayerView.setFilterWithConfig(mCurrentConfig);

                break;
            case R.id.filter3:
                mCurrentConfig = FILTERS[2];
                mPlayerView.setFilterWithConfig(mCurrentConfig);
                break;
            case R.id.filter4:
                mCurrentConfig = FILTERS[3];
                mPlayerView.setFilterWithConfig(mCurrentConfig);
                break;
            case R.id.addFilter:
                addFilter(outputFile,mPath);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void addFilter(final String outputFilename, final String inputFileName){
        progressDialog.show();

        final long start = System.currentTimeMillis();
        BackgroundExecutor.execute(new BackgroundExecutor.Task("",0,"") {
            @Override
            public void execute() {
                //bmp params is used for watermark, (just pass null if you don't want that)
                //and ususally the blend mode is CGE_BLEND_ADDREV for watermarks.
                CGEFFmpegNativeLibrary.generateVideoWithFilter(outputFilename, inputFileName,
                        mCurrentConfig, 1.0f, null/*bmp*/,
                        CGENativeLibrary.TextureBlendMode.CGE_BLEND_ADDREV, 1.0f, false);
                Log.i(LOG_TAG, "Done! The file is generated at: " + outputFilename);
                long end = System.currentTimeMillis();
                final long time = end - start;
                Log.i(LOG_TAG, "Done! time spend : " + time);
                UiThreadExecutor.runTask("", new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FilterTestActivity.this,"filtered ,time: "+time,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + outputFilename)));
                    }
                },0);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayerView.release();
        mPlayerView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }


}
