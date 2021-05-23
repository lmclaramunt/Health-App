package com.example.health;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class RespRate extends Service implements SensorEventListener {
    public static final String RESP_RATE = "respRate", GET_RESP_RATE = "getRespRate";
    private SensorManager sensorManager;
    private Sensor sensor;
    private int index = 0;
    private float ee = 0;
    private final int time = 216;
    private float xVal[] = new float[time];
    private float yVal[] = new float[time];
    private float zVal[] = new float[time];
    private float accel[] = new float[time-1];

    public RespRate() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            xVal[index] = event.values[0];
            yVal[index] = event.values[1];
            zVal[index] = event.values[2];
            index++;
            if(index >= time){
                sensorManager.unregisterListener(this);
                calcRespRate();
            }
        }
    }

    /**
     * Calculate the respiratory rate
     */
    private void calcRespRate(){
        for(int i = 0; i < time -1; i++){
            double xPow = Math.pow(xVal[i+1] - xVal[i], 2);
            double yPow = Math.pow(yVal[i+1] - yVal[i], 2);
            double zPow = Math.pow(zVal[i+1] - zVal[i], 2);
            accel[i] = (float) Math.sqrt(xPow + yPow + zPow);
            ee += accel[i];
        }
        sendBroadcastMessage(ee);
    }

    /**
     * Send results of respiratory rate back to activity, in order to update TextView
     * @param ee - Respiratory Rate
     */
    private void sendBroadcastMessage(float ee) {
        Intent intent = new Intent(GET_RESP_RATE);
        intent.putExtra(RESP_RATE, ee);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}