package com.wii.sean.wiimmfiitus.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PreferencesManager {

    public static final String HISTORYPREFERENCES = "searchesMade";
    public static final String FAVOURITESPREFERENCES = "favourites";
    public static final String DEFAULTPREFERENCES = "default";
    public static final String DIALOGSEARCHPREFERENCE = "searchToggle";

    private DB snappy;
    private String firstRun;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferenceEditor;
    private Context context;

    public PreferencesManager(Context context) {
        try {
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            this.context = context;
            snappy = DBFactory.open(context);
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
        Collection preferenceList;
        switch(preferenceKey) {
            case "searchesMade" : {
                preferenceList = new CircularFifoQueue(6);
                preferenceList.addAll(getPreferencesAsList(preferenceKey));
                break;
            }
            default: {
                preferenceList = new ArrayList();
                preferenceList.addAll(getPreferencesAsList(preferenceKey));
                break;
            }
        }
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

    public void removeFromPreference(String preferenceKey, Object toRemove) {
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

    public boolean isFirstRun() {
        return !sharedPreferences.getBoolean(PreferencesManager.DEFAULTPREFERENCES, false);
    }

    public void setFirstRunToBe(boolean bool) {
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putBoolean(PreferencesManager.DEFAULTPREFERENCES, bool);
        preferenceEditor.commit();
    }
}
