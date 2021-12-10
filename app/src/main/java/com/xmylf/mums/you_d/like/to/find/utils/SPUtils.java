package com.xmylf.mums.you_d.like.to.find.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.xmylf.mums.you_d.like.to.find.AppConstants;


public class SPUtils {
    public static final String TAG = "SPUtils";
    public SharedPreferences sp;
    public Context context;
    public SharedPreferences.Editor editor;

    public SPUtils(Context context){
        sp = context.getSharedPreferences(AppConstants.SHARED_PREF, 0);
        this.context = context;
        editor = sp.edit();



    }
    public String getStringValue(String name){
        return sp.getString(name, "");
    }

    public int getIntValue(String name){
        return sp.getInt(name, 0);
    }

    public int setStringValue(String name, String value){
        int success = 0;
        try{
            sp.edit().putString(name, value).commit();
            success = 1;
        }catch (Exception e){
            Log.e(TAG, "exception is: = " , e);
        }
        return success;
    }
    public int setIntValue(String name, int value){
        int success = 0;
        try{
            sp.edit().putInt(name, value).commit();
            success = 1;
        }catch (Exception e){
            Log.e(TAG, "exception is: = " , e);
        }
        return success;
    }


    public SharedPreferences.Editor getEditor() {
        return sp.edit();
    }



}
