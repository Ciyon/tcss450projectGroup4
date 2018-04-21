package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


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
    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.loginButton:
                    EditText usernameEditText = getView().findViewById(R.id.usernameEdit);
                    EditText passwordEditText = getView().findViewById(R.id.passwordEdit);
                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    boolean passes = true;

                    String empty = "Empty field ";

                    String usernameErrorMessage = "";
                    String passwordErrorMessage = "";

                    if (username.isEmpty()) {
                        usernameErrorMessage += empty;
                        passes = false;
                    }

                    if (password.isEmpty()) {
                        passwordErrorMessage += empty;
                        passes = false;
                    }

                    if (!usernameErrorMessage.isEmpty()) {
                        usernameEditText.setError(usernameErrorMessage);
                    }

                    if (!passwordErrorMessage.isEmpty()) {
                        passwordEditText.setError(passwordErrorMessage);
                    }

                    if (passes == true) {
                        mListener.onFragmentInteraction(username, password);
                    }
                    break;
                case R.id.registerButton:
                    mListener.onFragmentInteraction();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String username, String password);

        void onFragmentInteraction();
    }
}
