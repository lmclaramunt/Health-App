package com.example.health;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;

public class AddEditSymptom extends AppCompatActivity {
    public static final String SYMPTOM = "symptom";
    public static final String SYMPTOM_POSITION = "symptomPosition";
    private RatingBar symptomBar;
    private Spinner symptomSpinner;
    private Symptom symptom;
    private int symptomRating, position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_symptom);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*.6));


        symptomSpinner = findViewById(R.id.spinner_symptom);
        symptomBar = findViewById(R.id.ratingBar_symptom);
        symptomBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            symptomRating = (int) rating;
        });
        defineIntentions();
    }

    /**
     * Define if we are creating a new Symptom or editing an existing one
     */
    private void defineIntentions(){
        Intent intent = getIntent();
        if (intent.hasExtra(SYMPTOM_POSITION)) {
//            setTitle("App Info");
            position = intent.getIntExtra(SYMPTOM_POSITION, -1);
            symptom = (Symptom) intent.getParcelableExtra(SYMPTOM);
            initializeSymptom();
        } else {
//            setTitle("Add App to Policy");
            symptom = new Symptom("Fever", 0);   //Initialize variables if creating a new App
        }
    }

    private void initializeSymptom(){
        ArrayAdapter<CharSequence> adapter_sym = ArrayAdapter.createFromResource(this, R.array.symptoms_array, android.R.layout.simple_spinner_item);
        int symPosition = adapter_sym.getPosition(symptom.getName());
        symptomSpinner.setSelection(symPosition);
        symptomRating = symptom.getRating();
        symptomBar.setRating(symptomRating);
    }

    /**
     * OnClick listener for the Save Button. Save the Symptom selected by the user
     * @param view - View
     */
    public void saveSymptom(View view) {
        if(symptom == null) return;
        Intent intent = new Intent();
        symptom.setName(symptomSpinner.getSelectedItem().toString());
        symptom.setRating(symptomRating);
        intent.putExtra(SYMPTOM, symptom);
        intent.putExtra(SYMPTOM_POSITION, position);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    /**
     * OnClick listener for the Cancel Button. Cancel the process of adding/editing a symptom
     * @param view - View
     */
    public void cancelSymptom(View view) {
        finish();
    }
}