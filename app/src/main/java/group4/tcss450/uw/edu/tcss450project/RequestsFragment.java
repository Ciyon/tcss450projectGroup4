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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.ConnectionsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.PendingRequestsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;
import group4.tcss450.uw.edu.tcss450project.utils.SentRequestsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment implements PendingRequestsAdapter.OnPendingRequestsAdapterInteractionListener{
    private RecyclerView mPendingRecyclerView;
    private LinearLayoutManager mPendingLayoutManager;
    private PendingRequestsAdapter mPendingAdapter;
    private ArrayList<Connection> mPendingDataSet;

    private RecyclerView mSentRecyclerView;
    private LinearLayoutManager mSentLayoutManager;
    private SentRequestsAdapter mSentAdapter;
    private ArrayList<Connection> mSentDataSet;

    private String mUsername;
    private String mGetReceivedUrl;
    private String mGetSentUrl;
    private String mAcceptUrl;
    private String mDeleteUrl;

    private int mPendingAcceptPosition;
    private int mPendingDeletePosition;
    private int mSentDeletePosition;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        mPendingRecyclerView = view.findViewById(R.id.recyclerPendingConnections);
        // size should stay the same regardless of data
        mPendingLayoutManager = new LinearLayoutManager(this.getContext());
        mPendingRecyclerView.setLayoutManager(mPendingLayoutManager);

        // Make an empty list to hold the data
        mPendingDataSet = new ArrayList<>();
        mPendingAdapter = new PendingRequestsAdapter(mPendingDataSet, this);

        setUpRequests();
        requestPendingConnections();

        mPendingRecyclerView.setAdapter(mPendingAdapter);

        mSentRecyclerView = view.findViewById(R.id.recyclerSentConnections);
        // size should stay the same regardless of data
        mSentLayoutManager = new LinearLayoutManager(this.getContext());
        mSentRecyclerView.setLayoutManager(mSentLayoutManager);

        // Make an empty list to hold the data
        mSentDataSet = new ArrayList<>();
        mSentAdapter = new SentRequestsAdapter(mSentDataSet, this);

        requestSentConnections();
        mSentRecyclerView.setAdapter(mSentAdapter);

        return view;
    }

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

    @Override
    public void onConnectionDenied(String contactUsername, int position, boolean fromPending) {
        if(fromPending) {
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

        SendPostAsyncTask.Builder taskBuilder = new SendPostAsyncTask.Builder(mDeleteUrl, messageJson);

        if(fromPending) {
            taskBuilder.onPostExecute(this::deleteReceivedRequest);
        } else {
            taskBuilder.onPostExecute(this::deleteSentRequest);
        }

        taskBuilder.onCancelled(this::handleError);
        SendPostAsyncTask task = taskBuilder.build();
        task.execute();
    }

    private void deleteReceivedRequest(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mPendingDataSet.remove(mPendingDeletePosition);
                mPendingAdapter.notifyItemRemoved(mPendingDeletePosition);
                mPendingAdapter.notifyItemRangeChanged(mPendingDeletePosition, mPendingDataSet.size() - mPendingDeletePosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteSentRequest(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                mSentDataSet.remove(mSentDeletePosition);
                mSentAdapter.notifyItemRemoved(mSentDeletePosition);
                mSentAdapter.notifyItemRangeChanged(mSentDeletePosition, mSentDataSet.size() - mSentDeletePosition);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setUpRequests() {
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


    }

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
        Log.e("Requests ERROR!!!", msg.toString());
    }

    private void acceptConnectionOnPost(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
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
                    mPendingDataSet.addAll(connections);
                    mPendingAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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
                    mSentDataSet.addAll(connections);
                    mSentAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
