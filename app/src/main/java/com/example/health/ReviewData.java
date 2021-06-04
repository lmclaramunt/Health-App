package com.example.health;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Display data on the SQLite database
 * @author Luis Claramunt
 * June 2021
 */
public class ReviewData extends AppCompatActivity {

    private Database db;
    private ArrayList<HealthData> reviewData;
    private HealthDataRecyclerViewAdapter mAdapter;      //Bridge between data and RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Records");
        setContentView(R.layout.activity_review_data);
        db = new Database(this);
        reviewData = new ArrayList<>();
        getData();
        initializeRecyclerView();
    }

    /**
     * Select which menu to be displayed in the toolbar
     * @param menu - menu
     * @return - boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
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

    private void getData(){
        Cursor data = db.getAllData();
        while (data.moveToNext()){
            reviewData.add(new HealthData(data.getString(1), data.getInt(2),
                    data.getInt(3), data.getInt(4), data.getInt(5),
                    data.getInt(6), data.getInt(7), data.getInt(8),
                    data.getInt(9), data.getInt(10), data.getInt(11),
                    data.getInt(12), data.getInt(13)));
        }
    }

    /**
     * Initialize RecyclerView, adapter, swipe function, etc.
     */
    private void initializeRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view_data);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new HealthDataRecyclerViewAdapter(reviewData);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }
}