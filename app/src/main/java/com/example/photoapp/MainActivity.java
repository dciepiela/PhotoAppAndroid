package com.example.photoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addPhoto_button;
    private RecyclerView recyclerView;

    //DB helper
    MyDatabaseHelper myDB;

    //action bar
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init views
        addPhoto_button = findViewById(R.id.addPhoto_button);
        recyclerView = findViewById(R.id.recyclerView);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Lista zdjęć");

        //init db helper
        myDB = new MyDatabaseHelper(this);

        //load records
        loadRecords();

        addPhoto_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPhotoActivity.class);
                intent.putExtra("isEditMode", false); //want to add new date, set false
                startActivityForResult(intent, 1);
            }
        });
    }

    private void loadRecords() {
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this,
                myDB.getAllRecords(ConstantsDb.COLUMN_ADDED_TIMESTAMP + " DESC"));

        recyclerView.setAdapter(customAdapter);

        //set num of records
        actionBar.setSubtitle("Ilość wszystkich zdjęć: " + myDB.getRecordsCount());
    }

    private void searchRecords(String query){
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this,
                myDB.searchRecords(query));

        recyclerView.setAdapter(customAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords(); //refresh records list
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        //search view
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //search when search button on keyboard clicked
                searchRecords(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //search as you type
                searchRecords(newText);
                return true;
            }
        });
        //searchView.setQueryHint("Type here to search");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle menu items
        return super.onOptionsItemSelected(item);
    }
}