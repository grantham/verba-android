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

package org.magnopere.verba.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import org.magnopere.verba.R;

/**
 * @author Roger Grantham
 * @since 1/3/12
 */
public class InformationActivity extends Activity {
    
    private static final String TAG = InformationActivity.class.getSimpleName();
    public static final String MENU_ITEM_ID = "MENU_ITEM_ID";

    private MenuHandler menuHandler;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuHandler = new MenuHandler(this);
        setContentView(R.layout.information);
        final int infoTextID = getIntent().getIntExtra(MENU_ITEM_ID, R.id.about_menu_item);
        final String infoText = menuItemIDToString(infoTextID);
        final TextView textView = (TextView)findViewById(R.id.information_TextView);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(infoText);
    }
    
    
    private String menuItemIDToString(int menuItemID){
        String text;
        switch(menuItemID){
            case R.id.about_menu_item:
                 text = getString(R.string.about);
                break;
            case R.id.contribute_menu_item:
                text = getString(R.string.contribute);
                break;
            case R.id.licensing_menu_item:
                text = getString(R.string.about_licensing);
                break;
            case R.id.gpl_menu_item:
                text = getString(R.string.gpl);
                break;
            default:
                text = getString(R.string.resourceNotFound);
        }
        return text;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return menuHandler.performOnCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return menuHandler.performOnOptionsItemSelected(item);
    }

}