package group4.tcss450.uw.edu.tcss450project;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;


/**
 * {@link Fragment} that handles account options available prior to login.
 *
 * Activities that contain this fragment must implement the
 * {@link AccountOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AccountOptionsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public AccountOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_options, container, false);

        // Initialize buttons and set on click listeners
        Button b = v.findViewById(R.id.resendConfirmationButton);
        b.setOnClickListener(this);
        b = v.findViewById(R.id.resetPwEmailButton);
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

    /**
     * On click method for account options buttons
     *
     * @param v view/ button clicked
     */
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            // Depending on which button was clicked, perform the right action
            switch (v.getId()) {
                case R.id.resendConfirmationButton:
                    resendEmail();
                    break;
                case R.id.resetPwEmailButton:
                    sendResetCode();
                    break;
                case R.id.enterCodeButton:
                    mListener.onPasswordCodeSubmit();
                    break;
            }
        }
    }

    /**
     * Resends a confirmation email after verification of user information.
     */
    private void resendEmail() {
        EditText email = Objects.requireNonNull(getActivity()).findViewById(R.id.emailResend);
        boolean valid = true;

        // Check user information
        if (email.getText().toString().isEmpty()) {
            valid = false;
            email.setError("Empty Field!");
        } else if (!email.getText().toString().contains("@")) {
            valid = false;
            email.setError("Email is invalid.");
        }

        // Resend the e-mail (the login activity does this)
        if (valid) {
            mListener.onResendConfirmationClick(email.getText().toString());
        }
    }

    /**
     * Sends the user a code allowing them to reset their password
     * (the login activity does this)
     */
    private void sendResetCode() {
        EditText email = Objects.requireNonNull(getActivity()).
                findViewById(R.id.emailReset);
        boolean valid = true;

        // Check user information
        if (email.getText().toString().isEmpty()) {
            valid = false;
            email.setError("Empty Field!");
        } else if (!email.getText().toString().contains("@")) {
            valid = false;
            email.setError("Email is invalid.");
        }

        // Send the e-mail
        if (valid) {
            mListener.onSendResetCode(email.getText().toString());
        }
    }

    /**
     * Notifies a user if their given information doesn't pass client side checks.
     *
     * @param err The error
     */
    public void setError(String err) {
        TextView resendEmail = Objects.requireNonNull(getView()).findViewById(R.id.emailResend);
        TextView resetEmail = getView().findViewById(R.id.emailReset);
        if (err.contains("Email doesn't exist.") || err.contains("Email already verified.")) {
            resendEmail.setError(err);
        } else if (err.contains("Email must be confirmed in order to reset password.") || err.contains("Email doesn't belong to any account registered.")) {
            resetEmail.setError(err);
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
        void onResendConfirmationClick(String email);

        void onSendResetCode(String email);

        void onPasswordCodeSubmit();
    }
}
