package com.roomfinder.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessFault;

import com.roomfinder.R;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.model.RoomMateFinderUser;
import com.roomfinder.utils.KeyUtils;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;
import com.roomfinder.utils.Util;


// 749736696865-v8r9eonuj4ualf60ger7146l71bt1d69.apps.googleusercontent.com == client id

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private CheckBox rememberLoginBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        // initialize all the controls
        init();
    }

    private void init() {
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        rememberLoginBox = (CheckBox) findViewById(R.id.rememberLoginBox);

        TextView registerLink = (TextView) findViewById(R.id.registerLink);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        String tempString = getResources().getString(R.string.register_text);
        SpannableString underlinedContent = new SpannableString(tempString);
        underlinedContent.setSpan(new UnderlineSpan(), 0, tempString.length(), 0);
        registerLink.setText(underlinedContent);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterLinkClicked();
            }
        });

        // this login activity has been started by registration activity and hence try to login with th bundle details
        Bundle dataFromRegistration = getIntent().getExtras();
        if (dataFromRegistration != null) {
            String userEmail = dataFromRegistration.getString("mUserEmail");
            String userPassword = dataFromRegistration.getString("mUserPassword");
            if (userEmail != null && userPassword != null) {
                mEmailView.setText(userEmail);
                mPasswordView.setText(userPassword);
                rememberLoginBox.setChecked(true);
                attemptLogin();
            }
        }

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            // perform the user login attempt.
            boolean rememberLogin = rememberLoginBox.isChecked();

            if (Util.isNetworkAvailable(this)) {
                Backendless.UserService.login(email, password, new DefaultCallback<BackendlessUser>(LoginActivity.this, "Logging In.. Please wait..") {
                    public void handleResponse(BackendlessUser backendlessUser) {
                        super.handleResponse(backendlessUser);
                        Backendless.UserService.setCurrentUser(backendlessUser);
                        loginSuccessful(backendlessUser);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        super.handleFault(fault);
                        String message = null;
                        switch (fault.getCode()) {
                            case Defaults.LOGIN_DISABLED:
                                message = getResources().getString(R.string.MSG_LOGIN_DISABLED);
                                break;
                            case Defaults.LOGIN_ACCOUNT_MULTIPLE_LOGIN_LIMIT_REACHED:
                                message = getResources().getString(R.string.MSG_LOGIN_ACCOUNT_MULTIPLE_LOGIN_LIMIT_REACHED);
                                break;
                            case Defaults.LOGIN_INVALID_CREDENTIALS:
                                message = getResources().getString(R.string.MSG_LOGIN_INVALID_CREDENTIALS);
                                break;
                            case Defaults.LOGIN_DISABLED_MULTIPLE:
                                message = getResources().getString(R.string.MSG_LOGIN_DISABLED_MULTIPLE);
                                break;
                            case Defaults.LOGIN_ACCOUNT_LOCKED:
                                message = getResources().getString(R.string.MSG_LOGIN_ACCOUNT_LOCKED);
                                break;
                            default:
                                message = fault.getMessage();
                        }
                        Logger.v("Login Error " + fault.toString());
                        Util.showToast(LoginActivity.this, message);
                    }
                }, rememberLogin);
            } else
                Util.showToast(this, getResources().getString(R.string.network_error));
        }
    }

    private void loginSuccessful(BackendlessUser backendlessUser) {
        RoomMateFinderUser user = new RoomMateFinderUser(backendlessUser);
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(LoginActivity.this);
        sharedPreferenceManager.saveObjectIdOfLoggedInUser(backendlessUser.getObjectId());
        sharedPreferenceManager.saveIsProfilePicAvailable(user.getIsProfilePicAvailable());
        sharedPreferenceManager.saveNameOfLoggedInUser(user.getName());
        sharedPreferenceManager.saveEmailOfLoggedInUser(user.getEmail());
        sharedPreferenceManager.savePhNoOfLoggedInUser(user.getPhoneNumber());

        Intent i = new Intent(this, PreferencesActivity.class);
        i.putExtra(KeyUtils.KEY_PREFERENCE_TYPE, KeyUtils.PREFERENCE_SAVE);
        startActivity(i);
        finish();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    public void onRegisterLinkClicked() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

}

