package com.example.photoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addPhoto_button;
    private RecyclerView recyclerView;

    //DB helper
    private MyDatabaseHelper myDB;

    //action bar
    private ActionBar actionBar;

    //sort options
    private final String orderByNewest = ConstantsDb.COLUMN_ADDED_TIMESTAMP + " DESC";
    private final String orderByOldest = ConstantsDb.COLUMN_ADDED_TIMESTAMP + " ASC";
    private final String orderByTitleDesc = ConstantsDb.COLUMN_TITLE + " DESC";
    private final String orderByTitleAsc = ConstantsDb.COLUMN_TITLE + " ASC";

    //for refreshing records, refresh with last choosen sort option
    private String currentOrderByStatus = orderByNewest;


    //empty
    private ImageView empty_imageView;
    private TextView no_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //empty list
        empty_imageView = findViewById(R.id.empty_imageView);
        no_data = findViewById(R.id.no_data_text);

        //init views
        addPhoto_button = findViewById(R.id.addPhoto_button);
        recyclerView = findViewById(R.id.recyclerView);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Lista zdjęć");
        }

        //init db helper
        myDB = new MyDatabaseHelper(this);

        //load records (by default newest first)
        loadRecords(orderByNewest);

        addPhoto_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddAndUpdatePhotoActivity.class);
            intent.putExtra("isEditMode", false); //want to add new date, set false
            //startActivityForResult(intent, 1);
            startActivity(intent);
        });
    }

    private void loadRecords(String orderBy) {
        if(myDB.getRecordsCount() == 0){
            //Toast.makeText(this,"No data.", Toast.LENGTH_SHORT).show();
            empty_imageView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }

        currentOrderByStatus = orderBy;

        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this,
                myDB.getAllRecords(orderBy));

        recyclerView.setAdapter(customAdapter);

        //set num of records
        actionBar.setSubtitle("Ilość wszystkich zdjęć: " + myDB.getRecordsCount());
    }

    private void searchRecords(String query){
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this,
                myDB.searchRecords(query));

        recyclerView.setAdapter(customAdapter);
        if ( myDB.searchRecords(query).isEmpty()) {
            empty_imageView.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            empty_imageView.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecords(currentOrderByStatus); //refresh records list
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
        int id = item.getItemId();
        if(id == R.id.action_sort){
            //show sort options (show in dialog)
            sortOptionDialog();
        }
        else if(id == R.id.action_deleteAll){
            //delete all records
            confirmDeleteDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usunąć?");
        builder.setMessage("Jesteś pewny, że chcesz usunąć wsszystkie zdjęcia?");

        builder.setNegativeButton("Nie", (dialog, which) -> {

        });
        builder.setPositiveButton("Tak", (dialog, which) -> {
            myDB.deleteAllRecords();
            //Refresh activity
            onResume();
        });


        builder.create().show();
    }

    private void sortOptionDialog() {
        //option to display in dialog
        String[] options = {"Tytuł (A-Z)", "Tytuł (Z-A)", "Najnowsze", "Najstarsze"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sortuj według:")
                .setItems(options, (dialog, which) -> {
                    //handle option click
                    if(which == 0){
                        //title asc
                        loadRecords(orderByTitleAsc);
                    }
                    else if(which == 1){
                        //title desc
                        loadRecords(orderByTitleDesc);
                    }
                    else if(which == 2){
                        //newest
                        loadRecords(orderByNewest);
                    }
                    else if(which == 3){
                        //oldest
                        loadRecords(orderByOldest);
                    }
                })
                .create().show(); //show dialog
    }
}