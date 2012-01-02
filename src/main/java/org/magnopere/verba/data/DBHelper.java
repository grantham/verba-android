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
import android.util.Log;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.magnopere.verba.data.DBConstants.*;
import static org.magnopere.verba.data.Orthography.*;

/**
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


    private static float getAvailableMB(File target) {
        // TODO: make this work
//        final StatFs statFs = new StatFs(target.getPath());
//        long availableBytes = (long)statFs.getBlockSize() * (long)statFs.getAvailableBlocks();
//        return availableBytes / (1024.0f * 1024.0f);
        return DEFLATED_DB_SIZE * 2.0F;
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
    private void deployDB() throws IOException {
        Log.i(TAG, "deployDB()");
        final float availableMB = getAvailableMB(DEPLOYED_DB_FILE);
        if (availableMB < DEFLATED_DB_SIZE){
            throw new IOException(
                    String.format("Insufficient space for deflating verb DB; required %fMB, found %fMB",
                                    DEFLATED_DB_SIZE, availableMB));
        }
        Log.i(TAG, "generating tmp DB");
        final SQLiteDatabase tmp = getReadableDatabase();
        final String dbPath = tmp.getPath();
        Log.i(TAG, "tmp DB path: " + dbPath);
        tmp.close();
        Log.i(TAG, "tmp DB closed");
        InputStream in = null;
        OutputStream out = null;
        try {
            Log.i(TAG, "copying DB from assets to " + dbPath);
            final AssetManager assetManager = context.getResources().getAssets();
            in  = new GZIPInputStream(assetManager.open(DB_ASSET_NAME, AssetManager.ACCESS_STREAMING));
            out = new FileOutputStream(dbPath);
            byte[] buf = new byte[BUF_SIZE];
            for (int read = in.read(buf); read > 0; read = in.read(buf)){
                out.write(buf, 0, read);
            }
            out.flush();
            Log.i(TAG, "Finished DB copy from assets");
        } finally {
            if (in  != null) in.close();
            if (out != null) out.close();
            Log.i(TAG, "closed streams");
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
        // Check to see whether the database has already been deployed; if not, we must copy it over from assets
          if (!DEPLOYED_DB_FILE.exists()){
              try {
                  deployDB();
              } catch (IOException e) {
                  throw new RuntimeException(e);
              }
          }
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
        Log.i(TAG, "open()");
        if (DEPLOYED_DB_FILE.exists()){
            Log.i(TAG, "DB already deployed");
            verbaDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
            Log.i(TAG, "DB opened");
        } else {
            try {
                deployDB();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Log.i(TAG, "DB deployed from assets, now attempting to open");
            verbaDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
            Log.i(TAG, "DB opened after copy from assets");
        }
    }


}
