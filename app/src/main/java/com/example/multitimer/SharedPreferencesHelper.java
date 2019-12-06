package com.example.multitimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPreferencesHelper {

    static ArrayList<Item> loadData(Context mContext) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    //    SharedPreferences.Editor mEdit = mSharedPrefs.edit();

        Set<String> s = new HashSet<String>();
        Set<String> set_ids = mSharedPrefs.getStringSet("set_ids", s);
        List<String> list_ids = new ArrayList<String>(set_ids);

        ArrayList<Item> itemsList = new ArrayList();

        for (int i = 0; i < list_ids.size(); i++) {
            String id = list_ids.get(i);
            String title = mSharedPrefs.getString("title_" + id, null);
            long millisStart = mSharedPrefs.getLong("millis_start_" + id, -1);
            long millisEnd = mSharedPrefs.getLong("millis_end_" + id, -1);
            int interval = mSharedPrefs.getInt("interval_" + id, -1);
            int alertActive = mSharedPrefs.getInt("alert_active_" + id, 0);

            itemsList.add(new Item(Integer.parseInt(id), title, millisStart, millisEnd, 0, 0, interval, alertActive));
        }
    //    mEdit.commit();
        return itemsList;
    }

    static int getSortMode(Context mContext) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mSharedPrefs.getInt("sort_mode", 0);
    }

    static int getSortInverted(Context mContext) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        return mSharedPrefs.getInt("sort_inverted", 0);
    }

    static ArrayList<Item> getItemsWithActiveAlerts(Context mContext) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
     //   SharedPreferences.Editor mEdit = mSharedPrefs.edit();

        Set<String> s = new HashSet<String>();
        Set<String> set_ids = mSharedPrefs.getStringSet("set_ids", s);
        List<String> list_ids = new ArrayList<String>(set_ids);

        ArrayList<Item> itemsList = new ArrayList();

        for (int i = 0; i < list_ids.size(); i++) {
            String id = list_ids.get(i);
            String title = mSharedPrefs.getString("title_" + id, null);
            long millisEnd = mSharedPrefs.getLong("millis_end_" + id, -1);
            int alertActive = mSharedPrefs.getInt("alert_active_" + id, 0);
            if (alertActive == 1) {
                itemsList.add(new Item(Integer.parseInt(id), title, -1, millisEnd, 0, 0, -1, 1));
            }
        }
      //  mEdit.commit();
        return itemsList;
    }

    static int getIntervalForReset(Context mContext, Integer id) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        Integer interval = mSharedPrefs.getInt("interval_" + id, -1);
        return interval;
    }

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

    static void removeDataOfItemByID(Context context, int id) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.remove("title_" + id);
        editor.remove("millis_start_" + id);
        editor.remove("millis_end_" + id);
        editor.remove("interval_" + id);
        editor.remove("alert_active_" + id);
        Set<String> s = new HashSet<String>();
        Set<String> set_ids = mSharedPrefs.getStringSet("set_ids", s);
        set_ids.remove(String.valueOf(id));
        editor.putStringSet("set_ids", set_ids);
        editor.commit();
    }

    static void clearAllData(Context context) {
        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mSharedPrefs.edit().clear().commit();
    }

}
