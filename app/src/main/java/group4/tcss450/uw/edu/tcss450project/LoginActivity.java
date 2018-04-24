package group4.tcss450.uw.edu.tcss450project;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new LoginFragment(), "userlogin")
                        .commit();
            }
        }

    }

    @Override
    public void onFragmentInteraction(String username, String password) {
        // TODO: send login/user information via intent
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onFragmentInteraction() {
        RegisterFragment registerFragment;

        registerFragment = (RegisterFragment) getSupportFragmentManager().
                findFragmentByTag("register");

        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, registerFragment, "register")
                    .addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

    }
}