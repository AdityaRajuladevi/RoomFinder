package com.roomfinder.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import com.roomfinder.ui.LoginActivity;

/**
 * Created by Aditya on 2/13/2016.
 */
public class SharedPreferenceManager {

    public static final String KEY_USER_OBJ_ID = "userObjId";
    public static final String KEY_USER_IS_PROFILE_PIC_AVAILABLE = "isProfilePicAvailable";
    public static final String KEY_USER_PROFILE_NAME = "profilename";
    public static final String KEY_USER_PHONE_NO = "phono";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_HAS_HAVE_FLAT_REQUIREMENT = "haveAvbl";
    public static final String KEY_USER_HAS_NEED_FLAT_REQUIREMENT = "needAvbl";

    public static final String KEY_PREFERENCE_FOOD = "foodType";
    public static final String KEY_PREFERENCE_DRINKING = "drinkingType";
    public static final String KEY_PREFERENCE_SMOKING = "smokingType";
    public static final String KEY_PREFERENCE_LIFESTYLE = "lifeStyleType";

    public static final String KEY_PREFERENCE_DEVICE_MSG_RECEIVED = "msgReceived";
    // keys
    // Sharedpref file name
    private static final String PREF_NAME = "RoomFinderApp";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SharedPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    // set Obj Id
    public void saveObjectIdOfLoggedInUser(String objId) {
        editor.putString(KEY_USER_OBJ_ID, objId);
        // commit changes
        editor.commit();
    }

    // get obj id
    public String getObjectIdOFLoggedInUser() {
        return pref.getString(KEY_USER_OBJ_ID, null);
    }

    // set profile pic Id
    public void saveIsProfilePicAvailable(boolean isAvbl) {
        editor.putBoolean(KEY_USER_IS_PROFILE_PIC_AVAILABLE, isAvbl);
        // commit changes
        editor.commit();
    }

    public Boolean getIsProfilePicAvailable() {
        return pref.getBoolean(KEY_USER_IS_PROFILE_PIC_AVAILABLE, false);
    }

    // set name
    public void saveNameOfLoggedInUser(String name) {
        editor.putString(KEY_USER_PROFILE_NAME, name);
        // commit changes
        editor.commit();
    }

    // get name
    public String getNameOFLoggedInUser() {
        return pref.getString(KEY_USER_PROFILE_NAME, null);
    }

    // set phno
    public void savePhNoOfLoggedInUser(String name) {
        editor.putString(KEY_USER_PHONE_NO, name);
        // commit changes
        editor.commit();
    }

    // get phno
    public String getPhNoOFLoggedInUser() {
        return pref.getString(KEY_USER_PHONE_NO, null);
    }

    // set email
    public void saveEmailOfLoggedInUser(String name) {
        editor.putString(KEY_USER_EMAIL, name);
        // commit changes
        editor.commit();
    }

    // get email
    public String getEmailOFLoggedInUser() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    // save requirement status of the user to restrict him to post a single requirement only
    public void saveUserHasHaveFlatRequirement(boolean userReqAvbl) {
        editor.putBoolean(KEY_USER_HAS_HAVE_FLAT_REQUIREMENT, userReqAvbl);
        // commit changes
        editor.commit();
    }

    public boolean getUserHasHaveFlatRequest() {
        return pref.getBoolean(KEY_USER_HAS_HAVE_FLAT_REQUIREMENT, false);
    }

    // save requirement status of the user to restrict him to post a single requirement only
    public void saveUserHasNeedFlatRequirement(boolean userReqAvbl) {
        editor.putBoolean(KEY_USER_HAS_NEED_FLAT_REQUIREMENT, userReqAvbl);
        // commit changes
        editor.commit();
    }

    public boolean getUserHasNeedFlatRequest() {
        return pref.getBoolean(KEY_USER_HAS_NEED_FLAT_REQUIREMENT, false);
    }


    // save requirement status of the user to restrict him to post a single requirement only
    public void saveMessageReceivedStatus(boolean newMsgReceived) {
        editor.putBoolean(KEY_PREFERENCE_DEVICE_MSG_RECEIVED, newMsgReceived);
        // commit changes
        editor.commit();
    }

    public boolean getMessageStatus() {
        return pref.getBoolean(KEY_PREFERENCE_DEVICE_MSG_RECEIVED, false);
    }


    public void logoutUser() {
        //  clear shared Preferences..
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

    }

    public void savePrefs(String foodType, String drinkingType, String smokingType, String lifeStyleType) {
        editor.putString(KEY_PREFERENCE_FOOD, foodType);
        editor.putString(KEY_PREFERENCE_DRINKING, drinkingType);
        editor.putString(KEY_PREFERENCE_SMOKING, smokingType);
        editor.putString(KEY_PREFERENCE_LIFESTYLE, lifeStyleType);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> fetchUserPreferences() {
        HashMap<String, String> preferences = new HashMap<>();
        preferences.put(KEY_PREFERENCE_FOOD, pref.getString(KEY_PREFERENCE_FOOD, "Veg"));
        preferences.put(KEY_PREFERENCE_DRINKING, pref.getString(KEY_PREFERENCE_DRINKING, "No"));
        preferences.put(KEY_PREFERENCE_SMOKING, pref.getString(KEY_PREFERENCE_SMOKING, "No"));
        preferences.put(KEY_PREFERENCE_LIFESTYLE, pref.getString(KEY_PREFERENCE_LIFESTYLE, "EarlyBird"));
        return preferences;
    }

}
