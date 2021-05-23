package com.example.health;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * CSE 535 - Assignment 1
 * @author Luis Claramunt
 * June, 2021
 * Menu where user can go measure health conditions and
 * check recorded data
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Launch activity in charge of recording new health info
     * @param view - View
     */
    public void launchHealth(View view) {
        Intent intent = new Intent(this, Health_Rates.class);
        startActivity(intent);
    }
}