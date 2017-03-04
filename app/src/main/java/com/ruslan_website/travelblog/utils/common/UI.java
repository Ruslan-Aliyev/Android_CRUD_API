package com.ruslan_website.travelblog.utils.common;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UI {
    public static void setProgressStatus(Activity activity, boolean isInProgress, ProgressBar progressBar, Button[] buttons, String toast, String log){
        if(isInProgress){
            for(int i = 0; i < buttons.length; i++){
                buttons[i].setEnabled(false);
            }
            progressBar.setVisibility(View.VISIBLE);
        }else{
            for(int i = 0; i < buttons.length; i++){
                buttons[i].setEnabled(true);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
        Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
        Log.i("LoginMessage", log);
    }
}
