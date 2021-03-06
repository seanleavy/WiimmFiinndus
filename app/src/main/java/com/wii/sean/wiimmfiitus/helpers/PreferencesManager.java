package com.wii.sean.wiimmfiitus.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferencesManager {

    public static final String HISTORYPREFERENCES = "searchesMade";
    public static final String FAVOURITESPREFERENCES = "favourites";
    public static final String CHEATERPREFERENCES = "cheaters";
    public static final String APPFIRSTRUN = "default";
    public static final String SEARCHFIRSTRUN = "default";
    public static final String FIRSTFAVOURITEADDED = "firstFavouriteAdded";
    public static final String DIALOGSEARCHPREFERENCE = "searchToggle";

    private DB snappy;
    private String firstRun;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferenceEditor;
    private Context context;

    public PreferencesManager(Context context) {
        try {
            snappy = DBFactory.open(context);
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            this.context = context;
        } catch (SnappydbException e) {
            Log.e(LogHelper.getTag(getClass()), e.getMessage());
        }
    }

    public Object[] getPreferencesFor(String key) {
            try {
                if(snappy.getObjectArray(key, Object.class) != null)
                return snappy.getObjectArray(key, Object.class);
            }
            catch (SnappydbException e) {
                Log.e(LogHelper.getTag(getClass()), e.getMessage());
            }
        return null;
    }

    public List getPreferencesAsList(String key) {
        try {
            if(snappy.getObjectArray(key, Object.class) != null) {
                return Arrays.asList(snappy.getObjectArray(key, Object.class));
            }
        }
        catch (SnappydbException e) {
            Log.e(LogHelper.getTag(getClass()), e.getMessage());
        }
        return new ArrayList();
    }

    public void addToPreference(String preferenceKey, Object valueToAdd) {
        List preferenceList;
        if(getPreferencesFor(preferenceKey) != null)
            preferenceList = new ArrayList(Arrays.asList(getPreferencesFor(preferenceKey)));
        else
            preferenceList = new ArrayList();
        if(!preferenceList.contains(valueToAdd)) {
            try {
                preferenceList.add(valueToAdd);
                snappy.del(preferenceKey);
                snappy.put(preferenceKey, preferenceList.toArray());
            } catch (SnappydbException e) {
                Log.e(LogHelper.getTag(getClass()), e.getMessage());
            }
        }
    }


    public boolean removeFromPreference(String preferenceKey, Object toRemove) {
        List values = new ArrayList(Arrays.asList(getPreferencesFor(preferenceKey)));
        if(values.contains(toRemove)) {
            values.remove(toRemove);
            try {
                snappy.del(preferenceKey);
                snappy.put(preferenceKey, values.toArray());
            }
            catch (SnappydbException e) {
                Log.e(LogHelper.getTag(getClass()), e.getMessage());
            }
        }
        return false;
    }

    public boolean clearPrefencefromDB(String preferenceKey) {
        try {
            snappy.del(preferenceKey);
            return true;
        } catch (SnappydbException e) {
            Log.e(LogHelper.getTag(getClass()), e.getMessage());
            return false;
        }
    }

    public void overwritePreferenceWith(List list, String key) {
        try {
            snappy.del(key);
            snappy.put(key, list.toArray());
        } catch (SnappydbException e) {
            Log.e(LogHelper.getTag(getClass()), e.getMessage());
        }
    }

    public boolean isFirstRun(String preferenceType) {
        return !sharedPreferences.getBoolean(preferenceType, false);
    }

    public void setFirstRunPreference(String preferenceType, boolean bool) {
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putBoolean(preferenceType, bool);
        preferenceEditor.commit();
    }
}
