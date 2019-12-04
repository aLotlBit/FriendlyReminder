package com.example.multitimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesHelper {

    static void setString(Context context, String key, String value) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }


    static void setLong(Context context, String key, Long value) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    static void setInt(Context context, String key, Integer value) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }



    static void addIdToSet(Context context, int id) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        Set<String> s = new HashSet<String>();
        Set<String> set_ids = mSharedPrefs.getStringSet("set_ids", s);
        set_ids.add(String.valueOf(id));
        editor.putStringSet("set_ids", set_ids);
        editor.putInt("id_current", id + 1);
        editor.apply();
    }

    static int getCurrentID(Context context) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int id_current = mSharedPrefs.getInt("id_current", 0);
        return id_current;
    }

}
