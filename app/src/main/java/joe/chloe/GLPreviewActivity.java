package joe.chloe;

import android.app.ProgressDialog;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.ViewGroup;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.daasuu.mp4compose.composer.Mp4Composer;

import joe.chloe.drawer.GLRenderer;
import joe.chloe.drawer.GlVideoView;
import joe.chloe.drawer.IVideoSurface;
import joe.chloe.exoplayerfilter.FilterType;
import joe.chloe.exoplayerfilter.VideoFilterAdapter;
import joe.chloe.util.UriUtil;

public class GLPreviewActivity extends AppCompatActivity implements VideoFilterAdapter.FilterClickListener{

    private GlVideoView glSurfaceView;

    private String videoPath = "";
    private MediaPlayer mMediaPlayer;
    private int mOriginalWidth = 0;
    private int mOriginalHeight = 0;

    private RecyclerView filtersRv;
    private LinearLayoutManager filterLlm;
    private VideoFilterAdapter filterAdapter;
    private String mVideoPath = "";
    private Uri mVideoUri ;

    private ProgressDialog progressDialog;

    private Mp4Composer mp4Composer;
    private String outPath = "";

    private FilterType currentFilterType = FilterType.DEFAULT ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glpreview);

//        videoPath = getIntent().getStringExtra("path");

        mVideoUri = getIntent().getData();
        if (mVideoUri!=null){
            mVideoPath = UriUtil.getPath(this,mVideoUri);
        }else {
            finish();
        }
        outPath = PathUtils.getExternalMoviesPath().concat("/").concat(String.valueOf(System.currentTimeMillis())).concat("_testFilter.mp4");

        glSurfaceView = (GlVideoView) findViewById(R.id.gl_preview_view);
        glSurfaceView.init(new IVideoSurface() {
            @Override
            public void onCreated(SurfaceTexture surfaceTexture) {
                initMediaPlayer(surfaceTexture);
            }
        });


        filtersRv = findViewById(R.id.pre_filters_rv);
        filterLlm = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        filterAdapter = new VideoFilterAdapter();
        filterAdapter.setFilterNames(FilterType.createFilterList());
        filterAdapter.setFilterSelectListener(this);
        filtersRv.setAdapter(filterAdapter);
        filtersRv.setLayoutManager(filterLlm);

        //merge progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }
    private void initMediaPlayer(SurfaceTexture surfaceTexture) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(videoPath);
            Surface surface = new Surface(surfaceTexture);
            mMediaPlayer.setSurface(surface);
            surface.release();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    ViewGroup.LayoutParams lp = glSurfaceView.getLayoutParams();
//                    int videoWidth = mp.getVideoWidth();
//                    int videoHeight = mp.getVideoHeight();
//                    float videoProportion = (float) videoWidth / (float) videoHeight;
//                    int screenWidth = ScreenUtils.getScreenWidth();
//                    int screenHeight = ScreenUtils.getScreenHeight();
//                    float screenProportion = (float) screenWidth / (float) screenHeight;
//                    if (videoProportion > screenProportion) {
//                        lp.width = screenWidth;
//                        lp.height = (int) ((float) screenWidth / videoProportion);
//                    } else {
//                        lp.width = (int) (videoProportion * (float) screenHeight);
//                        lp.height = screenHeight;
//                    }
//                    glSurfaceView.setLayoutParams(lp);
//
//                    mOriginalWidth = videoWidth;
//                    mOriginalHeight = videoHeight;
//                    Log.e("videoView", "videoWidth:" + videoWidth + ", videoHeight:" + videoHeight);
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFilterClicked(int position, FilterType filterType) {
        currentFilterType = filterType;
//        glSurfaceView.setFilter(FilterType.createGlFilter(currentFilterType, getApplicationContext()));
    }
}
