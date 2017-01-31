package com.wii.sean.wiimmfiitus.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.wii.sean.wiimmfiitus.model.MiiCharacter;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PreferencesManager {

    public static String HISTORYPREFERENCES = "searchesMade";
    public static String FAVOURITESPREFERENCES = "favourites";
    public static String DEFAULTPREFERENCES = "default";

    private String savedHistory;
    private String savedFriends;
    private String firstRun;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferenceEditor;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        savedHistory = sharedPreferences.getString(HISTORYPREFERENCES, "");
        savedFriends = sharedPreferences.getString(FAVOURITESPREFERENCES, "");
    }

    public String getPreferencesFor(String key) {
        if(key.equals(HISTORYPREFERENCES)) {
            return savedHistory;
        } else if(key.equals(FAVOURITESPREFERENCES)) {
            return savedFriends;
        }
        return null;
    }

    public boolean addToPreference(String preferenceKey, String valueToAdd) {
        if(!getPreferencesFor(preferenceKey).contains(valueToAdd)) {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(getPreferencesFor(preferenceKey));
                jsonArray.put(valueToAdd);
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.remove(preferenceKey);
                preferenceEditor.apply();
                preferenceEditor.putString(preferenceKey, jsonArray.toString());
                return preferenceEditor.commit();
            } catch (JSONException e) {
                Log.e(LogHelper.getTag(getClass()), e.getMessage());
            }
        }
        return false;
    }

    public boolean removeFromPreference(String preferenceKey, String valueToRemove) {
        if(getPreferencesFor(preferenceKey).contains(valueToRemove)) {
            try {
                JSONArray jsonArray = new JSONArray(getPreferencesFor(preferenceKey));
                for(int i = 0; i < jsonArray.length(); i++) {
                    String prefVal = jsonArray.getJSONObject(i).toString();
                    if(prefVal.contains(preferenceKey)) {
                        jsonArray.remove(i);
                    }
                }
                preferenceEditor = sharedPreferences.edit();
                preferenceEditor.remove(preferenceKey);
                preferenceEditor.apply();
                preferenceEditor.putString(preferenceKey, jsonArray.toString());
                return preferenceEditor.commit();
            } catch (JSONException e) {
                Log.e(LogHelper.getTag(getClass()), e.getMessage());
            }
        }
        return false;
    }

    //todo generics instead
    public List getPreferencesAsList(String key) {
        if(key.equals(PreferencesManager.HISTORYPREFERENCES)) {
            List<String> list = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(key);
                for(int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getString(i));
                }
                return list;
            } catch (JSONException e) {
                Log.e(LogHelper.getTag(getClass()), e.getMessage());
            }
        }
        List<MiiCharacter> list = new ArrayList<>();
        try {
            Gson gson = new Gson();
            JSONArray jsonArray = new JSONArray(getPreferencesFor(key));
            for(int i = 0; i < jsonArray.length(); i++) {
                //todo pucnh myself in the face
                // wonderful design here
                list.add(MiiCharacter.gsonToMii(gson.toJson(jsonArray.get(i))));
            }
        } catch (JSONException e) {
            Log.e(LogHelper.getTag(getClass()), e.getMessage());
        }
        return list;
    }

    public void overWritePreferencesWith(Collection preferences, String preferenceType) {
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.remove(preferenceType);
        preferenceEditor.commit();
        JSONArray jsonArray = new JSONArray(preferences);
        preferenceEditor = sharedPreferences.edit();
        preferenceEditor.putString(preferenceType, jsonArray.toString());
        preferenceEditor.commit();
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
