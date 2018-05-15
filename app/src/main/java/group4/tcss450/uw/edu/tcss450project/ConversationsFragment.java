package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.model.Conversation;
import group4.tcss450.uw.edu.tcss450project.utils.ConversationsAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends Fragment implements View.OnClickListener{
    private NewConversationFragment.OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Conversation[] myDataset;

    public ConversationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        Button b = view.findViewById(R.id.loadConversationButton);
        b.setOnClickListener(this); //add this Fragment Object as the OnClickListener
        myDataset = createConversationsList();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerSelectConversations);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ConversationsAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NewConversationFragment.OnFragmentInteractionListener) {
            mListener = (NewConversationFragment.OnFragmentInteractionListener) context;
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
    @Override
    public void onClick(View v) {

        if (mListener != null) {
            if(v.getId() == R.id.loadConversationButton) {
                mListener.onFragmentInteraction();
            }
        }

    }

    public Conversation[] createConversationsList() {
        // Make a list of connections from JSONArray?
        Connection[] connections = new Connection[3];
        connections[0] = new Connection("use1", "The", "Boss");
        connections[1] = new Connection("user2","Billy", "Bob");
        connections[2] = new Connection("user3", "Bob", "Joe");

        Connection[] connections2 = new Connection[1];
        connections[0] = new Connection("use4", "Other", "Guy");

        Conversation[] conversations = new Conversation[2];
        conversations[0] = new Conversation(connections);
        conversations[1] = new Conversation(connections2);
        return conversations;
    }
}
