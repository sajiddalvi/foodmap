package com.tekdi.foodmap;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static String PREF_FILE = "com.tekdi.foodmap.prefs";
    private static String SERVER_ID_PREF = "server_id";
    private static String DEVICE_REG_ID_PREF = "device_reg_id";
    private static String SERVER_NAME_PREF = "server_name";
    private static String SERVER_PHONE_PREF = "server_phone";
    private static String SERVER_ADDRESS_PREF = "server_address";
    private static String SERVER_CUISINE_PREF = "server_cuisine";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_FILE, 0);
    }

    public static String getServeIdPref(Context context) {
        return getPrefs(context).getString(SERVER_ID_PREF, "");
    }

    public static String getSERVER_NAME_PREF(Context context) {
        return getPrefs(context).getString(SERVER_NAME_PREF, "");
    }

    public static String getSERVER_PHONE_PREF(Context context) {
        return getPrefs(context).getString(SERVER_PHONE_PREF, "");
    }

    public static String getSERVER_ADDRESS_PREF(Context context) {
        return getPrefs(context).getString(SERVER_ADDRESS_PREF, "");
    }

    public static String getSERVER_CUISINE_PREF(Context context) {
        return getPrefs(context).getString(SERVER_CUISINE_PREF, "");
    }

    public static String getDeviceRegIdPref(Context context) {
        return getPrefs(context).getString(DEVICE_REG_ID_PREF, "");
    }

    public static void setServerIdPref(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(SERVER_ID_PREF, value).commit();
    }

    public static void setSERVER_NAME_PREF(Context context, String value) {
        getPrefs(context).edit().putString(SERVER_NAME_PREF, value).commit();
    }

    public static void setSERVER_PHONE_PREF(Context context, String value) {
        getPrefs(context).edit().putString(SERVER_PHONE_PREF, value).commit();
    }

    public static void setSERVER_ADDRESS_PREF(Context context, String value) {
        getPrefs(context).edit().putString(SERVER_ADDRESS_PREF, value).commit();
    }

    public static void setSERVER_CUISINE_PREF(Context context, String value) {
        getPrefs(context).edit().putString(SERVER_CUISINE_PREF, value).commit();
    }

    public static void setDeviceIdRegPref(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(DEVICE_REG_ID_PREF, value).commit();
    }

}