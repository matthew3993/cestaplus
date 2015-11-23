package bc.cestaplus.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import bc.cestaplus.R;
import bc.cestaplus.activities.konto_activities.LoggedActivity;
import bc.cestaplus.activities.konto_activities.NotLoggedActivity;
import bc.cestaplus.fragments.RubrikyFragment;
import bc.cestaplus.listeners.ListStyleChangeListener;
import bc.cestaplus.listeners.RecyclerItemClickListener;
import bc.cestaplus.listeners.RecyclerTouchListener;
import bc.cestaplus.network.Parser;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.utilities.ClanokRecyclerViewAdapter;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SectionsUtil;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Util;

import static bc.cestaplus.extras.IKeys.KEY_PARENT_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_RUBRIKA_ACTIVITY;



public class SectionActivity
    extends ActionBarActivity
    implements SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener, ListStyleChangeListener{

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
    private RecyclerView recyclerViewSection; // konkretne pomenovanie v RubrikaActivity

    // recyclerView Adapter
    private ClanokRecyclerViewAdapter crvaRubrika;

    // vypis chyb
    private TextView tvVolleyErrorSection;

    // refresh image-button
    private ImageView ivRefresh;



// ======================================= METÓDY ==================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

    //inicializacia atributov
        zoznamRubrika = new ArrayList<>();
        volleySingleton = VolleySingleton.getInstance(this);
        session = new SessionManager(this);
        tvVolleyErrorSection = (TextView) findViewById(R.id.tvVolleyErrorSection);
        ivRefresh = (ImageView) findViewById(R.id.ivRefreshSection);

        //find SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeSection);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    //inicializacia RecyclerView
        recyclerViewSection = (RecyclerView) findViewById(R.id.rvSection);
        recyclerViewSection.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

    //nacitanie informacii, o ktoru rubriku ide z intentu
        Intent intent = getIntent();

        sectionName = intent.getStringExtra(RubrikyFragment.EXTRA_NAZOV_RUBRIKY);
        getSupportActionBar().setTitle(sectionName); //nastavenie label-u aktivity podla mena konkretnej rubriky

        sectionID = intent.getIntExtra(RubrikyFragment.EXTRA_ID_RUBRIKY, -1);  // negative number will cause translate method to return all == error

    // ======= RecyclerView Touch Listener ====================================================================
        recyclerViewSection.addOnItemTouchListener(
                new RecyclerTouchListener(this.getApplicationContext(), recyclerViewSection,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                handleClick(view, position);
                            }//end onClick

                            @Override
                            public void onLongClick(View view, int position) {
                                //onLongClick code
                            }//end onLongClick
                        }));

        crvaRubrika = Util.getCrvaType(session, getApplicationContext());

        if (savedInstanceState != null) { //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamRubrika = savedInstanceState.getParcelableArrayList(ULOZENE_RUBRIKA);
            pocSrt = savedInstanceState.getInt("pocSrt", 1);

        } else { // nove spustenie

            tryLoadArticles();

        } //end else savedInstanceState

        crvaRubrika.setClanky(zoznamRubrika);

        recyclerViewSection.setAdapter(crvaRubrika);

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    } //end onCreate

    private void handleClick(View view, int position) {
        if (position == zoznamRubrika.size()) { // ak bolo kliknute na button nacitaj viac

            crvaRubrika.startAnim();

            pocSrt++;                                // !!! zvysenie poctu nacitanych stran !!!
            //nacitanie dalsej stranky
            Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    tvVolleyErrorSection.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                    //page-ovanie
                    if (pocSrt == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                        zoznamRubrika = Parser.parseJsonArrayResponse(response);
                        if (zoznamRubrika.size() < MainActivity.ART_NUM){
                            crvaRubrika.setNoMoreArticles();
                        }

                    } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                        ArrayList<ArticleObj> moreArticles = Parser.parseJsonArrayResponse(response);
                        if (moreArticles.size() < MainActivity.ART_NUM){
                            crvaRubrika.setNoMoreArticles();
                        }
                        zoznamRubrika.addAll(moreArticles);
                    }
                    crvaRubrika.setClanky(zoznamRubrika);
                }

            };

            Response.ErrorListener errorLis = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    crvaRubrika.setError();
                    pocSrt--;                                // !!!  reducing of loaded pages number - because page was not loaded !!!
                    Toast.makeText(getApplicationContext(), "Chyba pri načítavaní ďalších článkov", Toast.LENGTH_SHORT).show();
                } //end of onErrorResponse
            };

            volleySingleton.createGetClankyArrayRequestGET(SectionsUtil.translateSectionId(sectionID), 20, pocSrt, responseLis, errorLis);
            //Toast.makeText(CustomApplication.getCustomAppContext(), "Načítavam stránku číslo " + pocSrt, Toast.LENGTH_SHORT).show();

        } else { // ak bolo kliknute na clanok
            final Intent intent;
            if (zoznamRubrika.get(position).getSection().equalsIgnoreCase("baterka")) { //if baterka was clicked
                intent = new Intent(getApplicationContext(), BaterkaActivity.class);
                intent.putExtra("baterka", zoznamRubrika.get(position));
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);

            } else { // if other sections was clicked
                intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("clanok", zoznamRubrika.get(position));
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_RUBRIKA_ACTIVITY);

            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                }
            }, 250);
        }
    } //end handleClick()

    private void tryLoadArticles(){
        if (Util.isNetworkAvailable(this)) {
            //vytvorí listenery a odošle request
            loadArticles(); //naplní article text zobrazení do webView

        } else {
            showNoConnection("Nie ste pripojený k sieti!");
        }
    }

    private void showNoConnection(String msg) {
        //Toast.makeText(getApplicationContext(), "ERROR ", Toast.LENGTH_LONG).show();
        recyclerViewSection.setVisibility(View.GONE);

        tvVolleyErrorSection.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);

        tvVolleyErrorSection.setText(msg);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoadArticles();
            }
        });
    }

    private void loadArticles() {
        pocSrt = 1; // set the default page number

        //nove nacitanie
        Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                recyclerViewSection.setVisibility(View.VISIBLE);

                tvVolleyErrorSection.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                ivRefresh.setVisibility(View.GONE);

                zoznamRubrika = Parser.parseJsonArrayResponse(response);
                if (zoznamRubrika.size() < MainActivity.ART_NUM){
                    crvaRubrika.setNoMoreArticles();
                }
                crvaRubrika.setClanky(zoznamRubrika);

                //Toast.makeText(CustomApplication.getCustomAppContext(), "Načítaných " + zoznamRubrika.size() + " článkov.", Toast.LENGTH_SHORT).show();
            }

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                showNoConnection("Chyba pripojenia na server!");
            } //end of onErrorResponse
        };

        volleySingleton.createGetClankyArrayRequestGET(SectionsUtil.translateSectionId(sectionID), 20, 1, responseLis, errorLis);
    }


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
                Util.showListStyleDialog(this, session, SectionActivity.this); /*not getApplicationContext()*/ // context for creation of Dialog - it has to be context of activity, not context of application);
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
                tvVolleyErrorSection.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
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
                volleySingleton.handleVolleyError(error, tvVolleyErrorSection);
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            } //end of onErrorResponse
        };

        volleySingleton.createGetClankyArrayRequestGET(SectionsUtil.translateSectionId(sectionID), 20, 1, responseLis, errorLis);

    }//en onRefresh

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_list_style")) {
            Util.refreshRecyclerView(session, getApplicationContext(), crvaRubrika, recyclerViewSection, zoznamRubrika);
        }
    }//end onSharedPreferenceChanged

    @Override
    public void handleSelection(DialogInterface dialog, int listStyle) {
        session.setListStyle(listStyle); //save list style

        crvaRubrika = Util.getCrvaType(session, getApplicationContext());
        crvaRubrika.setClanky(zoznamRubrika);
        recyclerViewSection.setAdapter(crvaRubrika);

        dialog.dismiss(); //dismiss the dialog
    }
}// end RubrikaActivity
