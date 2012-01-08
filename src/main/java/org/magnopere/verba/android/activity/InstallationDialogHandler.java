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

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

/**
 * @author Roger Grantham
 * @since 1/7/12
 */
public class InstallationDialogHandler extends Handler {

    public static final int PROGRESS_MSG = 0;
    public static final int INSUFFICIENT_SPACE_ERROR = -1;
    
    private final String            insufficientSpaceMessage;
    private final String            progressCompletingMessage;
    private final ProgressDialog    dialog;


    public InstallationDialogHandler(String progressCompletingMessage, 
                                     String insufficientSpaceMessage, 
                                     ProgressDialog dialog) {
        super();
        this.insufficientSpaceMessage = insufficientSpaceMessage;
        this.progressCompletingMessage = progressCompletingMessage;
        this.dialog = dialog;
    }

    @Override
    public void handleMessage(Message msg) {
        final int what = msg.what;
        switch (what){
            case INSUFFICIENT_SPACE_ERROR: 
                insufficientSpace(msg);
                break;
            case PROGRESS_MSG:
                progressUpdate(msg);
                break;
            default:
                throw new RuntimeException("Unhandled message!");                
        }
    }
    
    private void progressUpdate(Message msg){
        int progress = msg.arg1;
        dialog.setProgress(progress);
        if (progress >= 100){
            //   dialog.setIndeterminate(true);
            //   dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(progressCompletingMessage);
        }
        if (msg.arg2 == Integer.MAX_VALUE){
            dialog.dismiss();
        }    
    }
    
    private void insufficientSpace(Message msg){
        final int required = msg.arg1;
        final int found = msg.arg2;
        dialog.setProgress(0);
        dialog.setMessage(String.format(insufficientSpaceMessage, required, found));
    }


}


