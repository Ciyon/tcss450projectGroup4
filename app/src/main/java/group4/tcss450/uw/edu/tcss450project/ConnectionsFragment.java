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
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.ConnectionsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment implements ConnectionsAdapter.OnConnectionAdapterInteractionListener {

    private ConversationsFragment.OnConversationViewInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ConnectionsAdapter mAdapter;
    private ArrayList<Connection> mDataSet;
    private String mUsername;
    private String mSendUrl;
    private String mDeleteUrl;
    private String mCreateUrl;
    private String mAddMembersUrl;
    private String mContactName;
    private int mDeletePosition;
    private int mNewChatId;
    private int mMemberId;


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

        mRecyclerView = view.findViewById(R.id.connectionsList);

        // size should stay the same regardless of data
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Make an empty list to hold the data
        mDataSet = new ArrayList<>();
        mAdapter = new ConnectionsAdapter(mDataSet, this);

        setUpRequest();
        requestConnections();
        mRecyclerView.setAdapter(mAdapter);

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
        Log.e("Connections ERROR!!!", msg.toString());
    }

    private void createConnectionsList(final String result) {
        try {
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
                    mDataSet.addAll(connections);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionDeleted(String contactUsername, int position) {
        mDeletePosition = position;
        JSONObject messageJson = new JSONObject();

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
    public void onChatStarted(String contactUsername) {
        JSONObject messageJson = new JSONObject();
        mContactName = contactUsername;
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
            messageJson.put(key, mContactName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mAddMembersUrl, messageJson)
                .onPostExecute(this::handleAddMembersOnPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleAddMembersOnPost(final String result) {
        mListener.onConversationSelected(mNewChatId);
    }

    private void deleteConnection(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mDataSet.remove(mDeletePosition);
                mAdapter.notifyItemRemoved(mDeletePosition);
                mAdapter.notifyItemRangeChanged(mDeletePosition, mDataSet.size() - mDeletePosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
