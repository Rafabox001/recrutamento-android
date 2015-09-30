package com.blackboxstudios.rafael.reclutamento_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Rafael on 30/09/2015.
 */
public class Utils {
    private static int MAX_DIGITS = 3;

    public static String trimString(String str) {
        if (str.length() >= MAX_DIGITS)
            return str.substring(0, MAX_DIGITS);
        else
            return str;
    }

    /** Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return
     */
    static public boolean isNetworkAvailable(Context c){
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork !=null && activeNetwork.isConnectedOrConnecting();
    }
}
