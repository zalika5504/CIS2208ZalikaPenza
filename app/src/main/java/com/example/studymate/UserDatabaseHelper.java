package com.example.studymate;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 9; // Incremented to handle the new schema

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_IS_LOGGED_IN = "isLoggedIn";

    private static final String TABLE_FLASHCARDS = "flashcards";
    private static final String COLUMN_FLASHCARD_ID = "flashcard_id";
    private static final String COLUMN_FLASHCARD_TITLE = "title";
    private static final String COLUMN_FLASHCARD_DESCRIPTION = "description";
    private static final String COLUMN_FLASHCARD_QUESTION = "question";
    private static final String COLUMN_USER_ID = "user_id";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_IS_LOGGED_IN + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_TABLE_USERS);

        String CREATE_TABLE_FLASHCARDS = "CREATE TABLE " + TABLE_FLASHCARDS + "("
                + COLUMN_FLASHCARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FLASHCARD_TITLE + " TEXT,"
                + COLUMN_FLASHCARD_DESCRIPTION + " TEXT,"
                + COLUMN_FLASHCARD_QUESTION + " TEXT,"
                + COLUMN_USER_ID + " INTEGER"  // Add user_id column here
                + ")";
        db.execSQL(CREATE_TABLE_FLASHCARDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 9) {
            if (!columnExists(db, TABLE_FLASHCARDS, COLUMN_FLASHCARD_QUESTION)) {
                db.execSQL("ALTER TABLE " + TABLE_FLASHCARDS + " ADD COLUMN " + COLUMN_FLASHCARD_QUESTION + " TEXT");
            }
            // Add the user_id column if it doesn't exist
            if (!columnExists(db, TABLE_FLASHCARDS, COLUMN_USER_ID)) {
                db.execSQL("ALTER TABLE " + TABLE_FLASHCARDS + " ADD COLUMN " + COLUMN_USER_ID + " INTEGER");
            }
        }
    }



    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex != -1) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    if (name.equals(columnName)) {
                        cursor.close();
                        return true;
                    }
                }
            }
            cursor.close();
        }
        return false;
    }

    public long addUser(String name, String email, String password, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long userId = db.insert(TABLE_USERS, null, values);
        db.close();

        if (userId != -1) {
            setUserLoggedIn(userId, true);
            storeUserIdInPreferences(context, userId);
        }
        return userId;
    }

    public long addFlashcard(String title, String description, String question, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FLASHCARD_TITLE, title);
        values.put(COLUMN_FLASHCARD_DESCRIPTION, description);
        values.put(COLUMN_FLASHCARD_QUESTION, question);
        values.put(COLUMN_USER_ID, userId);
        long result = db.insert(TABLE_FLASHCARDS, null, values);
        db.close();
        return result;
    }

    public List<Flashcard> getAllFlashcards(int userId) {
        List<Flashcard> flashcards = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_FLASHCARD_ID, COLUMN_FLASHCARD_TITLE, COLUMN_FLASHCARD_DESCRIPTION, COLUMN_FLASHCARD_QUESTION};
        String selection = COLUMN_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COLUMN_FLASHCARD_TITLE;

        Cursor cursor = db.query(TABLE_FLASHCARDS, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor != null) {
            int idColumnIndex = cursor.getColumnIndex(COLUMN_FLASHCARD_ID);
            int titleColumnIndex = cursor.getColumnIndex(COLUMN_FLASHCARD_TITLE);
            int descriptionColumnIndex = cursor.getColumnIndex(COLUMN_FLASHCARD_DESCRIPTION);
            int questionColumnIndex = cursor.getColumnIndex(COLUMN_FLASHCARD_QUESTION);

            while (cursor.moveToNext()) {
                int id = (idColumnIndex != -1) ? cursor.getInt(idColumnIndex) : -1;
                String title = (titleColumnIndex != -1) ? cursor.getString(titleColumnIndex) : null;
                String description = (descriptionColumnIndex != -1) ? cursor.getString(descriptionColumnIndex) : null;
                String question = (questionColumnIndex != -1) ? cursor.getString(questionColumnIndex) : null;

                flashcards.add(new Flashcard(id, title, description, question));
            }
            cursor.close();
        }
        db.close();
        return flashcards;
    }

    public void deleteFlashcard(Flashcard flashcard) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLASHCARDS, COLUMN_FLASHCARD_ID + "=?", new String[]{String.valueOf(flashcard.getId())});
        db.close();
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return exists;
    }

    public boolean createFlashcard(String title, String description, String question, Context context) {
        long userId = getUserIdFromPreferences(context);
        if (userId == -1 || !isUserLoggedIn(userId)) {
            return false;
        }

        addFlashcard(title, description, question, (int) userId);
        return true;
    }

    public long getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID};
        String selection = COLUMN_EMAIL + "=?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        long userId = -1;
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(COLUMN_ID);
            if (columnIndex != -1 && cursor.moveToFirst()) {
                userId = cursor.getLong(columnIndex);
            }
            cursor.close();
        }
        db.close();
        return userId;
    }

    public void setUserLoggedIn(long userId, boolean isLoggedIn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_LOGGED_IN, isLoggedIn ? 1 : 0);
        db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public boolean isUserLoggedIn(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_IS_LOGGED_IN};
        String selection = COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        boolean isLoggedIn = false;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_IS_LOGGED_IN);
                if (columnIndex != -1) {
                    isLoggedIn = cursor.getInt(columnIndex) == 1;
                }
            }
            cursor.close();
        }
        db.close();
        return isLoggedIn;
    }

    public boolean loginUser(String email, String password, Context context) {
        if (checkUser(email, password)) {
            long userId = getUserId(email);
            setUserLoggedIn(userId, true);
            storeUserIdInPreferences(context, userId);
            return true;
        }
        return false;
    }

    public static void storeUserIdInPreferences(Context context, long userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", userId);
        editor.apply();
    }

    public static long getUserIdFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("userId", -1);
    }

    public static void clearUserIdFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userId");
        editor.apply();
    }
}
