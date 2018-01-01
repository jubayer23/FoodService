package com.smartysoft.foodservice.sharedprefs;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.smartysoft.foodservice.BuildConfig;
import com.smartysoft.foodservice.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jubayer on 6/6/2017.
 */


public class PrefManager {
    private static final String TAG = PrefManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static Gson GSON = new Gson();
    // Sharedpref file name
    private static final String PREF_NAME = BuildConfig.APPLICATION_ID;

    private static final String KEY_USER = "user";

    private static final String KEY_RECEIVED_CARD_OBJ = "received_card_obj";
    private static final String KEY_FAV_SERVICE = "fav_service";
    private static final String KEY_EMAIL_CACHE = "key_email_cache";
    private static final String KEY_PETROL_ID = "patrol_id";

    private static final String KEY_USER_START_LAT = "user_start_lat";
    private static final String KEY_USER_START_LANG = "user_start_lng";
    private static final String KEY_FCM_REG_ID = "fcm_reg_id";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    }

    public void setEmailCache(String obj) {
        editor = pref.edit();

        editor.putString(KEY_EMAIL_CACHE, obj);

        // commit changes
        editor.commit();
    }
    public String getEmailCache() {
        return pref.getString(KEY_EMAIL_CACHE,"");
    }
    public void setUserProfile(User obj) {
        editor = pref.edit();

        editor.putString(KEY_USER, GSON.toJson(obj));

        // commit changes
        editor.commit();
    }

    public void setUserProfile(String obj) {
        editor = pref.edit();

        editor.putString(KEY_USER, obj);

        // commit changes
        editor.commit();
    }

    public User getUserProfile() {

        String gson = pref.getString(KEY_USER, "");
        if (gson.isEmpty()) return null;
        return GSON.fromJson(gson, User.class);
    }

    public void setPathId(String type) {
        editor = pref.edit();

        editor.putString(KEY_PETROL_ID, type);
        // commit changes
        editor.commit();
    }

    public String getPathId() {
        return pref.getString(KEY_PETROL_ID, "");
    }

    public void setUserStartLat(String value) {
        editor = pref.edit();
        editor.putString(KEY_USER_START_LAT, value);
        editor.commit();
    }

    public String getUserStartLat() {
        return pref.getString(KEY_USER_START_LAT, "0");
    }

    public void setUserStartLang(String value) {
        editor = pref.edit();
        editor.putString(KEY_USER_START_LANG, value);
        editor.commit();
    }

    public String getUserStartLang() {
        return pref.getString(KEY_USER_START_LANG, "0");
    }

    public void setFcmRegId(String obj) {
        editor = pref.edit();

        editor.putString(KEY_FCM_REG_ID, obj);

        // commit changes
        editor.commit();
    }
    public String getFcmRegId() {
        return pref.getString(KEY_FCM_REG_ID,"");
    }
}