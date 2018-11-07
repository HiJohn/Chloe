package joe.chloe;

import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;

import joe.chloe.drawer.GLRenderer;

public class GLPreviewActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private GLRenderer glRenderer;

    private String videoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glpreview);


        videoPath = getIntent().getStringExtra("path");
        if (!TextUtils.isEmpty(videoPath)) {
            glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_preview_view);
            glSurfaceView.setEGLContextClientVersion(2);
            glRenderer = new GLRenderer(this, videoPath);
            glSurfaceView.setRenderer(glRenderer);
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        } else {
            ToastUtils.showLong("视频文件地址不对");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (glRenderer != null) {
            glRenderer.getMediaPlayer().release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glRenderer != null) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glRenderer != null) {
            glSurfaceView.onPause();
            glRenderer.getMediaPlayer().pause();
        }
    }
}
