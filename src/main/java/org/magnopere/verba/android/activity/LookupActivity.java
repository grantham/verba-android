/**
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

package org.magnopere.verba.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.magnopere.verba.R;
import org.magnopere.verba.android.data.Analysis;
import org.magnopere.verba.android.data.DBConstants;
import org.magnopere.verba.android.data.DBHelper;

import java.io.File;


public class LookupActivity extends QueryActivity implements Thread.UncaughtExceptionHandler {

	private static final String TAG = LookupActivity.class.getSimpleName();


	private ListView               candidateList;
	private DBHelper               dbHelper;
	private ArrayAdapter<Analysis> entryAdapter;
	private MenuHandler            menuHandler;
	private EditText               searchBox;
	private Button                 searchButton;


	private void deployDB() {
		// TODO:  perform a simple query to prove the DB has been properly installed?
		final File dbFile = getDatabasePath(DBConstants.LEXICON_DB_NAME);
		if (!dbFile.exists() || dbFile.length() < 1024) {
			// delete invalid db files
			dbFile.delete();

			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setOwnerActivity(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setTitle(getString(R.string.progressTitle));
			dialog.setMessage(getString(R.string.progressMessage));
			dialog.setCancelable(false);
			dialog.show();
			final Thread installer = new InstallationThread(dbHelper,
					new InstallationDialogHandler(getString(R.string.progressCompletingMessage),
							getString(R.string.insufficientStorageMessage),
							dialog));
			installer.setUncaughtExceptionHandler(this);
			installer.start();
		}
	}


	/**
	 * Method to be invoked when an item in this AdapterView has been clicked.
	 * <p/>
	 * Implementers can call getItemAtPosition(position) if they need
	 * to access the data associated with the selected item.
	 *
	 * @param position The position of the view in the adapter.
	 */

	public void itemSelected(int position) {
		final Analysis analysis = entryAdapter.getItem(position);
		final Intent showDefinitions = new Intent(this, ShowDefinitionsActivity.class);
		showDefinitions.putExtra(ShowDefinitionsActivity.ANALYSIS_LEMMA, analysis.getLemma());
		startActivity(showDefinitions);
	}


	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState If the activity is being re-initialized after
	 *                           previously being shut down then this Bundle contains the data it most
	 *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		dbHelper = new DBHelper(this);
		menuHandler = new MenuHandler(this);

		searchBox = (EditText) findViewById(R.id.search_EditText);
		searchBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		searchBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBoxClicked((EditText) v);
			}
		});

		searchButton = (Button) findViewById(R.id.search_Button);
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchButtonClicked();
			}
		});

		candidateList = (ListView) findViewById(R.id.candidateEntries_ListView);
		entryAdapter = new ArrayAdapter<Analysis>(this, R.layout.candidate_entry_list);
		candidateList.setAdapter(entryAdapter);
		candidateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				itemSelected(position);
			}
		});

		// ensure DB is properly deployed
		deployDB();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return menuHandler.performOnCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return menuHandler.performOnOptionsItemSelected(item);
	}


	private void searchBoxClicked(EditText searchBox) {
		searchBox.getText().clear();
		entryAdapter.clear();
	}


	private void searchButtonClicked() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
		final String searchInput = searchBox.getText().toString().trim().toLowerCase();
		entryAdapter.clear();
		final String[] tokens = searchInput.split("\\s+");
		for (String token : tokens) {
			for (Analysis analysis : dbHelper.findAnalyses(token)) {
				entryAdapter.add(analysis);
			}
		}
	}


	public void uncaughtException(Thread thread, Throwable throwable) {
		finish();
	}
}
