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

package org.magnopere.verba.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StatFs;
import android.util.Log;
import org.magnopere.verba.activity.InstallationListener;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.magnopere.verba.data.DBConstants.*;
import static org.magnopere.verba.data.Orthography.*;

/**
 * Facade to the database. <p>
 * <strong>NOTA BENE:</strong> after the APK is installed, the first time it is run, the database must be
 * unpacked from <code>/assets/verba.jpg</code>. Note that <code>/assets/verba.jpg</code> is, in fact a gzipped
 * SQLite database file! The file extension is a hack to get around a limitation of being able to only unpack <1MB of
 * non-image data from <code>/assets</code><p>
 * See also {@linkplain #deployDB(InstallationListener)}
 * @author Roger Grantham
 * @since 12/31/11
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    
    private final Context   context;
    private SQLiteDatabase  verbaDB;

    
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }



    private void checkDBIsOpen(){
        if (verbaDB == null || !verbaDB.isOpen()) throw new IllegalStateException("Verba DB is not open.");
    }
    
    @Override
    public synchronized void close() {
        if(verbaDB != null){
            verbaDB.close();
        }
        super.close();
    }


    /**
     * Deploys the DB from the assets location
     * @throws IOException on error
     */
    public void deployDB(InstallationListener listener) throws IOException {
        if (DBConstants.DEPLOYED_DB_FILE.exists()) return;
        listener.started();
        final SQLiteDatabase tmp = getReadableDatabase();
        final String dbPath = tmp.getPath();
        tmp.close();
        GZIPInputStream in = null;
        OutputStream    out = null;
        try {
            final AssetManager assetManager = context.getResources().getAssets();
            in  = new GZIPInputStream(assetManager.open(DB_ASSET_NAME, AssetManager.ACCESS_STREAMING));
            out = new FileOutputStream(dbPath);
            byte[] buf = new byte[BUF_SIZE];
            int totalBytesRead = 0;
            for (int read = in.read(buf), i = 0; read > 0; read = in.read(buf), i++){
                out.write(buf, 0, read);
                totalBytesRead += read;
                if (i % 100 == 0){
                    listener.progress(Math.min((double)totalBytesRead / (double)DEFLATED_DB_SIZE_BYTES, 1.0D));
                }
            }
            out.flush();
        } finally {
            if (in  != null) in.close();
            if (out != null) out.close();
            listener.completed();
        }
    }





    public Cursor lookupAnalyses(String wordForm) throws SQLException {
        checkDBIsOpen();
        Log.i(TAG, "DB is open, beginning query for morphology analyses of form " + wordForm);
        //"SELECT form, lemma, grammaticalCase, degree, gender, mood, number, person, pos, tense, voice FROM morphology WHERE form = ?"
        return verbaDB.query(MORPHOLOGY_TABLE,
                            MORPHOLOGY_FIELDS,
                            String.format("form = '%s'", wordForm), null, null, null, null);
    }


    public List<Analysis> findAnalyses(String wordForm){
        final String rectfiedForm = rectify(wordForm);
        Log.i(TAG, "Looking for " + rectfiedForm);
        final List<Analysis> analyses = new ArrayList<Analysis>();
        try {
            open();
            final Cursor cursor = lookupAnalyses(rectfiedForm);
            Log.i(TAG, "cursor returned for morph analysis query for " + rectfiedForm);
            final int rows = cursor.getCount();
            for (int i = 0; i < rows; i++){
                cursor.moveToPosition(i);
                //"form", "lemma", "grammaticalCase", "degree", "gender", "mood", "number", "person", "pos", "tense", "voice"
                final Analysis analysis = new Analysis(cursor);
                analyses.add(analysis);
            }
            cursor.close();
            close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return analyses;
    }
    
    
    private Cursor lookupEntry(String lemma) throws SQLException {
        checkDBIsOpen();
        //"SELECT lemma, ordinality, orthography, endings, gender, pos, definition FROM lexicon WHERE lemma = ? AND pos = ?";
        return verbaDB.query(LEXICON_TABLE,
                            LEXICON_FIELDS,
                            String.format("lemma = '%s'", lemma), null, null, null, null);
    }
    
    
    public List<Entry> lookup(String lemma){
        if (lemma == null) throw new IllegalArgumentException("null: analysis");
        final List<Entry> entries = new ArrayList<Entry>();
        try {
            open();
            final Cursor cursor = lookupEntry(rectify(lemma));
            final int rows = cursor.getCount();
            for (int i = 0; i < rows; i++){
                cursor.moveToPosition(i);
                final Entry entry = new Entry(cursor);
                entries.add(entry);
            }
            cursor.close();
            close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entries;
    }


    

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // see #deployDB(InstallationListener)
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: implement
    }


    /**
     * Opens the DB, deploying it first, if necessary
     * @throws SQLException on error
     */
    public void open() throws SQLException {
        if (DEPLOYED_DB_FILE.exists()){
            verbaDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        } else {
            throw new IllegalStateException("One-time database deployment has not been carried out! First call deployDB(ProgressListener)");
        }
    }


}
