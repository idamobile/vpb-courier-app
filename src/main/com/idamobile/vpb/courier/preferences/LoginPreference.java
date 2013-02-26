package com.idamobile.vpb.courier.preferences;

import android.content.SharedPreferences;

public class LoginPreference {

    public static final String LAST_LOGIN_PREF = "last_login";
    private SharedPreferences preferences;

    public LoginPreference(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public String getLogin() {
        return preferences.getString(LAST_LOGIN_PREF, null);
    }

    public void setLogin(String login) {
        preferences.edit().putString(LAST_LOGIN_PREF, login).commit();
    }

    public void clear() {
        preferences.edit().remove(LAST_LOGIN_PREF).commit();
    }
}
