package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.ConnectionsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ConnectionsAdapter mAdapter;
    private Connection[] mDataSet;
    private SharedPreferences mPref;

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
        mDataSet = createConnectionsList(mPref);

        mAdapter = new ConnectionsAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Update the connections
        mPref = getSharedPreferences();
        mDataSet = createConnectionsList(mPref);
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


    public Connection[] createConnectionsList(SharedPreferences prefs) {
        //TODO: get the user's connections
        String userName;
        String sendUrl;
        userName = prefs.getString(getString(R.string.keys_prefs_username), "");
        sendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_send_message))
                .build()
                .toString();
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", "1")
                .build();
        return new Connection[0];
    }
}
