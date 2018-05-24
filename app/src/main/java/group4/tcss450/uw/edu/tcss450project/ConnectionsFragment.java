package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.ConnectionsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment implements
        ConnectionsAdapter.OnConnectionAdapterInteractionListener,
        View.OnClickListener{

    private ConversationsFragment.OnConversationViewInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connections, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        mSearchDataSet = new ArrayList<>();

        mSearchAdapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        mSearchDataSet);
        mSearchText = (AutoCompleteTextView) view.findViewById(R.id.connectionsSearchText);
        mSearchText.setAdapter(mSearchAdapter);
        mSearchText.setThreshold(2);

        mSearchButton = view.findViewById(R.id.searchConnectionsButton);
        mSearchButton.setOnClickListener(this);
        mSearchButton.setEnabled(false);

        mProgressBar = view.findViewById(R.id.progressBarConnections);

        mRecyclerView = view.findViewById(R.id.connectionsList);

        // size should stay the same regardless of data
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Make an empty list to hold the data
        mMasterDataSet = new ArrayList<>();
        mDisplayDataSet = new ArrayList<>();
        mAdapter = new ConnectionsAdapter(mDisplayDataSet, this);

        setUpRequest();
        requestConnections();
        mRecyclerView.setAdapter(mAdapter);

        return view;

    }

    @Override
    public void onStart()
    {
        super.onStart();
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

    private void setUpRequest() {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
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

    private void handleError(final String msg) {
        mProgressBar.setVisibility(View.GONE);
        Log.e("Connections ERROR!!!", msg.toString());
    }

    private void createConnectionsList(final String result) {
        try {
            mProgressBar.setVisibility(View.GONE);
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ArrayList<Connection> connections = new ArrayList<>();

                if(res.has(getString(R.string.keys_json_result))){
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

    private void populateSearchAdapter() {
        for(int j = 0; j < mMasterDataSet.size(); j++) {
            String searchValue = mMasterDataSet.get(j).getUserName();
            if(!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
            searchValue = mMasterDataSet.get(j).getFirstName();

            if(!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
            searchValue = mMasterDataSet.get(j).getLastName();
            if(!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
            searchValue = mMasterDataSet.get(j).getEmail();
            if(!mSearchDataSet.contains(searchValue)) {
                mSearchDataSet.add(searchValue);
            }
        }
        Collections.sort(mSearchDataSet, String.CASE_INSENSITIVE_ORDER);
        mSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionDeleted(String contactUsername, int position) {
        mDeletePosition = position;
        JSONObject messageJson = new JSONObject();
        mSearchButton.setEnabled(false);

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_contactname), contactUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mDeleteUrl, messageJson)
                .onPostExecute(this::deleteConnection)
                .onCancelled(this::handleError)
                .build().execute();

    }

    @Override
    public void onChatStarted(int contactId) {
        JSONObject messageJson = new JSONObject();
        mContactId = contactId;
        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mCreateUrl, messageJson)
                .onPostExecute(this::handleCreateConversationOnPost)
                .onCancelled(this::handleServiceError)
                .build().execute();
    }

    private void handleServiceError(final String result) {
        Log.d("Fail", result);
    }

    private void handleCreateConversationOnPost(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                if(res.has(getString(R.string.keys_json_chatid))){
                    mNewChatId = res.getInt(getString(R.string.keys_json_chatid));
                    addChatMembers();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addChatMembers() {
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_chat_id), mNewChatId);
            //add in your member id
            String baseKey = getString(R.string.keys_json_member_id_caps);
            String key =  baseKey + 1;
            messageJson.put(key, mMemberId);

            key = baseKey + 2;
            messageJson.put(key, mContactId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mAddMembersUrl, messageJson)
                .onPostExecute(this::handleAddMembersOnPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleAddMembersOnPost(final String result) {
        Log.d("dssd", result);
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mListener.onConversationSelected(mNewChatId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteConnection(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
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

    @Override
    public void onClick(View v) {
        //User attempting to search
        if(mSearchButton.getText().toString().equals(getString(R.string.button_search))) {
            if(!mSearchText.getText().toString().equals("")) {
                Log.d("dssdf", "made it");
                mSearchButton.setEnabled(false);
                mSearchText.setEnabled(false);
                String text = mSearchText.getText().toString();
                text = text.toLowerCase();
                mDisplayDataSet.clear();

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
                if(mDisplayDataSet.size() == 0) {
                    mSearchText.setText("No Results Found");
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
