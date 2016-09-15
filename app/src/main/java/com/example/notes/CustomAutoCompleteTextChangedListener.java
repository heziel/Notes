package com.example.notes;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

public class CustomAutoCompleteTextChangedListener implements TextWatcher {

    public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
    Context context;

    public CustomAutoCompleteTextChangedListener(Context context) {
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /*
    *   Method to find matching when ever the text changed.
    */
    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

        SearchActivity searchActivity = ((SearchActivity) context);

        // query the database based on the user input
        searchActivity.item = searchActivity.getItemsFromDb(userInput.toString());

        // update the adapter
        searchActivity.myAdapter.notifyDataSetChanged();
        searchActivity.myAdapter = new ArrayAdapter<>(searchActivity,
                android.R.layout.simple_dropdown_item_1line, searchActivity.item);
        searchActivity.myAutoComplete.setAdapter(searchActivity.myAdapter);

    }
}