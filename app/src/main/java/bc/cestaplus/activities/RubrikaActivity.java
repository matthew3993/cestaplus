package bc.cestaplus.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import bc.cestaplus.R;
import bc.cestaplus.activities.konto_activities.LoggedActivity;
import bc.cestaplus.activities.konto_activities.NotLoggedActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter_All;
import bc.cestaplus.fragments.RubrikyFragment;
import bc.cestaplus.listeners.RecyclerItemClickListener;
import bc.cestaplus.network.Parser;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.utilities.ClanokRecyclerViewAdapter;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Util;

import static bc.cestaplus.extras.IKeys.KEY_MAIN_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_PARENT_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_RUBRIKA_ACTIVITY;



public class RubrikaActivity
    extends ActionBarActivity
    implements SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener{

// ======================================= ATRIBÚTY ================================================================================
    //constants
    private static final String ULOZENE_RUBRIKA = "ulozene_rubrika";

    //networking
    private VolleySingleton volleySingleton;

    // data
    private ArrayList<ArticleObj> zoznamRubrika; // konkretne pomenovanie v RubrikaActivity
    private int pocSrt;                          // pocet nacitanych stranok
    //received from intent
    private String sectionName;
    private int sectionID;

    // session
    private SessionManager session;

//UI
    //swipeRefreshLayout
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // recyclerView
    private RecyclerView recyclerViewRubrika; // konkretne pomenovanie v RubrikaActivity

    // recyclerView Adapter
    private ClanokRecyclerViewAdapter crvaRubrika;

    // vypis chyb
    private TextView tvVolleyErrorRubrika;


// ======================================= METÓDY ==================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubrika);

    //inicializacia atributov
        zoznamRubrika = new ArrayList<>();
        volleySingleton = VolleySingleton.getInstance(this);
        session = new SessionManager(this);
        tvVolleyErrorRubrika = (TextView) findViewById(R.id.tvVolleyErrorRubrika);

        //find SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeSection);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    //inicializacia RecyclerView
        recyclerViewRubrika = (RecyclerView) findViewById(R.id.rvRubrika);
        recyclerViewRubrika.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

    //nacitanie informacii, o ktoru rubriku ide z intentu
        Intent intent = getIntent();

        sectionName = intent.getStringExtra(RubrikyFragment.EXTRA_NAZOV_RUBRIKY);
        getSupportActionBar().setTitle(sectionName); //nastavenie label-u aktivity podla mena konkretnej rubriky

        sectionID = intent.getIntExtra(RubrikyFragment.EXTRA_ID_RUBRIKY, -1);  // negative number will cause translate method to return all == error

    // ======= RecyclerView Touch Listener ====================================================================
        recyclerViewRubrika.addOnItemTouchListener(
                new RecyclerItemClickListener(this.getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                if (position == zoznamRubrika.size()) { // ak bolo kliknute na button nacitaj viac

                                    pocSrt++;                                // !!! zvysenie poctu nacitanych stran !!!
                                    //nacitanie dalsej stranky
                                    Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {

                                            tvVolleyErrorRubrika.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                                            //page-ovanie
                                            if (pocSrt == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                                                zoznamRubrika = Parser.parseJsonArrayResponse(response);
                                            } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                                                zoznamRubrika.addAll(Parser.parseJsonArrayResponse(response));
                                            }
                                            crvaRubrika.setClanky(zoznamRubrika);
                                        }

                                    };

                                    Response.ErrorListener errorLis = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            volleySingleton.handleVolleyError(error, tvVolleyErrorRubrika);
                                        } //end of onErrorResponse
                                    };

                                    volleySingleton.createGetClankyArrayRequestGET(translateSectionId(sectionID), 20, pocSrt, responseLis, errorLis);
                                    Toast.makeText(CustomApplication.getCustomAppContext(), "Načítavam stránku číslo " + pocSrt, Toast.LENGTH_SHORT).show();

                                } else { // ak bolo kliknute na clanok
                                    if (zoznamRubrika.get(position).getSection().equalsIgnoreCase("baterka")){ //if baterka was clicked
                                        Intent intent = new Intent(getApplicationContext(), BaterkaActivity.class);
                                        intent.putExtra("baterka", zoznamRubrika.get(position));
                                        intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);

                                        startActivity(intent);

                                    } else { // if other sections was clicked
                                        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                                        intent.putExtra("clanok", zoznamRubrika.get(position));
                                        intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);

                                        startActivity(intent);
                                    }
                                }
                            }
                        }));

        crvaRubrika = Util.getCrvaType(session, getApplicationContext());

        if (savedInstanceState != null) { //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamRubrika = savedInstanceState.getParcelableArrayList(ULOZENE_RUBRIKA);
            pocSrt = savedInstanceState.getInt("pocSrt", 1);

        } else { // nove spustenie
            pocSrt = 1; // set the default page number

            //nove nacitanie
            Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    tvVolleyErrorRubrika.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                    zoznamRubrika = Parser.parseJsonArrayResponse(response);
                    crvaRubrika.setClanky(zoznamRubrika);

                    Toast.makeText(CustomApplication.getCustomAppContext(), "Načítaných " + zoznamRubrika.size() + " článkov.", Toast.LENGTH_SHORT).show();
                }

            };

            Response.ErrorListener errorLis = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                    volleySingleton.handleVolleyError(error, tvVolleyErrorRubrika);
                } //end of onErrorResponse
            };

            volleySingleton.createGetClankyArrayRequestGET(translateSectionId(sectionID), 20, 1, responseLis, errorLis);

        } //end else savedInstanceState

        crvaRubrika.setClanky(zoznamRubrika);

        recyclerViewRubrika.setAdapter(crvaRubrika);

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    } //end onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rubrika_aktivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                //Toast.makeText(this, "Rubriky back pressed", Toast.LENGTH_SHORT).show();
                return true;*/

            case R.id.account: {
                // Session manager
                final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRola() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_list_style: {
                Util.showListStyleDialog(session, RubrikaActivity.this /*not getApplicationContext()*/, // context for creation of Dialog - it has to be context of activity, not context of application
                    crvaRubrika, recyclerViewRubrika, zoznamRubrika);
                return true;
            }

            case R.id.action_settings: {
                // Launching the Settings activity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);
                startActivity(intent);
                return true;
            }

            case R.id.action_o_portali: {
                // Launching the O portáli activity
                Intent intent = new Intent(getApplicationContext(), OPortaliActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);
                startActivity(intent);
                //getActivity().finish();
                return true;
            }

        }//end switch item.getItemId()
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        // Save the current state of zoznamRubrika
        outState.putParcelableArrayList(ULOZENE_RUBRIKA, zoznamRubrika);

        //ulozenie poctu nacitanych stran
        outState.putInt("pocSrt", pocSrt);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        //nove nacitanie
        Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                tvVolleyErrorRubrika.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                zoznamRubrika = Parser.parseJsonArrayResponse(response);
                crvaRubrika.setClanky(zoznamRubrika);

                Toast.makeText(CustomApplication.getCustomAppContext(), "Načítaných " + zoznamRubrika.size() + " článkov.", Toast.LENGTH_SHORT).show();
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                volleySingleton.handleVolleyError(error, tvVolleyErrorRubrika);
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            } //end of onErrorResponse
        };

        volleySingleton.createGetClankyArrayRequestGET(translateSectionId(sectionID), 20, 1, responseLis, errorLis);

    }//en onRefresh


    private String translateSectionId(int sectionId){
        switch (sectionId){
            case  0: return "tema";
            case  1: return "180stupnov";
            case  2: return "naceste";
            case  3: return "rodicovskeskratky";
            case  4: return "napulze";
            case  5: return "umatusa";
            case  6: return "normalnarodinka";
            case  7: return "tabule";
            case  8: return "animamea";
            case  9: return "kuchynskateologia";
            case 10: return "kazatelnicazivot";
            case 11: return "zahranicami";
            case 12: return "fejton";
            case 13: return "poboxnebo";
            case 14: return "zparlamentu";
            case 15: return "baterka";
            default: return "all"; //in case of some error will return all articles
        } //end switch
    }//end translateSectionId

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_list_style")) {
            Util.refreshRecyclerView(session, getApplicationContext(), crvaRubrika, recyclerViewRubrika, zoznamRubrika);
        }
    }//end onSharedPreferenceChanged
}// end RubrikaActivity
