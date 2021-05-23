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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * @author Luis Claramunt
 * User starts recording health conditions
 * In this activity user measures heart & respiratory rate
 */
public class Health_Rates extends AppCompatActivity  {
    public static final int GET_SYMPTOMS_LIST = 0;
    private Sensor heartSensor;
    private TextView txtHeartRate, txtRespRate, txtSymptoms;
    private RecyclerView recyclerViewSym;
    private float respRate;
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
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList(SymptomsList.SYMPTOMS_LIST, symptoms);
//        intent.putExtras(bundle);
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
}