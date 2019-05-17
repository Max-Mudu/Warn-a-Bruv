package com.example.mudu.warnabruv.Helper;

import android.content.Context;
import android.widget.Toast;

public class Helper {

    public static final String NAME = "Name";
    public static final String EMAIL = "Email";
    public static final String PHONE = "Phone";
    public static final int SELECT_PICTURE = 2000;

    public static boolean isValidEmail(String email) {
        if (email.contains("@")) {
            return false;
        }
        return true;
    }

    public static void displayMessageToast(Context context, String displayMessage) {
        Toast.makeText(context, displayMessage, Toast.LENGTH_LONG).show();
    }
}
