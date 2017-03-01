package com.roomfinder.utils;

/**
 * Created by admin on 2/15/16.
 */
public class KeyUtils {

    public static String KEY_PREFERENCE_TYPE = "pref_type";
    public static int PREFERENCE_SAVE = 0;
    public static int PREFERENCE_UPDATE = 1;

    public static String DATE_FORMAT = "dd-MMM-yyyy";

    public static String KEY_HAVE_FLAT_RESULTS = "haveFlats";
    public static String KEY_NEED_FLAT_RESULTS = "needFlats";

    // amenitites
    public static int AMENITIES_TV = 0;
    public static int AMENITIES_FRIDGE = 1;
    public static int AMENITIES_KITCHEN = 2;
    public static int AMENITIES_WIFI = 3;
    public static int AMENITIES_MACHINE = 4;
    public static int AMENITIES_AC = 5;
    public static int AMENITIES_BACKUP = 6;
    public static int AMENITIES_COOK = 7;
    public static int AMENITIES_PARKING = 8;

    public static String[] AMENITIES_ARRAY_KEYS = {"isTVAvailable", "isFridgeAvailable", "isKitchenAvailable", "isWIFIAvailable"
            , "isMachineAvailable", "isACAvailable", "isBackupAvailable", "isCookAvailable", "isPArkingAvailable"};
}
