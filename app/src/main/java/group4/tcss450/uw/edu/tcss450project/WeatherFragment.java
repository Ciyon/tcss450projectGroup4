package group4.tcss450.uw.edu.tcss450project;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group4.tcss450.uw.edu.tcss450project.utils.SendApiQueryAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = "MyLocationsActivity";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    private static final int MY_PERMISSIONS_LOCATIONS = 814;

    private String mLocationKey = "351409";
    private String mLocationUrl;
    private String mSearchLocationUrl;
    private String mForecastUrl;
    private String mCurrentConditionsUrl;

    private ArrayAdapter<String> mArrayAdapter;

    private String mLocationString;
    private List<String> mSavedLocations;
    private Map<String, String> mLocationNames;

    private AutoCompleteTextView mSearchView;
    private ImageButton mSearchButton;
    private ImageButton mCurrentLocationButton;
    private Button mSaveLocationButton;
    private Spinner mSavedLocationsSpinner;
    private TextView mCurrentLocationText;
    private TextView mCurrentConditionsTemp;
    private TextView mWeatherCurrentConditions;
    private TextView mOneDayMinTemp;
    private TextView mOneDayMaxTemp;
    private TextView mOneDayDate;
    private ImageView mIconCurrentConditions;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    public WeatherFragment() {
        // Required empty public constructor
    }


    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        mCurrentConditionsTemp = view.findViewById(R.id.tempCurrentCondtions);
        mIconCurrentConditions = view.findViewById(R.id.iconCurrentConditions);
        mWeatherCurrentConditions = view.findViewById(R.id.weatherCurrentCondtions);
        mOneDayDate = view.findViewById(R.id.date);
        mOneDayMaxTemp = view.findViewById(R.id.maxTemp);
        mOneDayMinTemp = view.findViewById(R.id.minTemp);

        mSearchView = view.findViewById(R.id.searchLocation);
        mSearchButton = view.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(this);
        mSaveLocationButton = view.findViewById(R.id.saveButton);
        mSaveLocationButton.setOnClickListener(this::onClickSave);
        mCurrentLocationText = view.findViewById(R.id.textLocation);
        mSavedLocationsSpinner = view.findViewById(R.id.saveLocationsSpinner);

        mSavedLocations = new ArrayList<String>();
        mLocationNames = new HashMap<String, String>();
        populateSavedLocationsList();
        mArrayAdapter = new ArrayAdapter<String>(this.getContext(),
                R.layout.adapter_array_text, mSavedLocations);
        mSavedLocationsSpinner.setAdapter(mArrayAdapter);
        mSavedLocationsSpinner.setOnItemSelectedListener(this);


        // TODO: Set up a list of saved locations to autocomplete
        //mSearchView.setCompletionHint();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        }

        mCurrentLocationButton = view.findViewById(R.id.currentLocationButton);
        mCurrentLocationButton.setOnClickListener(this::onClickCurrent);
        return view;
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();

        // update location key
        //getCurrentLocationKey();
        // TODO: check for last location searched?
        /*
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_location))) {
            throw new IllegalStateException("No location in prefs!");
        }
        */

    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentConditions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Permission denied", "Nothing");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down…maybe ask for permission again?
                    getActivity().finishAndRemoveTask();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        //(http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        System.out.println("We're connected?");
        if (mCurrentLocation == null) {
            System.out.println("Location is null");
            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {
                    System.out.println("Got the location.");
                    Log.i(TAG, mCurrentLocation.toString());
                }
                startLocationUpdates();
            }
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, mCurrentLocation.toString());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }

    private void getCurrentLocationKey() {

        // latitude and longitude pair of current location
        String latlong = Double.toString(mCurrentLocation.getLatitude())
                + "," + Double.toString(mCurrentLocation.getLongitude());

        String[] endpoints = new String[] {getString(R.string.ep_api_locations),
                getString(R.string.ep_api_v1),
                getString(R.string.ep_api_cities),
                getString(R.string.ep_api_geoposition),
                getString(R.string.ep_api_search)};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::setLocationKey)
                        .onCancelled(this::handleError);
        builder.setmParamKey("q");
        builder.setmParamValue(latlong);
        builder.build().execute();


    }

    private void getSearchLocationKey() {
        //TODO
        String postalcode = mSearchView.getText().toString();

        String[] endpoints = new String[] {getString(R.string.ep_api_locations),
                getString(R.string.ep_api_v1),
                getString(R.string.ep_api_postalcodes),
                getString(R.string.ep_api_search)};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::setLocationKey)
                        .onCancelled(this::handleError);
        builder.setmParamKey("q");
        builder.setmParamValue(postalcode);
        builder.build().execute();
    }

    private void getLocationName(String key) {
        String[] endpoints = new String[] {getString(R.string.ep_api_locations),
                getString(R.string.ep_api_v1),
                key};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::insertLocationInMap)
                        .onCancelled(this::handleError);
        builder.build().execute();
    }

    private void getOneDayForecast() {
        // Make the forecast url
        String[] endpoints = new String[] {getString(R.string.ep_api_forecasts),
                getString(R.string.ep_api_v1),
                getString(R.string.ep_api_daily),
                getString(R.string.ep_api_one_day),
                mLocationKey};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                .onPostExecute(this::displayOneDayForecast)
                .onCancelled(this::handleError);
        builder.build().execute();
    }

    private void getFiveDayForecast() {

        String[] endpoints = new String[] {getString(R.string.ep_api_forecasts),
                getString(R.string.ep_api_v1),
                getString(R.string.ep_api_daily),
                getString(R.string.ep_api_five_day),
                mLocationKey};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::displayFiveDayForecast)
                        .onCancelled(this::handleError);
        builder.build().execute();
    }

    private void getCurrentConditions() {

        String[] endpoints = new String[] {
                getString(R.string.ep_api_current_conditions),
                getString(R.string.ep_api_v1),
                mLocationKey};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::displayCurrentConditions)
                        .onCancelled(this::handleError);
        builder.build().execute();
    }

    private void displayOneDayForecast(String result) {
        //parse json and display the weather
        //parse json and display the weather
        JSONObject res = null;
        String date = "";
        String minTemp = "";
        String maxTemp = "";
        int currentTemp = 0;
        try {
            res = new JSONObject(result);
            JSONArray forecasts = res.getJSONArray("DailyForecasts");
            JSONObject forecastObj = forecasts.getJSONObject(0);
            date = forecastObj.getString("Date");
            JSONObject temp = forecastObj.getJSONObject("Temperature");
            JSONObject min = temp.getJSONObject("Minimum");
            JSONObject max = temp.getJSONObject("Maximum");
            minTemp = min.getString("Value");
            maxTemp = max.getString("Value");
            mOneDayDate.setText(date);
            mOneDayMinTemp.setText(minTemp + (char) 0x00B0 + "F");
            mOneDayMaxTemp.setText(maxTemp + (char) 0x00B0 + "F");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayFiveDayForecast(String result) {
        //parse json and display the weather

    }

    private void displayCurrentConditions(String result) {
        //parse json and display the weather
        JSONArray resArray = null;
        int currentTemp = 0;
        try {
            resArray = new JSONArray(result);
            JSONObject resObj = resArray.getJSONObject(0);
            JSONObject temp = resObj.getJSONObject("Temperature");
            JSONObject fTemp = temp.getJSONObject("Imperial");
            currentTemp = fTemp.getInt("Value");
            mCurrentConditionsTemp.setText(Integer.toString(currentTemp) + (char) 0x00B0 + "F");
            mWeatherCurrentConditions.setText(resObj.getString("WeatherText"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void insertLocationInMap(String result) {
        //parse json and display the weather
        JSONArray resArray = null;
        String locationKey = "";
        String cityName = "";
        String stateName = "";
        try {
            resArray = new JSONArray(result);
            JSONObject resObj = resArray.getJSONObject(0);
            cityName = resObj.getString("EnglishName");
            locationKey = resObj.getString("Key");
            JSONObject administrativeArea = resObj.getJSONObject("AdministrativeArea");
            stateName = administrativeArea.getString("EnglishName");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLocationNames.put(locationKey, cityName + ", " + stateName);
    }

    private void setLocationKey(String result) {
        //parse json and display the weather
        JSONArray resArray = null;
        String locationKey = "";
        String cityName = "";
        String stateName = "";
        try {
            resArray = new JSONArray(result);
            JSONObject resObj = resArray.getJSONObject(0);
            cityName = resObj.getString("EnglishName");
            locationKey = resObj.getString("Key");
            JSONObject administrativeArea = resObj.getJSONObject("AdministrativeArea");
            stateName = administrativeArea.getString("EnglishName");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setLocationName(cityName + ", " + stateName);
        mLocationNames.put(locationKey, cityName + ", " + stateName);
        selectLocationKey(locationKey);
    }

    private void selectLocationKey(String locationKey) {
        if (locationKey != "") {
            mLocationKey = locationKey;
        } else {
            // notify?
        }
        if (mSavedLocations.contains(mLocationKey)) {
            mSaveLocationButton.setEnabled(false);
        } else {
            mSaveLocationButton.setEnabled(true);
        }
        updateWeather();
        if (mLocationNames.get(locationKey) != null) {
            mCurrentLocationText.setText(mLocationNames.get(locationKey));
        } else {
            getLocationName(locationKey);
        }

    }

    private void setLocationName(String name) {
        mCurrentLocationText.setText(name);
    }

    private void populateSavedLocationsList() {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // add the location to the JSONArray
        try {
            String array = prefs.getString(getString(R.string.keys_prefs_saved_locations),
                    "");
            if (array != "") {
                JSONArray list = new JSONArray(prefs.getString(getString(R.string.keys_prefs_saved_locations),
                        ""));
                for (int i = 0; i < list.length(); i++) {
                    String s = list.getString(i);
                    // Check to see if it's in the list
                    if (!(mSavedLocations.contains(s))) {
                        mSavedLocations.add(s);
                    }
                    // Check to see if it's in the map with a name
                    if (!mLocationNames.containsKey(s)) {
                        getLocationName(s);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void handleError(final String msg) {
        Log.e("Connections ERROR!!!", msg.toString());
    }

    private void updateWeather() {
        getCurrentConditions();
        getFiveDayForecast();
        getOneDayForecast();
    }

    public void onClickCurrent(View view) {
        getCurrentLocationKey();
    }

    public void onClickSave(View view) {
        // get shared prefs
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // add the location to the JSONArray
        try {
            String arr = prefs.getString(getString(R.string.keys_prefs_saved_locations),
                    "");
            JSONArray list;
            if (arr == "") {
                list = new JSONArray();
            } else {
                list = new JSONArray(arr);
            }
            list.put(mLocationKey);
            // save the new list
            prefs.edit().putString(
                    getString(R.string.keys_prefs_saved_locations),
                    list.toString())
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSaveLocationButton.setEnabled(false);
        populateSavedLocationsList();
    }


    @Override
    public void onClick(View view) {
        getSearchLocationKey();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String key = (String) parent.getItemAtPosition(position);
        selectLocationKey(key);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
