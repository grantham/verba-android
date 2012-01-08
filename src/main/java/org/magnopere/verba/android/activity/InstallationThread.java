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

import android.os.Message;
import org.magnopere.verba.android.data.DBHelper;

/**
 * Used to copy/decompress the database stored in /assets/verba.jpg and communicate progress information
 * to the progress dialog owned by {@linkplain LookupActivity}
 *
 * @author Roger Grantham
 * @since 1/5/12
 */
public class InstallationThread extends Thread {

    private static final int MAX_PROGRESS = 100;

    private final InstallationDialogHandler handler;
    private final DBHelper                  dbHelper;

    public InstallationThread(DBHelper dbHelper, InstallationDialogHandler handler) {
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
                msg.what = InstallationDialogHandler.PROGRESS_MSG;
                msg.arg1 = (int)Math.round(percentCompleted * MAX_PROGRESS);
                handler.sendMessage(msg);
            }

            public void completed() {
                Message msg = handler.obtainMessage();
                msg.what = InstallationDialogHandler.PROGRESS_MSG;
                msg.arg1 = MAX_PROGRESS;
                msg.arg2 = Integer.MAX_VALUE;
                handler.sendMessage(msg);
            }
            
            public void insufficientSpace(int requiredBytes, int foundBytes){
                Message msg = handler.obtainMessage();
                msg.what = InstallationDialogHandler.INSUFFICIENT_SPACE_ERROR;
                msg.arg1 = requiredBytes;
                msg.arg2 = foundBytes;
                handler.sendMessageAtFrontOfQueue(msg);
                try {
                    Thread.sleep(6000L);
                } catch (InterruptedException e) {
                    // ignore
                }
                throw new IllegalStateException("Installation failed.");
            }
            
        });
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
