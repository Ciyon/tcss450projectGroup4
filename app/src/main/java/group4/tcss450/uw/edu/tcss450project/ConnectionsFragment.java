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
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.ConnectionsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * {@link Fragment} that handles connections functionality.
 */
public class ConnectionsFragment extends Fragment implements
        ConnectionsAdapter.OnConnectionAdapterInteractionListener,
        View.OnClickListener {

    private ConversationsFragment.OnConversationViewInteractionListener mListener;
    private ConnectionsAdapter mAdapter;
    private ArrayAdapter<String> mSearchAdapter;
    private ArrayList<Connection> mMasterDataSet;
    private ArrayList<Connection> mDisplayDataSet;
    private List<String> mSearchDataSet;
    private String mUsername;
    private String mSendUrl;
    private String mDeleteUrl;
    private String mCreateUrl;
    private String mAddMembersUrl;
    private int mContactId;
    private int mDeletePosition;
    private int mNewChatId;
    private int mMemberId;
    private ProgressBar mProgressBar;
    private Button mSearchButton;
    private AutoCompleteTextView mSearchText;

    public ConnectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connections, container, false);
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        // Data set for recycler view for searching for connections
        mSearchDataSet = new ArrayList<>();

        // Initialize UI components
        mSearchAdapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        mSearchDataSet);
        mSearchText = view.findViewById(R.id.connectionsSearchText);
        mSearchText.setAdapter(mSearchAdapter);
        mSearchText.setThreshold(2);
        mSearchButton = view.findViewById(R.id.searchConnectionsButton);
        mSearchButton.setOnClickListener(this);
        mSearchButton.setEnabled(false);
        mProgressBar = view.findViewById(R.id.progressBarConnections);

        // Initialize recycler view
        RecyclerView mRecyclerView = view.findViewById(R.id.connectionsList);

        // Size should stay the same regardless of data
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Make an empty list to hold the data
        mMasterDataSet = new ArrayList<>();
        mDisplayDataSet = new ArrayList<>();
        mAdapter = new ConnectionsAdapter(mDisplayDataSet, this);

        // Set up all of the data
        setUpRequestUrls();
        requestConnections();
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // We shouldn't see the progress bar initially
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ConversationsFragment.OnConversationViewInteractionListener) {
            mListener = (ConversationsFragment.OnConversationViewInteractionListener) context;
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

    /**
     * Sets up parameters/urls for making a connection request,
     * removing a connection, creating a new chat with a connection,
     * and adding multiple connections to a chat.
     */
    private void setUpRequestUrls() {
        // Get the user's shared preferences
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
        // Get their username and memberid
        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");
        mMemberId = prefs.getInt(getString(R.string.keys_prefs_user_id), 0);

        // Prepare send urls
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_connections))
                .build()
                .toString();

        mDeleteUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_delete_connection))
                .build()
                .toString();

        mCreateUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_create_chat))
                .build()
                .toString();

        mAddMembersUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_add_all_to_chat))
                .build()
                .toString();

    }

    /**
     * Starts an AsyncTask to request a new connection.
     */
    private void requestConnections() {
        mProgressBar.setVisibility(View.VISIBLE);
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::createConnectionsList)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /**
     * Gets rid of progress bar if there is an error.
     *
     * @param msg error message
     */
    private void handleError(final String msg) {
        mProgressBar.setVisibility(View.GONE);
        Log.e("Connections ERROR!!!", msg);
    }

    /**
     * Creates a list of connects from JSON result
     *
     * @param result JSON result
     */
    private void createConnectionsList(final String result) {
        try {
            mProgressBar.setVisibility(View.GONE);
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ArrayList<Connection> connections = new ArrayList<>();

                if (res.has(getString(R.string.keys_json_result))) {
                    JSONArray members = res.getJSONArray(getString(R.string.keys_json_result));

                    for (int i = 0; i < members.length(); i++) {
                        JSONObject member = members.getJSONObject(i);
                        //there should be checks here to make sure the object actually has these
                        String uName = member.getString(getString(R.string.keys_json_username));
                        String fName = member.getString(getString(R.string.keys_json_firstname_long));
                        String lName = member.getString(getString(R.string.keys_json_lastname_long));
                        String email = member.getString(getString(R.string.keys_json_email));
                        int id = member.getInt(getString(R.string.keys_json_member_id));
                        connections.add(new Connection(uName, fName, lName, email, id));
                        Log.d("testing", connections.toString());
                    }
                    //Update the recycler view
                    mMasterDataSet.addAll(connections);
                    mDisplayDataSet.addAll(connections);
                    populateSearchAdapter();
                    mSearchButton.setEnabled(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populate the search data set, so that a user can see the
     * potential connections they're searching for.
     */
    private void populateSearchAdapter() {
        for (int j = 0; j < mMasterDataSet.size(); j++) {
            String searchValue = mMasterDataSet.get(j).getUserName();
            if (!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
            searchValue = mMasterDataSet.get(j).getFirstName();

            if (!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
            searchValue = mMasterDataSet.get(j).getLastName();
            if (!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
            searchValue = mMasterDataSet.get(j).getEmail();
            if (!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
        }

        mSearchDataSet.sort(String.CASE_INSENSITIVE_ORDER);
        mSearchAdapter.notifyDataSetChanged();
    }

    /**
     * Deletes a connection when it is selected from the recycler view for deletion.
     *
     * @param contactUsername The contact to be deleted.
     * @param position        The position in the recycler view.
     */
    @Override
    public void onConnectionDeleted(String contactUsername, int position) {
        mDeletePosition = position;

        // Build the JSON message to send to the webservice
        JSONObject messageJson = new JSONObject();
        mSearchButton.setEnabled(false);

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_contactname), contactUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Start the async task to remove the connection
        new SendPostAsyncTask.Builder(mDeleteUrl, messageJson)
                .onPostExecute(this::deleteConnection)
                .onCancelled(this::handleError)
                .build().execute();

    }

    /**
     * Starts an async task to create a new conversation with a connection.
     *
     * @param contactId Id of the connection to start a chat with
     */
    @Override
    public void onChatStarted(int contactId) {
        // Build the JSON message to send to the webservice
        JSONObject messageJson = new JSONObject();
        mContactId = contactId;
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Start the async task
        new SendPostAsyncTask.Builder(mCreateUrl, messageJson)
                .onPostExecute(this::handleCreateConversationOnPost)
                .onCancelled(this::handleServiceError)
                .build().execute();
    }

    private void handleServiceError(final String result) {
        Log.d("Fail", result);
    }

    /**
     * If a conversation is successfully created with connection,
     * handle the result.
     *
     * @param result JSON message to parse
     */
    private void handleCreateConversationOnPost(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                if (res.has(getString(R.string.keys_json_chatid))) {
                    mNewChatId = res.getInt(getString(R.string.keys_json_chatid));
                    addChatMembers();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add connections to a conversation.
     */
    private void addChatMembers() {
        // Build the JSON message to send to the webservice
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_chat_id), mNewChatId);
            //add in your member id
            String baseKey = getString(R.string.keys_json_member_id_caps);
            String key = baseKey + 1;
            messageJson.put(key, mMemberId);

            key = baseKey + 2;
            messageJson.put(key, mContactId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Start the asynctask
        new SendPostAsyncTask.Builder(mAddMembersUrl, messageJson)
                .onPostExecute(this::handleAddMembersOnPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /**
     * If connections are successfully added to a conversation,
     * handle the result.
     *
     * @param result JSON message to parse
     */
    private void handleAddMembersOnPost(final String result) {
        Log.d("dssd", result);
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mListener.onConversationSelected(mNewChatId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * If a connection was successfully delected from the database,
     * delete the connection from our datasets as well.
     *
     * @param result JSON message to parse
     */
    private void deleteConnection(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mMasterDataSet.remove(mDeletePosition);
                mDisplayDataSet.remove(mDeletePosition);
                mSearchDataSet.clear();
                populateSearchAdapter();
                mAdapter.notifyItemRemoved(mDeletePosition);
                mAdapter.notifyItemRangeChanged(mDeletePosition, mDisplayDataSet.size() - mDeletePosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSearchButton.setEnabled(true);
    }

    /**
     * On click for search and clear buttons.
     *
     * @param v the button
     */
    @Override
    public void onClick(View v) {
        //User attempting to search
        if (mSearchButton.getText().toString().equals(getString(R.string.button_search))) {
            if (!mSearchText.getText().toString().equals("")) {
                Log.d("dssdf", "made it");
                mSearchButton.setEnabled(false);
                mSearchText.setEnabled(false);
                String text = mSearchText.getText().toString();
                text = text.toLowerCase();
                mDisplayDataSet.clear();

                // Parse search results
                for (int i = 0; i < mMasterDataSet.size(); i++) {
                    Connection c = mMasterDataSet.get(i);
                    if (c.getUserName().toLowerCase().equals(text)
                            || c.getUserName().toLowerCase().startsWith(text)
                            || c.getFirstName().toLowerCase().equals(text)
                            || c.getFirstName().toLowerCase().startsWith(text)
                            || c.getLastName().toLowerCase().equals(text)
                            || c.getLastName().toLowerCase().startsWith(text)
                            || (c.getFirstName() + " " + c.getLastName()).toLowerCase().equals(text)
                            || c.getEmail().toLowerCase().equals(text)
                            || c.getEmail().toLowerCase().startsWith(text)) {
                        mDisplayDataSet.add(c);
                    }
                }

                mAdapter.notifyDataSetChanged();
                if (mDisplayDataSet.size() == 0) {
                    mSearchText.setText(R.string.no_results);
                }
                mSearchButton.setText(R.string.button_clear_results);
                mSearchButton.setEnabled(true);
            }
        } else { //User wants restore full contact list
            mSearchButton.setEnabled(false);
            mDisplayDataSet.clear();
            mDisplayDataSet.addAll(mMasterDataSet);
            mAdapter.notifyDataSetChanged();
            mSearchButton.setText(R.string.button_search);
            mSearchText.setText("");
            mSearchText.setEnabled(true);
            mSearchButton.setEnabled(true);
        }
    }
}
