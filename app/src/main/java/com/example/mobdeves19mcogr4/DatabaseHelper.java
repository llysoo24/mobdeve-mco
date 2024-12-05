package com.example.mobdeves19mcogr4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHOTO = "photo";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_PHOTO + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_NAME + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PHOTO + " TEXT");
        }
    }

    public boolean insertUser(String name, String email, String password, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHOTO, photoPath);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public String getUserPhoto(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_PHOTO + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String photoPath = null;
        if (cursor.moveToFirst()) {
            photoPath = cursor.getString(cursor.getColumnIndex(COLUMN_PHOTO));
        }
        cursor.close();
        db.close();
        return photoPath;
    }

    public boolean updateUserPhoto(String email, String newPhotoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHOTO, newPhotoPath);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        }
        cursor.close();
        db.close();
        return name;
    }

    public boolean updateUserName(String email, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }
}
