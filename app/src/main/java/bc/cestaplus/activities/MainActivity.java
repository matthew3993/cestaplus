package bc.cestaplus.activities;

import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import bc.cestaplus.fragments.FragmentRubriky;
import bc.cestaplus.R;
import bc.cestaplus.fragments.VsetkoFragment;
import bc.cestaplus.fragments.webViewTestFragment;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import bc.cestaplus.services.MyService;


public class MainActivity
    extends ActionBarActivity
    implements ActionBar.TabListener {

    public static final String API_KEY = "";        //API key

    // job constants
    private static final int UPDATE_JOB_ID = 50;   //ľubovoľne zvolená hodnota, ale stale tá istá pre update job
    private static final int UPDATE_PERIOD_MIN = 60; // čas medzi automatickými aktualizáciami

    public static Context context;

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    private JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //long currentTime = currentTimeMillis();

        MainActivity.context = getApplicationContext(); //getBaseContext();
        Log.i("LIFECYCLE", "MainActivity.onCreate() was called");
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        getCurrentFocus();

    //pokus nacitania z bundle
        if (savedInstanceState != null) { //ked nastala zmena stavu
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("selectedTab", 1));

        } else { //nove spustenie
            //create a job
            mJobScheduler = JobScheduler.getInstance(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    constructJob();
                }
            }, (UPDATE_PERIOD_MIN/2)*60*1000); //delay polovica z nastavenej update period
        }

    } // end ActivityMain onCreate method


    // moja metoda, vracia Context mainActivity
    public static Context getAPPContext() {
        //return MainActivity.getApplicationContext();
        /*if (context == null){
            context == get;
        }*/
        return MainActivity.context;
    }

    // vytvorenie menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //handler na kliknutie na itemy v action bar-e
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "Aktualizujem...", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                //star
                //composeMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LIFECYCLE", "MainActivity.onPause() was called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("LIFECYCLE", "MainActivity.onResume() was called");
    }


    /**
     * ulozenie do SharedPreferences - ostava ulozene aj po uplnom vypnuti aplikacie, teda aj po volani
     * metody onDestroy();
     * nacitanie zo SharedPreferences by malo byt v onCreate s kontrolou
     * if (savedInstanceState == null) {
     *     //nacitanie dat
     * }
     * */

    @Override
     protected void onStop() { // ulozenie aktualne vybratej tab do SharedPreferences pri prechode MainActivity do background
        super.onStop();
        Log.i("LIFECYCLE", "MainActivity.onStop() was called");

        // Store values between instances here
        //SharedPreferences preferences = getPreferences(MODE_PRIVATE); // MODE_PRIVATE - iba tato aplikacia ma pristup k tymto datam
        //SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI

        //int position = getSupportActionBar().getSelectedTab().getPosition(); // da sa pouzit aj ina metoda??

        //editor.putInt("position", position);

        //editor.commit();         // Commit to storage, very important!
    }

    @Override
    protected void onStart() { // nacitanie posledne vybratej tab do SharedPreferences pri prechode MainActivity do foreground
        super.onStart();
        Log.i("LIFECYCLE", "MainActivity.onStart() was called");

        // Get the between instance stored values
        //SharedPreferences preferences = getPreferences(MODE_PRIVATE); // vytvorenie reader-a

        //int position = preferences.getInt("position", 0);

        //getSupportActionBar().setSelectedNavigationItem(position); //ako odstranit warning ??
    }



    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.i("LIFECYCLE", "MainActivity.onSaveInstanceState() was called");

        //int position = getSupportActionBar().getSelectedTab().getPosition(); // da sa pouzit aj ina metoda??

        //outState.putInt("selectedTab", position);

        //getSupportFragmentManager().putFragment(outState, "prehlad", fPrehlad);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("LIFECYCLE", "MainActivity.onRestoreInstanceState() was called");

        //getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("selectedTab")); //ako odstranit warning ??
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("LIFECYCLE", "MainActivity.onDestroy() was called");
    }

    private void constructJob(){
        JobInfo.Builder builder = new JobInfo.Builder(UPDATE_JOB_ID, new ComponentName(this, MyService.class)); // Component name = meno služby, ktorú chceme spustiť

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        //builder.setPeriodic(10*60*1000); //kazde 10 min
        builder.setPeriodic(UPDATE_PERIOD_MIN*60*1000);

        mJobScheduler.schedule(builder.build());
    }

// ---------------- adapter, ktory vytvara obsahy jednotlivych tab-ov -------------------------------------------------------------
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter
        extends FragmentStatePagerAdapter {  // zmenene na FragmentSTATEpagerAdapter

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a coresponding fragment for each tab
            switch (position) {
                case 0:
                    return VsetkoFragment.newInstance();
                /*case 1:
                    return TemaFragment.newInstance();*/
                case 1:
                    return FragmentRubriky.newInstance();
                case 2:
                    return webViewTestFragment.newInstance();
            }
            return VsetkoFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                /*case 1:
                    return getString(R.string.title_section2).toUpperCase(l);*/
                case 1:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
    }


// ---------------- fragment Rubriky -----------------------------------------------------------------------------------------------------


} // end MainActivity
