package bc.cestaplus.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import bc.cestaplus.adapters.ClanokAdapter;
import bc.cestaplus.ClanokObj;
import bc.cestaplus.R;
import bc.cestaplus.fragments.FragmentPrehlad2;


public class MainActivity
    extends ActionBarActivity
    implements ActionBar.TabListener {

    public static final String EXTRA_RUBRIKA = "bc.cesta.RUBRIKA_CLANKU";
    public static final String API_KEY = "";        //API key

    public static Context context;

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        MainActivity.context = getBaseContext();
        getCurrentFocus();


    } // end ActivityMain onCreate method

    // moja metoda, vracia Context mainActivity
    public static Context getAPPContext() {
        //return MainActivity.getApplicationContext();
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

    /*
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
    */

    /**
     * ulozenie do SharedPreferences - ostava ulozene aj po uplnom vypnuti aplikacie, teda aj po volani
     * metody onDestroy();
     * nacitanie zo SharedPreferences by malo byt v onCreate s kontrolou
     * if (savedInstanceState == null) {
     *     //nacitanie dat
     * }

    @Override
     protected void onStop() { // ulozenie aktualne vybratej tab do SharedPreferences pri prechode MainActivity do background
        super.onStop();
        //Log.i("LIFECYCLE", "MainActivity.onStop() was called");

        // Store values between instances here
        SharedPreferences preferences = getPreferences(MODE_PRIVATE); // MODE_PRIVATE - iba tato aplikacia ma pristup k tymto datam
        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI

        int position = getSupportActionBar().getSelectedTab().getPosition(); // da sa pouzit aj ina metoda??

        editor.putInt("position", position);

        editor.commit();         // Commit to storage, very important!
    }

    @Override
    protected void onStart() { // nacitanie posledne vybratej tab do SharedPreferences pri prechode MainActivity do foreground
        super.onStart();
        //Log.i("LIFECYCLE", "MainActivity.onStart() was called");

        // Get the between instance stored values
        SharedPreferences preferences = getPreferences(MODE_PRIVATE); // vytvorenie reader-a

        int position = preferences.getInt("position", 0);

        getSupportActionBar().setSelectedNavigationItem(position); //ako odstranit warning ??
    }
    */

    /*
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //Log.i("LIFECYCLE", "MainActivity.onSaveInstanceState() was called");

        int position = getSupportActionBar().getSelectedTab().getPosition(); // da sa pouzit aj ina metoda??

        outState.putInt("selectedTab", position);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        //Log.i("LIFECYCLE", "MainActivity.onRestoreInstanceState() was called");

        getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("selectedTab")); //ako odstranit warning ??
    }
    */

    // ---------------- adapter, ktory vytvara obsahy jednotlivych tab-ov -------------------------------------------------------------
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {  // zmenene na FragmentSTATEpagerAdapter

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a coresponding fragment for each tab
            switch (position) {
                case 0:
                    //return PrehladFragment.newInstance(position + 1);
                    return FragmentPrehlad2.newInstance("", "");
                case 1:
                    return RubrikyFragment.newInstance(position + 1);
                case 2:
                    return BaterkaFragment.newInstance(position + 1); /*android.support.v4.app.fra*/
                    //return PrehladFragment.newInstance(position + 1);
            }
            return PrehladFragment.newInstance(position + 1);
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
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

// ---------------- fragment Prehľad ------------------------------------------------------------------------------------
    /**
     * fragment Prehľad
     */
    public static class PrehladFragment
        extends Fragment
        implements AdapterView.OnItemClickListener{

        static ArrayAdapter<ClanokObj> adapterVsetko;
        //static ArrayAdapter<ClanokObj> adapterNajcitanejsie;

        List<ClanokObj> vsetko;
        //List<ClanokObj> najcitanejsie;

        private static final String ARG_SECTION_NUMBER = "section_number"; //The fragment argument representing the section number for this fragment.

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PrehladFragment newInstance(int sectionNumber) {
            PrehladFragment fragment = new PrehladFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PrehladFragment() {
        }

        /*
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("tabState", R.layout.fi getSelectedTab());
            findViewById()
        }
        */

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_prehlad, container, false);

        /*
            // pridanie tabHost do rootView
            TabHost tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
            tabHost.setup();

            tabHost.getCurrentTab();
        */
            vsetko = new ArrayList<ClanokObj>();


    // vytvorenie testovacich clankoch - vsetko
            vsetko.add(new ClanokObj("PRESTÁVKA: Chlapček Sam",
                    "Kto je toto roztomilé chlapčiatko? Volá sa Sam, je zo štátu Georgia, USA a má veľmi zaujímavý príbeh.",
                    R.drawable.sam,
                    "Článok"));

            vsetko.add(new ClanokObj("Misionár, ktorý bude s nimi žiť všetko. Aj blchy, aj vši",
                    "Kto chce pracovať s bezdomovcami, mal by s nimi aj žiť. Mal by to byť človek, ktorý nie je ženatý alebo ak je to žena, tak nie je vydatá a môže s nimi",
                    R.drawable.kazatelnica1,
                    "Kazateľnica život"));

            vsetko.add(new ClanokObj("Budem ťa milovať, len keď...",
                    ".. ak budeme milovať, len keď.... tak nemilujeme... sme amatéri a nepochopili sme vôbec čo je to láska. Lebo ak čakáme, tak sme akurát tak dobrí",
                    R.drawable.baterka28_02_2015,
                    "Baterka"));


    /*
            najcitanejsie = new ArrayList<ClanokObj>();

    // vytvorenie testovacich clankoch - najcitanejsie
            najcitanejsie.add(new ClanokObj("Čo by som odkázal mladým mimo Cirkvi? Odpustite nám!",
                    "Stavali sme dom a večer som utekal rýchlo na svätú omšu. Len v šuštákoch a teniskách som zastal pri potoku, aby som sa očistil. Zrazu pozerám, ...",
                    R.drawable.kazatelnica1,
                    "Kazateľnica život"));

            najcitanejsie.add(new ClanokObj("Rady o manželstve, ktoré som potreboval počuť skôr",
                    "Nie som expert na vzťahy. To, že som si prešiel rozvodom, mi pomohlo vidieť veci inak, ako by som ich robil dnes. Až po 16 rokoch s mojou ženou, ktorú som stratil rozvodom, som prišiel na rady, ktoré píšem.",
                    R.drawable.porozvode,
                    "Článok"));

    */





    /*
    // pridanie jednotlivych tabs do tabHost-u
        // tab1
            TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
            spec1.setContent(R.id.tab1);
            spec1.setIndicator("Všetko");
            tabHost.addTab(spec1);*/

            //listViewVsetko
            //adapterVsetko = new ArrayAdapter<String>(MainActivity.context, R.layout.clanok_list_item, R.id.item_tvDescription, vsetko);
            adapterVsetko = new ClanokAdapter(MainActivity.context, R.layout.clanok_list_item, vsetko, getLayoutInflater(savedInstanceState));
            ListView listViewVsetko = (ListView) rootView.findViewById(R.id.lvVsetko);
            listViewVsetko.setAdapter(adapterVsetko);
            listViewVsetko.setOnItemClickListener(this);
                //Log.d("Vytvaranie", "Vytvoril sa listViewVsetko"); //kontrolny zapis do logu

    /*
        // tab2
            TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
            spec2.setContent(R.id.tab2);
            spec2.setIndicator("Naj" +
                    "čítanejšie");
            tabHost.addTab(spec2);

            //listViewNajcitanejsie
            adapterNajcitanejsie = new ClanokAdapter(MainActivity.context, R.layout.clanok_list_item, najcitanejsie, getLayoutInflater(savedInstanceState));
            ListView listViewNajcitanejsie = (ListView) rootView.findViewById(R.id.lvNajcitanejsie);
            listViewNajcitanejsie.setAdapter(adapterNajcitanejsie);
            listViewNajcitanejsie.setOnItemClickListener(this);
    */

        // tab 3
            /*
            TabHost.TabSpec spec3 = tabHost.newTabSpec("tab3");
            spec3.setContent(R.id.tab2);
            spec3.setIndicator("Rozhovor");
            tabHost.addTab(spec3);

        //tab 4
            TabHost.TabSpec spec4 = tabHost.newTabSpec("tab4");
            spec4.setContent(R.id.tab4);
            spec4.setIndicator("Názor");
            tabHost.addTab(spec4);*/


            return rootView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ClanokObj clanok = (ClanokObj) parent.getItemAtPosition(position);

            //TextView txtV = (TextView) view.findViewById(R.id.item_tvDescription);
            //Toast.makeText(MainActivity.context/*MainActivity.getContext()*/, "Klikli ste na " + txtV.getText(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.context, ClanokActivity.class);
            intent.putExtra(EXTRA_RUBRIKA, clanok.getRubrika());

            startActivity(intent);

        }
    } // end class PrehladFragment


// ---------------- fragment Rubriky -----------------------------------------------------------------------------------------------------
    /**
     * fragment Rubriky
     */

    public static class RubrikyFragment
        extends Fragment
        implements AdapterView.OnItemClickListener{

        String [] rubriky = {"Téma mesiaca", "Rozhovor", "Za hranicami", "Kazatenica život", "Anima Mea"};

        static ArrayAdapter<String> adapterRubriky;

        /**
         * The fragment argument representing the section number for this fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static RubrikyFragment newInstance(int sectionNumber) {
            RubrikyFragment fragment = new RubrikyFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public RubrikyFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_rubriky, container, false);

            adapterRubriky = new ArrayAdapter<String>(MainActivity.context, android.R.layout.simple_list_item_1, rubriky);

            ListView listViewRubriky = (ListView) rootView.findViewById(R.id.listViewRubriky);
            listViewRubriky.setAdapter(adapterRubriky);

            listViewRubriky.setOnItemClickListener(this);

            return rootView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView txtV = (TextView) view;
            //Toast.makeText(MainActivity.context/*MainActivity.getContext()*/, "Klikli ste na rubriku " + txtV.getText(), Toast.LENGTH_LONG).show();

        // potrebne nastavenia na spustenie novej aktivity Rubrika + preposlanie informacie ktora rubrika
            Intent intent = new Intent(MainActivity.context, RubrikaAktivity.class);
            intent.putExtra(EXTRA_RUBRIKA, txtV.getText());

            startActivity(intent);
        }

    }// end class RubrikyFragment


// ---------------- fragment BaterkaFragment ----------------------------------------------------------------------------------------------
    /**
     * fragment BaterkaFragment
     */
    public static class BaterkaFragment
            extends Fragment {
        /**
         * The fragment argument representing the section number for this fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static BaterkaFragment newInstance(int sectionNumber) {
            BaterkaFragment fragment = new BaterkaFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public BaterkaFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_baterka, container, false);
            return rootView;
        }
    }// end class BaterkaFragment



} // end MainActivity
