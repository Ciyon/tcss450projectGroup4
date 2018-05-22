package group4.tcss450.uw.edu.tcss450project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group4.tcss450.uw.edu.tcss450project.model.Connection;
import group4.tcss450.uw.edu.tcss450project.utils.SendPostAsyncTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SettingsFragment.OnFragmentInteractionListener,
        ConversationsFragment.OnConversationViewInteractionListener{

    private String mUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setUserTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        if (!prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in),
                false)) {
            mUsername = getIntent().getStringExtra("username");
            prefs.edit().putString("username", mUsername)
                    .apply();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_chat_bubble);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new NewConversationFragment());
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> {
            onLogout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setContentView(R.layout.content_main);
        if(savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer2) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer2, new HomeFragment())
                        .commit();
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        setDrawerColor();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loadFragment(new SettingsFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragment(new HomeFragment());
        } else if (id == R.id.nav_connections) {
            loadFragment(new ConnectionsFragment());
        } else if (id == R.id.nav_conversations) {
            loadFragment(new ConversationsFragment());
        } else if (id == R.id.nav_weather) {
            loadFragment(new WeatherFragment());
        } else if (id == R.id.nav_requests) {
            loadFragment(new RequestsFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onLogout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //This should remove all saved user data, we will need to save the selected theme though
        prefs.edit().clear();
        //prefs.edit().remove(getString(R.string.keys_prefs_username));
        //prefs.edit().remove(R.string.keys_prefs_user_id);
        prefs.edit().putBoolean(
                getString(R.string.keys_prefs_stay_logged_in),
                false)
                .apply();
        //the way to close an app programmaticaly
        finishAndRemoveTask();
    }


    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer2, frag)
                            .addToBackStack(null);


        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void onSettingsUpdate(int choice) {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().putInt(
                getString(R.string.keys_prefs_theme),
                choice)
                .apply();

        //reload activity
        Intent intent = getIntent();
        finish();

        startActivity(intent);

    }

    private void setUserTheme() {

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        int theme = prefs.getInt(getString(R.string.keys_prefs_theme), 1);
        switch(theme) {
            case 1:
                setTheme(R.style.AppTheme_NoActionBar);
                break;
            case 2:
                setTheme(R.style.AppTheme_NoActionBar2);
                break;
            case 3:
                setTheme(R.style.AppTheme_NoActionBar3);
                break;
            default:
                setTheme(R.style.AppTheme_NoActionBar);
                break;
        }

    }

    private void setDrawerColor() {
        LinearLayout t = findViewById(R.id.actionBarStuff);
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        int theme = prefs.getInt(getString(R.string.keys_prefs_theme), 1);
        switch(theme) {
            case 2:
                t.setBackgroundColor(Color.rgb(216, 27, 96));
                break;
            case 3:
                t.setBackgroundColor(Color.rgb(38, 198, 218));
                break;
            default:
                break;
        }

    }

    @Override
    public void onConversationSelected(int conversationID) {
        ChatFragment chatFrag = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(getString(R.string.keys_args_conversationID), conversationID);
        chatFrag.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer2, chatFrag)
                .addToBackStack(null);


        // Commit the transaction
        transaction.commit();
    }
}
