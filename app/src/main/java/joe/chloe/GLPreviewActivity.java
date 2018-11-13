package joe.chloe;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.daasuu.epf.filter.GlLookUpTableFilter;
import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlBilateralFilter;
import com.daasuu.mp4compose.filter.GlBoxBlurFilter;
import com.daasuu.mp4compose.filter.GlBulgeDistortionFilter;
import com.daasuu.mp4compose.filter.GlCGAColorspaceFilter;
import com.daasuu.mp4compose.filter.GlFilter;
import com.daasuu.mp4compose.filter.GlGaussianBlurFilter;
import com.daasuu.mp4compose.filter.GlGrayScaleFilter;
import com.daasuu.mp4compose.filter.GlHazeFilter;
import com.daasuu.mp4compose.filter.GlInvertFilter;
import com.daasuu.mp4compose.filter.GlLut512Filter;
import com.daasuu.mp4compose.filter.GlLutFilter;
import com.daasuu.mp4compose.filter.GlMonochromeFilter;
import com.daasuu.mp4compose.filter.GlSepiaFilter;
import com.daasuu.mp4compose.filter.GlSharpenFilter;
import com.daasuu.mp4compose.filter.GlSphereRefractionFilter;
import com.daasuu.mp4compose.filter.GlToneCurveFilter;
import com.daasuu.mp4compose.filter.GlVignetteFilter;

import java.io.IOException;
import java.io.InputStream;

import joe.chloe.drawer.GLRenderer;
import joe.chloe.drawer.GlVideoView;
import joe.chloe.drawer.IVideoSurface;
import joe.chloe.exoplayerfilter.FilterType;
import joe.chloe.exoplayerfilter.VideoFilterAdapter;
import joe.chloe.exoplayerfilter.filtersample.GlBitmapOverlaySample;
import joe.chloe.util.UriUtil;

import static joe.chloe.MvComposerActivity.exportMp4ToGallery;

public class GLPreviewActivity extends AppCompatActivity implements VideoFilterAdapter.FilterClickListener{

    private static final String TAG = "GLPreviewActivity" ;
    private GlVideoView glSurfaceView;

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

    private GlFilter currentFilter = new GlFilter();

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
            mMediaPlayer.setDataSource(mVideoPath);
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
        currentFilter = createGlFilter(currentFilterType,getApplicationContext());
        glSurfaceView.setFilter(currentFilter);
    }


    public static GlFilter createGlFilter(FilterType filterType, Context context){
        switch (filterType) {
            case DEFAULT:
                return new GlFilter();
            case SEPIA:
                return new GlSepiaFilter();
            case GRAY_SCALE:
                return new GlGrayScaleFilter();
            case INVERT:
                return new GlInvertFilter();
            case HAZE:
                return new GlHazeFilter();
            case MONOCHROME:
                GlMonochromeFilter glMonochromeFilter = new GlMonochromeFilter();
                glMonochromeFilter.setIntensity(0.5f);

                return glMonochromeFilter;
            case BILATERAL_BLUR:
                return new GlBilateralFilter();
            case BOX_BLUR:
                return new GlBoxBlurFilter();
            case LOOK_UP_TABLE_SAMPLE:
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lookup_sample);
                return new GlLutFilter(bitmap);
            case TONE_CURVE_SAMPLE:
                try {
                    InputStream is = context.getAssets().open("acv/tone_cuver_sample.acv");
                    return new GlToneCurveFilter(is);
                } catch (IOException e) {
                    Log.e("FilterType", "Error");
                }
                return new GlFilter();

            case SPHERE_REFRACTION:
                return new GlSphereRefractionFilter();
            case VIGNETTE:
                return new GlVignetteFilter();
//            case FILTER_GROUP_SAMPLE:
//                return new GlFilterGroup(new GlSepiaFilter(), new GlVignetteFilter());
            case GAUSSIAN_FILTER:
                return new GlGaussianBlurFilter();
            case BULGE_DISTORTION:
                return new GlBulgeDistortionFilter();
            case CGA_COLORSPACE:
                return new GlCGAColorspaceFilter();
            case SHARP:
                GlSharpenFilter glSharpenFilter = new GlSharpenFilter();
                glSharpenFilter.setSharpness(4f);
                return glSharpenFilter;
//            case BITMAP_OVERLAY_SAMPLE:
//                return new GlBitmapOverlaySample(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round));
            default:
                return new GlFilter();
        }
    }

    private void composeVideoWithFilter(){
        mp4Composer = null;
        progressDialog.show();
        mp4Composer = new Mp4Composer(mVideoPath, outPath)
                // .rotation(Rotation.ROTATION_270)
                //.size(720, 1280)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(new GlSepiaFilter())
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        LogUtils.d(TAG, "onProgress = " + progress);
                        runOnUiThread(() -> progressDialog.setProgress((int) (progress * 100)));
                    }

                    @Override
                    public void onCompleted() {
                        LogUtils.d(TAG, "onCompleted()");

                        exportMp4ToGallery(getApplicationContext(), mVideoPath);
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(GLPreviewActivity.this, "codec complete path =" + mVideoPath, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onCanceled() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        exception.printStackTrace();
                        progressDialog.dismiss();
                        LogUtils.d(TAG, "onFailed()"+exception.getMessage());
                        ToastUtils.showLong("合成滤镜失败");
                    }
                })
                .start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gl_preview,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId()==R.id.agl_compose){
            composeVideoWithFilter();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
