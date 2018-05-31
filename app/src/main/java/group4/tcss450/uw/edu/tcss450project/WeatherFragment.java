package group4.tcss450.uw.edu.tcss450project;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

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

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    /**
     * The location key that weather is currently being displayed for.
     */
    private String mLocationKey = "";

    /**
     * The location name currently being displayed.
     */
    private String mLocationName = "";

    private List<String> mSavedLocationNames;

    /**
     * Maps the names of saved locations to their corresponding location keys.
     */
    private Map<String, String> mLocationKeyMap;

    /* UI Components */
    private EditText mSearchView;
    private TextView mCurrentLocationText;
    private TextView mCurrentConditionsTemp;
    private TextView mWeatherCurrentConditions;
    private TextView mOneDayMinTemp;
    private TextView mOneDayMaxTemp;
    private TextView mOneDayDate;
    private TextView mOneDayConditions;

    private TextView mDayOneMinTemp;
    private TextView mDayOneMaxTemp;
    private TextView mDayOneDate;
    private TextView mDayOneConditions;

    private TextView mDayTwoMinTemp;
    private TextView mDayTwoMaxTemp;
    private TextView mDayTwoDate;
    private TextView mDayTwoConditions;

    private TextView mDayThreeMinTemp;
    private TextView mDayThreeMaxTemp;
    private TextView mDayThreeDate;
    private TextView mDayThreeConditions;

    private TextView mDayFourMinTemp;
    private TextView mDayFourMaxTemp;
    private TextView mDayFourDate;
    private TextView mDayFourConditions;

    private TextView mDayFiveMinTemp;
    private TextView mDayFiveMaxTemp;
    private TextView mDayFiveDate;
    private TextView mDayFiveConditions;

    private Button mSaveLocationButton;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        // Initialize saved location list and map
        mSavedLocationNames = new ArrayList<>();
        mLocationKeyMap = new HashMap<>();

        // Initialize UI Components
        mCurrentConditionsTemp = view.findViewById(R.id.tempCurrentCondtions);
        mWeatherCurrentConditions = view.findViewById(R.id.weatherCurrentCondtions);
        mOneDayDate = view.findViewById(R.id.oneDayDate);
        mOneDayMaxTemp = view.findViewById(R.id.oneDayMaxTemp);
        mOneDayMinTemp = view.findViewById(R.id.oneDayMinTemp);
        mOneDayConditions = view.findViewById(R.id.oneDayConditions);

        mDayOneDate = view.findViewById(R.id.dayOneDate);
        mDayOneMaxTemp = view.findViewById(R.id.dayOneMaxTemp);
        mDayOneMinTemp = view.findViewById(R.id.dayOneMinTemp);
        mDayOneConditions = view.findViewById(R.id.dayOneConditions);

        mDayTwoDate = view.findViewById(R.id.dayTwoDate);
        mDayTwoMaxTemp = view.findViewById(R.id.dayTwoMaxTemp);
        mDayTwoMinTemp = view.findViewById(R.id.dayTwoMinTemp);
        mDayTwoConditions = view.findViewById(R.id.dayTwoConditions);

        mDayThreeDate = view.findViewById(R.id.dayThreeDate);
        mDayThreeMaxTemp = view.findViewById(R.id.dayThreeMaxTemp);
        mDayThreeMinTemp = view.findViewById(R.id.dayThreeMinTemp);
        mDayThreeConditions = view.findViewById(R.id.dayThreeConditions);

        mDayFourDate = view.findViewById(R.id.dayFourDate);
        mDayFourMaxTemp = view.findViewById(R.id.dayFourMaxTemp);
        mDayFourMinTemp = view.findViewById(R.id.dayFourMinTemp);
        mDayFourConditions = view.findViewById(R.id.dayFourConditions);

        mDayFiveDate = view.findViewById(R.id.dayFiveDate);
        mDayFiveMaxTemp = view.findViewById(R.id.dayFiveMaxTemp);
        mDayFiveMinTemp = view.findViewById(R.id.dayFiveMinTemp);
        mDayFiveConditions = view.findViewById(R.id.dayFiveConditions);

        mSearchView = view.findViewById(R.id.searchLocation);
        ImageButton mSearchButton = view.findViewById(R.id.searchButton);
        mSaveLocationButton = view.findViewById(R.id.saveButton);
        mCurrentLocationText = view.findViewById(R.id.textLocation);
        Spinner mSavedLocationsSpinner = view.findViewById(R.id.saveLocationsSpinner);
        ImageButton mCurrentLocationButton = view.findViewById(R.id.currentLocationButton);

        // Initialize spinner and adapter for saved locations
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()),
                R.layout.adapter_array_text, mSavedLocationNames);
        mSavedLocationsSpinner.setAdapter(mArrayAdapter);

        // Set on click listeners
        mCurrentLocationButton.setOnClickListener(this::onClickCurrent);
        mSaveLocationButton.setOnClickListener(this::onClickSave);
        mSearchButton.setOnClickListener(this);
        mSavedLocationsSpinner.setOnItemSelectedListener(this);

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

        return view;
    }


    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();

        // Update data and weather displays
        if (mCurrentLocation != null) {
            getCurrentLocationKey();
            updateWeather();
        }
        // Update saved locations based on prefs
        populateSavedLocationsListAndMap();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Permission denied", "Nothing");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down…maybe ask for permission again?
                    Objects.requireNonNull(getActivity()).finishAndRemoveTask();
                }
                // else permission was granted, yay! Do the // locations-related task you need to do.

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    @SuppressWarnings("deprecation")
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        //(http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this.getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)
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
    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
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
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this.getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }

    /**
     * Starts an AsyncTask to get a location key from Accuweather
     * based on the device's current location
     */
    private void getCurrentLocationKey() {

        // latitude and longitude pair of current location
        String latlong = Double.toString(mCurrentLocation.getLatitude())
                + "," + Double.toString(mCurrentLocation.getLongitude());

        String[] endpoints = new String[]{getString(R.string.ep_api_locations),
                getString(R.string.ep_api_v1),
                getString(R.string.ep_api_cities),
                getString(R.string.ep_api_geoposition),
                getString(R.string.ep_api_search)};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::getLocationKeyFromJSONObject)
                        .onCancelled(this::handleError);
        builder.setmParamKey("q");
        builder.setmParamValue(latlong);
        builder.build().execute();


    }

    /**
     * Starts an AsyncTask to get a location key from Accuweather
     * based on a search parameter entered by the user.
     */
    private void getSearchLocationKey() {
        String postalcode = mSearchView.getText().toString();

        String[] endpoints = new String[]{getString(R.string.ep_api_locations),
                getString(R.string.ep_api_v1),
                getString(R.string.ep_api_postalcodes),
                getString(R.string.ep_api_search)};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::getLocationKeyFromJSONArray)
                        .onCancelled(this::handleError);
        builder.setmParamKey("q");
        builder.setmParamValue(postalcode);
        builder.build().execute();
    }

    /**
     * Starts an AsyncTask to get a location name from Accuweather
     * based on a provided location key.
     *
     * @param key The location key
     */
    private void getLocationName(String key) {
        String[] endpoints = new String[]{getString(R.string.ep_api_locations),
                getString(R.string.ep_api_v1),
                key};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::insertLocationInMap)
                        .onCancelled(this::handleError);
        builder.build().execute();
    }

    /**
     * Starts an AsyncTask to get a one day forecast from Accuweather
     * based on the location key saved in mLocationKey.
     */
    private void getOneDayForecast() {
        // Make the forecast url
        String[] endpoints = new String[]{getString(R.string.ep_api_forecasts),
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

    /**
     * Starts an AsyncTask to get a five day forecast from Accuweather
     * based on the location key saved in mLocationKey.
     */
    private void getFiveDayForecast() {

        String[] endpoints = new String[]{getString(R.string.ep_api_forecasts),
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

    /**
     * Starts an AsyncTask to get the current conditions from Accuweather
     * based on the location key saved in mLocationKey.
     */
    private void getCurrentConditions() {

        String[] endpoints = new String[]{
                getString(R.string.ep_api_current_conditions),
                getString(R.string.ep_api_v1),
                mLocationKey};
        SendApiQueryAsyncTask.Builder builder =
                new SendApiQueryAsyncTask.Builder(getString(R.string.ep_api_base_url), endpoints)
                        .onPostExecute(this::displayCurrentConditions)
                        .onCancelled(this::handleError);
        builder.build().execute();
    }

    /**
     * Parses a JSON string result and updates the one day forecast
     * UI components.
     *
     * @param result The JSON string to be parsed
     */
    @SuppressLint("SetTextI18n")
    private void displayOneDayForecast(String result) {
        //parse json and display the weather
        //parse json and display the weather
        JSONObject res;
        String date;
        String minTemp;
        String maxTemp;
        String conditions;
        try {
            res = new JSONObject(result);
            JSONArray forecasts = res.getJSONArray("DailyForecasts");
            JSONObject forecastObj = forecasts.getJSONObject(0);
            date = forecastObj.getString("Date");
            JSONObject temp = forecastObj.getJSONObject("Temperature");
            JSONObject day = forecastObj.getJSONObject("Day");
            JSONObject min = temp.getJSONObject("Minimum");
            JSONObject max = temp.getJSONObject("Maximum");
            minTemp = min.getString("Value");
            maxTemp = max.getString("Value");
            conditions = day.getString("IconPhrase");
            mOneDayDate.setText(date);
            mOneDayMinTemp.setText(minTemp + (char) 0x00B0 + "F");
            mOneDayMaxTemp.setText(maxTemp + (char) 0x00B0 + "F");
            mOneDayConditions.setText(conditions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a JSON string result and updates the five day forecast
     * UI components.
     *
     * @param result The JSON string to be parsed
     */
    @SuppressLint("SetTextI18n")
    private void displayFiveDayForecast(String result) {
        //parse json and display the weather
        //parse json and display the weather
        JSONObject res;
        String date;
        String minTemp;
        String maxTemp;
        String conditions;
        try {
            // Parse JSON and update UI components
            res = new JSONObject(result);
            JSONArray forecasts = res.getJSONArray("DailyForecasts");
            JSONObject forecastObj0 = forecasts.getJSONObject(0);
            date = forecastObj0.getString("Date");
            JSONObject temp0 = forecastObj0.getJSONObject("Temperature");
            JSONObject day0 = forecastObj0.getJSONObject("Day");
            JSONObject min0 = temp0.getJSONObject("Minimum");
            JSONObject max0 = temp0.getJSONObject("Maximum");
            minTemp = min0.getString("Value");
            maxTemp = max0.getString("Value");
            conditions = day0.getString("IconPhrase");
            mDayOneDate.setText(date);
            mDayOneMinTemp.setText(minTemp + (char) 0x00B0 + "F");
            mDayOneMaxTemp.setText(maxTemp + (char) 0x00B0 + "F");
            mDayOneConditions.setText(conditions);

            JSONObject forecastObj1 = forecasts.getJSONObject(1);
            date = forecastObj1.getString("Date");
            JSONObject temp1 = forecastObj1.getJSONObject("Temperature");
            JSONObject day1 = forecastObj1.getJSONObject("Day");
            JSONObject min1 = temp1.getJSONObject("Minimum");
            JSONObject max1 = temp1.getJSONObject("Maximum");
            minTemp = min1.getString("Value");
            maxTemp = max1.getString("Value");
            conditions = day1.getString("IconPhrase");
            mDayTwoDate.setText(date);
            mDayTwoMinTemp.setText(minTemp + (char) 0x00B0 + "F");
            mDayTwoMaxTemp.setText(maxTemp + (char) 0x00B0 + "F");
            mDayTwoConditions.setText(conditions);

            JSONObject forecastObj2 = forecasts.getJSONObject(2);
            date = forecastObj2.getString("Date");
            JSONObject temp2 = forecastObj2.getJSONObject("Temperature");
            JSONObject day2 = forecastObj2.getJSONObject("Day");
            JSONObject min2 = temp2.getJSONObject("Minimum");
            JSONObject max2 = temp2.getJSONObject("Maximum");
            minTemp = min2.getString("Value");
            maxTemp = max2.getString("Value");
            conditions = day2.getString("IconPhrase");
            mDayThreeDate.setText(date);
            mDayThreeMinTemp.setText(minTemp + (char) 0x00B0 + "F");
            mDayThreeMaxTemp.setText(maxTemp + (char) 0x00B0 + "F");
            mDayThreeConditions.setText(conditions);

            JSONObject forecastObj3 = forecasts.getJSONObject(3);
            date = forecastObj3.getString("Date");
            JSONObject temp3 = forecastObj3.getJSONObject("Temperature");
            JSONObject day3 = forecastObj3.getJSONObject("Day");
            JSONObject min3 = temp3.getJSONObject("Minimum");
            JSONObject max3 = temp3.getJSONObject("Maximum");
            minTemp = min3.getString("Value");
            maxTemp = max3.getString("Value");
            conditions = day3.getString("IconPhrase");
            mDayFourDate.setText(date);
            mDayFourMinTemp.setText(minTemp + (char) 0x00B0 + "F");
            mDayFourMaxTemp.setText(maxTemp + (char) 0x00B0 + "F");
            mDayFourConditions.setText(conditions);

            JSONObject forecastObj4 = forecasts.getJSONObject(4);
            date = forecastObj4.getString("Date");
            JSONObject temp4 = forecastObj4.getJSONObject("Temperature");
            JSONObject day4 = forecastObj4.getJSONObject("Day");
            JSONObject min4 = temp4.getJSONObject("Minimum");
            JSONObject max4 = temp4.getJSONObject("Maximum");
            minTemp = min4.getString("Value");
            maxTemp = max4.getString("Value");
            conditions = day4.getString("IconPhrase");
            mDayFiveDate.setText(date);
            mDayFiveMinTemp.setText(minTemp +
                    (char) 0x00B0 + "F");
            mDayFiveMaxTemp.setText(maxTemp +
                    (char) 0x00B0 + "F");
            mDayFiveConditions.setText(conditions);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parses a JSON string result and updates the current conditions
     * UI components.
     *
     * @param result The JSON string to be parsed
     */
    @SuppressLint("SetTextI18n")
    private void displayCurrentConditions(String result) {
        //parse json and display the weather
        JSONArray resArray;
        int currentTemp;
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

    /**
     * Parses a JSON string with location information, and saves it
     * for reference
     *
     * @param result The JSON string to be parsed.
     */
    @SuppressLint("SetTextI18n")
    private void insertLocationInMap(String result) {
        JSONObject res;
        String locationKey = "";
        String cityName = "";
        String stateName = "";
        try {
            res = new JSONObject(result);
            cityName = res.getString("EnglishName");
            locationKey = res.getString("Key");
            JSONObject administrativeArea = res.getJSONObject("AdministrativeArea");
            stateName = administrativeArea.getString("EnglishName");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLocationKeyMap.put(cityName + ", " + stateName, locationKey);
        if (mLocationKey.equals(locationKey)) {
            mCurrentLocationText.setText(cityName + ", " + stateName);
        }
    }

    @SuppressLint("SetTextI18n")
    private void getLocationKeyFromJSONArray(String result) {
        //parse json and display the weather
        JSONObject resObj;
        JSONArray resArray;
        String locationKey = "";
        String cityName = "";
        String stateName = "";
        try {
            resArray = new JSONArray(result);
            resObj = resArray.getJSONObject(0);
            cityName = resObj.getString("EnglishName");
            locationKey = resObj.getString("Key");
            JSONObject administrativeArea = resObj.getJSONObject("AdministrativeArea");
            stateName = administrativeArea.getString("EnglishName");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCurrentLocationText.setText(cityName + ", " + stateName);
        mLocationKeyMap.put(cityName + ", " + stateName, locationKey);
        selectLocationKey(locationKey, cityName + ", " + stateName);
    }

    @SuppressLint("SetTextI18n")
    private void getLocationKeyFromJSONObject(String result) {
        //parse json and display the weather
        JSONObject resObj;
        String locationKey = "";
        String cityName = "";
        String stateName = "";
        try {
            resObj = new JSONObject(result);
            cityName = resObj.getString("EnglishName");
            locationKey = resObj.getString("Key");
            JSONObject administrativeArea = resObj.getJSONObject("AdministrativeArea");
            stateName = administrativeArea.getString("EnglishName");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCurrentLocationText.setText(cityName + ", " + stateName);
        mLocationKeyMap.put(cityName + ", " + stateName, locationKey);
        selectLocationKey(locationKey, cityName + ", " + stateName);
    }

    /**
     * Change mLocationKey, enable or disable save button, update text and weather.
     *
     * @param locationKey  the location key
     * @param locationName the location name
     */
    private void selectLocationKey(String locationKey, String locationName) {
        if (!Objects.equals(locationKey, "") && !Objects.equals(locationName, "")) {
            mLocationKey = locationKey;
            mLocationName = locationName;
            if (mSavedLocationNames.contains(locationName)) {
                mSaveLocationButton.setEnabled(false);
            } else {
                mSaveLocationButton.setEnabled(true);
            }
            mCurrentLocationText.setText(locationName);
            updateWeather();
        } else {
            Context context = Objects.requireNonNull(this.getActivity()).getApplicationContext();
            CharSequence text = "No location";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    /**
     * Update the user's saved locations based on their prefs.
     */
    private void populateSavedLocationsListAndMap() {
        JSONArray list = getSavedLocationPrefs();
        for (int i = 0; i < list.length(); i++) {
            String s = null;
            try {
                s = list.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Check to see if it's in the list
            if (!(mSavedLocationNames.contains(s))) {
                mSavedLocationNames.add(s);
            }
            if (!(mLocationKeyMap.containsKey(s))) {
                getLocationName(s);
            }
        }

    }

    /**
     * Get the user's saved locations from prefs.
     *
     * @return a list of locations
     */
    private JSONArray getSavedLocationPrefs() {
        JSONArray list = new JSONArray();
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // add the location to the JSONArray
        try {
            list = new JSONArray(prefs.getString(getString(R.string.keys_prefs_saved_locations),
                    ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void handleError(final String msg) {
        Log.e("Connections ERROR!!!", msg);
    }

    /**
     * Update all weather displays
     */
    private void updateWeather() {
        getCurrentConditions();
        getFiveDayForecast();
        getOneDayForecast();
    }

    /**
     * Listener for current location button
     *
     * @param view the button
     */
    public void onClickCurrent(View view) {
        if (mCurrentLocation != null) {
            getCurrentLocationKey();
        } else {
            Context context = Objects.requireNonNull(this.getActivity()).getApplicationContext();
            CharSequence text = "Current location not available";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    /**
     * Listener for save button
     *
     * @param view the button
     */
    public void onClickSave(View view) {

        // get shared prefs
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // add the location and key to the JSONArray
        try {
            String arr = prefs.getString(getString(R.string.keys_prefs_saved_locations),
                    "");
            JSONArray list;
            if (arr.equals("")) {
                list = new JSONArray();
            } else {
                list = new JSONArray(arr);
            }
            list.put(mLocationName);
            // save the new list
            prefs.edit().putString(
                    getString(R.string.keys_prefs_saved_locations),
                    list.toString())
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSaveLocationButton.setEnabled(false);
        populateSavedLocationsListAndMap();
    }


    @Override
    public void onClick(View view) {
        getSearchLocationKey();
    }


    /**
     * Listener for spinner items
     *
     * @param parent   Spinner adapter
     * @param view     item selected
     * @param position position of item selected
     * @param id       id of item selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String name = (String) parent.getAdapter().getItem(position);
        selectLocationKey(mLocationKeyMap.get(name), name);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
