package com.hbidriver.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class NextActivity {


    /**
     * go activity function
     * @param context
     * @param activity
     */
    public  static  void goActivity(Context context, Activity activity){
        Intent intent=new Intent(context,activity.getClass());
        context.startActivity(intent);
    }
    public  static  void goActivityWithClearTasks(Context context, Activity activity){
        Intent intent=new Intent(context,activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
