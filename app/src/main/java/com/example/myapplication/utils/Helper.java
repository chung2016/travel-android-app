package com.example.myapplication.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class Helper extends Application {
    private static Toast myToast;

    public static void toast(Context context, String text) {
        if (myToast != null) {
            myToast.cancel();
            myToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            myToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        myToast.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
