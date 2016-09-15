package com.example.notes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_CREATED = "noteCreated";

    public static final String[] ALL_COLUMNS = {NOTE_ID,NOTE_TEXT,NOTE_CREATED};

    // SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    public DBOpenHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES );
        onCreate(db);
    }

    /*
    *   Method for reading lines that match the autoComplete text.
    */
    public List<String> read(String searchTerm) {

        List<String> recordsList = new ArrayList<>();

        // select query
        String sql = "";
        sql += "SELECT * FROM " + TABLE_NOTES;
        sql += " WHERE " + NOTE_TEXT + " LIKE '%" + searchTerm + "%'";
        sql += " ORDER BY " + NOTE_ID + " DESC";
        sql += " LIMIT 0,5";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int noteId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(NOTE_ID)));
                String noteName = cursor.getString(cursor.getColumnIndex(NOTE_TEXT));

                String myObject = noteId + "." + noteName;
                // add to list
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return the list of records
        return recordsList;
    }
}
