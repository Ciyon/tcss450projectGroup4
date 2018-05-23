package group4.tcss450.uw.edu.tcss450project;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import group4.tcss450.uw.edu.tcss450project.utils.SendGetAsyncTask;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

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

    private String mLocationKey;
    private String mLocationUrl;
    private String mForecastUrl;
    private String mCurrentConditionsUrl;
    private SearchView mSearchBar;

    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 814;
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

        mSearchBar = getActivity().findViewById(R.id.search_bar_location);

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

        // Make the location url
        mLocationUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_api_base_url))
                .appendPath(getString(R.string.ep_api_locations))
                .appendPath(getString(R.string.ep_api_v1))
                .appendPath(getString(R.string.ep_api_cities))
                .appendPath(getString(R.string.ep_api_geoposition))
                .appendPath(getString(R.string.ep_api_search))
                .build()
                .toString();

        // Make the forecast url
        mForecastUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_api_base_url))
                .appendPath(getString(R.string.ep_api_locations))
                .appendPath(getString(R.string.ep_api_v1))
                .appendPath(getString(R.string.ep_api_cities))
                .appendPath(getString(R.string.ep_api_geoposition))
                .appendPath(getString(R.string.ep_api_search))
                .build()
                .toString();

        // Make the current conditions url
        mCurrentConditionsUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_api_base_url))
                .appendPath(getString(R.string.ep_api_current_conditions))
                .appendPath(getString(R.string.ep_api_v1))
                .build()
                .toString();

        return view;
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();

        // update location key
        getCurrentLocationKey();
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

        // Get the location of the device
        /*
        mLocationUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_api_base_url))
                .appendPath(getString(R.string.ep_api_v1))
                .appendPath(getString(R.string.ep_api_locations))
                .appendQueryParameter(getString(R.string.keys_json_chat_id), Integer.toString(mLocationKey))
                .build()
                .toString();
*/


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

        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {
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
        SendGetAsyncTask.Builder builder = new SendGetAsyncTask.Builder(mLocationUrl)
                .onPostExecute(this::setLocationKey)
                .onCancelled(this::handleError);
        builder.setmParamKey("q");
        builder.setmParamValue(latlong);
        builder.build().execute();

    }

    private void displayCurrentConditions(String result) {
        //parse the json
    }

    private void setLocationKey(String result) {
        //parse the json
        //mLocationKey = result;
    }

    private void handleError(final String msg) {
        Log.e("Connections ERROR!!!", msg.toString());
    }

    private void getSearchLocationKey() {
        //TODO

    }
}
