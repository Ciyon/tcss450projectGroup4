package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.os.Bundle;
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
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button b = v.findViewById(R.id.loginButton);
        b.setOnClickListener(this); //add this Fragment Object as the OnClickListener

        b = v.findViewById(R.id.registerButton);
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
    public void onClick(View v) {

        if (mListener != null) {
            switch (v.getId()) {
                case R.id.loginButton:
                    login();
                    break;
                case R.id.registerButton:
                    mListener.onRegisterClicked();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }

    }

    //Performs the client side checks for login, will set errors for empty fields
    //and for any password < 5 characters, which cannot possibly be valid
    private void login() {
        EditText username = getActivity().findViewById(R.id.usernameLogin);
        EditText password = getActivity().findViewById(R.id.passwordEdit);
        String un = username.getText().toString();
        boolean valid = true;

        if(un.isEmpty()) {
            valid = false;
            username.setError("Empty Field!");
        }

        if(password.getText().toString().isEmpty()) {
            valid = false;
            password.setError("Empty Field!");
        }

        if(password.getText().toString().length() > 0 && password.getText().toString().length() < 5) {
            valid = false;
            password.setError("Invalid Password!");
        }

        if(valid) {
            Credentials c = new Credentials.Builder(un,password.getText())
                    .build();
            mListener.onLoginAttempt(c);
        }
    }

    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     *
     @param err
     the error message to display.
     */
    public void setError(String err) {
        TextView username = getView().findViewById(R.id.usernameLogin);
        TextView pass = getView().findViewById(R.id.passwordEdit);
        username.setError(null);
        pass.setError(null);
        if (err.contains("User hasn't confirmed email") || err.contains("Username not found.") || err.contains("Missing credentials"))
        {
            username.setError(err);
        }
        else if (err.contains("Incorrect password"))
        {
            pass.setError(err);
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
        void onLoginAttempt(Credentials credentials);
        void onRegisterClicked();
    }
}
