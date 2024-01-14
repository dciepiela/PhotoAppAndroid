package com.example.photoapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    MyDatabaseHelper(@Nullable Context context) {
        super(context, ConstantsDb.DATABASE_NAME, null, ConstantsDb.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConstantsDb.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + ConstantsDb.TABLE_NAME);
        //create table again
        onCreate(db);
    }

    public long insertRecord(String title, String image, String description, String keyWords,
                             String addedTime, String updatedTime, String address){

        //get writetable database (to write data)
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        //insert data
        values.put(ConstantsDb.COLUMN_TITLE, title);
        values.put(ConstantsDb.COLUMN_IMAGE, image);
        values.put(ConstantsDb.COLUMN_DESCRIPTION, description);
        values.put(ConstantsDb.COLUMN_KEY_WORDS, keyWords);
        values.put(ConstantsDb.COLUMN_ADDED_TIMESTAMP, addedTime);
        values.put(ConstantsDb.COLUMN_UPDATE_TIMESTAMP, updatedTime);
        values.put(ConstantsDb.COLUMN_ADDRESS, address);

        //insert row, it will return record id of saved record
        long id = db.insert(ConstantsDb.TABLE_NAME, null, values);

        //close db connection
        db.close();

        //return id of inserted record
        return id;
    }

    public void updateRecord(String id, String title, String image, String description, String keyWords,
                             String addedTime, String updatedTime, String address){

        //get writetable database (to write data)
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        //insert data
        values.put(ConstantsDb.COLUMN_TITLE, title);
        values.put(ConstantsDb.COLUMN_IMAGE, image);
        values.put(ConstantsDb.COLUMN_DESCRIPTION, description);
        values.put(ConstantsDb.COLUMN_KEY_WORDS, keyWords);
        values.put(ConstantsDb.COLUMN_ADDED_TIMESTAMP, addedTime);
        values.put(ConstantsDb.COLUMN_UPDATE_TIMESTAMP, updatedTime);
        values.put(ConstantsDb.COLUMN_ADDRESS, address);

        //insert row, it will return record id of saved record
        db.update(ConstantsDb.TABLE_NAME, values,ConstantsDb.COLUMN_ID + " = ?",
                new String[]{id});

        //close db connection
        db.close();
    }

    //get all data
    public ArrayList<ModelRecord> getAllRecords(String orderBy){
        ArrayList<ModelRecord> recordsList = new ArrayList<>();
        //query to select records
        String selectQuery = "SELECT * FROM " + ConstantsDb.TABLE_NAME + " ORDER BY " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        //looping through all records and add to list
        if (cursor.moveToFirst()){
            do {
                @SuppressLint("Range") ModelRecord modelRecord = new ModelRecord(
                        "" + cursor.getInt(cursor.getColumnIndex(ConstantsDb.COLUMN_ID)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_TITLE)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_IMAGE)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_DESCRIPTION)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_KEY_WORDS)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_ADDED_TIMESTAMP)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_UPDATE_TIMESTAMP)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_ADDRESS))
                );
                //add record to list
                recordsList.add(modelRecord);
            }while (cursor.moveToNext());
        }
        //close db connection
        db.close();

        //return the list
        return recordsList;
    }

    //search data
    public ArrayList<ModelRecord> searchRecords(String query){

        ArrayList<ModelRecord> recordsList = new ArrayList<>();
        //query to select records
        String selectQuery = "SELECT * FROM " + ConstantsDb.TABLE_NAME + " WHERE " +
                ConstantsDb.COLUMN_TITLE + " LIKE '%" + query + "%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        //looping through all records and add to list
        if (cursor.moveToFirst()){
            do {
                @SuppressLint("Range") ModelRecord modelRecord = new ModelRecord(
                        "" + cursor.getInt(cursor.getColumnIndex(ConstantsDb.COLUMN_ID)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_TITLE)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_IMAGE)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_DESCRIPTION)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_KEY_WORDS)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_ADDED_TIMESTAMP)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_UPDATE_TIMESTAMP)),
                        "" + cursor.getString(cursor.getColumnIndex(ConstantsDb.COLUMN_ADDRESS))
                );
                //add record to list
                recordsList.add(modelRecord);
            }while (cursor.moveToNext());
        }
        //close db connection
        db.close();

        //return the list

        return recordsList;
    }

    //get number of records
    public int getRecordsCount(){
        String countQuery = "SELECT * FROM " + ConstantsDb.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        return count;
    }
}
