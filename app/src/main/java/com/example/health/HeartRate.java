package com.example.health;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.Collections;


/**
 * @author Luis M. Claramunt
 * Measure user's heart rate using camera and flashlight
 */
public class HeartRate extends AppCompatActivity {
    private TextureView txtureV;        //TextureView to deploy camera data
    protected CameraDevice camDev;
    protected CameraCaptureSession camCaptSess;
    protected CaptureRequest.Builder captReq;
    private Size measurements;
    private static final int CAMERA_PERM = 1;

    // Thread handler member variables
    private Handler backHandler;
    private HandlerThread backThread;

    //Heart rate detector member variables
    public static int hrtratebpm;
    private int mCurrentRollingAverage;
    private int mLastRollingAverage;
    private int mLastLastRollingAverage;
    private long [] mTimeArray;
    private int numCaptures = 0;
    private int mNumBeats = 0;
    TextView txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        txtureV =  findViewById(R.id.texture);
        txtureV.setSurfaceTextureListener(textureListener);
        mTimeArray = new long [15];
        txt = findViewById(R.id.neechewalatext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        try {
            openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        if(camDev != null){
            camDev.close();
            camDev = null;
        }
        try {
            stopBackgroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    protected void startBackgroundThread() {
        backThread = new HandlerThread("Camera_Thread");
        backThread.start();
        backHandler = new Handler(backThread.getLooper());
    }

    protected void stopBackgroundThread() throws InterruptedException {
        backThread.quitSafely();
        backThread.join();
        backThread = null;
        backHandler = null;
    }


    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            Bitmap bmp = txtureV.getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = new int[height * width];
            // Get pixels from the bitmap, starting at (x,y) = (width/2,height/2)
            // and totaling width/20 rows and height/20 columns
            bmp.getPixels(pixels, 0, width, width / 2, height / 2, width / 20, height / 20);
            int sum = 0;
            for (int i = 0; i < height * width; i++) {
                int red = (pixels[i] >> 16) & 0xFF;
                sum = sum + red;
            }
            // Waits 20 captures, to remove startup artifacts.  First average is the sum.
            if (numCaptures == 20) {
                mCurrentRollingAverage = sum;
            }
            // Next 18 averages needs to incorporate the sum with the correct N multiplier
            // in rolling average.
            else if (numCaptures > 20 && numCaptures < 49) {
                mCurrentRollingAverage = (mCurrentRollingAverage*(numCaptures-20) + sum)/(numCaptures-19);
            }
            // From 49 on, the rolling average incorporates the last 30 rolling averages.
            else if (numCaptures >= 49) {
                mCurrentRollingAverage = (mCurrentRollingAverage*29 + sum)/30;
                if (mLastRollingAverage > mCurrentRollingAverage && mLastRollingAverage > mLastLastRollingAverage && mNumBeats < 15) {
                    mTimeArray[mNumBeats] = System.currentTimeMillis();
//                    tv.setText("beats="+mNumBeats+"\ntime="+mTimeArray[mNumBeats]);
                    mNumBeats++;
                    if (mNumBeats == 15) {
                        calcBPM();
                    }
                }
            }

            // Another capture
            numCaptures++;
            // Save previous two values
            mLastLastRollingAverage = mLastRollingAverage;
            mLastRollingAverage = mCurrentRollingAverage;
        }
    };

    private final CameraDevice.StateCallback stCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            camDev = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            camDev.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            if (camDev != null)
                camDev.close();
            camDev = null;
        }
    };

    @SuppressLint("SetTextI18n")
    private void calcBPM() {
        int med;
        long [] timedist = new long [14];
        for (int i = 0; i < 14; i++) {
            timedist[i] = mTimeArray[i+1] - mTimeArray[i];
        }
        Arrays.sort(timedist);
        med = (int) timedist[timedist.length/2];
        hrtratebpm= 60000/med;
        TextView tv = findViewById(R.id.neechewalatext);
        tv.setText("Heart Rate = "+hrtratebpm+" BPM");
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = txtureV.getSurfaceTexture();
            texture.setDefaultBufferSize(measurements.getWidth(), measurements.getHeight());
            Surface surface = new Surface(texture);
            captReq = camDev.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captReq.addTarget(surface);
            camDev.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        camCaptSess = cameraCaptureSession;
                        captReq.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        captReq.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                        try {
                            camCaptSess.setRepeatingRequest(captReq.build(), null, backHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {}

            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // Opening the rear-facing camera for use
    private void openCamera() throws CameraAccessException {
        CameraManager camMang = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String id = camMang.getCameraIdList()[0];
        CameraCharacteristics characteristics = camMang.getCameraCharacteristics(id);
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        measurements = map.getOutputSizes(SurfaceTexture.class)[0];
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM);
            return;
        }
        camMang.openCamera(id, stCallBack, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Cannot operate without permissions", Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
