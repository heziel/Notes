package com.example.notes;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editorActionHandler();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*
    *   this Method to handle the various action insert/edit
    */
    private void editorActionHandler() {

        editor = (EditText)findViewById(R.id.editText) ;

        // the intent that lunch this activity.
        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        // if press insert button it will be null
        if( uri  == null ){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));

        }else{
            action = Intent.ACTION_EDIT;
            // Where Clause
            noteFilter = DBOpenHelper.NOTE_ID  + "=" + uri.getLastPathSegment();

            // retrieve the row from the database
            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS,noteFilter,null,null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            // Display the text
            editor.setText(oldText);
            // soft keyboard
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            cursor.close();
        }
    }

    /*
    *  Method to display the Menu when edit activity chosen
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //  this adds items to the action bar if it is present.
        if ( action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    /*
    *  Method For all menu Options
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch( id ){
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNoteDialog();
                break;
            case R.id.action_save:
                updateNote("Note Saved");
                oldText = editor.getText().toString().trim();
                break;
        }
        return true;
    }

    /*
    *  Method For deleting a note.
    */
    private void deleteNote(){

        getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this,R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void deleteNoteDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    /*
    *  Method that handle the text update after editing is finished
    */
    private void finishEditing() {

        String newText  = editor.getText().toString().trim();

        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length()==0){
                    finish();
                }else {
                    exitDialog("insert");
                }
                break;
            case Intent.ACTION_EDIT:

                if (oldText.equals(newText)){
                    // Go back to the MainActivity
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }else {
                    exitDialog("update");
                }
                break;
        }
    }

    /*
    *  Method to display a dialog when exiting the activity
    */
    public void exitDialog(final String operation){

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Save Note")
                .setMessage("Do you want to save your note?")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (operation.equals("insert"))
                            insertNote();
                        else if (operation.equals("update"))
                            updateNote("Note Updated");
                        // finished with my activity go back to parent activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                })
                .setNegativeButton("Back", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                })
                .create();
                dialog.show();
    }

    /*
    *  Method that update existing note in the dataBase.
    */
    private void updateNote( String msg ) {

        String newText  = editor.getText().toString().trim();

        ContentValues values =  new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText );

        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, msg , Toast.LENGTH_SHORT).show();

        // message to the main activity
        setResult(RESULT_OK);
    }

    /*
    *  Method that insert new note to the dataBase
    */
    private void insertNote() {

        String newText  = editor.getText().toString().trim();

        ContentValues values =  new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, newText );

        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
