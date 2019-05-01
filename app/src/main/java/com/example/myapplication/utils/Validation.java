package com.example.myapplication.utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.util.regex.Pattern;

public class Validation {
    public static boolean validateFields(String name) {
        if(name != null && !name.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
