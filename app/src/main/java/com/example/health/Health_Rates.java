package com.example.health;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Luis Claramunt
 * User starts recording health conditions
 * In this activity user measures heart & respiratory rate
 * Stores resutls in SQLite database
 */
public class Health_Rates extends AppCompatActivity  {
    private Database db;        //SQLite
    public static final int GET_SYMPTOMS_LIST = 0;
    public static final int GET_HEART_RATE = 1;
    private TextView txtHeartRate, txtRespRate, txtSymptoms;
    private float respRate = 8;
    private int bpm = 68;
    private ArrayList<Symptom> symptoms;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_rates);
        setTitle("Measurements");
        txtHeartRate = findViewById(R.id.txtHeartRate);
        txtRespRate = findViewById(R.id.txtRespRate);
        txtSymptoms = findViewById(R.id.tv_health_symptoms);
        symptoms = new ArrayList<>();
        txtSymptoms.setText(R.string.none);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        respRate = intent.getFloatExtra(RespRate.RESP_RATE, 0);
                        txtRespRate.setText("%.2f", TextView.BufferType.valueOf(Float.toString( respRate)));
                    }
                }, new IntentFilter(RespRate.GET_RESP_RATE)
        );
        db = new Database(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, RespRate.class));
    }

    /**
     * Measure Heart Rate
     * @param view - View
     */
    public void measureHeart(View view) {
        Intent heartIntent = new Intent(this, HeartRate.class);
        startActivityForResult(heartIntent, GET_HEART_RATE);
    }


    /**
     * Measure Respiratory Rate
     * @param view - View
     */
    public void measureResp(View view) {
        startService(new Intent(Health_Rates.this, RespRate.class));
    }

    /**
     * Open Activity where user adds symptoms
     * @param view - View
     */
    public void launchSymptoms(View view) {
        Intent intent = new Intent(this, SymptomsList.class);
        intent.putParcelableArrayListExtra(SymptomsList.SYMPTOMS_LIST, symptoms);
        startActivityForResult(intent, GET_SYMPTOMS_LIST);
    }

    /**
     * Handle the information returned from the activity to handle Symptoms
     * To initialize a new Symptom and add it to the Symptom ListView
     * @param requestCode - why the activity was opened
     * @param resultCode - result code from activity, e.g. OK/CANCEL
     * @param data - data return from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_SYMPTOMS_LIST && resultCode == Activity.RESULT_OK) {
            assert data != null;
            Bundle bundle = data.getExtras();
            symptoms = bundle.getParcelableArrayList(SymptomsList.SYMPTOMS_LIST);
            if (symptoms.isEmpty()) {
                txtSymptoms.setText(R.string.none);
            } else {
                StringBuilder sym = new StringBuilder();
                for (Symptom s : symptoms) {
                    sym.append(s.toString()).append("\n");
                }
                txtSymptoms.setText(sym.toString());
            }
        }
    }

    /**
     * Find if symptom was selected and get it rating (0-5)
     * @param name - Symptom name
     * @return - Symptom rating
     */
    private int getSymptomRating(String name){
        for(Symptom symptom: symptoms) {
            if (symptom.getName().equals(name))
                return symptom.getRating();
        }
        return 0;
    }

    /**
     * Upload Health information into SQLite database
     * @param view - for button
     */
    public void uploadMeasures(View view) {
        Date currentTime = Calendar.getInstance().getTime();
        int[] symptomsRatings = new int[10];
        String[] symptomsNames = getResources().getStringArray(R.array.symptoms_array);
        for(int i=0; i < 10; i++){
            symptomsRatings[i] = getSymptomRating(symptomsNames[i]);
        }
        boolean inserted = db.insertData(currentTime.toString(), bpm, Math.round(respRate), symptomsRatings[0], symptomsRatings[1],
                symptomsRatings[2],symptomsRatings[3], symptomsRatings[4], symptomsRatings[5], symptomsRatings[6],
                symptomsRatings[7], symptomsRatings[8], symptomsRatings[9]);
        String message = (inserted) ? "Data saved successfully!" : "Error occurred, could not save data";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}