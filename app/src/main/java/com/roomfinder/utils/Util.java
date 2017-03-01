package com.roomfinder.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import com.roomfinder.R;

/**
 * Created by admin on 2/12/16.
 */
public class Util {


    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public static Typeface getCustomTypeFace(Context context, int index) {
        String fontPath = null;
        Typeface tf = null;
        try {
            switch (index) {
                case 0:
                    fontPath = "fonts/Roboto-Regular.ttf";
                    break;
                case 1:
                    fontPath = "fonts/GothamLight.ttf";
            }
            // Loading Font Face
            tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        } catch (Exception e) {
            tf = Typeface.DEFAULT;
        }
        return tf;
    }

    public static File getStorageDirectory(Context ctx) {
        //Logger.v(" Ext Storage "+ isExternalStorageAvailable());
        File storage = ctx.getExternalFilesDir(null);
        if (storage != null && !storage.exists())
            storage.mkdir();
        return storage;
    }

    public static Bitmap scaleDown(Bitmap originalBitmap, float maxSize, boolean filter) {
        float ratio = Math.min((float) maxSize / originalBitmap.getWidth(), (float) maxSize / originalBitmap.getHeight());
        int prWidth = Math.round((float) ratio * originalBitmap.getWidth());
        int prHeight = Math.round((float) ratio * originalBitmap.getHeight());
        Bitmap bitmap = Bitmap.createScaledBitmap(originalBitmap, prWidth, prHeight, filter);
        return bitmap;
    }


    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public static boolean isPhoneNoValid(String phno) {
        //TODO: Replace this with your own logic
        return phno.length() == 10;
    }

    public static boolean isNameValid(String name) {
        //TODO: Replace this with your own logic
        return name.length() > 0;
    }

    public static void showToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

}
