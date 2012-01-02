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

/**
 * @author Roger Grantham
 * @since 1/2/12
 */
public class Orthography {

    /** not to be instantiated */
    private Orthography() {
        // no op
    }

    /**
     * Rectifies orthography to the consonantal i/u system
     * @param token to rectify
     * @return rectified token
     */
    public static String rectify(String token){
        return token == null ? null : token.toLowerCase().replaceAll("j", "i").replaceAll("v", "u");
    }


    /**
     * Trims the string and minimizes whitespace
     * @param text to prune
     * @return the pruned string
     */
    public static String prune(String text){
        return text.trim().replaceAll("\\s+", " ");
    }

    
}
