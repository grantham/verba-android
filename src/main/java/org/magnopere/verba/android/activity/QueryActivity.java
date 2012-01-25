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
import android.os.Environment;
import org.magnopere.verba.android.data.DBConstants;

import java.io.File;

/**
 * @author Roger Grantham
 * @since 1/24/12
 */
public class QueryActivity extends Activity {

    private final String INTERNAL_DB_PATH           = "/data/data/org.magnopere.verba/databases/";
    private final String SD_DB_PATH                 =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/org.magnopere.verba/databases/";
    private final File INTERNALLY_DEPLOYED_LEXICON  = new File(INTERNAL_DB_PATH + DBConstants.LEXICON_DB_NAME);
    private final File SD_DEPLOYED_LEXICON          = new File(SD_DB_PATH + DBConstants.LEXICON_DB_NAME);

    @Override
    public File getDatabasePath(String name){
        // if a database exists, use that, else prefer SD card to internal storage
        File dbPath = INTERNALLY_DEPLOYED_LEXICON;
        if (INTERNALLY_DEPLOYED_LEXICON.exists()){
            // just use this path
        } else if (isSDCardAvailable()){
            dbPath = SD_DEPLOYED_LEXICON;
        }
        return dbPath;
    }

    private boolean isSDCardAvailable(){
        return android.os.Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
    }


    @Override
    public File getExternalCacheDir() {
        return super.getExternalCacheDir();
    }
}