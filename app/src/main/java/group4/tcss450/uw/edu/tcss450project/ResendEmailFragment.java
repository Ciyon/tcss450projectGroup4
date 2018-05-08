package group4.tcss450.uw.edu.tcss450project;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import group4.tcss450.uw.edu.tcss450project.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResendEmailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ResendEmailFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    public ResendEmailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_resend_email, container, false);
        Button b = v.findViewById(R.id.sendButton);
        b.setOnClickListener(this);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onClick(View v)
    {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.sendButton:
                    resendEmail();
                    break;
            }
        }
    }

    private void resendEmail()
    {
        EditText email = getActivity().findViewById(R.id.emailResend);
        boolean valid = true;

        if(email.getText().toString().isEmpty()) {
            valid = false;
            email.setError("Empty Field!");
        }

        if(valid) {
            mListener.onSendClicked(email.getText().toString());
        }
    }

    public void setError(String err) {
        TextView email = getView().findViewById(R.id.emailResend);
        email.setError(err);
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
        // TODO: Update argument type and name
        void onSendClicked(String email);
    }
}
