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
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.magnopere.verba.R;

/**
 * @author Roger Grantham
 * @since 1/3/12
 */
public class MenuHandler {

    private final Activity context;

    public MenuHandler(Activity context) {
        this.context = context;
    }


    public boolean performOnCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = context.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }


    public boolean performOnOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        final Intent aboutIntent = new Intent(context, InformationActivity.class);
        aboutIntent.putExtra(InformationActivity.MENU_ITEM_ID, id);
        context.startActivity(aboutIntent);
        return false;
    }


}
