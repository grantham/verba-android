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

import android.database.Cursor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import static org.magnopere.verba.data.DBConstants.BUF_SIZE;
import static org.magnopere.verba.data.Orthography.*;

/**
 * Represents an entry in the lexicon
 * @author Roger Grantham
 * @since 1/2/12
 */
public class Entry {

    private String  lemma;
    private int     ordinality;
    private String  orthography;
    private String  endings;
    private String  gender;
    private String  pos;
    private String  definition;
    
    public Entry() {
    }

    public Entry(Cursor cursor) {
        if (cursor == null) throw new IllegalArgumentException("null: cursor");
        //"SELECT lemma, ordinality, orthography, endings, gender, pos, definition FROM lexicon WHERE lemma = ? AND pos = ?";
        lemma       = cursor.getString(0);
        ordinality  = cursor.getInt(1);
        orthography = cursor.getString(2);
        endings     = rectify(cursor.getString(3));
        gender      = cursor.getString(4);
        pos         = cursor.getString(5);
        definition  = deflateToString(cursor.getBlob(6));
    }


    /**
     * Accepts a Blob which is excepted to be a GZIPed string and deflates it.
     * @param blob to deflated
     * @return deflated string
     */
    public static String deflateToString(byte[] blob){
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ByteArrayInputStream bais = new ByteArrayInputStream(blob);
        GZIPInputStream gzipIn = null;
        try {
            final byte[] buf = new byte[BUF_SIZE];
            gzipIn = new GZIPInputStream(bais);
            for (int read = gzipIn.read(buf); read > 0; read = gzipIn.read(buf)){
                baos.write(buf, 0, read);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (gzipIn != null) gzipIn.close();
            } catch (IOException e) {
                // give up
            }
        }
        final String deflated;
        try {
            deflated = baos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return deflated;
    }
    
    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getOrdinality() {
        return ordinality;
    }

    public void setOrdinality(int ordinality) {
        this.ordinality = ordinality;
    }

    public String getOrthography() {
        return orthography;
    }

    public void setOrthography(String orthography) {
        this.orthography = orthography;
    }

    public String getEndings() {
        return endings;
    }

    public void setEndings(String endings) {
        this.endings = endings;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return String.format("%d. %s [%s] %s; %s %s%n\t%s%n",
                getOrdinality(),
                getLemma(),
                getOrthography(),
                getEndings(),
                getPos(),
                getGender(),
                getDefinition());
    }
}
