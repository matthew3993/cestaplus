package bc.cestaplus.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;

import bc.cestaplus.activities.BaterkaActivity;
import bc.cestaplus.activities.OPortaliActivity;
import bc.cestaplus.activities.SettingsActivity;
import bc.cestaplus.activities.konto_activities.LoggedActivity;
import bc.cestaplus.activities.konto_activities.NotLoggedActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter_PicturesAndTitles;
import bc.cestaplus.network.Endpoints;
import bc.cestaplus.network.Parser;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.activities.ArticleActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter_All;
import bc.cestaplus.extras.ArticlesLoadedListener;
import bc.cestaplus.listeners.RecyclerItemClickListener;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.tasks.UpdateTask;
import bc.cestaplus.utilities.ClanokRecyclerViewAdapter;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.MyApplication;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Templator;
import bc.cestaplus.utilities.Util;

//staticke importy
import static bc.cestaplus.extras.IKeys.KEY_MAIN_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_PARENT_ACTIVITY;
import static bc.cestaplus.utilities.SessionManager.LIST_STYLE_ALL;
import static bc.cestaplus.utilities.SessionManager.LIST_STYLE_PICTURES_AND_TITLES;
import static java.lang.System.currentTimeMillis;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VsetkoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VsetkoFragment
    extends Fragment
    implements ArticlesLoadedListener, SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener
        //implements RecyclerView.OnClickListener
    {

    private static final String ULOZENE_VSETKO = "ulozeny_vsetko";

//networking
    private VolleySingleton volleySingleton;

// data
    private ArrayList<ArticleObj> zoznamVsetko; // konkretne pomenovanie vo FragmenteVsetko
    private int pocSrt;                        // pocet nacitanych stranok

// session
    private SessionManager session;

//UI
    //swipeRefreshLayout
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // recyclerView
    private RecyclerView recyclerViewVsetko; // konkretne pomenovanie vo FragmentePrehlad
    private ClanokRecyclerViewAdapter crvaVsetko;

    // vypis chyb
    private TextView tvVolleyError;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VsetkoFragment.
     */

    public static VsetkoFragment newInstance() {
        VsetkoFragment fragment = new VsetkoFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public VsetkoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first
        setHasOptionsMenu(true); //this fragment has it's own menu different from menu of activity
        /*if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }*/

        zoznamVsetko = new ArrayList<>();
        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //prístup ku kontextu main aktivity
        session = new SessionManager(getActivity());

        Log.i("LIFECYCLE", "Vsetko.onCreate() was called");
    } //end onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("LIFECYCLE", "Vsetko.onCreateVIEW() was called");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prehlad, container, false);

        //find error TextView
        tvVolleyError = (TextView) view.findViewById(R.id.tvVolleyError);

        //find SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeAll);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //inicializacia RecyclerView
        recyclerViewVsetko = (RecyclerView) view.findViewById(R.id.rvVsetko);
        recyclerViewVsetko.setLayoutManager(new LinearLayoutManager(getActivity()) );

        // ======= RecyclerView Touch Listener ====================================================================
        recyclerViewVsetko.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                if (position == zoznamVsetko.size()){ // ak bolo kliknute na button nacitaj viac

                                    pocSrt++;                                // !!! zvysenie poctu nacitanych stran !!!
                                    //nacitanie dalsej stranky
                                    Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                                            tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                                            //page-ovanie
                                            if (pocSrt == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                                                zoznamVsetko = Parser.parseJsonArrayResponse(response);
                                            } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                                                zoznamVsetko.addAll(Parser.parseJsonArrayResponse(response));
                                            }
                                            crvaVsetko.setClanky(zoznamVsetko);
                                        }

                                    };

                                    Response.ErrorListener errorLis = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                                            volleySingleton.handleVolleyError(error, tvVolleyError);
                                        } //end of onErrorResponse
                                    };

                                    volleySingleton.createGetClankyArrayRequestGET("all", 20, pocSrt, responseLis, errorLis);
                                    Toast.makeText(getActivity().getApplicationContext(), "Načítavam stránku číslo " + pocSrt, Toast.LENGTH_SHORT).show();

                                } else { // ak bolo kliknute na clanok
                                    if (zoznamVsetko.get(position).getSection().equalsIgnoreCase("baterka")){ //if baterka was clicked
                                        Intent intent = new Intent(getActivity().getApplicationContext(), BaterkaActivity.class);
                                        intent.putExtra("baterka", zoznamVsetko.get(position));
                                        intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);

                                        startActivity(intent);

                                    } else { // if other sections was clicked
                                        Intent intent = new Intent(getActivity().getApplicationContext(), ArticleActivity.class);
                                        intent.putExtra("clanok", zoznamVsetko.get(position));
                                        intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);

                                        startActivity(intent);
                                    }
                                }
                            }
                        }));


        crvaVsetko = Util.getCrvaType(session, getActivity().getApplicationContext());

        if (savedInstanceState != null){ //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamVsetko = savedInstanceState.getParcelableArrayList(ULOZENE_VSETKO);
            pocSrt = savedInstanceState.getInt("pocSrt", 1);

            //ošetrenie prípadu, keď po rýchlom otočení po spustení ostal zoznam prázdny
            if (zoznamVsetko.isEmpty()){ //ak je zoznam clankov prazdny,
                //start the update task - will trigger onArticlesLoaded
                Toast.makeText(getActivity(), "Aktualizujem...", Toast.LENGTH_SHORT).show();
                new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time

            } else { //v pripade, ze nie je prazdny
                crvaVsetko.setClanky(zoznamVsetko);
            }

        } else { // nove spustenie
            pocSrt = 1; // set the page number

            //start the update task - will trigger onArticlesLoaded
                Toast.makeText(getActivity().getApplicationContext(), "Aktualizujem...", Toast.LENGTH_SHORT).show();
            new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time

        } //end else savedInstanceState

        recyclerViewVsetko.setAdapter(crvaVsetko);

        return view;
    } // end onCreateView

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vsetko_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //handler na kliknutie na itemy v action bar-e
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.account: {
                // Session manager
                //final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRola() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getActivity().getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_list_style: {
                Util.showListStyleDialog(session, getActivity() /*not getActivity.getApplicationContext()*/, // context for creation of Dialog - it has to be context of activity, not context of application
                        crvaVsetko, recyclerViewVsetko, zoznamVsetko);
                return true;
            }

            case R.id.action_settings: {
                // Launching the Settings activity
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                startActivity(intent);
                return true;
            }

            case R.id.action_o_portali: {
                // Launching the O portáli activity
                Intent intent = new Intent(getActivity().getApplicationContext(), OPortaliActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                startActivity(intent);
                //getActivity().finish();
                return true;
            }

            case R.id.action_test_notification:
                Util.issueNotification("Počet nových článkov: ???", 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }// onOptionsItemSelected()

    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.i("LIFECYCLE", "Vsetko.onSaveInstanceState() was called");

        // Save the current state of zoznamVsetko
        outState.putParcelableArrayList(ULOZENE_VSETKO, zoznamVsetko);

        //ulozenie poctu nacitanych stran
        outState.putInt("pocSrt", pocSrt);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }// end onSaveInstanceState()

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
            .registerOnSharedPreferenceChangeListener(this);

        //check last try time
        long defaultVal = 0;
        try {
            defaultVal = Endpoints.dateFormatAPP.parse("2010-01-01 00:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long lastTryTime = MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(), "lastTryTime", defaultVal);
        if (currentTimeMillis() - lastTryTime > 3600000){// if last try to update was
            //start the update task - will trigger onArticlesLoaded
            Toast.makeText(getActivity().getApplicationContext(), "Aktualizujem...", Toast.LENGTH_SHORT).show();
            new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time
        }

    }//end onResume()

        /*
        @Override
        public void onPause() {
            super.onPause();
            PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                    .unregisterOnSharedPreferenceChangeListener(this);

        }*/

    @Override
    public void onArticlesLoaded(ArrayList<ArticleObj> listArticles) {
        // !!!!!!!!! TODO: solve problem with paging !!!
        //stop refreshing animation -- important!
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }

        zoznamVsetko = listArticles;
        crvaVsetko.setClanky(listArticles);
    }//end onArticlesLoaded

    @Override
    public void numNewArticles(int count) {
        if (count == 0){
            Toast.makeText(getActivity().getApplicationContext(), "Žiadne nové články", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Počet nových článkov: " + count, Toast.LENGTH_SHORT).show();
        }
    } // end numNewArticles

    @Override
    public void onRefresh() {
        //Toast.makeText(getActivity().getApplicationContext(), "Aktualizujem...", Toast.LENGTH_SHORT).show();
        //start the update task - will trigger onArticlesLoaded
        new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time

    }//en onRefresh

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_list_style")) {
            crvaVsetko = Util.getCrvaType(session, getActivity().getApplicationContext());
            crvaVsetko.setClanky(zoznamVsetko);
            recyclerViewVsetko.setAdapter(crvaVsetko);
        }
    }//end onSharedPreferenceChanged

} // end class FragmentVsetko
