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

package org.magnopere.verba.android.data;

import java.io.File;

/**
 * @author Roger Grantham
 * @since 1/1/12
 */
public interface DBConstants {

    int    BUF_SIZE            = 1024;
    double DEFLATED_DB_SIZE_BYTES = 93223936.0D;
    String DB_NAME             = "verba.db";
    String DB_ASSET_NAME       = "verba.jpg";
    String DB_PATH             = "/data/data/org.magnopere.verba/databases/";
    int    DB_VERSION          = 1;

    String MORPHOLOGY_TABLE    = "morphology";
    String[] MORPHOLOGY_FIELDS = new String[]{"form", "lemma", "grammaticalCase", "degree", "gender", "mood", "number", "person", "pos", "tense", "voice"};
    String LEXICON_TABLE       = "lexicon";
    String[] LEXICON_FIELDS    = new String[]{"lemma", "ordinality", "orthography", "endings", "gender", "pos", "definition"};
    File DEPLOYED_DB_FILE    = new File(DB_PATH + DB_NAME);
}
