package com.roomfinder.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.FileInfo;
import com.backendless.persistence.BackendlessDataQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.roomfinder.R;
import com.roomfinder.adapter.AmenitiesGridAdapter;
import com.roomfinder.adapter.ImagesGridAdapter;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.callbacks.ItemClickSupport;
import com.roomfinder.model.AmenitiesItem;
import com.roomfinder.model.HaveFlat;
import com.roomfinder.model.NeedFlat;
import com.roomfinder.model.UserPreferences;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.Util;

public class SelectedProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private String selectedObjId;
    private String companionId;
    private Boolean isHaveFlatType;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        selectedObjId = getIntent().getStringExtra("SelectedObjId");
        isHaveFlatType = getIntent().getBooleanExtra("isHaveFlatType", false);

        if (Util.isNetworkAvailable(this))
            fetchDataFromServer();
        else
            Toast.makeText(SelectedProfileActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

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


    private void initViews() {
        ImageView profileImage = (ImageView) findViewById(R.id.selected_profile_image);
        TextView rent = (TextView) findViewById(R.id.txt_approx_rent);
        TextView occupancy = (TextView) findViewById(R.id.txt_occupancy);
        TextView lastUpdated = (TextView) findViewById(R.id.txt_lastUpdated);
        TextView foodType = (TextView) findViewById(R.id.txt_preference_food);
        TextView smokingType = (TextView) findViewById(R.id.txt_preference_smoking);
        TextView drinkingType = (TextView) findViewById(R.id.txt_preference_drinking);
        TextView lifeStyleType = (TextView) findViewById(R.id.txt_preference_lifeStyle);
        TextView fromLocation = (TextView) findViewById(R.id.profile_from_location);
        TextView toLocation = (TextView) findViewById(R.id.profile_to_location);

        RecyclerView picturesGridView = (RecyclerView) findViewById(R.id.selected_image_grid);
        RecyclerView amenitiesGridView = (RecyclerView) findViewById(R.id.amenities_grid);

        TextView description = (TextView) findViewById(R.id.profile_description);
        Button call = (Button) findViewById(R.id.btn_call);
        Button chat = (Button) findViewById(R.id.btn_chat);


    }


    private void fetchDataFromServer() {

        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), "Fetching User Information Please Wait ..");

        String whereClause = "objectId =" + "'" + selectedObjId + "'";
        // Logger.v(" Fetch where " + whereClause);
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);

        if (isHaveFlatType)
            fetchHaveFlatObject(dataQuery);
        else
            fetchNeedFlatObject(dataQuery);
    }


    private void fetchUserPreferencesFromServer(String userId) {
        String whereClause = "ownerId =" + "'" + userId + "'";
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(UserPreferences.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserPreferences>>() {

            @Override
            public void handleResponse(BackendlessCollection<UserPreferences> userPreferencesBackendlessCollection) {

                List<UserPreferences> resultList = userPreferencesBackendlessCollection.getData();
                if (resultList.size() > 0) {
                    UserPreferences currentUserPreference = resultList.get(0);
                    updatePreferencesUI(currentUserPreference);
                }
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Logger.v(" Fault " + backendlessFault.toString());
            }
        });
    }

    private void fetchHaveFlatObject(BackendlessDataQuery dataQuery) {
        Backendless.Persistence.of(HaveFlat.class).find(dataQuery, new AsyncCallback<BackendlessCollection<HaveFlat>>() {

            @Override
            public void handleResponse(BackendlessCollection<HaveFlat> haveFlatBackendLessCollection) {

                List<HaveFlat> resultList = haveFlatBackendLessCollection.getData();
                // Logger.v(" List size " + resultList.size());
                if (resultList.size() > 0) {
                    HaveFlat currentItem = resultList.get(0);

                    updateHaveFlatUI(currentItem);
                    //fetch user preferences
                    String currentUserId = currentItem.getUserId();
                    fetchUserPreferencesFromServer(currentUserId);

                } else {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Logger.v(" Fault " + backendlessFault.toString());
            }
        });
    }


    private void fetchNeedFlatObject(BackendlessDataQuery dataQuery) {
        Backendless.Persistence.of(NeedFlat.class).find(dataQuery, new AsyncCallback<BackendlessCollection<NeedFlat>>() {

            @Override
            public void handleResponse(BackendlessCollection<NeedFlat> needFlatBackendLessCollection) {

                List<NeedFlat> resultList = needFlatBackendLessCollection.getData();
                // Logger.v(" List size " + resultList.size());
                if (resultList.size() > 0) {
                    NeedFlat currentItem = resultList.get(0);

                    updateNeedFlatUI(currentItem);

                    //fetch user preferences
                    String currentUserId = currentItem.getUserId();
                    fetchUserPreferencesFromServer(currentUserId);

                } else {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Logger.v(" Fault " + backendlessFault.toString());
            }
        });
    }


    private void updatePreferencesUI(UserPreferences preferences) {
        TextView foodType = (TextView) findViewById(R.id.txt_preference_food);
        TextView smokingType = (TextView) findViewById(R.id.txt_preference_smoking);
        TextView drinkingType = (TextView) findViewById(R.id.txt_preference_drinking);
        TextView lifeStyleType = (TextView) findViewById(R.id.txt_preference_lifeStyle);

        foodType.setText(preferences.getFoodType());
        smokingType.setText(preferences.getSmokingType());
        drinkingType.setText(preferences.getDrinkingType());
        lifeStyleType.setText(preferences.getLifeStyleType());

    }

    private void updateHaveFlatUI(HaveFlat flatItem) {

        ImageView profileImage = (ImageView) findViewById(R.id.selected_profile_image);
        TextView profileName = (TextView) findViewById(R.id.selected_profile_name);
        TextView rent = (TextView) findViewById(R.id.txt_approx_rent);
        TextView occupancy = (TextView) findViewById(R.id.txt_occupancy);
        TextView lastUpdated = (TextView) findViewById(R.id.txt_lastUpdated);

        TextView fromLocation = (TextView) findViewById(R.id.profile_from_location);
        TextView toLocation = (TextView) findViewById(R.id.profile_to_location);

        GridLayoutManager imagesGridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        RecyclerView picturesGridView = (RecyclerView) findViewById(R.id.selected_image_grid);
        picturesGridView.setLayoutManager(imagesGridLayoutManager);
        picturesGridView.setHasFixedSize(true);

        ArrayList<String> imageUrl = new ArrayList<>();
        final ImagesGridAdapter adapter = new ImagesGridAdapter(SelectedProfileActivity.this, imageUrl, false);
        picturesGridView.setAdapter(adapter);
        setHaveFlatImages(flatItem.getUserId(), adapter);

        ItemClickSupport.addTo(picturesGridView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent photoActivityIntent = new Intent(SelectedProfileActivity.this, PhotosFullScreenViewActivity.class);
                photoActivityIntent.putStringArrayListExtra(PhotosFullScreenViewActivity.KEY_PHOTOS_URLS, adapter.getmImagesList());
                photoActivityIntent.putExtra(PhotosFullScreenViewActivity.KEY_PHOTOS_CURRENT_PHOTO, position);
                startActivity(photoActivityIntent);
            }
        });

        GridLayoutManager amenitiesGridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        RecyclerView amenitiesGridView = (RecyclerView) findViewById(R.id.amenities_grid);
        amenitiesGridView.setLayoutManager(amenitiesGridLayoutManager);
        amenitiesGridView.setHasFixedSize(true);


        ArrayList<AmenitiesItem> amenitiesItems = new ArrayList<>();
        try {
            JSONObject amenitiesJsonObj = new JSONObject(flatItem.getAmenitiesAvailable());
            Iterator<String> amenities = amenitiesJsonObj.keys();
            while (amenities.hasNext()) {
                switch (amenities.next()) {
                    case "isTVAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_tv), R.drawable.icon_tv));
                        break;
                    case "isFridgeAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_fridge), R.drawable.icon_fridge));
                        break;
                    case "isKitchenAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_kitchen), R.drawable.icon_kitchen));
                        break;
                    case "isWIFIAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_wifi), R.drawable.icon_wifi));
                        break;
                    case "isMachineAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_machine), R.drawable.icon_machine));
                        break;
                    case "isACAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_ac), R.drawable.icon_fridge));
                        break;
                    case "isBackupAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_backup), R.drawable.icon_backup));
                        break;
                    case "isCookAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_cook), R.drawable.icon_cook));
                        break;
                    case "isPArkingAvailable":
                        amenitiesItems.add(new AmenitiesItem(getResources().getString(R.string.amenities_parking), R.drawable.icon_parking));
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AmenitiesGridAdapter amenitiesGridAdapter = new AmenitiesGridAdapter(this, amenitiesItems);
        amenitiesGridView.setAdapter(amenitiesGridAdapter);

        TextView description = (TextView) findViewById(R.id.profile_description);
        description.setText(flatItem.getDescription());
        Button call = (Button) findViewById(R.id.btn_call);
        Button chat = (Button) findViewById(R.id.btn_chat);
        if (!flatItem.isShouldShowContactNumber())
            call.setVisibility(View.GONE);
        call.setOnClickListener(this);
        chat.setOnClickListener(this);

        Picasso.with(SelectedProfileActivity.this).load(Defaults.FILES_PROFILE_PIC_URL + "/" + flatItem.getUserId() + ".png").resize(250, 250).
                error(R.drawable.profile).into(profileImage);
        profileName.setText(flatItem.getUserName());
        rent.setText(Integer.toString(flatItem.getRentRange()));
        occupancy.setText(flatItem.getOccupancy());
        //foodType.setText(flatItem.f);
        fromLocation.setText(flatItem.getFlatLocation());
        toLocation.setVisibility(View.GONE);

        companionId = flatItem.getUserId();

    }


    private void updateNeedFlatUI(NeedFlat needFlatObj) {

        ImageView profileImage = (ImageView) findViewById(R.id.selected_profile_image);
        TextView profileName = (TextView) findViewById(R.id.selected_profile_name);
        TextView rent = (TextView) findViewById(R.id.txt_approx_rent);
        TextView occupancy = (TextView) findViewById(R.id.txt_occupancy);
        TextView lastUpdated = (TextView) findViewById(R.id.txt_lastUpdated);

        TextView fromLocation = (TextView) findViewById(R.id.profile_from_location);
        TextView toLocation = (TextView) findViewById(R.id.profile_to_location);

        LinearLayout pictureLayout = (LinearLayout) findViewById(R.id.pictures_layout);
        pictureLayout.setVisibility(View.GONE);

        LinearLayout amenitiesLayout = (LinearLayout) findViewById(R.id.amenities_layout);
        amenitiesLayout.setVisibility(View.GONE);

        Picasso.with(SelectedProfileActivity.this).load(Defaults.FILES_PROFILE_PIC_URL + "/" + needFlatObj.getUserId() + ".png").resize(250, 250).
                error(R.drawable.profile).into(profileImage);
        profileName.setText(needFlatObj.getUserName());
        rent.setText(Integer.toString(needFlatObj.getRentRange()));
        occupancy.setText(needFlatObj.getOccupancy());
        fromLocation.setText(needFlatObj.getCurrentFlatLocation());
        toLocation.setVisibility(View.VISIBLE);
        toLocation.setText(needFlatObj.getNeedFlatLocation());

        TextView description = (TextView) findViewById(R.id.profile_description);
        description.setText(needFlatObj.getDescription());
        Button call = (Button) findViewById(R.id.btn_call);
        if (!needFlatObj.isShouldShowContactNumber())
            call.setVisibility(View.GONE);
        Button chat = (Button) findViewById(R.id.btn_chat);

        call.setOnClickListener(this);
        chat.setOnClickListener(this);

        companionId = needFlatObj.getUserId();
    }

    private void setHaveFlatImages(String userId, final ImagesGridAdapter imagesAdapter) {
        Backendless.Files.listing("/" + Defaults.FILES_HAVE_FLAT_DIRECTORY + "/" + userId, new AsyncCallback<BackendlessCollection<FileInfo>>() {
            @Override
            public void handleResponse(BackendlessCollection<FileInfo> fileInfoBackendlessCollection) {
                ArrayList<String> imageUrl = new ArrayList<>();
                for (FileInfo file : fileInfoBackendlessCollection.getCurrentPage()) {
                    String publicURL = file.getPublicUrl();
                    imageUrl.add(publicURL);
                }
                imagesAdapter.refreshAdapter(imageUrl);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chat:
                Intent i = new Intent(SelectedProfileActivity.this, UserChatActivity.class);
                i.putExtra(Defaults.KEY_COMPANION_ID, companionId);
                startActivity(i);
                break;
            case R.id.btn_call:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 123);
                        return;
                    }
                } else
                    makeCall();

        }
    }


    private void makeCall() {
        final Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:232131232"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 123) {

            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                } else
                    Toast.makeText(this, "Yo cannot perform call operation without this permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
