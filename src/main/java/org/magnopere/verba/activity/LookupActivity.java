/*
 * Copyright (c) 2012 Roger Grantham
 *
 * All rights reserved.
 *
 * This file is part of Verba-Android.
 *
 * Verba-Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Verba-Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Verba-Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.magnopere.verba.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.magnopere.verba.R;
import org.magnopere.verba.data.Analysis;
import org.magnopere.verba.data.DBHelper;

public class LookupActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = LookupActivity.class.getSimpleName();


    private EditText                searchBox;
    private Button                  searchButton;
    private ListView                candidateList;
    private ArrayAdapter<Analysis>  entryAdapter;

    private DBHelper                dbHelper;
    private MenuHandler             menuHandler;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.main);
        dbHelper        = new DBHelper(this);
        menuHandler     = new MenuHandler(this);
        searchBox       = (EditText)findViewById(R.id.search_EditText);
        searchBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchBox.setOnClickListener(this);
        searchButton    = (Button)findViewById(R.id.search_Button);
        searchButton.setOnClickListener(this);
        candidateList   = (ListView)findViewById(R.id.candidateEntries_ListView);
        entryAdapter    = new ArrayAdapter<Analysis>(this, R.layout.candidate_entry_list);
        candidateList.setAdapter(entryAdapter);
        candidateList.setOnItemClickListener(this);
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    public void onClick(View v) {
        final int id = v.getId();
        switch (id){
            case R.id.search_EditText:
                searchBoxClicked((EditText)v);
                break;
            case R.id.search_Button:
                searchButtonClicked((Button)v);
                break;
            default: // no op
        }    
    }


    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Analysis analysis = entryAdapter.getItem(position);
        final Intent showDefinitions = new Intent(this, ShowDefinitionsActivity.class);
        showDefinitions.putExtra(ShowDefinitionsActivity.ANALYSIS_LEMMA, analysis.getLemma());
        startActivity(showDefinitions);
    }

    private void searchBoxClicked(EditText searchBox){
        searchBox.getText().clear();
        entryAdapter.clear();
    }

    private void searchButtonClicked(Button searchButton){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        final String searchInput = searchBox.getText().toString().trim().toLowerCase();
        entryAdapter.clear();
        final String[] tokens = searchInput.split("\\s+");
        for (String token: tokens){
            for (Analysis analysis: dbHelper.findAnalyses(token)){
                entryAdapter.add(analysis);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return menuHandler.performOnCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return menuHandler.performOnOptionsItemSelected(item);
    }
    
    
    
}

