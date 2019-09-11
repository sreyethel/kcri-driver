package com.hbidriver.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbidriver.app.model.AdminUser;

public class SharedPrefManager {
    public static final String PROJECT="project";
    public static final String ID="id";
    public static final String USER_DATA="user_data";
    public static final String IS_LOGIN="is_login";

    public static void setUserData(Activity activity, String value){
        SharedPreferences sharedPreferences=activity.getSharedPreferences(PROJECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(USER_DATA,value).apply();
    }
    public static AdminUser getUserData(Activity activity){
        SharedPreferences sharedPreferences=activity.getSharedPreferences(PROJECT, Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String json = sharedPreferences.getString(USER_DATA,"");
        return gson.fromJson(json,new TypeToken<AdminUser>(){}.getType());
    }

    //set log in
    public static void setLogin(Activity activity,boolean value){
        SharedPreferences sharedPreferences=activity.getSharedPreferences(PROJECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(IS_LOGIN,value).apply();
    }

    //check log in status
    public static boolean getLogin(Activity activity){
        SharedPreferences sharedPreferences=activity.getSharedPreferences(PROJECT, Context.MODE_PRIVATE);
        return  sharedPreferences.getBoolean(IS_LOGIN,false);

    }
}
