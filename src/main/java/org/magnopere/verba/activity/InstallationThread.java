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

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import org.magnopere.verba.data.DBHelper;

/**
 * @author Roger Grantham
 * @since 1/5/12
 */
public class InstallationThread extends Thread {

    private static final int MAX_PROGRESS = 100;

    private final Handler           handler;
    private final DBHelper          dbHelper;

    public InstallationThread(DBHelper dbHelper, Handler handler) {
        this.dbHelper = dbHelper;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
        dbHelper.deployDB(new InstallationListener() {
            public void started() {
                Message msg = handler.obtainMessage();
                msg.arg1 = 0;
                handler.sendMessage(msg);
            }

            public void progress(double percentCompleted) {
                Message msg = handler.obtainMessage();
                msg.arg1 = (int)Math.round(percentCompleted * MAX_PROGRESS);
                handler.sendMessage(msg);
            }

            public void completed() {
                Message msg = handler.obtainMessage();
                msg.arg1 = MAX_PROGRESS;
                msg.arg2 = Integer.MAX_VALUE;
                handler.sendMessage(msg);
            }
        });
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
