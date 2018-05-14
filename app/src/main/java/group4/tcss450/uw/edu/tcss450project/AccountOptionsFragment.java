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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AccountOptionsFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    public AccountOptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_options, container, false);
        Button b = v.findViewById(R.id.resendConfirmationButton);
        b.setOnClickListener(this);
        b = v.findViewById(R.id.resetPasswordButton);
        b.setOnClickListener(this);
        b = v.findViewById(R.id.enterCodeButton);
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
                case R.id.resendConfirmationButton:
                    resendEmail();
                    break;
                case R.id.resetPasswordButton:
                    break;
                case R.id.enterCodeButton:
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
