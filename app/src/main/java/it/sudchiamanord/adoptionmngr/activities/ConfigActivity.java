package it.sudchiamanord.adoptionmngr.activities;

import android.content.Context;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import it.sudchiamanord.adoptionmngr.R;
import it.sudchiamanord.adoptionmngr.ops.mediator.Proxy;
import it.sudchiamanord.adoptionmngr.ops.results.RegisterResult;
import it.sudchiamanord.adoptionmngr.util.Tags;
import it.sudchiamanord.adoptionmngr.util.Utils;

public class ConfigActivity extends AppCompatActivity
{
    private static final String TAG = ConfigActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private final int SERVER_ADDR_FRAGMENT_IDX = 0;
    private final int NEW_USER_FRAGMENT_IDX = 1;

    static final String CONFIG_FILE = "config.properties";

    private DisposableObserver<RegisterResult> registerResultObserver;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar (toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter (getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById (R.id.container);
        mViewPager.setAdapter (mSectionsPagerAdapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    @Override
    protected void onDestroy()
    {
        Log.i (TAG, "Calling onDestroy");
        super.onDestroy();
        if ((registerResultObserver != null) && (!registerResultObserver.isDisposed())) {
            registerResultObserver.dispose();
        }
    }

    private void hideKeyboard (IBinder windowToken)
    {
        InputMethodManager mgr = (InputMethodManager) this.getSystemService (
                Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow (windowToken, 0);
    }

    public void updateServerAddress (View view)
    {
        hideKeyboard (view.getWindowToken());

        EditText serverAddrEditText = (EditText) view.findViewById (R.id.serverEditText);

        if ((serverAddrEditText.getText() == null) ||
                (serverAddrEditText.getText().toString().isEmpty())) {
            Utils.generateErrorDialog (R.string.noServerAddressError, this);
        }
        else {
            Properties properties = Utils.getProperties (this.getFileStreamPath (CONFIG_FILE),
                    R.string.confFileReadError, this);
            String addr = serverAddrEditText.getText().toString();
            properties.setProperty (Tags.SERVER_ADDRESS, addr);
            Utils.updateConfig (new File (CONFIG_FILE), properties, this);

            Toast.makeText (this, R.string.serverAddressCorrectlyUpdated,
                    Toast.LENGTH_SHORT).show();

            ((ServerUpdateFragment) mSectionsPagerAdapter.getItem (SERVER_ADDR_FRAGMENT_IDX))
                    .setServerAddress (addr);
        }
    }

    public void registerUser (View view)
    {
        Properties properties = Utils.getProperties (this.getFileStreamPath (CONFIG_FILE),
                R.string.confFileReadError, this);
        final String serverAddress = properties.getProperty (Tags.SERVER_ADDRESS);
        if ((serverAddress == null) || (serverAddress.isEmpty())) {
            Toast.makeText (this, R.string.noServerAddressError, Toast.LENGTH_SHORT).show();
            return;
        }


        EditText userEditText = (EditText) view.findViewById (R.id.serverRegisterUser);
        EditText passwordEditText = (EditText) view.findViewById (R.id.serverRegisterPassword);
        CheckBox showPwCheckBox = (CheckBox) view.findViewById (R.id.serverRegisterShowPassword);
        showPwCheckBox.setOnClickListener (this::checkPasswordFormat);  // Not sure this works....
        ProgressBar progressBar = (ProgressBar) findViewById (R.id.serverRegisterProgressBar);
        Button registerUserButton = (Button) findViewById (R.id.registerUserButton);

        hideKeyboard (userEditText.getWindowToken());
        hideKeyboard (passwordEditText.getWindowToken());

        String user = userEditText.getText().toString();
        String pw = passwordEditText.getText().toString();

        if ((user.isEmpty()) || (pw.isEmpty())) {
            Toast.makeText (this, getString (R.string.nullUserAndPwMsg), Toast.LENGTH_SHORT).show();
            return;
        }

        Callable<RegisterResult> registerUserCallable = () -> {
            Log.i (TAG, "Started register to server doInBackground");
            return Proxy.doRegisterUser (user, pw, serverAddress);
        };

        registerResultObserver = getRegisterUserDisposableObserver();
        Observable.fromCallable (registerUserCallable)
                .subscribeOn (Schedulers.io())
                .observeOn (AndroidSchedulers.mainThread())
                .doOnSubscribe (disposable -> {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUserButton.setEnabled (false);
                    Log.i (TAG, "Progressbar set visible" );
                })
//                .subscribe (getRegisterUserDisposableObserver());
                .subscribe (registerResultObserver);
    }

    public void checkPasswordFormat (View view)
    {
        EditText passwordEditText = (EditText) view.findViewById (R.id.serverRegisterPassword);
        CheckBox showPwCheckBox = (CheckBox) view.findViewById (R.id.serverRegisterShowPassword);

        int start = passwordEditText.getSelectionStart();
        int end = passwordEditText.getSelectionEnd();
        if (showPwCheckBox.isChecked()) {
            passwordEditText.setTransformationMethod (null);
        }
        else {
            passwordEditText.setTransformationMethod (new PasswordTransformationMethod());
        }
        passwordEditText.setSelection (start, end);
    }

    private DisposableObserver<RegisterResult> getRegisterUserDisposableObserver()
    {
        return new DisposableObserver<RegisterResult>()
        {
            ProgressBar progressBar = (ProgressBar) findViewById (R.id.serverRegisterProgressBar);
            Button button = (Button) findViewById (R.id.registerUserButton);

            @Override
            public void onComplete()
            {
                Log.i (TAG, "OnComplete");
                progressBar.setVisibility (View.INVISIBLE);
                button.setEnabled (true);
                Log.i (TAG, "Hiding Progressbar");
            }

            @Override
            public void onError (Throwable e)
            {
                Log.i (TAG, "OnError");
                progressBar.setVisibility (View.INVISIBLE);
                button.setEnabled (true);
                Log.i (TAG, "Hiding Progressbar");
            }

            @Override
            public void onNext (RegisterResult result)
            {
                Log.i (TAG, "onNext");
                if (result == null) {
                    Log.d (TAG, "Cancelled Register User operation");
                    return;
                }

                if (result.getUser() == null) {
                    Log.e (TAG, "Null user in the register user result - Ignoring");
                    return;
                }

                if (result.isSuccessful()) {
                    if ((result.getPw() == null) || (result.getSchoolYear() == null)) {
                        Log.e (TAG, "Null password or school year in the register user result - Ignoring");
                        return;
                    }

                    Properties properties = Utils.getProperties (
                            getApplicationContext().getFileStreamPath (CONFIG_FILE),
                            R.string.confFileReadError, getApplicationContext());

                    String currentUserProperty = null;
                    String currentPwProperty;
                    for (String property : properties.stringPropertyNames()) {
                        if (properties.getProperty (property).equals (result.getUser())) {
                            currentUserProperty = property;
                            break;
                        }
                    }
                    if (currentUserProperty != null) {
                        String uuid = currentUserProperty.replace (Tags.USER, "");
                        currentPwProperty = Tags.PW + uuid;
                    }
                    else {
                        String uuid = UUID.randomUUID().toString();
                        currentUserProperty = Tags.USER + uuid;
                        currentPwProperty = Tags.PW + uuid;
                    }

                    properties.setProperty (currentUserProperty, result.getUser());
                    properties.setProperty (currentPwProperty, result.getPw());
                    properties.setProperty (Tags.SCHOOL_YEAR, result.getSchoolYear());
                    Utils.updateConfig (new File (CONFIG_FILE), properties, getApplicationContext());
                }
                else {
//                    String user = data.getStringExtra (Tags.USER);

                    Properties properties = Utils.getProperties (
                            getApplicationContext().getFileStreamPath (CONFIG_FILE),
                            R.string.confFileReadError, getApplicationContext());

                    String currentUserProperty = null;
                    String currentPwProperty = null;
                    for (String property : properties.stringPropertyNames()) {
                        if (properties.getProperty (property).equals (result.getUser())) {
                            currentUserProperty = property;
                            break;
                        }
                    }
                    if (currentUserProperty != null) {
                        String uuid = currentUserProperty.replace (Tags.USER, "");
                        properties.remove (currentUserProperty);
                        currentPwProperty = Tags.PW + uuid;
                    }
                    if (currentPwProperty != null) {
                        properties.remove (currentPwProperty);
                    }

                    Utils.updateConfig (new File (CONFIG_FILE), properties, getApplicationContext());
                }
            }
        };
    }

    public static class ServerUpdateFragment extends Fragment
    {
        private EditText mServerAddrEditText;

        public ServerUpdateFragment()
        {
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState)
        {
            return inflater.inflate (R.layout.server_update_fragment, container, false);
        }

        // This event is triggered soon after onCreateView().
        // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
        @Override
        public void onViewCreated (View view, Bundle savedInstanceState)
        {
            mServerAddrEditText = (EditText) view.findViewById (R.id.serverEditText);

            Properties properties = Utils.getProperties (
                    getActivity().getFileStreamPath (CONFIG_FILE),
                    R.string.confFileReadError, getActivity());
            String addr;
            if ((properties.containsKey (Tags.SERVER_ADDRESS)) &&
                    (properties.getProperty (Tags.SERVER_ADDRESS) != null)) {
                addr = properties.getProperty (Tags.SERVER_ADDRESS);
            }
            else {
                addr = getResources().getString (R.string.defaultServer);
            }
            setServerAddress (addr);
        }

        public void setServerAddress (String addr)
        {
            mServerAddrEditText.setText (addr);
        }
    }

    public static class NewUserFragment extends Fragment
    {
        public NewUserFragment()
        {
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState)
        {
            return inflater.inflate (R.layout.new_user_fragment, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter (FragmentManager fm)
        {
            super (fm);
        }

        @Override
        public Fragment getItem (int position)
        {
            switch (position) {
                case SERVER_ADDR_FRAGMENT_IDX:
                    return new ServerUpdateFragment();

                case NEW_USER_FRAGMENT_IDX:
                    return new NewUserFragment();
            }

            return null;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle (int position)
        {
            switch (position) {
                case SERVER_ADDR_FRAGMENT_IDX:
                    return getResources().getString (R.string.serverUpdateTab);

                case NEW_USER_FRAGMENT_IDX:
                    return getResources().getString (R.string.newUserTab);
            }

            return null;
        }
    }
}
