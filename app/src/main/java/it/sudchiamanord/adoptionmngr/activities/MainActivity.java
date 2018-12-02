package it.sudchiamanord.adoptionmngr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

import it.sudchiamanord.adoptionmngr.R;
import it.sudchiamanord.adoptionmngr.util.Utils;
import it.sudchiamanord.adoptionmngr.util.scheleton.LifecycleLoggingActivity;
import it.sudchiamanord.adoptionmngr.util.Tags;

public class MainActivity extends LifecycleLoggingActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mIsLoggedIn = false;
    private String mUser;

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setTheme (android.R.style.Theme_Holo_Light_DarkActionBar);
        setContentView (R.layout.activity_main);

        if (savedInstanceState != null) {
            mIsLoggedIn = savedInstanceState.getBoolean (Tags.USER_LOGGED_IN);
            mUser = savedInstanceState.getString (Tags.CURRENT_USER);
        }

        Log.d (TAG, mIsLoggedIn ? "The user is logged in" : "The user is not logged in");
    }

//    @Override
//    protected void onRestoreInstanceState (Bundle savedInstanceState)
//    {
//        Log.d (TAG, "Inside onRestore");
//        if (savedInstanceState != null) {
//            mIsLoggedIn = savedInstanceState.getBoolean (Tags.USER_LOGGED_IN);
//            mUser = savedInstanceState.getString (Tags.CURRENT_USER);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate (R.menu.action_settings, menu);

        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch (item.getItemId()) {

            case R.id.action_configure:
                startActivity (new Intent (this, ConfigActivity.class));
                break;


            case R.id.action_backup:
//                if (!checkLogin()) {  // TODO: ADD THIS BACK
//                    return true;
//                }

                Intent intent = new Intent (this, BackupActivity.class);
                intent.putExtra (Tags.CURRENT_USER, mUser);
                startActivity (intent);
                break;

            case R.id.action_load:
                if (!checkLogin()) {
                    return true;
                }

                startActivity (new Intent (this, LoadActivity.class));

                break;
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putBoolean (Tags.USER_LOGGED_IN, mIsLoggedIn);
        outState.putString (Tags.CURRENT_USER, mUser);

        super.onSaveInstanceState (outState);
    }

    public void login (View view)
    {
        startActivityForResult (new Intent (this, LoginActivity.class), Tags.LOGIN_REQUEST);
    }

//    public void configure (View view)
//    {
//        startActivity (new Intent (this, ConfigActivity.class));
//    }

    public void add (View view)
    {
        if (!checkLogin()) {
            return;
        }

        Intent intent = new Intent (this, KidInfoActivity.class);
        intent.putExtra (Tags.CURRENT_USER, mUser);
        startActivity (intent);
    }

    public void search (View view)
    {
        if (!checkLogin()) {
            return;
        }

        Intent intent = new Intent (this, SearchActivity.class);
        intent.putExtra (Tags.CURRENT_USER, mUser);
        startActivity (intent);
    }

    public void download (View view)
    {
        if (!checkLogin()) {
            return;
        }

        Intent intent = new Intent (this, DownloadActivity.class);
        intent.putExtra (Tags.CURRENT_USER, mUser);
        startActivity (intent);
    }

    public void upload (View view)
    {
        if (!checkLogin()) {
            return;
        }

        // TODO: implement
    }

    private boolean checkLogin()
    {
        if (!mIsLoggedIn) {
            Utils.generateErrorDialog (R.string.userNotLoggedInError, this);
            return false;
        }
        if (mUser == null) {
            Utils.generateErrorDialog (R.string.noCurrentUserError, this);
            mIsLoggedIn = false;
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Tags.LOGIN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                mUser = data.getStringExtra (Tags.USER);
                mIsLoggedIn = true;
                Toast.makeText (this, R.string.successfulLogin, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == Tags.RESULT_LOGIN_UNCHANGED) {
                Log.d (TAG, "Back button pressed, nothing to change");
            }
            else {
                mIsLoggedIn = false;
            }
        }
    }

}
