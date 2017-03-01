package com.roomfinder.ui.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.roomfinder.R;
import com.roomfinder.adapter.AutoCompletePlacesAdapter;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.model.NeedFlat;
import com.roomfinder.model.PlaceDetails;
import com.roomfinder.model.UserPreferences;
import com.roomfinder.ui.HomeActivity;
import com.roomfinder.utils.KeyUtils;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;
import com.roomfinder.utils.Util;

/**
 * A simple {@link Fragment} subclass..
 */
public class PostRequirementNeedFlatFragment extends Fragment implements View.OnClickListener,
        AutoCompletePlacesAdapter.OnDataFetchedForSelectedPlace {

    // Places AutoComplete
    private AutoCompletePlacesAdapter mCurrentAreaAutoPlacesAdapter, mAnotherAreaAutoPlacesAdapter;
    private PlaceDetails currentSelectedPlaceDetails, anotherSelectedPlaceDetails;
    private AutoCompleteTextView mCurrentAreaAutoCompleteView, mAnotherAreaAutoCompleteView;

    private FragmentInteractionListener mListener;
    private EditText mContactNumberView;
    private RadioGroup mRadioContactNumber;
    private EditText mDescriptionView;
    private EditText mRentView;
    private DatePickerDialog needFromDateDialog;
    private Date mNeedFromDate;
    private RadioGroup mRadioGroupLookingFor;
    private RadioGroup mRadioGroupOccupancy;

    private NeedFlat existingNeedFlat;
    private EditText needFromEditText;

    public PostRequirementNeedFlatFragment() {
        // Required empty public constructor
    }

    public static PostRequirementNeedFlatFragment newInstance() {
        PostRequirementNeedFlatFragment fragment = new PostRequirementNeedFlatFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_post_requirement_need_flat, container, false);
        initUI(rootView);
        fetchAlreadyExistingPostRequestIfPresent();
        return rootView;
    }

    private void initUI(View rootView) {

        // current location
        mCurrentAreaAutoCompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.need_flat_your_location);
        mCurrentAreaAutoCompleteView.setThreshold(1);
        mCurrentAreaAutoPlacesAdapter = new AutoCompletePlacesAdapter(getActivity(), android.R.layout.simple_list_item_1, this, 0);
        mCurrentAreaAutoCompleteView.setAdapter(mCurrentAreaAutoPlacesAdapter);
        mCurrentAreaAutoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentAreaAutoPlacesAdapter != null)
                    mCurrentAreaAutoPlacesAdapter.getPlaceDetails(position);
            }
        });

        ImageButton yourLocationclearText = (ImageButton) rootView.findViewById(R.id.need_flat_your_location_clearTextBtn);
        yourLocationclearText.setOnClickListener(this);

        // another location
        mAnotherAreaAutoCompleteView = (AutoCompleteTextView) rootView.findViewById(R.id.need_flat_another_location);
        mAnotherAreaAutoCompleteView.setThreshold(1);
        mAnotherAreaAutoPlacesAdapter = new AutoCompletePlacesAdapter(getActivity(), android.R.layout.simple_list_item_1, this, 1);
        mAnotherAreaAutoCompleteView.setAdapter(mAnotherAreaAutoPlacesAdapter);
        mAnotherAreaAutoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAnotherAreaAutoPlacesAdapter != null)
                    mAnotherAreaAutoPlacesAdapter.getPlaceDetails(position);
            }
        });

        ImageButton anotherLocationclearText = (ImageButton) rootView.findViewById(R.id.need_flat_another_location_clearTextBtn);
        anotherLocationclearText.setOnClickListener(this);

        mRentView = (EditText) rootView.findViewById(R.id.need_flat_rent);

        needFromEditText = (EditText) rootView.findViewById(R.id.need_flat_date);
        needFromEditText.setInputType(InputType.TYPE_NULL);
        needFromEditText.setOnClickListener(this);
        setPresentDate(needFromEditText);

        mRadioGroupLookingFor = (RadioGroup) rootView.findViewById(R.id.radioGrp_looking_for);
        mRadioGroupLookingFor.check(R.id.radio_looking_for_any);
        mRadioGroupOccupancy = (RadioGroup) rootView.findViewById(R.id.radioGrp_occupancy);
        mRadioGroupOccupancy.check(R.id.radio_occupancy_any);


        mContactNumberView = (EditText) rootView.findViewById(R.id.need_flat_contact_number);
        mRadioContactNumber = (RadioGroup) rootView.findViewById(R.id.radioGrp_contact);
        mRadioContactNumber.check(R.id.radio_contact_hide);

        mDescriptionView = (EditText) rootView.findViewById(R.id.need_flat_description);

        Button submit_btn = (Button) rootView.findViewById(R.id.btn_need_flat_submit);
        submit_btn.setOnClickListener(this);

    }


    private void fetchAlreadyExistingPostRequestIfPresent() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        // fetch it if true..
        if (sharedPreferenceManager.getUserHasNeedFlatRequest()) {
            String objId = sharedPreferenceManager.getObjectIdOFLoggedInUser();
            String whereClause = "ownerId =" + "'" + objId + "'";
            //Logger.v(" Fetch where " + whereClause);
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause(whereClause);

            Backendless.Persistence.of(NeedFlat.class).find(dataQuery, new DefaultCallback<BackendlessCollection<NeedFlat>>(getActivity(), "Fetching Post Request..") {
                @Override
                public void handleResponse(BackendlessCollection<NeedFlat> needFlatBackendlessCollection) {
                    super.handleResponse(needFlatBackendlessCollection);
                    List<NeedFlat> resultList = needFlatBackendlessCollection.getData();
                    if (resultList.size() > 0) {
                        // Logger.v(" Old REquest object received  of NeedFlat = "+resultList.get(0).getDescription());
                        existingNeedFlat = resultList.get(0);
                        NeedFlat currentNeedFlat = resultList.get(0);
                        if (currentNeedFlat != null) {
                            mCurrentAreaAutoCompleteView.setText(currentNeedFlat.getCurrentFlatLocation());
                            mCurrentAreaAutoCompleteView.dismissDropDown();
                            GeoPoint currentPt = currentNeedFlat.getCurrentLocationCoordinates();
                            PlaceDetails placeDetails = new PlaceDetails(currentPt.getLatitude(), currentPt.getLongitude(), (String) currentPt.getMetadata("locationName"), (String) currentPt.getMetadata("address"));
                            currentSelectedPlaceDetails = placeDetails;

                            mAnotherAreaAutoCompleteView.setText(currentNeedFlat.getNeedFlatLocation());
                            mAnotherAreaAutoCompleteView.dismissDropDown();
                            GeoPoint needPt = currentNeedFlat.getAnotherLocationCoordinates();
                            PlaceDetails needPlaceDetails = new PlaceDetails(needPt.getLatitude(), needPt.getLongitude(), (String) needPt.getMetadata("locationName"), (String) needPt.getMetadata("address"));
                            anotherSelectedPlaceDetails = needPlaceDetails;

                            mRentView.setText(currentNeedFlat.getRentRange() + "$");

                            // setting the looking for value
                            switch (currentNeedFlat.getLookingFor()) {
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
                            switch (currentNeedFlat.getOccupancy()) {
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


                            mNeedFromDate = currentNeedFlat.getNeedToShiftDate();
                            SimpleDateFormat dateFormatter = new SimpleDateFormat(KeyUtils.DATE_FORMAT, Locale.US);
                            needFromEditText.setText(dateFormatter.format(mNeedFromDate));

                            if (currentNeedFlat.isShouldShowContactNumber())
                                mRadioContactNumber.check(R.id.radio_contact_show);
                            else
                                mRadioContactNumber.check(R.id.radio_contact_hide);
                            mContactNumberView.setText(currentNeedFlat.getPhoneNumber());
                            mDescriptionView.setText(currentNeedFlat.getDescription());
                        }
                    }
                    super.handleResponse(needFlatBackendlessCollection);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    super.handleFault(fault);
                    Logger.v("fetch and update need Flag Failed " + fault.toString());
                }
            });

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.need_flat_your_location_clearTextBtn:
                mCurrentAreaAutoCompleteView.clearComposingText();
                mCurrentAreaAutoCompleteView.setText("");
                // clear previous selection
                currentSelectedPlaceDetails = null;
                break;
            case R.id.need_flat_another_location_clearTextBtn:
                mAnotherAreaAutoCompleteView.clearComposingText();
                mAnotherAreaAutoCompleteView.setText("");
                // clear previous selection
                anotherSelectedPlaceDetails = null;
                break;
            case R.id.need_flat_date:
                needFromDateDialog.show();
                break;
            case R.id.btn_need_flat_submit:
                attemptToPostRequirement();
                break;
        }
    }


    @Override
    public void onPlaceDetailsFetched(PlaceDetails selectedPlaceItem, int viewIdentifier) {
        switch (viewIdentifier) {
            // current place
            case 0:
                if (selectedPlaceItem != null) {
                    currentSelectedPlaceDetails = selectedPlaceItem;
                    Logger.v("Current Place Details " + currentSelectedPlaceDetails.getPlaceName());
                }
                break;

            // another place
            case 1:
                if (selectedPlaceItem != null) {
                    anotherSelectedPlaceDetails = selectedPlaceItem;
                    Logger.v("Another Place Details " + anotherSelectedPlaceDetails.getPlaceName());
                }
                break;
        }
    }

    private void setPresentDate(final EditText availableFromEditText) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat(KeyUtils.DATE_FORMAT, Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        mNeedFromDate = newCalendar.getTime();
        availableFromEditText.setText(dateFormatter.format(mNeedFromDate));
        needFromDateDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mNeedFromDate = newDate.getTime();
                availableFromEditText.setText(dateFormatter.format(mNeedFromDate));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        needFromDateDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
    }

    private void attemptToPostRequirement() {
        boolean isInvalid = false;
        View focusView = null;

        // remove all error codes
        mCurrentAreaAutoCompleteView.setError(null);
        mAnotherAreaAutoCompleteView.setError(null);
        mRentView.setError(null);

        // get all values from views
        String rentText = mRentView.getText().toString().trim();
        String contactNumberText = mContactNumberView.getText().toString().trim();
        String descriptionText = mDescriptionView.getText().toString().trim();

        //validating the current place auto complete text view
        if (currentSelectedPlaceDetails == null) {
            isInvalid = true;
            focusView = mCurrentAreaAutoCompleteView;
            mCurrentAreaAutoCompleteView.setError(" Please selected your current place and then proceed");
        }

        //validating the current place auto complete text view
        if (anotherSelectedPlaceDetails == null) {
            isInvalid = true;
            focusView = mAnotherAreaAutoCompleteView;
            mAnotherAreaAutoCompleteView.setError(" Please selected the interested place and then proceed");
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

            final SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());
            // all fields are available attempt to save the details to server

            NeedFlat needFlat = null;
            if (existingNeedFlat == null) {
                needFlat = new NeedFlat();
            } else {
                needFlat = existingNeedFlat;
            }

            needFlat.setUserName(sharedPreferenceManager.getNameOFLoggedInUser());
            needFlat.setUserId(sharedPreferenceManager.getObjectIdOFLoggedInUser());
            needFlat.setRentRange(Integer.parseInt(rentText.substring(0, rentText.length() - 1)));
            needFlat.setLookingFor(lookingForText);
            needFlat.setOccupancy(occupancyText);
            needFlat.setNeedToShiftDate(mNeedFromDate);
            needFlat.setPhoneNumber(contactNumberText);
            needFlat.setDescription(descriptionText);
            needFlat.setShouldShowContactNumber(showContactNumber);
            needFlat.setCurrentFlatLocation(currentSelectedPlaceDetails.getPlaceName());
            needFlat.setNeedFlatLocation(anotherSelectedPlaceDetails.getPlaceName());
            GeoPoint currentPoint = new GeoPoint(currentSelectedPlaceDetails.getLatitude(), currentSelectedPlaceDetails.getLongitude());
            currentPoint.addCategory("needFlatCurrentPlace");
            currentPoint.putMetadata("locationName", currentSelectedPlaceDetails.getPlaceName());
            currentPoint.putMetadata("address", currentSelectedPlaceDetails.getAddress());
            needFlat.setCurrentLocationCoordinates(currentPoint);
            GeoPoint anotherPoint = new GeoPoint(currentSelectedPlaceDetails.getLatitude(), currentSelectedPlaceDetails.getLongitude());
            anotherPoint.addCategory("needFlatAnotherPlace");
            anotherPoint.putMetadata("locationName", currentSelectedPlaceDetails.getPlaceName());
            anotherPoint.putMetadata("address", currentSelectedPlaceDetails.getAddress());
            needFlat.setAnotherLocationCoordinates(anotherPoint);
            if (Util.isNetworkAvailable(getActivity())) {
                Backendless.Data.of(NeedFlat.class).save(needFlat, new DefaultCallback<NeedFlat>(getActivity(), "Posting your need flat request ...") {
                    @Override
                    public void handleResponse(NeedFlat response) {
                        super.handleResponse(response);
                        if (response != null) {
                            updateUserPrefValue();
                            // save a pref that user has already posted a request
                            sharedPreferenceManager.saveUserHasNeedFlatRequirement(true);
                        }

                        // finish the activity
                        getActivity().setResult(HomeActivity.HOME_POST_RESULT_CODE);
                        getActivity().finish();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        super.handleFault(fault);
                        Log.e("Fault", fault.getMessage());
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
                        currentUserPreference.setIsUserPostedNeedFlatRequirement(true);
                        Backendless.Persistence.save(currentUserPreference, new AsyncCallback<UserPreferences>() {
                            public void handleResponse(UserPreferences response) {
                                // new user Preferences instance has been saved
                                if (response != null) {
                                    // save it in preferences
                                    sharedPreferenceManager.saveUserHasNeedFlatRequirement(response.isUserPostedNeedFlatRequirement());
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
}
