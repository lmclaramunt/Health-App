package com.example.health;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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
    Database db;        //SQLite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Database(this);
    }

    /**
     * Launch activity in charge of recording new health info
     * @param view - View
     */
    public void launchHealth(View view) {
        Intent intent = new Intent(this, Health_Rates.class);
        startActivity(intent);
    }

    public void launchRecords(View view) {
        Cursor cursor = db.getAllData();
        if(cursor.getCount() == 0){
            showMessage("Error", "No data");
            return;
        }else{
            StringBuffer buffer = new StringBuffer();
            while (cursor.moveToNext()){
                buffer.append("Date: " + cursor.getString(1)+"\n");
                buffer.append("BPM: " + cursor.getString(2)+"\n");
                buffer.append("Nausea: " + cursor.getString(4)+"\n");
            }

            showMessage("Data", buffer.toString());
        }
    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}