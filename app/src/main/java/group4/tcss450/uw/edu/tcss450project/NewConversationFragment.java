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

/**
 * A simple {@link Fragment} subclass.
 */
public class NewConversationFragment extends Fragment implements View.OnClickListener{
    private NewConversationFragment.OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Connection[] myDataset;

    public NewConversationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //just a place holder, we need to populate this
        //myDataset = new Connection[0];
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_conversation, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        Button b = view.findViewById(R.id.createConversationButton);
        b.setOnClickListener(this); //add this Fragment Object as the OnClickListener
        //mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerSelectConnections);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //mAdapter = new NewConversationAdapter(myDataset);
       // mRecyclerView.setAdapter(mAdapter);
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
            if(v.getId() == R.id.createConversationButton) {
                mListener.onFragmentInteraction();
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
