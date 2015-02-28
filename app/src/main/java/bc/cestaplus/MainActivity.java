package bc.cestaplus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity
    extends ActionBarActivity
    implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private static Context context;

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

    } // end ActivityMain onCreate method

    /*
    public static Context getContext() {
        return MainActivity.context;
    }
    */

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

// ---------------- adapter, ktory vytvara obsahy jednotlivych tab-ov -------------------------------------------------------------
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a coresponding fragment for each tab
            switch (position) {
                case 0:
                    return PrehladFragment.newInstance(position + 1);
                case 1:
                    return RubrikyFragment.newInstance(position + 1);
                case 2:
                    return BaterkaFragment.newInstance(position + 1); /*android.support.v4.app.fra*/
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

        static ArrayAdapter<ClanokObj2> adapterVsetko;
        static ArrayAdapter<String> adapterNajcitanejsie;

        List<ClanokObj2> clanky;
        String [] najcitanejsie = {"najcitanejsie 1", "najcitanejsie 2", "najcitanejsie 3", "najcitanejsie 4", "najcitanejsie 5"};

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_prehlad, container, false);

            // pridanie tabHost do rootView
            TabHost tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
            tabHost.setup();

            clanky = new ArrayList<ClanokObj2>();

    // vytvorenie testovacich clankoch
            clanky.add(new ClanokObj2("PRESTÁVKA: Chlapček Sam",
                                     "http://www.cestaplus.sk/cestaplus/clanok/prestavka-chlapcek-sam",
                                     "Kto je toto roztomilé chlapčiatko? Volá sa Sam, je zo štátu Georgia, USA a má veľmi zaujímavý príbeh.",
                                     new Date(2015, 2, 28, 2, 7, 20),
                                     R.drawable.sam, "Petra Babulíková"));

            clanky.add(new ClanokObj2("Misionár, ktorý bude s nimi žiť všetko. Aj blchy, aj vši",
                    "http://www.cestaplus.sk/kazatelnicazivot/nazov/misionar-ktory-bude-s-nimi-zit-vsetko",
                    "Kto chce pracovať s bezdomovcami, mal by s nimi aj žiť. Mal by to byť človek, ktorý nie je ženatý alebo ak je to žena, tak nie je vydatá a môže s nimi",
                    new Date(2015, 2, 28, 1, 54, 57),
                    R.drawable.kazatelnica1, "Farár Maroš Kuffa"));

            clanky.add(new ClanokObj2("Budem ťa milovať, len keď...",
                    "http://www.cestaplus.sk/baterka/datum/28-02-2015",
                    ".. ak budeme milovať, len keď.... tak nemilujeme... sme amatéri a nepochopili sme vôbec čo je to láska. Lebo ak čakáme, tak sme akurát tak dobrí",
                    new Date(2015, 2, 28, 0, 00, 00),
                    R.drawable.baterka28_02_2015, "Félix Mária OFM"));








    // pridanie jednotlivych tabs do tabHost-u
        // tab1
            TabHost.TabSpec spec1 = tabHost.newTabSpec("tab1");
            spec1.setContent(R.id.tab1);
            spec1.setIndicator("Všetko");
            tabHost.addTab(spec1);

            //listViewVsetko
            //adapterVsetko = new ArrayAdapter<String>(MainActivity.context, R.layout.clanok_list_item, R.id.item_tvDescription, vsetko);
            adapterVsetko = new ClanokAdapter(MainActivity.context, R.layout.clanok_list_item, clanky, getLayoutInflater(savedInstanceState));
            ListView listViewVsetko = (ListView) rootView.findViewById(R.id.lvVsetko);
            listViewVsetko.setAdapter(adapterVsetko);
            listViewVsetko.setOnItemClickListener(this);
                //Log.d("Vytvaranie", "Vytvoril sa listViewVsetko"); //kontrolny zapis do logu


        // tab2
            TabHost.TabSpec spec2 = tabHost.newTabSpec("tab2");
            spec2.setContent(R.id.tab2);
            spec2.setIndicator("Naj" +
                    "čítanejšie");
            tabHost.addTab(spec2);

            //listViewNajcitanejsie
            adapterNajcitanejsie = new ArrayAdapter<String>(MainActivity.context, android.R.layout.simple_list_item_1, najcitanejsie);
            ListView listViewNajcitanejsie = (ListView) rootView.findViewById(R.id.lvNajcitanejsie);
            listViewNajcitanejsie.setAdapter(adapterNajcitanejsie);
            listViewNajcitanejsie.setOnItemClickListener(this);

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

            TextView txtV = (TextView) view.findViewById(R.id.item_tvDescription);
            Toast.makeText(MainActivity.context/*MainActivity.getContext()*/, "Klikli ste na " + txtV.getText(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.context, ClanokActivity.class);
            /*
            EditText editText = (EditText) findViewById(R.id.edit_message);
            String message = editText.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);*/
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
            Toast.makeText(MainActivity.context/*MainActivity.getContext()*/, "Klikli ste na rubriku " + txtV.getText(), Toast.LENGTH_LONG).show();
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
