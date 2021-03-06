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
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.NewConversationAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;

/**
 * {@link Fragment} that handles new conversation functionality
 */
public class NewConversationFragment extends Fragment implements View.OnClickListener, NewConversationAdapter.OnConnectionSelectedInteractionListener {
    private ConversationsFragment.OnConversationViewInteractionListener mListener;

    /**
     * Recycler view and data to display connections a user can start a conversation with
     */
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Connection> mDataSet;
    private ArrayList<Connection> mSelectedDataSet;
    private ArrayList<Integer> mSelectedItems;

    /**
     * User's information
     */
    private String mUsername;
    private int mMemberId;


    private String mGetConnectionsUrl;
    private String mAddMembersUrl;
    private int mNewChatId;

    public NewConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_conversation, container, false);
        //Hide the FAB
        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        //This may need to be tweaked
        Button mLoadConversationButton = view.findViewById(R.id.createConversationButton);
        mLoadConversationButton.setOnClickListener(this); //add this Fragment Object as the OnClickListener
        mRecyclerView = view.findViewById(R.id.recyclerSelectConnections);
        mRecyclerView.setHasFixedSize(true);


        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDataSet = new ArrayList<>();
        mSelectedDataSet = new ArrayList<>();
        mSelectedItems = new ArrayList<>();

        // specify an adapter (see also next example)
        mAdapter = new NewConversationAdapter(mDataSet, this);

        setUpRequest();
        requestConnections();

        return view;
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
     * On click starts an async task to start a new conversation
     *
     * @param v the button
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.createConversationButton) {
            //Get all selected contacts
            ArrayList<Connection> selectedContacts = new ArrayList<>();
            for (Integer i : mSelectedItems) {
                selectedContacts.add(mDataSet.get(i));
            }
            mSelectedDataSet.addAll(selectedContacts);

            //build the web service URL
            String sendUrl = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_create_chat))
                    .build()
                    .toString();

            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put(getString(R.string.keys_json_username), mUsername);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Start the async task
            new SendPostAsyncTask.Builder(sendUrl, messageJson)
                    .onPostExecute(this::handleCreateConversationOnPost)
                    .onCancelled(this::handleServiceError)
                    .build().execute();
        }

    }


    private void handleServiceError(final String result) {
        Log.d("Load Fail", result);
    }

    /**
     * Handles result of creating a new conversation
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
                    Log.d("SUCCESS", Integer.toString(mNewChatId));
                    addChatMembers();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a {@link SendPostAsyncTask} to add members to a conversation
     */
    private void addChatMembers() {
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_chat_id), mNewChatId);
            int count = 1;
            //add in your member id
            String baseKey = getString(R.string.keys_json_member_id_caps);
            String key = baseKey + count;
            messageJson.put(key, mMemberId);
            count++;
            for (Connection c : mSelectedDataSet) {
                key = baseKey + count;
                messageJson.put(key, c.getId());
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mAddMembersUrl, messageJson)
                .onPostExecute(this::handleAddMembersOnPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    /**
     * Handle the result of adding members
     *
     * @param result JSON message
     */
    private void handleAddMembersOnPost(final String result) {
        // mNewChatId was prevously saved
        mListener.onConversationSelected(mNewChatId);
    }

    /**
     * Set up urls to make requests to the webservice
     */
    private void setUpRequest() {
        // Get the user's username and memberid
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

        mGetConnectionsUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_connections))
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
     * Start a {@link SendPostAsyncTask} to get the user's connections
     */
    private void requestConnections() {
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mGetConnectionsUrl, messageJson)
                .onPostExecute(this::createConnectionsList)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleError(final String msg) {
        Log.e("Connections ERROR!!!", msg);
    }

    /**
     * Make the list of connections a user can make a new conversation with
     *
     * @param result JSON to parse
     */
    private void createConnectionsList(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if (res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ArrayList<Connection> connections = new ArrayList<>();

                if (res.has(getString(R.string.keys_json_result))) {
                    JSONArray members = res.getJSONArray(getString(R.string.keys_json_result));

                    // Parse each entry in the connections list
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
                    mDataSet.addAll(connections);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listener for the connections recyclerview
     *
     * @param selectedItems item that was selected
     */
    @Override
    public void onConnectionSelected(ArrayList<Integer> selectedItems) {
        mSelectedItems = selectedItems;
    }

}
