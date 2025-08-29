package com.example.thepizzamaniaproject.Helper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.thepizzamaniaproject.Domain.UserDomain;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PizzaMania.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_PROFILE_IMAGE = "profile_image";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_GENDER + " TEXT,"
                + COLUMN_PROFILE_IMAGE + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Check if user exists by email
    public boolean checkUserExists(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Add new user
    public long addUser(UserDomain user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_PROFILE_IMAGE, user.getProfileImage());

        return db.insert(TABLE_USERS, null, values);
    }

    // Check user credentials for login
    public boolean checkUser(String email, String password)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Get user by email
    public UserDomain getUserByEmail(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD,
                        COLUMN_PHONE, COLUMN_ADDRESS, COLUMN_GENDER, COLUMN_PROFILE_IMAGE},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst())
        {
            UserDomain user = new UserDomain(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)
            );
            cursor.close();
            return user;
        }
        return null;
    }

    // Update user profile
    public boolean updateUser(UserDomain user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_ADDRESS, user.getAddress());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_PROFILE_IMAGE, user.getProfileImage());

        return db.update(TABLE_USERS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())}) > 0;
    }


    // Add this method to your DatabaseHelper class
    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int result = db.update(TABLE_USERS, values, COLUMN_EMAIL + " = ?",
                new String[]{email});
        return result > 0;
    }


}