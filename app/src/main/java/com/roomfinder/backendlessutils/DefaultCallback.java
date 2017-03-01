package com.roomfinder.backendlessutils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;

import com.roomfinder.R;

public class DefaultCallback<T> extends BackendlessCallback<T> {
    private Context context;
    private ProgressDialog progressDialog;

    public DefaultCallback(Context context) {
        this.context = context;
        progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.app_name), "Loading...", true);
    }

    public DefaultCallback(Context context, String message) {
        this.context = context;
        progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.app_name), message, true);
    }

    @Override
    public void handleResponse(T response) {
        progressDialog.cancel();
    }

    @Override
    public void handleFault(BackendlessFault fault) {
        progressDialog.cancel();
        // Toast.makeText( context, fault.getMessage(), Toast.LENGTH_LONG ).show();
    }
}