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

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ArrayAdapter;
import org.magnopere.verba.R;
import org.magnopere.verba.data.DBHelper;
import org.magnopere.verba.data.Entry;

import java.util.List;

/**
 * Displays the list of lexicon entries
 * @author Roger Grantham
 * @since 1/2/12
 */
public class ShowDefinitionsActivity extends ListActivity {
    
    public  static final String ANALYSIS_LEMMA  = "ANALYSIS_LEMMA";
    private static final String TAG             = ShowDefinitionsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final DBHelper dbHelper = new DBHelper(this);
        final List<Entry> entries = dbHelper.lookup(getLemma());
        final Spanned[] entrySpans = new Spanned[entries.size()];
        for (int i = 0; i < entries.size(); i++){
            entrySpans[i] = Html.fromHtml(entries.get(i).toString());
        }
        setListAdapter(new ArrayAdapter<Spanned>(this, 
                                                 R.layout.show_definitions, 
                                                entrySpans.length == 0 ? getNoDefinitionsError() : entrySpans));
    }

    private String getLemma(){
        return getIntent().getStringExtra(ANALYSIS_LEMMA);
    }

     private Spanned[] getNoDefinitionsError(){
         return new Spanned[]{Html.fromHtml(getResources().getString(R.string.noDefinitionsFound))};
     }
}