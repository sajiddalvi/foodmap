package com.tekdi.foodmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.stripe.model.Customer;

public class Prefs {
    private static String PREF_FILE = "com.tekdi.foodmap.prefs";
    private static String SERVER_ID_PREF = "server_id";
    private static String DEVICE_REG_ID_PREF = "device_reg_id";
    private static String SERVER_NAME_PREF = "server_name";
    private static String SERVER_PHONE_PREF = "server_phone";
    private static String SERVER_ADDRESS_PREF = "server_address";
    private static String SERVER_CUISINE_PREF = "server_cuisine";
    private static String CREDIT_CARD_CUST_PREF = "credit_card_cust";
    private static String CREDIT_CARD_LAST4_PREF = "credit_card_last4";


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

    public static Customer getCreditCard(Context context) {
        Gson gson = new Gson();
        String json = getPrefs(context).getString(CREDIT_CARD_CUST_PREF, "");
        
        Log.v("sajid",json);
        
        if (json.isEmpty())
            return null;
        
        Customer customer = gson.fromJson(json, Customer.class);
        return customer;
    }

    public static String getCreditCardLast4Pref(Context context) {
        return getPrefs(context).getString(CREDIT_CARD_LAST4_PREF, "");
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

    public static void setCreditCard(Context context, Customer customer) {

        Gson gson = new Gson();
        String json = gson.toJson(customer);
        Log.v("sajid",json);
        
        getPrefs(context).edit().putString(CREDIT_CARD_CUST_PREF, json).commit();
    }
    
    public static void setCreditCardLast4(Context context, String value) {
        getPrefs(context).edit().putString(CREDIT_CARD_LAST4_PREF, value).commit();
        
    }

}