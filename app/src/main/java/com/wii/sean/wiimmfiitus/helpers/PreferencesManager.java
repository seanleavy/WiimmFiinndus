package com.wii.sean.wiimmfiitus.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.wii.sean.wiimmfiitus.model.MiiCharacter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PreferencesManager {

    public static String HISTORYPREFERENCES = "searchesMade";
    public static String FAVOURITESPREFERENCES = "favourites";
    public static String DEFAULTPREFERENCES = "default";

    private Set<String> savedHistory;
    private Set<String> savedFriends;
    private String firstRun;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferenceEditor;
    private Context context;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        savedHistory = sharedPreferences.getStringSet(HISTORYPREFERENCES, new LinkedHashSet<String>());
        savedFriends = sharedPreferences.getStringSet(FAVOURITESPREFERENCES, new LinkedHashSet<String>());
        this.context = context;
    }

    public Set<String> getPreferencesFor(String key) {
        if(key.equals(HISTORYPREFERENCES)) {
            return savedHistory;
        } else if(key.equals(FAVOURITESPREFERENCES)) {
            return savedFriends;
        }
        return null;
    }

    public boolean addToPreference(String preferenceKey, String valueToAdd) {
        if(!getPreferencesFor(preferenceKey).contains(valueToAdd)) {
            Set<String> set = getPreferencesFor(preferenceKey);
            set.add(valueToAdd);
            preferenceEditor = sharedPreferences.edit();
            preferenceEditor.remove(preferenceKey);
            preferenceEditor.apply();
            preferenceEditor.putStringSet(preferenceKey, set);
            return preferenceEditor.commit();
        }
        return false;
    }

    public boolean removeFromPreference(String preferenceKey, String valueToRemove) {
        if(getPreferencesFor(preferenceKey).contains(valueToRemove)) {
            Set<String> set = getPreferencesFor(preferenceKey);
            set.remove(valueToRemove);
            preferenceEditor = sharedPreferences.edit();
            preferenceEditor.remove(preferenceKey);
            preferenceEditor.apply();
            preferenceEditor.putStringSet(preferenceKey, set);
            return preferenceEditor.commit();
        }
        return false;
    }

    public void overwritePreferenceWith(List list, String key) {
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.remove(key).commit();
        preferenceEditor = sharedPreferences.edit();
        Set<String> set = new LinkedHashSet<>();
        for(int i = 0; i < list.size(); i++) {
            if(list.get(i) instanceof MiiCharacter) {
                set.add(((MiiCharacter) list.get(i)).toGson());
            }
            else
                set.add((String) list.get(i));
        }
        preferenceEditor.putStringSet(key, set).commit();
    }

    public List<MiiCharacter> getPreferencesAsList(String key) {
        List list = new ArrayList<>();
        for(String s : getPreferencesFor(key)) {
            if(!key.equals(PreferencesManager.HISTORYPREFERENCES)) {
                list.add(MiiCharacter.gsonToMii(s));
            }
            else
                list.add(s);
        }
        return list;
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
