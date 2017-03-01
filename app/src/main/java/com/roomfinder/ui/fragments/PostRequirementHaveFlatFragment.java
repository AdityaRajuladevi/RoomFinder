package com.roomfinder.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.files.FileInfo;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.roomfinder.R;
import com.roomfinder.adapter.AutoCompletePlacesAdapter;
import com.roomfinder.adapter.SelectedImagesGridAdapter;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.callbacks.ItemClickSupport;
import com.roomfinder.customview.CheckableLinearLayout;
import com.roomfinder.model.HaveFlat;
import com.roomfinder.model.PlaceDetails;
import com.roomfinder.model.UserPreferences;
import com.roomfinder.ui.HomeActivity;
import com.roomfinder.utils.KeyUtils;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;
import com.roomfinder.utils.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostRequirementHaveFlatFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener,
        CheckableLinearLayout.OnCheckedChangeListener, AutoCompletePlacesAdapter.OnDataFetchedForSelectedPlace {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    ArrayList<Uri> image_uris = new ArrayList<Uri>(0);
    boolean[] amenities_array = {false, false, false,
            false, false, false,
            false, false, false};
    private FragmentInteractionListener mListener;
    // Places AutoComplete
    private AutoCompletePlacesAdapter mFlatAreaAutoPlacesAdapter;
    private PlaceDetails selectedPlaceDetails;
    private AutoCompleteTextView mFlatAreaAutoCompleteView;
    private DatePickerDialog availableFromDateDialog;
    private Date mAvailableFromDate;
    private RadioGroup mRadioGroupLookingFor;
    private RadioGroup mRadioGroupOccupancy;
    private RadioGroup mRadioContactNumber;
    private SelectedImagesGridAdapter selectedImagesAdapter;
    private EditText mRentView;
    private EditText availableFromEditText;
    private EditText mContactNumberView;
    private EditText mDescriptionView;
    private CheckableLinearLayout tv;
    private CheckableLinearLayout fridge;
    private CheckableLinearLayout kitchen;
    private CheckableLinearLayout wifi;
    private CheckableLinearLayout ac;
    private CheckableLinearLayout backup;
    private CheckableLinearLayout machine;
    private CheckableLinearLayout cook;
    private CheckableLinearLayout parking;

    private HaveFlat existingHaveFlat;

    public PostRequirementHaveFlatFragment() {
        // Required empty public constructor
    }

    public static PostRequirementHaveFlatFragment newInstance() {
        PostRequirementHaveFlatFragment fragment = new PostRequirementHaveFlatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_requirement_have_flat, container, false);
        initUI(rootView);
        fetchAlreadyExistingPostRequestIfPresent();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            mListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void initUI(View rootView) {
        mFlatAreaAutoCompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.have_flat_area);
        mFlatAreaAutoCompleteView.setThreshold(1);
        mFlatAreaAutoPlacesAdapter = new AutoCompletePlacesAdapter(getActivity(), android.R.layout.simple_list_item_1, this, 0);
        mFlatAreaAutoCompleteView.setAdapter(mFlatAreaAutoPlacesAdapter);
        mFlatAreaAutoCompleteView.setOnItemClickListener(this);

        ImageButton clearText = (ImageButton) rootView.findViewById(R.id.have_flat_clearTextBtn);
        clearText.setOnClickListener(this);

        mRentView = (EditText) rootView.findViewById(R.id.have_flat_rent);

        availableFromEditText = (EditText) rootView.findViewById(R.id.have_flat_available_from);
        availableFromEditText.setInputType(InputType.TYPE_NULL);
        availableFromEditText.setOnClickListener(this);
        setPresentDate(availableFromEditText);

        mRadioGroupLookingFor = (RadioGroup) rootView.findViewById(R.id.radioGrp_looking_for);
        mRadioGroupLookingFor.check(R.id.radio_looking_for_any);

        mRadioGroupOccupancy = (RadioGroup) rootView.findViewById(R.id.radioGrp_occupancy);
        mRadioGroupOccupancy.check(R.id.radio_occupancy_any);

        ImageButton addPicsBtn = (ImageButton) rootView.findViewById(R.id.have_flat_addPicsBtn);
        addPicsBtn.setOnClickListener(this);
        RecyclerView selectedImageRecyclerView = (RecyclerView) rootView.findViewById(R.id.selected_image_grid);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        selectedImageRecyclerView.setLayoutManager(gridLayoutManager);
        selectedImageRecyclerView.setHasFixedSize(true);

        selectedImagesAdapter = new SelectedImagesGridAdapter(getActivity(), image_uris);
        selectedImageRecyclerView.setAdapter(selectedImagesAdapter);
        ItemClickSupport.addTo(selectedImageRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                launchImageSelectActivity();
            }
        });

        // Amenities part init
        tv = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_tv);
        tv.setOnCheckedChangeListener(this);
        fridge = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_fridge);
        fridge.setOnCheckedChangeListener(this);
        kitchen = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_kitchen);
        kitchen.setOnCheckedChangeListener(this);
        wifi = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_wifi);
        wifi.setOnCheckedChangeListener(this);
        ac = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_ac);
        ac.setOnCheckedChangeListener(this);
        backup = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_backup);
        backup.setOnCheckedChangeListener(this);
        machine = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_machine);
        machine.setOnCheckedChangeListener(this);
        cook = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_cook);
        cook.setOnCheckedChangeListener(this);
        parking = (CheckableLinearLayout) rootView.findViewById(R.id.amenities_parking);
        parking.setOnCheckedChangeListener(this);

        mContactNumberView = (EditText) rootView.findViewById(R.id.have_flat_contact_number);
        mRadioContactNumber = (RadioGroup) rootView.findViewById(R.id.radioGrp_contact);
        mRadioContactNumber.check(R.id.radio_contact_hide);

        mDescriptionView = (EditText) rootView.findViewById(R.id.have_flat_description);

        Button submit_btn = (Button) rootView.findViewById(R.id.btn_have_flat_submit);
        submit_btn.setOnClickListener(this);
    }


    private void fetchAlreadyExistingPostRequestIfPresent() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        // fetch it if true..
        if (sharedPreferenceManager.getUserHasHaveFlatRequest()) {
            String objId = sharedPreferenceManager.getObjectIdOFLoggedInUser();
            String whereClause = "ownerId =" + "'" + objId + "'";
            //Logger.v(" Fetch where " + whereClause);
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause);
            Backendless.Persistence.of(HaveFlat.class).find(dataQuery, new DefaultCallback<BackendlessCollection<HaveFlat>>(getActivity(), " Fetching Post.. Please Wait") {

                @Override
                public void handleResponse(BackendlessCollection<HaveFlat> haveFlatBackendlessCollection) {

                    List<HaveFlat> resultList = haveFlatBackendlessCollection.getData();
                    if (resultList.size() > 0) {
                        existingHaveFlat = resultList.get(0);
                        HaveFlat currentHaveFlat = resultList.get(0);
                        if (currentHaveFlat != null) {
                            // Logger.v(" Old REquest object received ");
                            mFlatAreaAutoCompleteView.setText(currentHaveFlat.getFlatLocation());
                            mFlatAreaAutoCompleteView.dismissDropDown();
                            GeoPoint pt = currentHaveFlat.getCoordinates();
                            PlaceDetails placeDetails = new PlaceDetails(pt.getLatitude(), pt.getLongitude(), (String) pt.getMetadata("locationName"), (String) pt.getMetadata("address"));
                            selectedPlaceDetails = placeDetails;

                            mRentView.setText(currentHaveFlat.getRentRange() + "$");

                            // setting the looking for value
                            switch (currentHaveFlat.getLookingFor()) {
                                case "Any":
                                    mRadioGroupLookingFor.check(R.id.radio_looking_for_any);
                                    break;
                                case "Male":
                                    mRadioGroupLookingFor.check(R.id.radio_male);
                                    break;
                                case "Female":
                                    mRadioGroupLookingFor.check(R.id.radio_female);
                                    break;
                            }

                            // setting the occupancy value
                            switch (currentHaveFlat.getOccupancy()) {
                                case "Any":
                                    mRadioGroupOccupancy.check(R.id.radio_occupancy_any);
                                    break;
                                case "Shared":
                                    mRadioGroupOccupancy.check(R.id.radio_occupancy_shared);
                                    break;
                                case "Single":
                                    mRadioGroupOccupancy.check(R.id.radio_occupancy_single);
                                    break;
                            }


                            mAvailableFromDate = currentHaveFlat.getNeedToShiftDate();
                            SimpleDateFormat dateFormatter = new SimpleDateFormat(KeyUtils.DATE_FORMAT, Locale.US);
                            availableFromEditText.setText(dateFormatter.format(mAvailableFromDate));

                            if (currentHaveFlat.isShouldShowContactNumber())
                                mRadioContactNumber.check(R.id.radio_contact_show);
                            else
                                mRadioContactNumber.check(R.id.radio_contact_hide);
                            mContactNumberView.setText(currentHaveFlat.getPhoneNumber());
                            mDescriptionView.setText(currentHaveFlat.getDescription());

                            setExistingImages(currentHaveFlat.getUserId());

                            // Amenities setting
                            try {
                                JSONObject amenitiesJsonObj = new JSONObject(currentHaveFlat.getAmenitiesAvailable());
                                Iterator<String> amenities = amenitiesJsonObj.keys();
                                while (amenities.hasNext()) {
                                    switch (amenities.next()) {
                                        case "isTVAvailable":
                                            tv.setChecked(true);
                                            break;
                                        case "isFridgeAvailable":
                                            fridge.setChecked(true);
                                            break;
                                        case "isKitchenAvailable":
                                            kitchen.setChecked(true);
                                            break;
                                        case "isWIFIAvailable":
                                            wifi.setChecked(true);
                                            break;
                                        case "isMachineAvailable":
                                            machine.setChecked(true);
                                            break;
                                        case "isACAvailable":
                                            ac.setChecked(true);
                                            break;
                                        case "isBackupAvailable":
                                            backup.setChecked(true);
                                            break;
                                        case "isCookAvailable":
                                            cook.setChecked(true);
                                            break;
                                        case "isPArkingAvailable":
                                            parking.setChecked(true);
                                            break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }
                    super.handleResponse(haveFlatBackendlessCollection);
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    super.handleFault(backendlessFault);
                    Logger.v("fetch and update HaveFlat Flag Failed " + backendlessFault.toString());
                }
            });
        }
    }

    private void setExistingImages(String userId) {
        Backendless.Files.listing("/" + Defaults.FILES_HAVE_FLAT_DIRECTORY + "/" + userId, new AsyncCallback<BackendlessCollection<FileInfo>>() {
            @Override
            public void handleResponse(BackendlessCollection<FileInfo> fileInfoBackendlessCollection) {
                ArrayList<Uri> imagesList = new ArrayList<Uri>();
                for (FileInfo file : fileInfoBackendlessCollection.getCurrentPage()) {
                    String publicURL = file.getPublicUrl();
                    Uri uri = Uri.parse(publicURL);
                    imagesList.add(uri);
                }
                selectedImagesAdapter.refreshAdapter(imagesList);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Logger.v("Fetch Have Flat Images Issue " + backendlessFault.toString());
            }
        });
    }

    private void setPresentDate(final EditText availableFromEditText) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(KeyUtils.DATE_FORMAT, Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        mAvailableFromDate = newCalendar.getTime();
        availableFromEditText.setText(dateFormatter.format(mAvailableFromDate));
        availableFromDateDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mAvailableFromDate = newDate.getTime();
                availableFromEditText.setText(dateFormatter.format(mAvailableFromDate));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        availableFromDateDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }

    private void showMedia(ArrayList<Uri> selectedList) {
        selectedImagesAdapter.refreshAdapter(selectedList);
    }

    private void launchImageSelectActivity() {

        Config config = new Config();
        config.setToolbarTitleRes(R.string.select_image);
        config.setSelectionMin(0);
        config.setSelectionLimit(6);
        ImagePickerActivity.setConfig(config);
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    @Override
    public void onCheckedChanged(View checkableView, boolean isChecked) {
        if (isChecked) {
            checkableView.setBackgroundColor(getResources().getColor(R.color.ran));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkableView.setBackground(getResources().getDrawable(R.drawable.amenities_rectangular_box, getActivity().getTheme()));
            } else {
                checkableView.setBackground(getResources().getDrawable(R.drawable.amenities_rectangular_box));
            }

        }

        switch (checkableView.getId()) {
            case R.id.amenities_tv:
                amenities_array[KeyUtils.AMENITIES_TV] = isChecked;
                break;
            case R.id.amenities_fridge:
                amenities_array[KeyUtils.AMENITIES_FRIDGE] = isChecked;
                break;
            case R.id.amenities_kitchen:
                amenities_array[KeyUtils.AMENITIES_KITCHEN] = isChecked;
                break;
            case R.id.amenities_wifi:
                amenities_array[KeyUtils.AMENITIES_WIFI] = isChecked;
                break;
            case R.id.amenities_machine:
                amenities_array[KeyUtils.AMENITIES_MACHINE] = isChecked;
                break;
            case R.id.amenities_ac:
                amenities_array[KeyUtils.AMENITIES_AC] = isChecked;
                break;
            case R.id.amenities_backup:
                amenities_array[KeyUtils.AMENITIES_BACKUP] = isChecked;
                break;
            case R.id.amenities_cook:
                amenities_array[KeyUtils.AMENITIES_COOK] = isChecked;
                break;
            case R.id.amenities_parking:
                amenities_array[KeyUtils.AMENITIES_PARKING] = isChecked;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.have_flat_available_from:
                availableFromDateDialog.show();
                break;
            case R.id.have_flat_addPicsBtn:
                launchImageSelectActivity();
                break;
            case R.id.have_flat_clearTextBtn:
                mFlatAreaAutoCompleteView.clearComposingText();
                mFlatAreaAutoCompleteView.setText("");
                // clear previous selection
                selectedPlaceDetails = null;
                break;

            case R.id.btn_have_flat_submit:
                // Logger.v("Amenities = " + amenities_json_array.toString());
                attemptToPostRequirement();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {
            ArrayList<Uri> selectedImageList = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            selectedImageList.addAll(image_uris);
            // Logger.v("Selected Images ="+image_uris.size());
            if (selectedImageList != null) {
                showMedia(selectedImageList);
            }
        }
    }

    @Override
    public void onPlaceDetailsFetched(PlaceDetails selectedPlaceItem, int viewIdentifier) {
        if (selectedPlaceItem != null) {
            selectedPlaceDetails = selectedPlaceItem;
            //  Logger.v(" Place Details "+ selectedPlaceItem.placeName);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mFlatAreaAutoPlacesAdapter != null)
            mFlatAreaAutoPlacesAdapter.getPlaceDetails(position);
    }

    private void attemptToPostRequirement() {
        boolean isInvalid = false;
        View focusView = null;

        // remove all error codes
        mFlatAreaAutoCompleteView.setError(null);
        mRentView.setError(null);

        // get all values from views
        String rentText = mRentView.getText().toString().trim();
        String contactNumberText = mContactNumberView.getText().toString().trim();
        String descriptionText = mDescriptionView.getText().toString().trim();

        //validating the selected place auto complete text view
        if (selectedPlaceDetails == null) {
            isInvalid = true;
            focusView = mFlatAreaAutoCompleteView;
            mFlatAreaAutoCompleteView.setError(" Please selected the place and then proceed");
        }

        // rent view validity
        if (!isInvalid && TextUtils.isEmpty(rentText)) {
            isInvalid = true;
            focusView = mRentView;
            mRentView.setError(" Mandatory field");
        }

        // contact number view validity
        if (!isInvalid && TextUtils.isEmpty(contactNumberText)) {
            isInvalid = true;
            focusView = mContactNumberView;
            mContactNumberView.setError(" Mandatory field");
        }

        // description view validity
        if (!isInvalid && TextUtils.isEmpty(descriptionText)) {
            isInvalid = true;
            focusView = mDescriptionView;
            mDescriptionView.setError(" Mandatory field");
        }

        if (isInvalid) {

            // There was an error; don't attempt to register the request and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String lookingForText = null;
            switch (mRadioGroupLookingFor.getCheckedRadioButtonId()) {
                case R.id.radio_looking_for_any:
                    lookingForText = getResources().getString(R.string.any);
                    break;
                case R.id.radio_male:
                    lookingForText = getResources().getString(R.string.male);
                    break;
                case R.id.radio_female:
                    lookingForText = getResources().getString(R.string.female);
                    break;
            }

            String occupancyText = null;
            switch (mRadioGroupOccupancy.getCheckedRadioButtonId()) {
                case R.id.radio_occupancy_any:
                    occupancyText = getResources().getString(R.string.any);
                    break;
                case R.id.radio_occupancy_shared:
                    occupancyText = getResources().getString(R.string.occupancy_shared);
                    break;
                case R.id.radio_occupancy_single:
                    occupancyText = getResources().getString(R.string.occupancy_single);
                    break;
            }

            //show or hide contact number..
            boolean showContactNumber = false;
            switch (mRadioContactNumber.getCheckedRadioButtonId()) {
                case R.id.radio_contact_show:
                    showContactNumber = true;
                    break;
                case R.id.radio_contact_hide:
                    showContactNumber = false;
                    break;
            }

            // amenities array
            JSONObject amenities_json_obj = new JSONObject();
            for (int i = 0; i < amenities_array.length; i++) {
                if (amenities_array[i]) {
                    try {
                        amenities_json_obj.put(KeyUtils.AMENITIES_ARRAY_KEYS[i], amenities_array[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            String amenitiesAvailable = amenities_json_obj.toString();

            final SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());

            // all fields are available attempt to save the details to server
            HaveFlat haveFlat = null;
            if (existingHaveFlat == null) {
                haveFlat = new HaveFlat();
            } else {
                haveFlat = existingHaveFlat;
            }
            haveFlat.setUserName(sharedPreferenceManager.getNameOFLoggedInUser());
            haveFlat.setUserId(sharedPreferenceManager.getObjectIdOFLoggedInUser());
            haveFlat.setRentRange(Integer.parseInt(rentText.substring(0, rentText.length() - 1)));
            haveFlat.setLookingFor(lookingForText);
            haveFlat.setOccupancy(occupancyText);
            haveFlat.setNeedToShiftDate(mAvailableFromDate);
            haveFlat.setPhoneNumber(contactNumberText);
            haveFlat.setDescription(descriptionText);
            haveFlat.setAmenitiesAvailable(amenitiesAvailable);
            haveFlat.setShouldShowContactNumber(showContactNumber);
            haveFlat.setFlatLocation(selectedPlaceDetails.getPlaceName());
            GeoPoint pt = new GeoPoint(selectedPlaceDetails.getLatitude(), selectedPlaceDetails.getLongitude());
            pt.addCategory("HaveFlat");
            pt.putMetadata("locationName", selectedPlaceDetails.getPlaceName());
            pt.putMetadata("address", selectedPlaceDetails.getAddress());
            haveFlat.setCoordinates(pt);

            if (Util.isNetworkAvailable(getActivity())) {
                Backendless.Data.of(HaveFlat.class).save(haveFlat, new DefaultCallback<HaveFlat>(getActivity(), "Posting your  request. Please Wait ...") {
                    @Override
                    public void handleResponse(HaveFlat response) {
                        super.handleResponse(response);
                        if (response != null) {
                            uploadFlatImages(response.getUserId());
                            updateUserPrefValue();
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        super.handleFault(fault);
                        Log.e("Fault", fault + "");
                    }
                });
            } else
                Logger.toast(getActivity(), getResources().getString(R.string.network_error));
        }
    }


    //update the post requirement preference into preference class of the user..
    private void updateUserPrefValue() {
        final SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        String objId = sharedPreferenceManager.getObjectIdOFLoggedInUser();
        String whereClause = "ownerId =" + "'" + objId + "'";
        //Logger.v(" Fetch where " + whereClause);
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(UserPreferences.class).find(dataQuery, new AsyncCallback<BackendlessCollection<UserPreferences>>() {

            @Override
            public void handleResponse(BackendlessCollection<UserPreferences> userPreferencesBackendlessCollection) {

                List<UserPreferences> resultList = userPreferencesBackendlessCollection.getData();
                if (resultList.size() > 0) {
                    UserPreferences currentUserPreference = resultList.get(0);
                    if (currentUserPreference != null) {
                        currentUserPreference.setIsUserPostedHaveFlatRequirement(true);
                        Backendless.Persistence.save(currentUserPreference, new AsyncCallback<UserPreferences>() {
                            public void handleResponse(UserPreferences response) {
                                // new user Preferences instance has been saved
                                if (response != null) {
                                    // save it in preferences
                                    sharedPreferenceManager.saveUserHasHaveFlatRequirement(response.isUserPostedHaveFlatRequirement());
                                }
                            }

                            public void handleFault(BackendlessFault fault) {
                                Logger.v("Update Pref Failed " + fault.toString());
                                // an error has occurred, the error code can be retrieved with fault.getCode()
                            }
                        });
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Logger.v("Update HaveFlat Flag Failed " + backendlessFault.toString());
            }
        });
    }

    private void uploadFlatImages(String userId) {
        if (image_uris != null && image_uris.size() > 0) // upload images into a new directory with name as that as userId
        {
            String directoryName = Defaults.FILES_HAVE_FLAT_DIRECTORY + "/" + userId;
            for (int i = 0; i < image_uris.size(); i++) {
                Bitmap imageBitmap = getImageBitmapFromUri(image_uris.get(i));
                if (imageBitmap != null) {
                    Random rnd = new Random();
                    String imageName = "pic" + rnd.nextInt();
                    ;
                    Backendless.Files.Android.upload(imageBitmap, Bitmap.CompressFormat.PNG, 100, imageName + ".png", directoryName,
                            new AsyncCallback<BackendlessFile>() {
                                @Override
                                public void handleResponse(final BackendlessFile backendlessFile) {
                                    Logger.v("  Pic Upload success " +
                                            backendlessFile.getFileURL());
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Logger.v("  Pic Upload Failed fault " + backendlessFault.toString());
                                    Logger.v("  Pic Upload Failed " + backendlessFault.getDetail());
                                    Logger.v("  Pic Upload Failed message " + backendlessFault.getMessage());
                                }
                            });
                }
            }
            // finish the activity after all the work here is done
            getActivity().finish();
        } else {
            Logger.v(" No Pics To Upload");
            getActivity().setResult(HomeActivity.HOME_POST_RESULT_CODE);
            getActivity().finish();
        }
    }

    private Bitmap getImageBitmapFromUri(Uri imageUri) {
        Bitmap uploadImageBitmap = null;
        File eachFile = new File(imageUri.getPath());
        if (eachFile.exists()) {
            ContentResolver contentResolver = getActivity().getContentResolver();
            try {
                uploadImageBitmap = Util.scaleDown(MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(eachFile)), 500, true);
            } catch (IOException e) {
                e.printStackTrace();
                return uploadImageBitmap;
            }
        }
        return uploadImageBitmap;
    }
}
