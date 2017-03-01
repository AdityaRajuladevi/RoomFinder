package com.roomfinder.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.List;

import com.roomfinder.R;
import com.roomfinder.adapter.AutoCompletePlacesAdapter;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.model.HaveFlat;
import com.roomfinder.model.NeedFlat;
import com.roomfinder.model.PlaceDetails;
import com.roomfinder.model.SearchResultItem;
import com.roomfinder.ui.SearchResultsActivity;
import com.roomfinder.utils.KeyUtils;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;

/**
 * Created by Aditya on 2/20/2016.
 */
public class FlatMateFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, AutoCompletePlacesAdapter.OnDataFetchedForSelectedPlace {

    private FragmentInteractionListener mListener;
    private AutoCompletePlacesAdapter mAutoCompletePlacesAdapter;
    private PlaceDetails selectedPlaceDetails;
    private AutoCompleteTextView autoCompView;
    private TextView price_range_text;
    private ProgressDialog mProgressDialog;
    private String currentUserId;

    public FlatMateFragment() {
        // Required empty public constructor
    }

    public static FlatMateFragment newInstance() {
        FlatMateFragment fragment = new FlatMateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //  mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_flat_mate, container, false);
        initUI(rootView);
        return rootView;
    }


    private void initUI(View rootView) {
        autoCompView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);
        autoCompView.setThreshold(1);
        mAutoCompletePlacesAdapter = new AutoCompletePlacesAdapter(getActivity(), android.R.layout.simple_list_item_1, this, 0);
        autoCompView.setAdapter(mAutoCompletePlacesAdapter);
        autoCompView.setOnItemClickListener(this);


        ImageButton clearText = (ImageButton) rootView.findViewById(R.id.clearTextBtn);
        clearText.setOnClickListener(this);

        price_range_text = (TextView) rootView.findViewById(R.id.price_range_text);

        SeekBar priceControl = (SeekBar) rootView.findViewById(R.id.price_range_bar);
        priceControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 350;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                price_range_text.setText("$" + progressChanged + "+");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // price_range_text.setText("$"+progressChanged+"+");
            }
        });

        Button submit = (Button) rootView.findViewById(R.id.btn_flat_mate_submit);
        submit.setOnClickListener(this);

        // initialize the user Id value from shared Preferences
        currentUserId = new SharedPreferenceManager(getActivity()).getObjectIdOFLoggedInUser();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null)
            mListener.onFragmentInteraction();
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
    public void onResume() {
        super.onResume();
        autoCompView.clearComposingText();
        autoCompView.setText("");
        selectedPlaceDetails = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearTextBtn:
                autoCompView.clearComposingText();
                autoCompView.setText("");
                // clear previous selection
                selectedPlaceDetails = null;
                break;
            case R.id.btn_flat_mate_submit:
                performSearch();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAutoCompletePlacesAdapter != null)
            mAutoCompletePlacesAdapter.getPlaceDetails(position);

        // hide soft input keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                autoCompView.getWindowToken(), 0);

    }

    @Override
    public void onPlaceDetailsFetched(PlaceDetails selectedPlaceItem, int viewIdentifier) {
        if (selectedPlaceItem != null) {
            selectedPlaceDetails = selectedPlaceItem;
        }
    }

    private void performSearch() {
        if (selectedPlaceDetails != null) {
            String whereClause = "distance(" + selectedPlaceDetails.getLatitude() + "," + selectedPlaceDetails.getLongitude() +
                    ", coordinates.latitude, coordinates.longitude ) < km(10)";

            final BackendlessDataQuery dataQuery = new BackendlessDataQuery(whereClause);
            QueryOptions queryOptions = new QueryOptions();
            queryOptions.setRelationsDepth(1);
            dataQuery.setQueryOptions(queryOptions);
            mProgressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.app_name), "Fetching Results...");
            Backendless.Data.of(HaveFlat.class).find(dataQuery, new AsyncCallback<BackendlessCollection<HaveFlat>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<HaveFlat> response) {
                            ArrayList<SearchResultItem> haveFlatSearchResults = new ArrayList<SearchResultItem>(0);
                            if (response != null) {
                                Logger.v("Total Have Search Results =" + response.getTotalObjects());
                                List<HaveFlat> haveFlatResults = response.getData();
                                if (haveFlatResults != null) {
                                    for (HaveFlat flat : haveFlatResults) {
                                        //  add all users except the current logged in user
                                        if (currentUserId != null && !flat.getUserId().equals(currentUserId)) {
                                            SearchResultItem eachHaveFlatItem = new SearchResultItem(flat.getObjectId(), flat.getUserId(), flat.getUserName(), flat.getRentRange(),
                                                    flat.getFlatLocation(), flat.getOccupancy());
                                            haveFlatSearchResults.add(eachHaveFlatItem);
                                        }
                                        // Log.v("Latest Search Result", "=" + flat.getObjectId());
                                    }
                                }
                            }
                            //perform need flat search
                            performNeedFlatSearch(haveFlatSearchResults);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            Log.e("Fault", fault.getMessage());

                            // fault scenario
                            ArrayList<SearchResultItem> haveFlatSearchResults = new ArrayList<SearchResultItem>(0);
                            performNeedFlatSearch(haveFlatSearchResults);

                            // Toast.makeText(getActivity()," Could Not Fetch Results ... ",Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    private void performNeedFlatSearch(final ArrayList<SearchResultItem> haveFlatItems) {
        if (selectedPlaceDetails != null) {
            String whereClause = "distance(" + selectedPlaceDetails.getLatitude() + "," + selectedPlaceDetails.getLongitude() +
                    ", anotherLocationCoordinates.latitude, anotherLocationCoordinates.longitude ) < km(10)";

            final BackendlessDataQuery dataQuery = new BackendlessDataQuery(whereClause);
            QueryOptions queryOptions = new QueryOptions();
            queryOptions.setRelationsDepth(1);
            dataQuery.setQueryOptions(queryOptions);
            Backendless.Data.of(NeedFlat.class).find(dataQuery, new AsyncCallback<BackendlessCollection<NeedFlat>>() {
                        @Override
                        public void handleResponse(BackendlessCollection<NeedFlat> response) {
                            ArrayList<SearchResultItem> needFlatArrayList = new ArrayList<SearchResultItem>(0);
                            if (response != null) {
                                List<NeedFlat> needFlatResults = response.getData();
                                if (needFlatResults != null) {
                                    for (NeedFlat flat : needFlatResults) {
                                        //  add all users except the current logged in user
                                        if (currentUserId != null && !flat.getUserId().equals(currentUserId)) {
                                            SearchResultItem eachNeedFlatItem = new SearchResultItem(flat.getObjId(), flat.getUserId(), flat.getUserName(), flat.getRentRange(),
                                                    flat.getCurrentFlatLocation(), flat.getOccupancy());
                                            needFlatArrayList.add(eachNeedFlatItem);
                                        }
                                    }
                                }
                                Logger.v("Need Flat Search Result Size = " + needFlatResults.size());
                            }
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            takeToSearchResultsActivity(haveFlatItems, needFlatArrayList);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e("Fault", fault.getMessage());
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            // resume code execution by putting dummy values
                            ArrayList<SearchResultItem> needFlatArrayList = new ArrayList<SearchResultItem>(0);
                            takeToSearchResultsActivity(haveFlatItems, needFlatArrayList);
                            //Toast.makeText(getActivity(), " Could Not Fetch Results ... ", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }

    private void takeToSearchResultsActivity(ArrayList<SearchResultItem> haveFlats, ArrayList<SearchResultItem> needFlats) {
        Intent i = new Intent(getActivity(), SearchResultsActivity.class);
        i.putParcelableArrayListExtra(KeyUtils.KEY_HAVE_FLAT_RESULTS, haveFlats);
        i.putParcelableArrayListExtra(KeyUtils.KEY_NEED_FLAT_RESULTS, needFlats);
        startActivity(i);
    }
}
