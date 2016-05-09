package com.sam_chordas.android.stockhawk.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

/**
 * Created by abhishek on 11/04/16.
 */
public class PrefsHelper {

    public static final String APP_PREFERENCES = "collectionpreferences";
    public static final String PREF_TYPE_INTEGER = "integer";
    public static final String PREF_TYPE_STRING = "string";
    public static final String PREF_TYPE_BOOLEAN = "boolean";
    public static final String PREF_TYPE_FLOAT = "float";
    public static final String PREF_TYPE_LONG = "long";
    public static final String PREF_TYPE_STRINGSET = "stringset";

    private SharedPreferences prefs;



    public PrefsHelper(Context context) {
        prefs = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Save shared preference.
     *
     * @param prefType
     * @param key
     * @param value
     */
    public void savePreference(String prefType, String key, Object value) {

        try {
            SharedPreferences.Editor editor = prefs.edit();
            switch (prefType) {
                case PREF_TYPE_INTEGER:
                    editor.putInt(key, (Integer) value);
                    break;

                case PREF_TYPE_STRING:
                    editor.putString(key, (String) value);
                    break;

                case PREF_TYPE_BOOLEAN:
                    editor.putBoolean(key, (Boolean) value);
                    break;

                case PREF_TYPE_FLOAT:
                    editor.putFloat(key, (Float) value);
                    break;

                case PREF_TYPE_LONG:
                    editor.putLong(key, (Long) value);
                    break;

                case PREF_TYPE_STRINGSET:
                    editor.putStringSet(key, (Set<String>) value);
                    break;
            }
            editor.commit();
        } catch (Exception e) {
            Log.i(PrefsHelper.class.getSimpleName(), "Unable to store data to Shared preferences");
        }
    }

    /**
     * Get shared preference.
     *
     * @param key
     * @param prefType
     * @return
     */
    public Object getPreference(String prefType, String key) {

        Object value = null;
        if (prefType.equals(PREF_TYPE_INTEGER)) {
            value = prefs.getInt(key, 0);
        } else if (prefType.equals(PREF_TYPE_STRING)) {
            value = prefs.getString(key, null);
        } else if (prefType.equals(PREF_TYPE_BOOLEAN)) {
            value = prefs.getBoolean(key, false);
        } else if (prefType.equals(PREF_TYPE_FLOAT)) {
            value = prefs.getFloat(key, 0);
        } else if (prefType.equals(PREF_TYPE_LONG)) {
            value = prefs.getLong(key, 0);
        } else if (prefType.equals(PREF_TYPE_STRINGSET)) {
            value = prefs.getStringSet(key, null);
        }
        return value;
    }

    public void deletePreference(String prefKey) {
        if (prefs.contains(prefKey))
            prefs.edit().remove(prefKey).apply();
    }

    public SharedPreferences getSharedPref() {
        return prefs;
    }

    public boolean getBoolean(String booleanKey) {
        return prefs.getBoolean(booleanKey, false);
    }
}
