package com.example.photoapp;

public class ConstantsDb {
    public static final String DATABASE_NAME = "PhotoLibrary.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "my_photo_details";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "photo_title";
    public static final String COLUMN_IMAGE = "photo_image";
    public static final String COLUMN_DESCRIPTION = "photo_description";
    public static final String COLUMN_KEY_WORDS = "photo_key_words";
    public static final String COLUMN_ADDED_TIMESTAMP = "photo_added_time_stamp";
    public static final String COLUMN_UPDATE_TIMESTAMP = "photo_update_time_stamp";
    public static final String COLUMN_ADDRESS = "photo_address";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_IMAGE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_KEY_WORDS + " TEXT, " +
                    COLUMN_ADDED_TIMESTAMP + " TEXT, " +
                    COLUMN_UPDATE_TIMESTAMP + " TEXT, " +
                    COLUMN_ADDRESS + " TEXT);";
}
