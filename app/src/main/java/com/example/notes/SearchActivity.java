package com.example.notes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final int EDITOR_REQUEST_CODE = 1001;
    protected CustomAutoCompleteView myAutoComplete;
    // Adapter for auto-complete
    protected ArrayAdapter<String> myAdapter;
    // For database operations
    private DBOpenHelper databaseH;
    // Add some initial value
    protected String[] item = new String[] {"Please search..."};

    protected int notesId[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        autoCompleteHandler();
    }

    /*
    *   Method for handling the data for the autoComplete
    */
    private void autoCompleteHandler() {

        try{
            // Instantiate database handler
            databaseH = new DBOpenHelper(this);

            // autoCompleteTextView
            myAutoComplete = (CustomAutoCompleteView) findViewById(R.id.myautocomplete);

            // add the listener so it will tries to suggest while the user types
            myAutoComplete.addTextChangedListener(new CustomAutoCompleteTextChangedListener(this));

            // set our adapter
            myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, item);
            myAutoComplete.setAdapter(myAdapter);

            myAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Var for the activity i want to go - Editor Activity
                    Intent intent = new Intent(SearchActivity.this, EditorActivity.class);

                    Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + notesId[position]);

                    intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                    startActivityForResult(intent, EDITOR_REQUEST_CODE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    *   Method to get all the relevant results from the dataBase for each chosen word
    */
    public String[] getItemsFromDb(String searchTerm){

        // add items on the array dynamically
        List<String> notes = databaseH.read(searchTerm);
        int rowCount = notes.size();

        notesId = new int[rowCount];

        String[] item = new String[rowCount];
        int x = 0;

        for (String record : notes) {

            // find where  the id ends
            int dotPosition = record.indexOf(".");
            notesId[x] = Integer.parseInt(record.substring(0,dotPosition));
            item[x] = record.substring(dotPosition+1, record.length());
            x++;
        }

        return item;
    }

}