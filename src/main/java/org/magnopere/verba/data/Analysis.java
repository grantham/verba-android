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

/**
 * Represents a morphological analysis of a word form
 * @author Roger Grantham
 * @since 1/2/12
 */
public class Analysis {

    private String form = "";
    private String lemma = "";
    private String grammaticalCase;
    private String degree;
    private String gender;
    private String mood;
    private String number;
    private String person;
    private String pos;
    private String tense;
    private String voice;

    public Analysis() {
    }

    public Analysis(Cursor cursor) {
        if (cursor == null) throw new IllegalArgumentException("null: cursor");
        form = cursor.getString(0);
        lemma = cursor.getString(1);
        grammaticalCase = cursor.getString(2);
        degree = cursor.getString(3);
        gender = cursor.getString(4);
        mood = cursor.getString(5);
        number = cursor.getString(6);
        person = cursor.getString(7);
        pos = cursor.getString(8);
        tense = cursor.getString(9);
        voice = cursor.getString(10);
    }
    
    

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public String getGrammaticalCase() {
        return grammaticalCase;
    }

    public void setGrammaticalCase(String grammaticalCase) {
        this.grammaticalCase = grammaticalCase;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getTense() {
        return tense;
    }

    public void setTense(String tense) {
        this.tense = tense;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    @Override
    public String toString() {
        return String.format(
                "%s%s%s%s%s%s%s%s%s%s%s",
                form == null || form.length() == 0 ? form: form + " ",
                lemma == null || lemma.length() == 0 ? lemma: "(" + lemma + ") ",
                pos == null || pos.length() == 0 ? pos : pos + " ",
                person == null || person.length() == 0 ? person : person + " person ",
                grammaticalCase == null || grammaticalCase.length() == 0 ? grammaticalCase : grammaticalCase + " ",
                gender == null || gender.length() == 0 ? gender : gender + " ",
                degree == null || degree.length() == 0 ? degree : degree + " ",
                number == null || number.length() == 0 ? number : number + " ",
                tense == null || tense.length() == 0 ? tense : tense + " ",
                mood == null || mood.length() == 0 ? mood : mood + " ",
                voice == null || voice.length() == 0 ? voice : voice + " ");
    }
}
