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
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.ConnectionsAdapter;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ConnectionsAdapter mAdapter;
    private Connection[] mDataSet;
    private SharedPreferences mPref;
    private String mUsername;
    private String mSendUrl;

    public ConnectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connections, container, false);

        mRecyclerView = view.findViewById(R.id.connectionsList);

        // size should stay the same regardless of data
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mPref = getSharedPreferences();
        // TODO: populate the dataset with the user's contacts (change static method)
        mDataSet = createConnectionsList();

        mAdapter = new ConnectionsAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        /*
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");

        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_connections))
                .build()
                .toString();
        // Update the connections
       // mPref = getSharedPreferences();
        mDataSet = createConnectionsList();
        */
    }

    private SharedPreferences getSharedPreferences() {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        return prefs;
    }

    private void handleError(final String msg) {
        Log.e("Connections ERROR!!!", msg.toString());
    }
    //private Connection[] getCon
    private Connection[] createConnectionsList() {
        /*
        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
                */
        // Make a list of connections from JSONArray?
        Connection[] connections = new Connection[3];
        connections[0] = new Connection("use1", "The", "Boss");
        connections[1] = new Connection("user2","Billy", "Bob");
        connections[2] = new Connection("user3", "Bob", "Joe");
        return connections;
    }

}
