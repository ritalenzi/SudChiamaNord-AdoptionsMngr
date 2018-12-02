package it.sudchiamanord.adoptionmngr.activities;

import android.content.Context;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Properties;

import it.sudchiamanord.adoptionmngr.R;
import it.sudchiamanord.adoptionmngr.util.Tags;
import it.sudchiamanord.adoptionmngr.util.Utils;

public class ConfigActivity extends AppCompatActivity
{
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

    public void updateServerAddress (View view)
    {
        InputMethodManager mgr = (InputMethodManager) this.getSystemService (
                Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow (view.getWindowToken(), 0);

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

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment
//    {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment()
//        {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance (int sectionNumber)
//        {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt (ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments (args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView (LayoutInflater inflater, ViewGroup container,
//                                  Bundle savedInstanceState)
//        {
//            View rootView = inflater.inflate (R.layout.fragment_config, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

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
            return inflater.inflate (R.layout.server_update_fragment_config, container, false);
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
