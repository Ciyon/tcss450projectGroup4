package group4.tcss450.uw.edu.tcss450project;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.Objects;


/**
 * {@link Fragment} handles user settings
 */
public class SettingsFragment extends Fragment {
    private SettingsFragment.OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        int theme = prefs.getInt(getString(R.string.keys_prefs_theme), 1);
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize UI components
        RadioButton rb = v.findViewById(R.id.radioTheme1);
        rb.setOnClickListener(this::onRadioColorClicked);

        if (theme == 1) rb.toggle();
        rb = v.findViewById(R.id.radioTheme2);
        rb.setOnClickListener(this::onRadioColorClicked);

        if (theme == 2) rb.toggle();
        rb = v.findViewById(R.id.radioTheme3);
        rb.setOnClickListener(this::onRadioColorClicked);
        if (theme == 3) rb.toggle();
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
     * Listener for layout radio buttons
     *
     * @param view the button
     */
    public void onRadioColorClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioTheme1:
                if (checked)
                    mListener.onSettingsUpdate(1);
                break;
            case R.id.radioTheme2:
                if (checked)
                    mListener.onSettingsUpdate(2);
                break;
            case R.id.radioTheme3:
                if (checked)
                    mListener.onSettingsUpdate(3);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics//communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSettingsUpdate(int choice);
    }
}
