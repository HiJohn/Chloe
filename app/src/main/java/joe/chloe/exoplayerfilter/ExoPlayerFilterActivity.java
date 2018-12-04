package joe.chloe.exoplayerfilter;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.daasuu.epf.EPlayerView;
import com.daasuu.mp4compose.FillMode;
import com.daasuu.mp4compose.composer.Mp4Composer;
import com.daasuu.mp4compose.filter.GlFilter;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import joe.chloe.MvComposerActivity;
import joe.chloe.R;
import joe.chloe.util.FilterTypeConverter;
import joe.chloe.util.UriUtil;


public class ExoPlayerFilterActivity extends AppCompatActivity implements VideoFilterAdapter.FilterClickListener {

    private static final String TAG = "ExoPlayerFilterActivity";

    private EPlayerView ePlayerView;
    private SimpleExoPlayer player;
    private Button button;
    private Button merge;
    private SeekBar seekBar;

    private PlayerViewModel playerViewModel;

    private PlayerTimer playerTimer;
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
        setContentView(R.layout.activity_exoplayer_filter);
        setUpViews();

        mVideoUri = getIntent().getData();
        if (mVideoUri!=null){
            mVideoPath = UriUtil.getPath(this,mVideoUri);
        }else {
            finish();
        }
        outPath = PathUtils.getExternalMoviesPath().concat("/").concat(String.valueOf(System.currentTimeMillis())).concat("_testFilter.mp4");


        playerViewModel = ViewModelProviders.of(this).get(PlayerViewModel.class);

        subscriberSeekbar();
    }


    private void subscriberSeekbar(){

        playerViewModel.getSeekBarValue().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer==null){
                    return;
                }
                LogUtils.i(TAG,"  on changed "+integer);
                seekBar.setProgress(integer);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpSimpleExoPlayer();
        setUoGlPlayerView();
        setUpTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        if (playerTimer != null) {
            playerTimer.stop();
            playerTimer.removeMessages(0);
        }
    }

    private void setUpViews() {

        filtersRv = findViewById(R.id.filters_rv);
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

        // play pause
        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) return;

                if (button.getText().toString().equals(ExoPlayerFilterActivity.this.getString(R.string.pause))) {
                    player.setPlayWhenReady(false);
                    button.setText(R.string.play);
                } else {
                    player.setPlayWhenReady(true);
                    button.setText(R.string.pause);
                }
            }
        });
        // merge
        merge = findViewById(R.id.merge);
        merge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCodec();
            }
        });

        // seek
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player == null) return;

                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

    }


    private void setUpSimpleExoPlayer() {

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "chloe"), new DefaultBandwidthMeter());
        MediaSource videoSource =new  ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mVideoUri);

        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                LogUtils.i(TAG," jon ,playWhenReady: "+playWhenReady+",playbackState: "+playbackState);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }


    private void setUoGlPlayerView() {
        ePlayerView = findViewById(R.id.ePlayerView);
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.onResume();

//        ePlayerView = new EPlayerView(this);
//        ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).addView(ePlayerView);
    }


    private void setUpTimer() {
        playerTimer = new PlayerTimer();
        playerTimer.setCallback(new PlayerTimer.Callback() {
            @Override
            public void onTick(long timeMillis) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();

                if (duration <= 0) return;

                seekBar.setMax((int) duration / 1000);
                playerViewModel.setSeekBarValue((int) position / 1000);
//                seekBar.setProgress((int) position / 1000);

            }
        });
        playerTimer.start();
    }

    private void startCodec(){
        mp4Composer = null;

        GlFilter glFilter = FilterTypeConverter.convertToComposerFilter(this,currentFilterType);

        progressDialog.show();

        mp4Composer = new Mp4Composer(mVideoPath, outPath)
                // .rotation(Rotation.ROTATION_270)
                //.size(720, 1280)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(glFilter)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        LogUtils.d(TAG, "onProgress = " + progress);
                        runOnUiThread(() -> progressDialog.setProgress((int) (progress * 100)));
                    }

                    @Override
                    public void onCompleted() {
                        LogUtils.d(TAG, "onCompleted()");
                        MvComposerActivity.exportMp4ToGallery(getApplicationContext(), outPath);
                        runOnUiThread(() -> {
                            progressDialog.setProgress(100);
                            progressDialog.dismiss();
                            ToastUtils.showLong( "codec complete path =" + outPath);
                            viewTheResult();
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


    private void releasePlayer() {
        ePlayerView.onPause();
//        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).removeAllViews();
        ePlayerView = null;
        player.stop();
        player.release();
        player = null;
    }


    private void viewTheResult(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(outPath), "video/mp4");
        startActivity(intent);
    }

    @Override
    public void onFilterClicked(int position, FilterType filterType) {
        currentFilterType = filterType;
        ePlayerView.setGlFilter(FilterType.createGlFilter(currentFilterType, getApplicationContext()));
    }
}