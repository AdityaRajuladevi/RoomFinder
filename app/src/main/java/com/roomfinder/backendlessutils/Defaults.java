package com.roomfinder.backendlessutils;

public class Defaults {
    public static final String APPLICATION_ID = "BD0EE523-FE44-D82A-FF16-6F2B12F93600";
    public static final String SECRET_KEY = "7B943651-3626-4240-FF10-46759054D100";
    public static final String VERSION = "v1";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static final int CAMERA_RESULT_CODE = 1001;
    public static final int GALLERY_RESULT_CODE = 1002;

    public static final String FILES_PROFILE_PIC_DIRECTORY = "profilePics";
    public static final String FILES_PROFILE_PIC_URL = SERVER_URL + "/" + APPLICATION_ID + "/" + VERSION + "/files/" + FILES_PROFILE_PIC_DIRECTORY;

    public static final String FILES_HAVE_FLAT_DIRECTORY = "haveflatImages";
    public static final String FILES_HAVE_FLAT_URL = SERVER_URL + "/" + APPLICATION_ID + "/" + VERSION + "/files/" + FILES_HAVE_FLAT_DIRECTORY;


    public static final String MAPS_KEY_WINDOWS = "AIzaSyA4M0s1VK6H0Y84qpZlT5xVuqwcH6DALX4";
    // your application server key to be kept here..
    public static final String MAPS_API_KEY = "AIzaSyDwVnkZ0TL2pQ4-8xu5rOVnm7SJ7ElHtdk";

    // login error codes
    public static final String LOGIN_DISABLED = "3000";
    public static final String LOGIN_DISABLED_MULTIPLE = "3002";
    public static final String LOGIN_INVALID_CREDENTIALS = "3003";
    public static final String LOGIN_ACCOUNT_LOCKED = "3036";
    public static final String LOGIN_ACCOUNT_MULTIPLE_LOGIN_LIMIT_REACHED = "3044";

    public static final String REGISTRATION_FAILED = "3014";
    public static final String REGISTRATION_USER_ALREADY_EXISTS = "3033";
    public static final String REGISTRATION_EMAIL_WRONG = "3040";
    public static final String REGISTRATION_ERROR = "3021";

    // messaging features ...
    public static final String GOOGLE_PROJECT_ID = "574204642042";

    public static final String DEFAULT_CHANNEL = "default";
    public static final String GCM_TAG = "GCM TAG";
    public static final String KEY_COMPANION_ID = "companionId";


}