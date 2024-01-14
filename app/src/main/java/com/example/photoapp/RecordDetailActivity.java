package com.example.photoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class RecordDetailActivity extends AppCompatActivity {

    //views
    private ImageView imageViewPhoto;
    private TextView title_text, description_text,keyWords_text,addedTime_text,updatedTime_text,address_text;

    //actionbar
    private ActionBar actionBar;

    //db helper
    private MyDatabaseHelper myDB;

    private String recordID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        //setting actionbar
        actionBar = getSupportActionBar();
        actionBar.setSubtitle("Szczegóły zdjęcia");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //get record id from adapter
        Intent intent = getIntent();
        recordID = intent.getStringExtra("RECORD_ID");

        //init db helper
        myDB = new MyDatabaseHelper(this);

        //init views
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        title_text = findViewById(R.id.title_text);
        description_text = findViewById(R.id.description_text);
        keyWords_text = findViewById(R.id.keyWords_text);
        addedTime_text = findViewById(R.id.addedTime_text);
        updatedTime_text = findViewById(R.id.updatedTime_text);
        address_text = findViewById(R.id.address_text);

        showRecordDetails();
    }

    private void showRecordDetails() {
        //get record details

        //query to select record based on record id
        String selectQuery = "SELECT * FROM " + ConstantsDb.TABLE_NAME + " WHERE " + ConstantsDb.COLUMN_ID + " =\"" + recordID +"\""; //moze zle

        SQLiteDatabase db = myDB.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //keep checking in whole db for that record
        if(cursor.moveToFirst()){
            do{
                //get data
                @SuppressLint("Range") String id = "" + cursor.getInt(cursor.getColumnIndex(ConstantsDb.COLUMN_ID));
                @SuppressLint("Range") String title = "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_TITLE));
                @SuppressLint("Range") String image = "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_IMAGE));
                @SuppressLint("Range") String description = "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_DESCRIPTION));
                @SuppressLint("Range") String keyWords = "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_KEY_WORDS));
                @SuppressLint("Range") String timestampAdded = "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_ADDED_TIMESTAMP));
                @SuppressLint("Range") String timestampUpdated = "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_UPDATE_TIMESTAMP));
                @SuppressLint("Range") String address = "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_ADDRESS));

                //convert timestamp
                Calendar calendar1 = Calendar.getInstance(Locale.getDefault());
                calendar1.setTimeInMillis(Long.parseLong(timestampAdded));
                String timeAdded ="" + DateFormat.format("dd/MM/yyyy hh:mm:aa",calendar1);

                Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
                calendar1.setTimeInMillis(Long.parseLong(timestampUpdated));
                String timeUpdated ="" + DateFormat.format("dd/MM/yyyy hh:mm:aa",calendar2);

                //set data
                title_text.setText(title);
                description_text.setText(description);
                keyWords_text.setText(keyWords);
                addedTime_text.setText(timeAdded);
                updatedTime_text.setText(timeUpdated);
                address_text.setText(address);
                if(image.equals("null")){
                    //no image
                    imageViewPhoto.setImageResource(R.drawable.ic_photo);

                } else{
                    imageViewPhoto.setImageURI(Uri.parse(image));
                }            }while (cursor.moveToNext());
        }
        //close db connection
        db.close();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //go to previous activity
        return super.onSupportNavigateUp();
    }
}