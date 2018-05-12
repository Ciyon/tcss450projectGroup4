package group4.tcss450.uw.edu.tcss450project;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.model.ConnectionsAdapter;
import group4.tcss450.uw.edu.tcss450project.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends Fragment {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    ConnectionsAdapter mAdapter;
    Connection[] mDataSet;

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

        // TODO: populate the dataset with the user's contacts
        mDataSet = new Connection[0];

        mAdapter = new ConnectionsAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        return view;

    }



}
