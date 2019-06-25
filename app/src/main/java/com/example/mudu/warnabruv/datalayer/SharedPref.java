package com.example.mudu.warnabruv.datalayer;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private static final String SHARED_NAME = "my_shared";
    private static final String AVATAR_PATH = "avatar_path";
    private static SharedPref sharedPref;
    private SharedPreferences sharedPreferences;

    private SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPref getInstance(Context context) {
        if (sharedPref == null) {
            sharedPref = new SharedPref(context);
        }

        return sharedPref;
    }


    public void saveAvatarPath(String path) {
        sharedPreferences.edit().putString(AVATAR_PATH, path).commit();
    }

    public String getAvatarPath() {
        return sharedPreferences.getString(AVATAR_PATH, "");
    }
}
