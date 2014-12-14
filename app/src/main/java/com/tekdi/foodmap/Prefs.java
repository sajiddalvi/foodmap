package com.tekdi.foodmap;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static String PREF_FILE = "com.tekdi.foodmap.prefs";
    private static String SERVER_ID_PREF = "server_id";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_FILE, 0);
    }

    public static String getServeIdPref(Context context) {
        return getPrefs(context).getString(SERVER_ID_PREF, "");
    }

    public static void setServerIdPref(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(SERVER_ID_PREF, value).commit();
    }

}