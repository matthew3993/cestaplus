package bc.cestaplus.activities;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import bc.cestaplus.activities.konto_activities.LoggedActivity;
import bc.cestaplus.activities.konto_activities.NotLoggedActivity;
import bc.cestaplus.fragments.BaterkaFragment;
import bc.cestaplus.fragments.RubrikyFragment;
import bc.cestaplus.R;
import bc.cestaplus.fragments.VsetkoFragment;
import bc.cestaplus.network.Parser;
import bc.cestaplus.network.VolleySingleton;

import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SessionManager;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import bc.cestaplus.services.UpdateService;

/**
 * Hlavná aktivita, spúšťa sa ako prvá pri spustení aplikácie
 */
public class MainActivity
    extends ActionBarActivity
    implements ActionBar.TabListener, SharedPreferences.OnSharedPreferenceChangeListener {

 // job constants
    private static final int UPDATE_JOB_ID = 50;   //ľubovoľne zvolená hodnota, ale stale tá istá pre update job
    public static final int UPDATE_PERIOD_MIN = 60; // čas medzi automatickými aktualizáciami

 // atributes
    public static Context context;

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    private JobScheduler mJobScheduler;

// session
    private SessionManager session;

    private VolleySingleton volleySingleton;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(getApplicationContext());
        volleySingleton = VolleySingleton.getInstance(getApplicationContext()); //inicializácia volleySingleton - dôležité !!!

    //kontrola módu aplikácie
        int rola = session.getRola();

        if (rola == 50){ //prve spustenie appky
            // Launching the login activity
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();

        } else { //dalsie spustenia

            if (rola > 0) { //ak používame aplikáciu v prihlásenom móde

                //TODO: kontrola prihlásenia
                if (!session.isLoggedIn()) {// ak už nie sme prihlásení
                    //pokus o opätovné prihlásenie
                    // Progress dialog
                    pDialog = new ProgressDialog(this);
                    pDialog.setCancelable(false);

                    loginTry(session.getEmail(), session.getPassword());
                }

            } //else { //ak používam aplikáciu v neprihlásenom móde == tak nič :D

            //}
        }

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
            if (session.getPostNotificationStatus()){ //if notifications are on
                //create a job
                mJobScheduler = JobScheduler.getInstance(this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        constructJob();
                    }
                },
                //(UPDATE_PERIOD_MIN/2)*60*1000); //delay polovica z nastavenej update period
                30*1000); //30 sek
            }
        }

        //register shared preferences change listener
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);

    } // end ActivityMain onCreate method


    // moja metoda, vracia Context mainActivity
    public static Context getAPPContext() {
        //return MainActivity.getApplicationContext();
        /*if (context == null){
            context == get;
        }*/
        return MainActivity.context;
    }

    private void checkScreenSize() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String toastMsg;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                toastMsg = "Extra Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = "Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = "Normal screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                toastMsg = "Small screen";
                break;
            default:
                toastMsg = "Nedá sa určiť veľkosť obrazovky!";
        }
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        //when onNewIntent() is called from notification super.onNewIntent() automatically stops and hides other activities
        super.onNewIntent(intent); // so this is important!!

        if (intent.getBooleanExtra("fromNotification", false)) {
            //restart activity
            this.finish();

            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    } // end onNewIntent()

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
        //1 - vytvorenie Builder-a
        JobInfo.Builder builder = new JobInfo.Builder(UPDATE_JOB_ID, new ComponentName(this, UpdateService.class)); // Component name = meno služby, ktorú chceme spustiť

        //2 - nastavenie vlastností Jobu
        builder.setPeriodic(UPDATE_PERIOD_MIN*60*1000); // v milisekundách
        //builder.setPeriodic(60*1000); //1 min
        builder.setPersisted(true);
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

        //3 - naplánovanie Jobu
        mJobScheduler.schedule(builder.build());

        //4 - (optional) info notification
        //Util.issueNotification("Job naplánovaný", 3); // naplánovanie id = 3
    }

    private void loginTry(final String email, final String password) {
        // Tag used to cancel the request
        //String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){

                int error_code = Parser.parseErrorCode(response);

                if (error_code == 0){
                    String API_key = Parser.parseAPI_key(response);

                    session.prihlas(API_key);
                    session.setRola(1); //používame aplikáciu v prihlásenom móde

                    //inform the user
                    hideDialog();
                    Toast.makeText(CustomApplication.getCustomAppContext(), "Prihlásenie s uloženými údajmi bolo úspešné!", Toast.LENGTH_LONG).show();

                } else {
                    hideDialog();
                    Parser.handleLoginErrorCode(error_code);
                }

            }//end onResponse
        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        "CHYBA PRIHLASOVANIA " + error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        volleySingleton.createLoginRequestPOST(params, responseLis, errorLis);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_post_notifications")) {
            mJobScheduler = JobScheduler.getInstance(this);
            if (session.getPostNotificationStatus()){
                constructJob();
                Toast.makeText(this, "Notifikácie povolené", Toast.LENGTH_SHORT).show();

            } else {
                mJobScheduler.cancel(UPDATE_JOB_ID); //cancel update job
                Toast.makeText(this, "Notifikácie zakázané", Toast.LENGTH_SHORT).show();
            }
        }//end if
    }//end onSharedPreferenceChanged

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
                    return RubrikyFragment.newInstance();
                case 2:
                    return BaterkaFragment.newInstance();
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
    }//end SectionPagerAdapter

} // end MainActivity
