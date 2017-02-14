package sk.cestaplus.cestaplusapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.SettingsActivity;
import sk.cestaplus.cestaplusapp.activities.konto_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.konto_activities.NotLoggedActivity;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.adapters.SimpleDividerItemDecoration;
import sk.cestaplus.cestaplusapp.listeners.ListStyleChangeListener;
import sk.cestaplus.cestaplusapp.listeners.RecyclerTouchListener;
import sk.cestaplus.cestaplusapp.network.Endpoints;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.ArticleActivity;
import sk.cestaplus.cestaplusapp.listeners.ArticlesLoadedListener;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.tasks.UpdateTask;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.MyApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Util;

//staticke importy
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static java.lang.System.currentTimeMillis;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllFragment_old#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllFragment_old
    extends Fragment
    implements ArticlesLoadedListener, SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener, ListStyleChangeListener
    {

    private static final String ULOZENE_VSETKO = "ulozeny_vsetko";

    //networking
    private VolleySingleton volleySingleton;

    private SessionManager session; // session

    // data
    private ArticleObj headerArticle;
    private ArrayList<ArticleObj> zoznamVsetko; // konkretne pomenovanie vo FragmenteVsetko
    private int pocSrt;                        // pocet nacitanych stranok

    //UI
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // recyclerView
    private RecyclerView recyclerViewVsetko; // konkretne pomenovanie vo FragmentePrehlad
    private ArticleRecyclerViewAdapter crvaVsetko;

    private TextView tvVolleyError;     // vypis chyb
    private ProgressBar progressBar; //loading animation in fragment, NOT in load more btn

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AllFragment_old.
     */

    public static AllFragment_old newInstance() {
        AllFragment_old fragment = new AllFragment_old();
        return fragment;
    }

    public AllFragment_old() {
        // Required empty public constructor
    }

        /**
         * SOMETHING ABOUT FRAGMETN LIFECYCLE: https://developer.android.com/guide/components/fragments.html
          * @param savedInstanceState
         */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first
        setHasOptionsMenu(true); //this fragment has it's own menu different from menu of activity

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
        View view = inflater.inflate(R.layout.fragment_all, container, false);

        //find progressBar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarFragmentAll);
        progressBar.setIndeterminate(true); //set the progress bar to be intermediate

        //find error TextView
        tvVolleyError = (TextView) view.findViewById(R.id.tvVolleyErrorFragmentAll);

        //find SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRecyclerViewMain);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //inicializacia RecyclerView
        recyclerViewVsetko = (RecyclerView) view.findViewById(R.id.rvAll);
        recyclerViewVsetko.setLayoutManager(new LinearLayoutManager(getActivity()) );
        recyclerViewVsetko.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));

        //SOURCE: http://stackoverflow.com/questions/26568087/parallax-header-effect-with-recyclerview  ! ANSWER 2 !
        recyclerViewVsetko.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View view = recyclerView.getChildAt(0);
                if(view != null && recyclerView.getChildAdapterPosition(view) == 0)  {
                    view.setTranslationY(-view.getTop() / 2);// or use view.animate().translateY();
                }
            }
        });

        // ======= RecyclerView Touch Listener ====================================================================
        recyclerViewVsetko.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerViewVsetko,
                    new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                handleClick(view, position);
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                //onLongClick code
                            }
                        }));


        getCrvaType();

        if (savedInstanceState != null){ //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamVsetko = savedInstanceState.getParcelableArrayList(ULOZENE_VSETKO);
            pocSrt = savedInstanceState.getInt("pocSrt", 1);

            //ošetrenie prípadu, keď po rýchlom otočení po spustení ostal zoznam prázdny
            if (zoznamVsetko.isEmpty()){ //ak je zoznam clankov prazdny,
                loadArticles();

            } else { //v pripade, ze nie je prazdny
                crvaVsetko.setArticlesList(zoznamVsetko, headerArticle);
                progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)
                recyclerViewVsetko.setVisibility(View.VISIBLE);
            }

        } else { // nove spustenie
            loadArticles();

        } //end else savedInstanceState

        //set adapter
        recyclerViewVsetko.setAdapter(crvaVsetko);

        return view;
    } // end onCreateView

        private void getCrvaType() {
            crvaVsetko = Util.getCrvaType(session, getActivity().getApplicationContext(), true);
        }

        private void loadArticles() {
            pocSrt = 1; // set the page number
            startLoadingAnimation();

            //start the update task - will trigger onArticlesLoaded
            new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time
        }

        private void startLoadingAnimation() {
            progressBar.setVisibility(View.VISIBLE);
        }

        private void handleClick(View view, int position) {
            if (position == (zoznamVsetko.size() + 1)){ // if loadmore button or progress bar was clicked

                if (!crvaVsetko.isLoading()) {
                    //all this code should run only if we are not loading more articles

                    crvaVsetko.startAnim();

                    pocSrt++;  // !!! zvysenie poctu nacitanych stran !!!
                    //nacitanie dalsej stranky
                    Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                            tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                            //page-ovanie
                            if (pocSrt == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                                zoznamVsetko = Parser.parseJsonArrayResponse(response);
                                if (zoznamVsetko.size() < MainActivity.ART_NUM) {
                                    crvaVsetko.setNoMoreArticles();
                                }

                            } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                                ArrayList<ArticleObj> moreArticles = Parser.parseJsonArrayResponse(response);
                                if (moreArticles.size() < MainActivity.ART_NUM) {
                                    crvaVsetko.setNoMoreArticles();
                                }
                                zoznamVsetko.addAll(Parser.parseJsonArrayResponse(response));
                            }
                            crvaVsetko.setArticlesList(zoznamVsetko, headerArticle);
                        }

                    };

                    Response.ErrorListener errorLis = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                            crvaVsetko.setError();
                            //volleySingleton.handleVolleyError(error, tvVolleyError);
                            pocSrt--;                                // !!!  reducing of loaded pages number - because page was not loaded !!!
                            Toast.makeText(getActivity(), "Chyba pri načítavaní ďalších článkov", Toast.LENGTH_SHORT).show();
                        } //end of onErrorResponse
                    };

                    volleySingleton.createGetClankyArrayRequestGET("all", 20, pocSrt, responseLis, errorLis);
                    //Toast.makeText(getActivity().getApplicationContext(), "Načítavam stránku číslo " + pocSrt, Toast.LENGTH_SHORT).show();
                }

            } else {
                final Intent intent;
                if (position == 0) {
                    //header was clicked
                    intent = new Intent(getActivity().getApplicationContext(), ArticleActivity.class);
                    intent.putExtra("clanok", headerArticle);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);

                } else {
                    // article was clicked
                    if (zoznamVsetko.get(position).getSection().equalsIgnoreCase("baterka")) { //if baterka was clicked
                        intent = new Intent(getActivity().getApplicationContext(), BaterkaActivity.class);
                        intent.putExtra("baterka", zoznamVsetko.get(position-1));
                        intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);

                    } else { // if other sections was clicked
                        intent = new Intent(getActivity().getApplicationContext(), ArticleActivity.class);
                        intent.putExtra("clanok", zoznamVsetko.get(position-1));
                        intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                }, 250);
            }
        }//end handleClick()

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
                Util.showListStyleDialog(this, session, getActivity()); /*not getActivity.getApplicationContext()*/ // context for creation of Dialog - it has to be context of activity, not context of application);
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

            /*case R.id.action_test_notification:
                Util.issueNotification("Počet nových článkov: ???", 1);
                return true;
            */
            /*
            case R.id.action_test_size_and_density:
                checkScreenSize();
                return true;
            */
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
        Log.i("LIFECYCLE", "Vsetko.onResume() was called");

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

        if (currentTimeMillis() - lastTryTime > 60*60*1000){// 60 min in miliseconds
            //Toast.makeText(getActivity().getApplicationContext(), "Aktualizujem...", Toast.LENGTH_SHORT).show();

            startLoadingAnimation();
            //start the update task - will trigger onArticlesLoaded
            new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time
        }

    }//end onResume()

    /**
     * This method is called EVERY! time, even if there is problem with network.
     * It is because, even there is no network, it shows list of articles loaded from database.
     * And if network is ok, it shows updated list of articles.
     */
    @Override
    public void onArticlesLoaded(ArrayList<ArticleObj> listArticles) {
        // !!!!!!!!! TODO: solve problem with paging !!!

        //stop refreshing or loading animation -- important!
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            //TODO: is this enought? is this good way to stop or i have to do something else?
            progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)
        }

        zoznamVsetko = listArticles;
        //addTestArticle();

        headerArticle = getTestHeaderArticle(); //TODO: change this to real header article
        crvaVsetko.setArticlesList(listArticles, headerArticle);
        pocSrt = 1;
        recyclerViewVsetko.setVisibility(View.VISIBLE);
        tvVolleyError.setVisibility(View.GONE);
    }//end onArticlesLoaded


        @Override
        public void onLoadingError() {
            showNoConnection(getResources().getString(R.string.connection_error_msg));
        }

        private void showNoConnection(String msg) {
            progressBar.setVisibility(View.GONE); //stop loading animation !
            tvVolleyError.setVisibility(View.VISIBLE);
            tvVolleyError.setText(msg);
        }

        @Override
    public void numNewArticles(int count) {
        Context context;

        // null check added after Igor's feedback and error report - not tested yet!!!
        Activity activity = getActivity();

        if (activity != null) {
            context = getActivity().getApplicationContext();

        } else {
            context = CustomApplication.getCustomAppContext();
        }

        if (count == 0) {
            Toast.makeText(context, "Žiadne nové články", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Počet nových článkov: " + count, Toast.LENGTH_SHORT).show();
        }
    } // end numNewArticles

    @Override
    public void onRefresh() {
        //refreshing animation will start automatically
        //start the update task - will trigger onArticlesLoaded or void onLoadingError();
        new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time

    }//en onRefresh

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_list_style")) {
            if (getActivity() != null){
                getCrvaType();
            } else {
                crvaVsetko = Util.getCrvaType(session, CustomApplication.getCustomAppContext(), true);
            }
            crvaVsetko.setArticlesList(zoznamVsetko, headerArticle);
            recyclerViewVsetko.setAdapter(crvaVsetko);
        }
    }//end onSharedPreferenceChanged

        @Override
        public void handleListStyleSelection(DialogInterface dialog, int listStyle) {
            session.setListStyle(listStyle); //save list style

            getCrvaType();
            crvaVsetko.setArticlesList(zoznamVsetko, headerArticle);
            recyclerViewVsetko.setAdapter(crvaVsetko);

            dialog.dismiss(); //dismiss the dialog
        }

        public ArticleObj getTestHeaderArticle() {
            return new ArticleObj(
                    "#psyché_ Čo je dôležité robiť v manželstve, aby skutočne fungovalo?",
                    "Manželské puto je tým najbližším, najintímnejším a najosobnejším medziľudským vzťahom, aký existuje. " +
                            "Muž a žena žijú v jednej domácnosti, spávajú v jednej posteli, zažívajú fyzickú...",
                    "Mgr. Lucia Drábiková, PhD.",
                    //"http://www.cestaplus.sk/images/_small/img_700x467/clanok_psmanzelstvo.jpg", // more white background
                    //"http://www.cestaplus.sk/images/_small/img_700x467/tema_irackybiskup.jpg",   // nearly black background
                    "http://www.cestaplus.sk/images/_small/img_700x467/clanok_vaseckacas.jpg",
                    new Date(2016, 11, 19, 9, 50, 31),
                    "clanok",
                    "clanok_1835",
                     true
            );
        }

        private void addTestArticle() {
            ArticleObj testArticle =
                    new ArticleObj("Musia mať katolíci sex len toľkokrát, koľko majú detí? Nemusia",
                            "Článok o mužovi zmierajúcom po svojej žene v jej plodnom období rozprúdil " +
                                    "diskusie. Sú katolíci naozaj tak ťažko skúšaní? Je vôbec v ľudských silách vydržať takéto skúšky? " +
                                    "A je pravda,...",
                            "Neviem kto",
                            "http://www.cestaplus.sk/images/tema_clanok/hlavny_clanok_obrazok/katolicilpp.jpg",
                            new Date(2016, 11, 11, 19, 57, 58),
                            "tema",
                            "temacl_270",
                            false
                    );
            zoznamVsetko.add(0, testArticle);
        }
    } // end class FragmentVsetko
