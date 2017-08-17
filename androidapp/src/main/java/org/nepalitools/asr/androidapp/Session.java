package org.nepalitools.asr.androidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by dixya on 8/15/17.
 */
public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setUserName(String email) {
        prefs.edit().putString("usename", email).commit();
       // prefsCommit();
    }

    public String getUserName() {
        String usename = prefs.getString("usename","");
        return usename;
    }
}
