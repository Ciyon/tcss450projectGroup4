package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import group4.tcss450.uw.edu.tcss450project.model.Credentials;


/**
 * {@link android.support.v4.app.Fragment} handles register functionality
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

    /**
     * On click for the register submit button
     *
     * @param v the button
     */
    @Override
    public void onClick(View v) {
        // Get the text from the edit text fields
        EditText username = Objects.requireNonNull(getActivity()).findViewById(R.id.usernameRegister);
        EditText password = getActivity().findViewById(R.id.password);
        EditText password2 = getActivity().findViewById(R.id.confirmPassword);
        EditText email = getActivity().findViewById(R.id.emailRegister);
        EditText firstName = getActivity().findViewById(R.id.firstName);
        EditText lastName = getActivity().findViewById(R.id.lastName);

        boolean unValid = true;
        boolean pw1Valid = true;
        boolean pw2Valid = true;
        boolean emailValid = true;
        boolean firstNameValid = true;
        boolean lastNameValid = true;

        //Check and throw errors for username edittext
        if (username.getText().toString().isEmpty()) {
            unValid = false;
            username.setError("Empty Field!");
        }

        //Check and throw errors for password edittext
        if (password.getText().toString().isEmpty()) {
            pw1Valid = false;
            password.setError("Empty Field!");
        } else if (password.getText().toString().length() > 0 && password.getText().toString().length() < 5) {
            pw1Valid = false;
            password.setError("Password must be at least 5 characters");
        }


        //Check and throw errors for password conformation edittext
        if (password2.getText().toString().isEmpty()) {
            pw2Valid = false;
            password2.setError("Empty Field!");
        } else if (pw2Valid) {
            String confError = "";
            if (!password2.getText().toString().equals(password.getText().toString())) {
                pw2Valid = false;
                confError += "Passwords do not match!";
            }
            if (password2.getText().toString().length() < 5) {
                pw2Valid = false;
                if (!confError.isEmpty()) {
                    confError += "\n";
                }
                confError += "Password must be at least 5 characters";
            }
            if (!pw2Valid) {
                password2.setError(confError);
            }
        }


        //Check and throw errors for email edittext
        String e = email.getText().toString();
        if (e.isEmpty()) {
            emailValid = false;
            email.setError("Empty Field!");
        } else if (!e.contains("@")) {
            email.setError("Invalid email address");
        }

        //Check and throw errors for name fields
        if (firstName.getText().toString().isEmpty()) {
            firstNameValid = false;
            firstName.setError("Empty Field!");
        }
        if (lastName.getText().toString().isEmpty()) {
            lastNameValid = false;
            lastName.setError("Empty Field!");
        }

        if (unValid && pw1Valid && pw2Valid && emailValid && firstNameValid && lastNameValid) {
            Credentials c = new Credentials.Builder(username.getText().toString(), password.getText())
                    .addEmail(email.getText().toString())
                    .addFirstName(firstName.getText().toString())
                    .addLastName(lastName.getText().toString())
                    .build();
            mListener.onRegisterAttempt(c);
        }
    }

    /**
     * Notify the user of any errors in the their input
     *
     * @param error the error message
     */
    public void setError(String error) {
        TextView username = Objects.requireNonNull(getView()).findViewById(R.id.usernameRegister);
        TextView email = getView().findViewById(R.id.emailRegister);
        username.setError(error);
        email.setError(error);
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

