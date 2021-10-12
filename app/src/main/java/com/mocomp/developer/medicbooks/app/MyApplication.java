package com.mocomp.developer.medicbooks.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApplication extends Application {
    private static Application _instance;
    private static SharedPreferences _preferences;
    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        FirebaseMessaging.getInstance().subscribeToTopic("tipsappnotification");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

    }
    public static Application get() {
        return _instance;
    }

    /**
     * Gets shared preferences.
     *
     * @return the shared preferences
     */
    public static SharedPreferences getSharedPreferences() {
        if (_preferences == null)
            _preferences = PreferenceManager.getDefaultSharedPreferences(_instance);
        return _preferences;
    }


    //set methods
    public static void setPreferences(String key, String value) {
        getSharedPreferences().edit().putString(key, value).commit();
    }

    public static void setPreferences(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).commit();
    }

    public static void setPreferences(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).commit();
    }

    public static void setPreferencesBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).commit();
    }


    //get methods
    public static String getPrefranceData(String key) {
        return getSharedPreferences().getString(key, "");
    }

    public static int getPrefranceDataInt(String key) {
        return getSharedPreferences().getInt(key, 0);
    }

    public static boolean getPrefranceDataBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public static long getPrefranceDataLong(String interval) {
        return getSharedPreferences().getLong(interval, 0);
    }
}
