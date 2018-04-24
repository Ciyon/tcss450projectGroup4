package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationsFragment extends Fragment implements View.OnClickListener{
    private NewConversationFragment.OnFragmentInteractionListener mListener;

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
}
