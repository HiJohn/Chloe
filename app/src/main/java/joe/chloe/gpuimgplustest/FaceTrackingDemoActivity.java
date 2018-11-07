package joe.chloe.gpuimgplustest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.wysaid.camera.CameraInstance;
import org.wysaid.view.CameraGLSurfaceView;
import org.wysaid.view.TrackingCameraGLSurfaceView;

import joe.chloe.R;

public class FaceTrackingDemoActivity extends AppCompatActivity {

    private TrackingCameraGLSurfaceView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_tracking_demo);

        mCameraView = (TrackingCameraGLSurfaceView) findViewById(R.id.myGLSurfaceView);
        mCameraView.presetCameraForward(false);
        mCameraView.presetRecordingSize(480, 640);
        mCameraView.setPictureSize(2048, 2048, true);
        mCameraView.setZOrderMediaOverlay(true);

        mCameraView.setOnCreateCallback(new CameraGLSurfaceView.OnCreateCallback() {
            @Override
            public void createOver() {
                FaceTrackingDemo demo = new FaceTrackingDemo();
                mCameraView.setTrackingProc(demo);
            }
        });
    }

    public void switchCamera(View view) {
        mCameraView.switchCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInstance.getInstance().stopCamera();
        mCameraView.release(null);
        mCameraView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }


}
