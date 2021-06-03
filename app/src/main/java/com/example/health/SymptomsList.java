package com.example.health;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * @author Luis M. Claramunt
 * Display Symptoms selected by the user
 */
public class SymptomsList extends AppCompatActivity {
    public static final String SYMPTOMS_LIST = "symList";
    public static final int CREATE_SYMPTOM = 1;
    public static final int EDIT_SYMPTOM = 2;

    private ArrayList<Symptom> symptomList;
    private SymptomsRecyclerViewAdapter mAdapter;      //Bridge between data and RecyclerView


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Symptoms");
        setContentView(R.layout.activity_symptoms_list);
        Intent intent = getIntent();
        symptomList = intent.getParcelableArrayListExtra(SYMPTOMS_LIST);
        initializeRecyclerView();
    }

    /**
     * Initialize RecyclerView, adapter, swipe function, etc.
     */
    private void initializeRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_symptoms);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SymptomsRecyclerViewAdapter(symptomList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(SymptomsList.this, AddEditSymptom.class);
            intent.putExtra(AddEditSymptom.SYMPTOM, (Parcelable) symptomList.get(position));
            intent.putExtra(AddEditSymptom.SYMPTOM_POSITION, position);      //Position in the RecyclerView
            startActivityForResult(intent, EDIT_SYMPTOM);
        });
        //Add swipe functionality to delete Symptoms
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                alertDialog(position);      //AlertDialog to confirm if the user wants to delete symptom
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    /**
     * Select which menu to be displayed in the toolbar
     * @param menu - menu
     * @return - boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);       //RecyclerView filter
                return false;
            }
        });

        return true;
    }

    /**
     * Handle when actionbar menu items are selected
     * @param item - selected
     * @return - boolean
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_plus: {
                Intent intent = new Intent(SymptomsList.this, AddEditSymptom.class);
                startActivityForResult(intent, CREATE_SYMPTOM);
                return true;
            }
            case android.R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handle the information returned from the activity to create/edit an Symptom
     * To initialize a new Symptom and add it to the Symptom ListView
     * @param requestCode - why the activity was opened
     * @param resultCode - result code from activity, e.g. OK/CANCEL
     * @param data - data return from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CREATE_SYMPTOM && resultCode == Activity.RESULT_OK){
            assert data != null;
            Symptom symptom = (Symptom) data.getParcelableExtra(AddEditSymptom.SYMPTOM);
            int index = getSymptom(symptom.getName());
            if(index != -1){                            //The symptom already exists, so update it
                symptomList.set(index, symptom);
                mAdapter.symptomEdited(index, symptom);
                Toast.makeText(SymptomsList.this,  symptom.getName() + " was updated",
                        Toast.LENGTH_SHORT).show();
            }else {
                symptomList.add(symptom);
                mAdapter.symptomCreated(symptom);
            }
        }else if(requestCode == EDIT_SYMPTOM && resultCode == Activity.RESULT_OK){
            assert data != null;
            Symptom symptom = (Symptom) data.getParcelableExtra(AddEditSymptom.SYMPTOM);
            int position = data.getIntExtra(AddEditSymptom.SYMPTOM_POSITION, -1);    //We'll get position >= 0 in the ArrayList only if editing an App
            try {
                symptomList.set(position, symptom);
            }catch (Exception e){
                Toast.makeText(SymptomsList.this,  e.toString(), Toast.LENGTH_SHORT).show();
            }
            mAdapter.symptomEdited(position, symptom);
        }
    }

    /**
     * Display Alert Dialog so the user can confirm if he/she really wants to delete app
     * @param position - position in the RecyclerView
     */
    private void alertDialog(int position){
        String symptomName = symptomList.get(position).getName();
        AlertDialog.Builder builder = new AlertDialog.Builder(SymptomsList.this);
        builder.setTitle(R.string.please_confirm);
        String message = "Remove: " + symptomName + "?";
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            symptomList.remove(position);
            mAdapter.symptomRemoved(position);
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> mAdapter.notifyItemChanged(position));
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    /**
     * Return List of Symptoms Selected by the User
     * @param view - View
     */
    public void submitSymptoms(View view) {
        if(symptomList == null)
            symptomList = new ArrayList<>();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SYMPTOMS_LIST, symptomList);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Look for a symptom in the ArrayList. If the Symptom is present will return the index,
     * otherwise it will return -1
     * @param name - name of the symptom
     * @return - symptom's index in the ArrayList if present
     */
    private int getSymptom(String name){
        for(int i = 0; i <symptomList.size(); i++){
            if(symptomList.get(i).getName().equals(name)){
                return i;
            }
        }
        return -1;
    }
}