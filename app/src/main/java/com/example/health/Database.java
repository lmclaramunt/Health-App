package com.example.health;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * SQLite Database
 * @author Luis M. Claramunt
 * June 2021
 */
public class Database extends SQLiteOpenHelper {
    public static final String DATABASE = "Health.db";
    public static final String TABLE = "health_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Date";
    public static final String COL_3 = "BPM";
    public static final String COL_4 = "Respiratory_Rate";
    public static final String COL_5 = "Nausea";
    public static final String COL_6 = "Headache";
    public static final String COL_7 = "Diarrhea";
    public static final String COL_8 = "Sore_Throat";
    public static final String COL_9 = "Fever";
    public static final String COL_10 = "Muscle_Ache";
    public static final String COL_11 = "Loss_of_Smell_or_Taste";
    public static final String COL_12 = "Cough";
    public static final String COL_13 = "Shortness_of_Breath";
    public static final String COL_14 = "Feeling_Tired";

    public Database(@Nullable Context context) {
        super(context, DATABASE, null, 1);
//        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 +" INTEGER, " + COL_3 +" INTEGER, " + COL_4 +" INTEGER, " + COL_5 +" INTEGER, " +
                COL_6 +" INTEGER, " + COL_7 +" INTEGER, " + COL_8 +" INTEGER, " + COL_9 +" INTEGER, " +
                COL_10 +" INTEGER, " + COL_11 +" INTEGER, " + COL_12 +" INTEGER, " + COL_13 +" INTEGER, " +
                COL_14 +" INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /**
     * Insert data into the SQLite database
     * @return - boolean, true if successful
     */
    public boolean insertData(String date, int bpm, int respRate, int nausea, int headache,
                              int diarrhea, int soreThroat, int fever, int muscleAche, int smellTaste,
                              int cough, int shortBreath, int tired){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, date);
        contentValues.put(COL_3, bpm);
        contentValues.put(COL_4, respRate);
        contentValues.put(COL_5, nausea);
        contentValues.put(COL_6, headache);
        contentValues.put(COL_7, diarrhea);
        contentValues.put(COL_8, soreThroat);
        contentValues.put(COL_9, fever);
        contentValues.put(COL_10, muscleAche);
        contentValues.put(COL_11, smellTaste);
        contentValues.put(COL_12, cough);
        contentValues.put(COL_13, shortBreath);
        contentValues.put(COL_14, tired);
        long inserted = db.insert(TABLE, null, contentValues);
        return inserted != -1;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE, null);
        return cursor;
    }
}
