package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import group4.tcss450.uw.edu.tcss450project.model.Credentials;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private RegisterFragment.OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Button b = v.findViewById(R.id.submitButton);
        b.setOnClickListener(this); //add this Fragment Object as the OnClickListener

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterFragment.OnFragmentInteractionListener) {
            mListener = (RegisterFragment.OnFragmentInteractionListener) context;
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
    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.submitButton:
                    EditText usernameEditText = getView().findViewById(R.id.usernameRegister);
                    EditText passwordEditText = getView().findViewById(R.id.password);
                    EditText confirmPasswordEditText = getView().findViewById(R.id.confirmPassword);
                    EditText firstNameEditText = getView().findViewById(R.id.firstName);
                    EditText lastNameEditText = getView().findViewById(R.id.lastName);
                    EditText emailEditText = getView().findViewById(R.id.emailRegister);

                    String username = usernameEditText.getText().toString();
                    Editable password = passwordEditText.getEditableText();
                    Editable confirmPassword = confirmPasswordEditText.getEditableText();
                    String firstname = firstNameEditText.getText().toString();
                    String lastname = lastNameEditText.getText().toString();
                    String email = emailEditText.getText().toString();

                    boolean passes = true;

                    String empty = "Empty field ";
                    String match = "Passwords don't match ";
                    String length = "Password must be at least 5 characters";

                    String usernameErrorMessage = "";
                    String passwordErrorMessage = "";
                    String confirmPasswordErrorMessage = "";

                    if (username.isEmpty()) {
                        usernameErrorMessage += empty;
                        passes = false;
                    }

                    if (password.toString().isEmpty()) {
                        passwordErrorMessage += empty;
                        passes = false;
                    }

                    if (confirmPassword.toString().isEmpty()) {
                        confirmPasswordErrorMessage += empty;
                        passes = false;
                    }

                    if (password.length() < 5) {
                        passwordErrorMessage += length;
                        passes = false;
                    }

                    if (!password.toString().equals(confirmPassword.toString())) {
                        confirmPasswordErrorMessage += match;
                        passes = false;
                    }

                    if (!usernameErrorMessage.isEmpty()) {
                        usernameEditText.setError(usernameErrorMessage);
                    }

                    if (!passwordErrorMessage.isEmpty()) {
                        passwordEditText.setError(passwordErrorMessage);
                    }

                    if (!confirmPasswordErrorMessage.isEmpty()) {
                        confirmPasswordEditText.setError(confirmPasswordErrorMessage);
                    }

                    if (passes) {
                        Credentials credentials = new Credentials
                                .Builder(username, password)
                                .addFirstName(firstname)
                                .addLastName(lastname)
                                .addEmail(email)
                                .build();
                        mListener.onRegisterAttempt(credentials);
                    }
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }
    public void setError(String error) {
        TextView tv = getView().findViewById(R.id.usernameRegister);
        if (error.contains("email"))
        {
            tv = getView().findViewById(R.id.emailRegister);
        }
        tv.setError("Login Unsuccessful: " + error);
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
        void onRegisterAttempt(Credentials credentials);
    }
}

