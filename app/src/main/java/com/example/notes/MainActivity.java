package com.example.notes;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EDITOR_REQUEST_CODE = 1001;
    private static final int SEARCH_REQUEST_CODE = 1002;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        floatingButtonMainActivity();

        displayLogoOnActionBar();

        mainListViewConnection();

        getLoaderManager().initLoader(0, null, this);
    }

    /*
    * Method to display logo on the action bar
    */
    private void displayLogoOnActionBar() {

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.notes);
    }

    /*
    * Method for handling the ListView
    */
    private void mainListViewConnection() {

        // From - where the Data is coming from.
        String[] from = { DBOpenHelper.NOTE_TEXT, DBOpenHelper.NOTE_CREATED };
        // TO - who is going to display the data.
        int[] to = {R.id.textViewNote,R.id.textViewDate};

        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.note_list_item,null,from, to,0);

        // Reference To the ListView
        ListView list = (ListView) findViewById(android.R.id.list);
        // Pass the data to the list
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Var for the activity we want to go - Editor Activity
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                // The Uri represent the primary key value of the current selected item list
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });
    }

    /*
     *  Method for handling our Floating Button behavior
     */
    private void floatingButtonMainActivity() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditorForNewNote();
            }
        });
    }

    /*
    * Restart the loader.
    */
    private Loader<Cursor> restartLoader() {

        return getLoaderManager().restartLoader(0,null,this);
    }

    /*
    * Open the EditorActivity for new Notes.
    */
    private void openEditorForNewNote() {

        Intent intent = new Intent(this,EditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    /*
    * Method for opening the SearchActivity for new Notes.
    */
    private void openSearchActivity() {

        Intent intent = new Intent(this,SearchActivity.class);
        startActivityForResult(intent, SEARCH_REQUEST_CODE);
    }

    /*
    * Method to display the Menu
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //  this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    *  Method For all menu Options
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if ( id == R.id.action_delete_all ) {
            deleteAllNotes();
        }
        else if ( id == R.id.action_about ) {
            Toast.makeText(MainActivity.this, getString(R.string.about),
                    Toast.LENGTH_SHORT).show();
        }
        else if ( id == R.id.action_search ){
            openSearchActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    *   Method to delete all notes with dialog
    */
    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(
                                    NotesProvider.CONTENT_URI,null,null
                            );
                            restartLoader();
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.all_deleted),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    /*
    *   Method for calling when data needed from the contentProvider
    */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,NotesProvider.CONTENT_URI,null,null,null,null);
    }

    /*
    *   This Method is for moving data to the cursor
    */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        cursorAdapter.swapCursor(data);
    }

    /*
    *   This Method is for erase data to the cursor
    */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }

    /*
    *   This Method is when Activity Result is good
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode ==EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }
}