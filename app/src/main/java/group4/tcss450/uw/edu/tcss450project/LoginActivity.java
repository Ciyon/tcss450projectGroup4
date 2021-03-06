package group4.tcss450.uw.edu.tcss450project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import group4.tcss450.uw.edu.tcss450project.model.Credentials;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;

/**
 * {@link AppCompatActivity} that manages login and registration functionality and fragments
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener, AccountOptionsFragment.OnFragmentInteractionListener, ResetPasswordFragment.OnFragmentInteractionListener {

    private Credentials mCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //No user theme should get set here, since no user is logged in with their theme
        //setUserTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                SharedPreferences prefs =
                        getSharedPreferences(
                                getString(R.string.keys_shared_prefs),
                                Context.MODE_PRIVATE);
                // Check to see if the user has checked stay logged in on a previouc login
                if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in),
                        false)) {
                    // if they have, skip login
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragmentContainer, new LoginFragment(),
                                    getString(R.string.keys_fragment_login))
                            .commit();
                }
            }
        }

    }

    /**
     * Inflates the layout for the options action bar
     *
     * @param menu the menu
     * @return on success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_drawer, menu);
        return true;
    }

    /**
     * Handles selection of action bar items
     *
     * @param item action bar item
     * @return true if the item is selected, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.accountOptions) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new AccountOptionsFragment(),
                            getString(R.string.keys_fragment_account_options))
                    .addToBackStack(null);
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds the register fragment to the fragment container, takes
     * the user to the register page.
     */
    @Override
    public void onRegisterClicked() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new RegisterFragment(),
                        getString(R.string.keys_fragment_register))
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Starts a {@link SendPostAsyncTask} to make a login attempt.
     *
     * @param credentials The user's login credentials
     */
    @Override
    public void onLoginAttempt(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();
        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();
        mCredentials = credentials;
        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    @Override
    public void onRegisterAttempt(Credentials credentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register))
                .build();
        //build the JSONObject
        JSONObject msg = credentials.asJSONObject();
        mCredentials = credentials;
        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegisterOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

    }

    @Override
    public void onResendConfirmationClick(String email) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_resend))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", email);
        } catch (JSONException e) {
            Log.wtf("RESEND EMAIL", "Error creating JSON: " + e.getMessage());
        }
        //instantiate and execute the AsyncTask.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleResendEmailOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Start an {@link SendPostAsyncTask} to send a reset code to the user
     *
     * @param email the user's email
     */
    @Override
    public void onSendResetCode(String email) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_code))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", email);
        } catch (JSONException e) {
            Log.wtf("RESET PASSWORD", "Error creating JSON: " + e.getMessage());
        }
        //instantiate and execute the AsyncTask.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleResetPasswordOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Takes the user to the reset password fragment.
     */
    @Override
    public void onPasswordCodeSubmit() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new ResetPasswordFragment(),
                        getString(R.string.keys_fragment_reset_password))
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Start a {@link SendPostAsyncTask} to reset a user's password.
     *
     * @param password the user's new password
     * @param code     the password code
     * @param email    the user's email
     */
    @Override
    public void onSubmitPassword(Editable password, String code, String email) {

        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_reset_password))
                .build();
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("password", password.toString());
            msg.put("code", code);
            msg.put("email", email);
        } catch (JSONException e) {
            Log.wtf("PASSWORD SUBMIT", "Error creating JSON: " + e.getMessage());
        }
        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleSubmitPasswordOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Sets the user's preferences to indicate they checked stay logged in.
     */
    private void checkStayLoggedIn() {
        if (((CheckBox) findViewById(R.id.logCheckBox)).isChecked()) {
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //save the username for later usage
            prefs.edit().putString(
                    getString(R.string.keys_prefs_username),
                    mCredentials.getUsername())
                    .apply();
            //save the users “want” to stay logged in
            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_logged_in),
                    true)
                    .apply();
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                if (resultsJSON.has(getString(R.string.keys_json_member_id))) {
                    int id = resultsJSON.getInt(getString(R.string.keys_json_member_id));
                    SharedPreferences prefs =
                            getSharedPreferences(
                                    getString(R.string.keys_shared_prefs),
                                    Context.MODE_PRIVATE);
                    //save the username for later usage
                    prefs.edit().putInt(
                            getString(R.string.keys_prefs_user_id), id)
                            .apply();
                }
                checkStayLoggedIn();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("username", mCredentials.getUsername());
                startActivity(intent);
                //if false pop up the toast and do nothing
            } else {
                // Login was unsuccessful. Don’t switch fragments and inform the user
                LoginFragment frag =
                        (LoginFragment) getSupportFragmentManager()
                                .findFragmentByTag(getString(R.string.keys_fragment_login));

                String error = resultsJSON.get("error").toString();
                frag.setError(error);
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Handle the registration of a user if the webservice was successful in registering.
     *
     * @param result JSON message to parse
     */
    private void handleRegisterOnPost(String result) {
        try {
            // Parse the JSON
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                Toast.makeText(this,
                        "Registration Successful!\nPlease Respond to Confirmation Email",
                        Toast.LENGTH_LONG).show();

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new LoginFragment(),
                                getString(R.string.keys_fragment_login));
                // Commit the transaction
                transaction.commit();

            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the user
                RegisterFragment frag =
                        (RegisterFragment) getSupportFragmentManager()
                                .findFragmentByTag(getString(R.string.keys_fragment_register));

                String error = resultsJSON.get("error").toString();
                frag.setError(error);
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Handle resend e-mail if the webservice call was successful.
     *
     * @param result JSON message to parse
     */
    private void handleResendEmailOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                Toast.makeText(this,
                        "Email Resent!\nPlease Respond to Confirmation Email",
                        Toast.LENGTH_LONG).show();

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new LoginFragment(),
                                getString(R.string.keys_fragment_login));
                // Commit the transaction
                transaction.commit();

            } else {
                AccountOptionsFragment frag =
                        (AccountOptionsFragment) getSupportFragmentManager()
                                .findFragmentByTag(getString(R.string.keys_fragment_account_options));
                String error = resultsJSON.get("error").toString();
                frag.setError(error);
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Handle resetting the password if the webservice call was successful.
     *
     * @param result JSON message to parse
     */
    private void handleResetPasswordOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                // Notify the user that the e-mail has been sent
                Toast.makeText(this,
                        "Password reset code email sent!\nPlease enter " +
                                "the code on this page within 24 hours to reset your password.",
                        Toast.LENGTH_LONG).show();

                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new AccountOptionsFragment(),
                                getString(R.string.keys_fragment_account_options));
                // Commit the transaction
                transaction.commit();

            } else {
                //Login was unsuccessful. Don’t switch fragments and inform the user
                AccountOptionsFragment frag =
                        (AccountOptionsFragment) getSupportFragmentManager()
                                .findFragmentByTag(getString(R.string.keys_fragment_account_options));

                String error = resultsJSON.get("error").toString();
                frag.setError(error);
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * If passwords reset was successful on the webserver, notifies the user.
     *
     * @param result JSON message to parse
     */
    private void handleSubmitPasswordOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            // Tell the user password reset was successful
            if (success) {
                Toast.makeText(this,
                        "Password has been reset.",
                        Toast.LENGTH_LONG).show();
                getSupportFragmentManager().popBackStack();
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new LoginFragment(),
                                getString(R.string.keys_fragment_login));
                // Commit the transaction
                transaction.commit();

            } else {
                ResetPasswordFragment frag =
                        (ResetPasswordFragment) getSupportFragmentManager()
                                .findFragmentByTag(getString(R.string.keys_fragment_reset_password));

                String error = resultsJSON.get("error").toString();
                frag.setError(error);
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }
}