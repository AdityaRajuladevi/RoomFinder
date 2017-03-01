package com.roomfinder.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.List;

import com.roomfinder.R;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.model.UserPreferences;
import com.roomfinder.utils.KeyUtils;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;
import com.roomfinder.utils.Util;

public class PreferencesActivity extends AppCompatActivity {

    private int prefSubmitType;
    private RadioGroup radioGroup_food, radioGroup_smoking, radioGroup_drinking, radioGroup_life_style;
    private String objId;
    private UserPreferences currentUserPreference;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        objId = sharedPreferenceManager.getObjectIdOFLoggedInUser();

        ActionBar actionBar = getSupportActionBar();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            prefSubmitType = extras.getInt(KeyUtils.KEY_PREFERENCE_TYPE);
        if (actionBar != null && prefSubmitType == KeyUtils.PREFERENCE_UPDATE)
            actionBar.setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init() {

        radioGroup_food = (RadioGroup) findViewById(R.id.radioGrp_food);
        radioGroup_smoking = (RadioGroup) findViewById(R.id.radioGrp_smoking);
        radioGroup_drinking = (RadioGroup) findViewById(R.id.radioGrp_drinking);
        radioGroup_life_style = (RadioGroup) findViewById(R.id.radioGrp_lifestyle);

        radioGroup_food.check(R.id.radio_food_veg);
        radioGroup_drinking.check(R.id.radio_drinking_no);
        radioGroup_smoking.check(R.id.radio_smoking_no);
        radioGroup_life_style.check(R.id.radio_lifestyle_early);

        Button prefBtn = (Button) findViewById(R.id.btn_pref_update);
        if (prefSubmitType == KeyUtils.PREFERENCE_SAVE) {
            prefBtn.setText(getResources().getString(R.string.save));
        } else {
            prefBtn.setText(getResources().getString(R.string.update));
        }
        // fetch the user preferences from server and display them
        if (Util.isNetworkAvailable(this))
            fetchUserPreferencesFromServer();
        else
            Util.showToast(this, getResources().getString(R.string.network_error));

        // save or update button logic
        prefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save prefs and finish the activity
                capturePreferences();
            }
        });
    }


    private void fetchUserPreferencesFromServer() {
        String whereClause = "ownerId =" + "'" + objId + "'";
        //Logger.v(" Fetch where " + whereClause);
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(UserPreferences.class).find(dataQuery, new DefaultCallback<BackendlessCollection<UserPreferences>>(this, " Loading Preferences Please Wait ..") {

            @Override
            public void handleResponse(BackendlessCollection<UserPreferences> userPreferencesBackendlessCollection) {

                super.handleResponse(userPreferencesBackendlessCollection);

                List<UserPreferences> resultList = userPreferencesBackendlessCollection.getData();
                if (resultList.size() > 0) {
                    currentUserPreference = resultList.get(0);
                    //   Logger.v(" Fetch obj " + currentUserPreference.getObjId() + " ==  " + currentUserPreference.getFoodType());
                    if (currentUserPreference != null) {
                        // set values of food
                        switch (currentUserPreference.getFoodType()) {
                            case "Veg":
                                radioGroup_food.check(R.id.radio_food_veg);
                                break;
                            case "NonVeg":
                                radioGroup_food.check(R.id.radio_food_non);
                                break;
                            case "Don't Care":
                                radioGroup_food.check(R.id.radio_food_dont);
                                break;
                        }

                        // set values of drinking
                        switch (currentUserPreference.getDrinkingType()) {
                            case "Yes":
                                radioGroup_drinking.check(R.id.radio_drinking_yes);
                                break;
                            case "No":
                                radioGroup_drinking.check(R.id.radio_drinking_no);
                                break;
                            case "Don't Care":
                                radioGroup_drinking.check(R.id.radio_drinking_dont);
                                break;
                        }

                        // set values of smoking
                        switch (currentUserPreference.getSmokingType()) {
                            case "Yes":
                                radioGroup_smoking.check(R.id.radio_smoking_yes);
                                break;
                            case "No":
                                radioGroup_smoking.check(R.id.radio_smoking_no);
                                break;
                            case "Don't Care":
                                radioGroup_smoking.check(R.id.radio_smoking_dont);
                                break;
                        }

                        // set values of smoking
                        switch (currentUserPreference.getLifeStyleType()) {
                            case "EarlyBird":
                                radioGroup_life_style.check(R.id.radio_lifestyle_early);
                                break;
                            case "NightOwl":
                                radioGroup_life_style.check(R.id.radio_lifestyle_night);
                                break;
                            case "Don't Care":
                                radioGroup_life_style.check(R.id.radio_lifestyle_dont);
                                break;
                        }
                    }
                } else {
                    // fault scenario create a dummy preference
                    currentUserPreference = new UserPreferences();
                    currentUserPreference.setObjId(objId);
                    currentUserPreference.setDrinkingType("No");
                    currentUserPreference.setFoodType("Veg");
                    currentUserPreference.setSmokingType("No");
                    currentUserPreference.setLifeStyleType("EarlyBird");
                    currentUserPreference.setIsUserPostedHaveFlatRequirement(false);
                }

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                super.handleFault(backendlessFault);

                // user preferences table does not exist in the console
                if (backendlessFault.getCode().equals("1009")) {

                }
                // fault scenario create a dummy preference
                currentUserPreference = new UserPreferences();
                currentUserPreference.setObjId(objId);
                currentUserPreference.setDrinkingType("No");
                currentUserPreference.setFoodType("Veg");
                currentUserPreference.setSmokingType("No");
                currentUserPreference.setLifeStyleType("EarlyBird");
                currentUserPreference.setIsUserPostedHaveFlatRequirement(false);
                Logger.v(" Fault " + backendlessFault.toString());
            }
        });
    }


    private void capturePreferences() {
        // capturing food preference
        String foodType = null;
        switch (radioGroup_food.getCheckedRadioButtonId()) {
            case R.id.radio_food_veg:
                foodType = getResources().getString(R.string.preference_food_veg);
                break;
            case R.id.radio_food_non:
                foodType = getResources().getString(R.string.preference_food_non);
                break;
            case R.id.radio_food_dont:
                foodType = getResources().getString(R.string.preference_dont_care);
                break;
        }

        // capturing drinking preference
        String drinkingType = null;
        switch (radioGroup_drinking.getCheckedRadioButtonId()) {
            case R.id.radio_drinking_yes:
                drinkingType = getResources().getString(R.string.preference_yes);
                break;
            case R.id.radio_drinking_no:
                drinkingType = getResources().getString(R.string.preference_no);
                break;
            case R.id.radio_drinking_dont:
                drinkingType = getResources().getString(R.string.preference_dont_care);
                break;
        }

        // capturing smoking preference
        String smokingType = null;
        switch (radioGroup_smoking.getCheckedRadioButtonId()) {
            case R.id.radio_smoking_yes:
                smokingType = getResources().getString(R.string.preference_yes);
                break;
            case R.id.radio_smoking_no:
                smokingType = getResources().getString(R.string.preference_no);
                break;
            case R.id.radio_smoking_dont:
                smokingType = getResources().getString(R.string.preference_dont_care);
                break;
        }

        // capturing Life Style preference
        String lifeStyleType = null;
        switch (radioGroup_life_style.getCheckedRadioButtonId()) {
            case R.id.radio_lifestyle_early:
                lifeStyleType = getResources().getString(R.string.preference_life_style_night_early_bird);
                break;
            case R.id.radio_lifestyle_night:
                lifeStyleType = getResources().getString(R.string.preference_life_style_night_owl);
                break;
            case R.id.radio_lifestyle_dont:
                lifeStyleType = getResources().getString(R.string.preference_dont_care);
                break;
        }

        if (currentUserPreference != null) {
            currentUserPreference.setObjId(objId);
            currentUserPreference.setFoodType(foodType);
            currentUserPreference.setDrinkingType(drinkingType);
            currentUserPreference.setSmokingType(smokingType);
            currentUserPreference.setLifeStyleType(lifeStyleType);

            if (Util.isNetworkAvailable(PreferencesActivity.this))
                savePreferences(currentUserPreference);
            else
                Util.showToast(this, getResources().getString(R.string.network_error));
        }
    }

    private void savePreferences(UserPreferences userPreferences) {
        // save object asynchronously
        Backendless.Persistence.save(userPreferences, new DefaultCallback<UserPreferences>(this, " Saving User Preferences ...") {
            public void handleResponse(UserPreferences response) {
                // new user Preferences instance has been saved
                if (response != null) {

                    // save it in preferences
                    sharedPreferenceManager.savePrefs(response.getFoodType(), response.getDrinkingType(), response.getSmokingType(), response.getLifeStyleType());
                    sharedPreferenceManager.saveUserHasHaveFlatRequirement(response.isUserPostedHaveFlatRequirement());
                    sharedPreferenceManager.saveUserHasNeedFlatRequirement(response.isUserPostedNeedFlatRequirement());

                    if (prefSubmitType == KeyUtils.PREFERENCE_SAVE) {
                        Intent i = new Intent(PreferencesActivity.this, HomeActivity.class);
                        startActivity(i);
                    }
                    finish();
                }
                super.handleResponse(response);
            }

            public void handleFault(BackendlessFault fault) {
                super.handleFault(fault);
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
