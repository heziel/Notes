package com.example.notes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class NotesProvider extends ContentProvider{

    private static final String AUTHORITY = "com.example.notes.notesprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    // Parse the uri and give the operation requested
    private static final UriMatcher urimatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "n" +
            "ote";

    static {
        urimatcher.addURI(AUTHORITY,BASE_PATH,NOTES);
        urimatcher.addURI(AUTHORITY,BASE_PATH + "/#" ,NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    /*
    *   Method to create query from the dataBase
    */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // matching uri with numeric Value means i want a single row
        if (urimatcher.match(uri) == NOTES_ID){
           selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_NOTES,DBOpenHelper.ALL_COLUMNS,
                selection,null,null,null,
                DBOpenHelper.NOTE_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_NOTES,
                null,values);
        return Uri.parse(BASE_PATH + "/" + id );
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_NOTES,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTES,values,selection,selectionArgs);
    }
}
