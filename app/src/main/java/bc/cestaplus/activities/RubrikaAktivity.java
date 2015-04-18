package bc.cestaplus.activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
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
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter;
import bc.cestaplus.fragments.FragmentRubriky;
import bc.cestaplus.listeners.RecyclerItemClickListener;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.tasks.UpdateTask;
import bc.cestaplus.utilities.CustomApplication;


public class RubrikaAktivity
    extends ActionBarActivity {

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
        tvVolleyErrorRubrika = (TextView) findViewById(R.id.tvVolleyErrorRubrika);


        //inicializacia RecyclerView
        recyclerViewRubrika = (RecyclerView) findViewById(R.id.rvRubrika);
        recyclerViewRubrika.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

    //nacitanie informacii, o ktoru rubriku ide z intentu
        Intent intent = getIntent();

        sectionName = intent.getStringExtra(FragmentRubriky.EXTRA_NAZOV_RUBRIKY);
        getSupportActionBar().setTitle(sectionName); //nastavenie label-u aktivity podla mena konkretnej rubriky

        sectionID = intent.getIntExtra(FragmentRubriky.EXTRA_ID_RUBRIKY, -1);  // negative number will cause translate method to return all == error

    if (sectionID > 1){ // NEpodporovaná rubrika
        recyclerViewRubrika.setVisibility(View.GONE);

        tvVolleyErrorRubrika.setVisibility(View.VISIBLE);


    } else { // podporovaná rubrika

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
                                                zoznamRubrika = volleySingleton.parseJsonArrayResponse(response);
                                            } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                                                zoznamRubrika.addAll(volleySingleton.parseJsonArrayResponse(response));
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

                                    volleySingleton.sendGetClankyArrayRequestGET(translateSectionId(sectionID), 20, pocSrt, responseLis, errorLis);
                                    Toast.makeText(CustomApplication.getCustomAppContext(), "Load more in RubrikaActivity" + pocSrt, Toast.LENGTH_SHORT).show();

                                } else { // ak bolo kliknute na clanok
                                    Intent intent = new Intent(CustomApplication.getCustomAppContext(), ArticleActivity.class);
                                    intent.putExtra("clanok", zoznamRubrika.get(position));


                                    startActivity(intent);
                                }
                            }
                        }));

        crvaRubrika = new ClanokRecyclerViewAdapter(this.getApplicationContext());

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
                    zoznamRubrika = volleySingleton.parseJsonArrayResponse(response);
                    crvaRubrika.setClanky(zoznamRubrika);
                }

            };

            Response.ErrorListener errorLis = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                    volleySingleton.handleVolleyError(error, tvVolleyErrorRubrika);
                } //end of onErrorResponse
            };

            volleySingleton.sendGetClankyArrayRequestGET(translateSectionId(sectionID), 20, 1, responseLis, errorLis);

        } //end else savedInstanceState

        crvaRubrika.setClanky(zoznamRubrika);

        recyclerViewRubrika.setAdapter(crvaRubrika);

    } // end else sectionId > 2
    } //end onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rubrika_aktivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                //Toast.makeText(this, "Rubriky back pressed", Toast.LENGTH_SHORT).show();
                return true;
        }
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

    private String translateSectionId(int sectionId){
        switch (sectionId){
            case 0: return "tema";
            case 1: return "tabule";
            case 2: return "baterka";
            case 3: return "hranice";
            case 4: return "kazatelnica";
            case 5: return "anima";
            default: return "all"; //in case of some error will return all articles
        } //end switch
    }//end translateSectionId

}// end RubrikaActivity
