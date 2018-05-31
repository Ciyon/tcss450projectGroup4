package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.PendingRequestsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;
import group4.tcss450.uw.edu.tcss450project.utils.SentRequestsAdapter;


/**
 * {@link Fragment} handles request functionality
 */
public class RequestsFragment extends Fragment implements PendingRequestsAdapter.OnPendingRequestsAdapterInteractionListener,
        View.OnClickListener {
    private PendingRequestsAdapter mPendingAdapter;
    private ArrayList<Connection> mPendingDataSet;

    private SentRequestsAdapter mSentAdapter;
    private ArrayList<Connection> mSentDataSet;
    private ArrayAdapter<String> mSearchAdapter;
    private List<String> mSearchDataSet;

    private int mPendingAcceptPosition;
    private int mPendingDeletePosition;
    private int mSentDeletePosition;

    /*
     * UI Components
     */
    private String mUsername;
    private int mMemberId;
    private String mGetReceivedUrl;
    private String mGetSentUrl;
    private String mAcceptUrl;
    private String mDeleteUrl;
    private String mSendRequestUrl;
    private String mGetUnconnectedUrl;
    private AutoCompleteTextView mConnectionSearchText;
    private Button mRequestConnectionButton;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        // Initialize data set and ui components
        mSearchDataSet = new ArrayList<>();
        mSearchAdapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        mSearchDataSet);
        mConnectionSearchText = view.findViewById(R.id.searchContactEdit);
        mConnectionSearchText.setAdapter(mSearchAdapter);
        mConnectionSearchText.setThreshold(4);

        mRequestConnectionButton = view.findViewById(R.id.sendRequestButton);
        mRequestConnectionButton.setOnClickListener(this);
        mRequestConnectionButton.setEnabled(false);

        // Set up urls
        setUpRequests();
        requestUnconnectedMembers();

        // Initialize recycler views
        RecyclerView mPendingRecyclerView = view.findViewById(R.id.recyclerPendingConnections);
        // size should stay the same regardless of data
        LinearLayoutManager mPendingLayoutManager = new LinearLayoutManager(this.getContext());
        mPendingRecyclerView.setLayoutManager(mPendingLayoutManager);

        // Make an empty list to hold the data
        mPendingDataSet = new ArrayList<>();
        mPendingAdapter = new PendingRequestsAdapter(mPendingDataSet, this);


        requestPendingConnections();

        mPendingRecyclerView.setAdapter(mPendingAdapter);

        RecyclerView mSentRecyclerView = view.findViewById(R.id.recyclerSentConnections);
        // size should stay the same regardless of data
        LinearLayoutManager mSentLayoutManager = new LinearLayoutManager(this.getContext());
        mSentRecyclerView.setLayoutManager(mSentLayoutManager);

        // Make an empty list to hold the data
        mSentDataSet = new ArrayList<>();
        mSentAdapter = new SentRequestsAdapter(mSentDataSet, this);

        requestSentConnections();
        mSentRecyclerView.setAdapter(mSentAdapter);

        return view;
    }

    /**
     * Starts a {@link SendPostAsyncTask} to accept a connection request
     *
     * @param contactUsername The new connection's username
     * @param position        the position of the selection in the recycler view
     */
    @Override
    public void onConnectionAccepted(String contactUsername, int position) {
        mPendingAcceptPosition = position;
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_contactname), contactUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mAcceptUrl, messageJson)
                .onPostExecute(this::acceptConnectionOnPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /**
     * Starts a {@link SendPostAsyncTask} to deny a connection request
     *
     * @param contactUsername the connection's username
     * @param position        the selection's position in the recyclerview
     * @param fromPending     if the request was pending
     */
    @Override
    public void onConnectionDenied(String contactUsername, int position, boolean fromPending) {
        if (fromPending) {
            mPendingDeletePosition = position;
        } else {
            mSentDeletePosition = position;
        }
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_contactname), contactUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Start an async task to delete the connection request
        SendPostAsyncTask.Builder taskBuilder = new SendPostAsyncTask.Builder(mDeleteUrl, messageJson);

        // Handle differently if the request was from pending
        if (fromPending) {
            taskBuilder.onPostExecute(this::deleteReceivedRequest);
        } else {
            taskBuilder.onPostExecute(this::deleteSentRequest);
        }

        taskBuilder.onCancelled(this::handleError);
        SendPostAsyncTask task = taskBuilder.build();
        task.execute();
    }

    /**
     * When the webservice successfully deletes a received request, remove it
     * from datasets as well
     *
     * @param result JSON to parse
     */
    private void deleteReceivedRequest(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                // Remove from datasets
                mPendingDataSet.remove(mPendingDeletePosition);
                mPendingAdapter.notifyItemRemoved(mPendingDeletePosition);
                mPendingAdapter.notifyItemRangeChanged(mPendingDeletePosition, mPendingDataSet.size() - mPendingDeletePosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * When the webservice successfully deletes a sent request, remove it
     * from datasets as well
     *
     * @param result JSON to parse
     */
    private void deleteSentRequest(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mSentDataSet.remove(mSentDeletePosition);
                mSentAdapter.notifyItemRemoved(mSentDeletePosition);
                mSentAdapter.notifyItemRangeChanged(mSentDeletePosition, mSentDataSet.size() - mSentDeletePosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up urls for requests to the webservice
     */
    private void setUpRequests() {
        // Get the user's prefs
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        if (!prefs.contains(getString(R.string.keys_prefs_user_id))) {
            throw new IllegalStateException("No user Id in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");
        mMemberId = prefs.getInt(getString(R.string.keys_prefs_user_id), 0);

        // Build the urls
        mGetReceivedUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_received_requests))
                .build()
                .toString();


        mGetSentUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_sent_requests))
                .build()
                .toString();

        mAcceptUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_accept_request))
                .build()
                .toString();


        mDeleteUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_delete_connection))
                .build()
                .toString();

        mSendRequestUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_add_connection))
                .build()
                .toString();

        mGetUnconnectedUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_unconnected))
                .build()
                .toString();

    }

    /**
     * Starts a {@link SendPostAsyncTask} to get the user's pending connections
     */
    private void requestPendingConnections() {
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mGetReceivedUrl, messageJson)
                .onPostExecute(this::createPendingConnectionsList)
                .onCancelled(this::handleError)
                .build().execute();

    }

    /**
     * Starts a {@link SendPostAsyncTask} to get user's the user is not connected to
     */
    private void requestUnconnectedMembers() {
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_member_id_caps), mMemberId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mGetUnconnectedUrl, messageJson)
                .onPostExecute(this::populateSearchView)
                .onCancelled(this::handleError)
                .build().execute();

    }

    /**
     * Displays results of a search
     *
     * @param result JSON to parse
     */
    private void populateSearchView(final String result) {
        Log.d("testing", Integer.toString(mMemberId));
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                ArrayList<String> connections = new ArrayList<>();

                if (res.has(getString(R.string.keys_json_result))) {
                    JSONArray members = res.getJSONArray(getString(R.string.keys_json_result));
                    Log.d("testing", Integer.toString(members.length()));
                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        //there should be checks here to make sure the object actually has these
                        String uName = member.getString(getString(R.string.keys_json_username));
                        connections.add(uName);
                    }
                    mSearchDataSet.sort(String.CASE_INSENSITIVE_ORDER);
                    mSearchDataSet.addAll(connections);
                    mSearchAdapter.notifyDataSetChanged();

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRequestConnectionButton.setEnabled(true);
    }

    /**
     * Starts a {@link SendPostAsyncTask} to get the user's sent connection requests
     */
    private void requestSentConnections() {
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mGetSentUrl, messageJson)
                .onPostExecute(this::createSentConnectionsList)
                .onCancelled(this::handleError)
                .build().execute();

    }

    private void handleError(final String msg) {
        Log.e("Requests ERROR!!!", msg);
    }

    /**
     * When a connection has successfully been accepted by the webservice,
     * update the datasets
     *
     * @param result JSON to parse
     */
    private void acceptConnectionOnPost(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mPendingDataSet.remove(mPendingAcceptPosition);
                mPendingAdapter.notifyItemRemoved(mPendingAcceptPosition);
                mPendingAdapter.notifyItemRangeChanged(mPendingAcceptPosition,
                        mPendingDataSet.size() - mPendingAcceptPosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the pending connections list received from the webservice
     *
     * @param result JSON to parse
     */
    private void createPendingConnectionsList(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ArrayList<Connection> connections = new ArrayList<>();

                if (res.has(getString(R.string.keys_json_requests))) {
                    JSONArray members = res.getJSONArray(getString(R.string.keys_json_requests));

                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        //there should be checks here to make sure the object actually has these
                        String uName = member.getString(getString(R.string.keys_json_username));
                        String fName = "";
                        String lName = "";
                        String email = "";
                        int id = -1;
                        connections.add(new Connection(uName, fName, lName, email, id));
                    }
                    //Update the recycler view
                    mPendingDataSet.clear();
                    mPendingDataSet.addAll(connections);
                    mPendingAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the sent connections list received from the webservice
     *
     * @param result JSON to parse
     */
    private void createSentConnectionsList(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ArrayList<Connection> connections = new ArrayList<>();

                if (res.has(getString(R.string.keys_json_requests))) {
                    JSONArray members = res.getJSONArray(getString(R.string.keys_json_requests));

                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        //there should be checks here to make sure the object actually has these
                        String uName = member.getString(getString(R.string.keys_json_username));
                        String fName = "";
                        String lName = "";
                        String email = "";
                        int id = -1;
                        connections.add(new Connection(uName, fName, lName, email, id));
                    }
                    //Update the recycler view
                    //remove any items in it
                    mSentDataSet.clear();
                    mSentDataSet.addAll(connections);
                    mSentAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * On click for the send request button
     *
     * @param v the button
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendRequestButton) {
            // Get the contact's username
            String contactUsername = mConnectionSearchText.getText().toString();

            if (!contactUsername.isEmpty()) {
                mRequestConnectionButton.setEnabled(false);
                JSONObject messageJson = new JSONObject();

                try {
                    messageJson.put(getString(R.string.keys_json_username), mUsername);
                    messageJson.put(getString(R.string.keys_json_contactname), contactUsername);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Start the async task to send a new request
                new SendPostAsyncTask.Builder(mSendRequestUrl, messageJson)
                        .onPostExecute(this::acceptSentRequestOnPost)
                        .onCancelled(this::handleSendRequestError)
                        .build().execute();
            } else {
                mConnectionSearchText.setError("Empty Field");
            }
        }
    }

    /**
     * Handle a failed request
     *
     * @param msg Error message
     */
    private void handleSendRequestError(final String msg) {
        mRequestConnectionButton.setEnabled(true);
        mConnectionSearchText.setError("Request Failed");
        Log.e("Requests ERROR!!!", msg);
    }

    /**
     * When the webservice successfully processes an accepted request, process
     * Otherwise, notify the user of the error
     *
     * @param result JSON to parse
     */
    private void acceptSentRequestOnPost(final String result) {
        mRequestConnectionButton.setEnabled(true);
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                // Clear the search text
                mConnectionSearchText.setText("");
                //Reload the sent Connections List
                requestSentConnections();
                requestUnconnectedMembers();
            } else {
                String error = res.get("error").toString();
                if (error.equals("Connection already exists.")) {
                    mConnectionSearchText.setError(error);
                } else {
                    mConnectionSearchText.setError("User Not Found");
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
