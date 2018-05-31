package group4.tcss450.uw.edu.tcss450project;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.Objects;


/**
 * {@link Fragment} handles resetting a user's password
 * <p>
 * Activities that contain this fragment must implement the
 * {@link ResetPasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ResetPasswordFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private ProgressBar mProgressBar;
    private Button mSubmitButton;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);

        // Initialize UI components
        mSubmitButton = v.findViewById(R.id.submitPasswordButton);
        mSubmitButton.setOnClickListener(this);

        mProgressBar = v.findViewById(R.id.progressBarReset);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mProgressBar.setVisibility(View.GONE);
        mSubmitButton.setEnabled(true);
    }

    /**
     * On click for submit button
     *
     * @param v the button
     */
    @Override
    public void onClick(View v) {
        if (mListener != null) {
            switch (v.getId()) {
                case R.id.submitPasswordButton:
                    resetPassword();
                    break;
            }
        }
    }

    /**
     * Performs client side checks and resets the user's password
     */
    private void resetPassword() {
        // User's credentials
        EditText email = Objects.requireNonNull(getActivity()).findViewById(R.id.emailResetPassword);
        EditText code = getActivity().findViewById(R.id.codeResetPassword);
        EditText pw1 = getActivity().findViewById(R.id.newPassword);
        EditText pw2 = getActivity().findViewById(R.id.reenterNewPassword);
        boolean valid = true;

        // Check for validity
        if (pw1.getText().toString().isEmpty() || pw2.getText().toString().isEmpty()) {
            valid = false;
            pw1.setError("Empty Field!");
            pw2.setError("Empty Field!");

        } else if (pw1.getText().toString().length() < 5 || pw2.getText().toString().length() < 5) {
            valid = false;
            pw1.setError("Password must be at least 5 characters.");
            pw2.setError("Password must be at least 5 characters.");
        }

        if (!pw1.getText().toString().equals(pw2.getText().toString())) {
            valid = false;
            pw1.setError("Passwords must be equal to each other.");
            pw2.setError("Passwords must be equal to each other.");
        }

        if (email.getText().toString().isEmpty()) {
            valid = false;
            email.setError("Empty Field!");
        } else if (!email.getText().toString().contains("@")) {
            valid = false;
            email.setError("Invalid email.");
        }

        if (code.getText().toString().length() != 6) {
            valid = false;
            code.setError("Code must be 6 characters.");
        }

        // If all the checks are passed, reset the password
        if (valid) {
            mSubmitButton.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            mListener.onSubmitPassword(pw1.getEditableText(), code.getText().toString(), email.getText().toString());
        }
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
     * Notifies the user of any errors in their input
     *
     * @param error the error message
     */
    public void setError(String error) {
        mSubmitButton.setEnabled(true);
        mProgressBar.setVisibility(View.GONE);
        EditText email = Objects.requireNonNull(getActivity()).findViewById(R.id.emailResetPassword);
        EditText code = getActivity().findViewById(R.id.codeResetPassword);
        EditText pw1 = getActivity().findViewById(R.id.newPassword);
        EditText pw2 = getActivity().findViewById(R.id.reenterNewPassword);

        if (error.toLowerCase().contains("email")) {
            email.setError(error);
        }
        if (error.toLowerCase().contains("code")) {
            code.setError(error);
        }
        if (error.toLowerCase().contains("password")) {
            pw1.setError(error);
            pw2.setError(error);
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
        // TODO: Update argument type and name
        void onSubmitPassword(Editable password, String code, String email);
    }
}
