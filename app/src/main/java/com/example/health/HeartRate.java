package com.example.health;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


/**
 * @author Luis M. Claramunt
 * Measure user's heart rate using camera and flashlight
 */
public class HeartRate extends AppCompatActivity {
    public static final String BPM = "bpm";
    private static final long TIMER = 50000;
    private static final int CAMERA_PERM = 1;
    private long timeLeft = TIMER;
    private Size measurements;
    private TextureView txtureV;
    protected CameraDevice camDev;
    private CountDownTimer countDownTimer;
    private int currentAvg, lastAvg, oldAvg, bpm = -1,
            surfaceUpdate = 0;
    private TextView tvTimer, tvBpm;
    private ArrayList<Long> timeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Heart Rate");
        setContentView(R.layout.activity_heart_rate);
        tvTimer = findViewById(R.id.tvTimer);
        tvBpm = findViewById(R.id.tvMeasBpm);
        txtureV =  findViewById(R.id.textureView);
        txtureV.setSurfaceTextureListener(textureListener);
        timeList = new ArrayList<>();
    }

    /**
     * Start Timer so user know how much time is left
     * measuring heart rate
     */
    private void startTimer(){
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimer();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                bpm = calcHeartRate();
                tvBpm.setText(bpm + " BPM");
            }
        }.start();
    }

    /**
     * Update the TextView that displays the Timer's Countdown
     */
    private void updateTimer(){
        int min = (int) (timeLeft /1000)/60;
        int sec = (int) (timeLeft /1000)%60;
        String timeLeftString = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
        tvTimer.setText(timeLeftString);
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                getCamera();
                startTimer();
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
            int redPixels = getRedPixels();
            //Give 5 seconds for the user to set up
            if (surfaceUpdate == 100) {
                Toast.makeText(HeartRate.this, "Starting Measurements", Toast.LENGTH_SHORT).show();
                currentAvg = redPixels;
            }else if(surfaceUpdate > 100 && surfaceUpdate < 139){
                currentAvg = (currentAvg *(surfaceUpdate-100) + redPixels)/(surfaceUpdate-99);
            }else if(surfaceUpdate >= 139) {
                currentAvg = (currentAvg * 39 + redPixels) / (40);
                if (lastAvg > currentAvg && lastAvg > oldAvg) {
                    timeList.add(System.currentTimeMillis());       //There has been an increase in red pixels
                }
            }
            oldAvg = lastAvg;
            lastAvg = currentAvg;
            surfaceUpdate++;
        }
    };

    /**
     * Get number of red pixels, used to calculate the bpm/heart rate
     * @return - number of red pixels in bitmap
     */
    private int getRedPixels(){
        int totalRedPix = 0;
        Bitmap bmp = txtureV.getBitmap();
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[height * width];
        bmp.getPixels(pixels, 0, width, width / 2, height / 2, width / 20, height / 20);
        for (int i = 0; i < height * width; i++) {
            totalRedPix += (pixels[i] >> 16) & 0xFF;;
        }
        return totalRedPix;
    }

    private final CameraDevice.StateCallback stCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            camDev = camera;
            try {
                SurfaceTexture texture = txtureV.getSurfaceTexture();
                texture.setDefaultBufferSize(measurements.getWidth(), measurements.getHeight());
                Surface surface = new Surface(texture);
                CaptureRequest.Builder captReq = camDev.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captReq.addTarget(surface);
                camDev.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        captReq.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                        captReq.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                        try {
                            Handler backHandler = null;
                            cameraCaptureSession.setRepeatingRequest(captReq.build(), null, backHandler);
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

        @Override
        public void onDisconnected(CameraDevice camera) {
            camDev.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {}
    };

    private int calcHeartRate() {
        int avg = 0;
        for(int i = 0; i < timeList.size()-1; i++){
            avg += timeList.get(i+1) - timeList.get(i);
        }
        return 60000/(avg/timeList.size());
    }


    /**
     * Get camera permissions and set it up to measure the heart rate
     * @throws CameraAccessException - Camera Exception
     */
    private void getCamera() throws CameraAccessException {
        CameraManager camMan = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String id = camMan.getCameraIdList()[0];
        measurements = camMan.getCameraCharacteristics(id)
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                .getOutputSizes(SurfaceTexture.class)[0];
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM);
            return;
        }
        camMan.openCamera(id, stCallBack, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Cannot operate without permissions", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void closeCamera() {
        if (null != camDev) {
            camDev.close();
            camDev = null;
        }
    }

    /**
     * OnClick listener for the Save button
     * @param view - for button
     */
    public void saveBpm(View view) {
        if(bpm > 0) {
            closeCamera();
            Intent intent = new Intent();
            intent.putExtra(BPM, bpm);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else{
            Toast.makeText(HeartRate.this, "Heart Rate has not been measured",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
