package com.roomfinder.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.roomfinder.R;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.model.PlaceDetails;
import com.roomfinder.utils.Logger;

/**
 * Created by Aditya on 2/21/2016.
 */
public class AutoCompletePlacesAdapter extends ArrayAdapter<AutoCompletePlacesAdapter.PlaceAutocomplete> implements Filterable {

    public static final String LOG_TAG = "Autocomplete";
    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    public static final String OUT_JSON = "/json";
    public static final String COUNTRY = "us";
    public static final String TYPE_DETAILS = "/details";
    // the latitude and longitude that define the search center
    public static final String RESULTS_CENTER = "36.169941,-115.139830";
    public static final String RESULTS_RADIUS = "100";
    private Context mContext;
    private int mViewIdentifier;
    private ArrayList<PlaceAutocomplete> mResultList;
    private OnDataFetchedForSelectedPlace mDataFetchCallBack;

    public AutoCompletePlacesAdapter(Context context, int textViewResourceId,
                                     OnDataFetchedForSelectedPlace callback, int viewIdentifier) {
        super(context, textViewResourceId);
        mContext = context;
        mDataFetchCallBack = callback;
        mViewIdentifier = viewIdentifier;
        mResultList = new ArrayList<>(0);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = super.getView(position, convertView, parent);
        return rootView;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public AutoCompletePlacesAdapter.PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    ArrayList<PlaceAutocomplete> predictionsList = predictions(constraint.toString());
                    if (predictionsList != null)
                        mResultList = predictionsList;
//                    else
//                   Logger.toast(mContext,mContext.getResources().getString(R.string.network_error));
                    // Assign the data to the FilterResults
                    if (mResultList != null) {
                        filterResults.values = mResultList;
                        filterResults.count = mResultList.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<PlaceAutocomplete> predictions(String input) {
        ArrayList<PlaceAutocomplete> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + Defaults.MAPS_API_KEY);
            sb.append("&components=country:" + COUNTRY);
            sb.append("&location=" + RESULTS_CENTER);
            sb.append("&radius=" + RESULTS_RADIUS);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            // Log.v(" Url ", sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(new PlaceAutocomplete(predsJsonArray.getJSONObject(i).getString("place_id"), predsJsonArray.getJSONObject(i).getString("description")));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
            return resultList;
        }

        return resultList;
    }

    public void getPlaceDetails(int position) {
        PlaceAutocomplete currentSelectedPlace = getItem(position);
        String placeId = String.valueOf(currentSelectedPlace.placeId);
        new FetchPlaceDetailsAsync().execute(placeId);
        //  Logger.v("Place Selected  " + currentSelectedPlace.description);
    }

    private String fetchPlaceDetails(String placeId) {
        // https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJ387edqKTyzsR4kSTb57nEiw&key=AIzaSyDwVnkZ0TL2pQ4-8xu5rOVnm7SJ7ElHtdk
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(AutoCompletePlacesAdapter.PLACES_API_BASE + AutoCompletePlacesAdapter.TYPE_DETAILS + AutoCompletePlacesAdapter.OUT_JSON);
            sb.append("?placeid=" + URLEncoder.encode(placeId, "utf8"));
            sb.append("&key=" + Defaults.MAPS_API_KEY);
            sb.append("&components=country:" + COUNTRY);

            // Log.v(" Url ", sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults.toString();

    }

    public interface OnDataFetchedForSelectedPlace {
        void onPlaceDetailsFetched(PlaceDetails selectedPlaceItem, int viewIdentifier);
    }

    private class FetchPlaceDetailsAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return fetchPlaceDetails(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(s);
                JSONObject result = jsonObj.getJSONObject("result");
                JSONObject geometry = result.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                PlaceDetails placeDetails = new PlaceDetails(location.getDouble("lat"), location.getDouble("lng"), result.getString("name"), result.getString("formatted_address"));
                mDataFetchCallBack.onPlaceDetailsFetched(placeDetails, mViewIdentifier);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Cannot process JSON results", e);
            }
            super.onPostExecute(s);
        }
    }

    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }


}