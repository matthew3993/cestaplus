package sk.cestaplus.cestaplusapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.adapters.SimpleDividerItemDecoration;
import sk.cestaplus.cestaplusapp.listeners.ListStyleChangeListener;
import sk.cestaplus.cestaplusapp.listeners.RecyclerTouchListener;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.CustomRecyclerViewClickHandler;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.Util;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PREF_LIST_STYLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_SECTION;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SECTION_ID;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SECTION_NAME;

public class SectionFragment
    extends Fragment
    implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        ListStyleChangeListener,
        CustomRecyclerViewClickHandler.CustomRecyclerViewClickHandlerDataProvider{

    // ======================================= ATTRIBUTES ================================================================================
    // data
    private ArrayList<ArticleObj> articlesOfSection;
    private int pagesNum;                            // number of loaded pages

    //passed by arguments
    private String sectionID;
    private String sectionName;

    // utils
    private VolleySingleton volleySingleton; //networking
    private SessionManager session;
    private CustomRecyclerViewClickHandler recyclerViewClickHandler;

    private SectionFragmentInteractionListener listener;

    // UI components
    // data views
    private RecyclerView recyclerViewSection;
    private ArticleRecyclerViewAdapter arvaSection; // arva = Article Recycler View Adapter

    // loading & error views
    private TextView tvVolleyErrorSection; // network error
    private ImageView ivRefresh;
    private ProgressBar progressBar;       // loading animation

    // ======================================= METHODS ==================================================================================

    /**
     *     private SectionFragmentInteractionListener listener;
     */
    public SectionFragment() {
        // Required empty public constructor
    }

    public static SectionFragment newInstance(String sectionID, String sectionName) {
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SECTION_ID, sectionID);
        args.putString(KEY_SECTION_NAME, sectionName);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first

        // get parameters from Arguments
        if (getArguments() != null) {
            sectionID = getArguments().getString(KEY_SECTION_ID);
            sectionName = getArguments().getString(KEY_SECTION_NAME);
        }

        // init data & utils
        // initialisations
        articlesOfSection = new ArrayList<>();
        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext());
        session = new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section, container, false);

        initDataViews(view);
        initLoadingAndErrorViews(view);

        //try to load saved state from bundle
        if (savedInstanceState != null) { //if is not null = change of state - for example rotation of device
            restoreState(savedInstanceState);

        } else {
            //new start of activity
            tryLoadArticles();
        } //end else savedInstanceState

        recyclerViewSection.setAdapter(arvaSection); //set adapter

        //register On Shared Preference Change Listener
        /*
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
        */

        return view;
    }

    //region LIFECYCLE METHODS

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SectionFragmentInteractionListener) {
            listener = (SectionFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SectionFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //endregion

    //region INITIALISATION METHODS

    private void initDataViews(View view){
        recyclerViewSection = (RecyclerView) view.findViewById(R.id.rvSection);
        recyclerViewSection.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerViewSection.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));

        recyclerViewClickHandler = new CustomRecyclerViewClickHandler(
                this, this, KEY_MAIN_ACTIVITY);

        // we have custom TOUCH listener and also custom CLICK HANDLER
        recyclerViewSection.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity().getApplicationContext(),
                        recyclerViewSection, recyclerViewClickHandler));

        getRecyclerViewAdapterType();
    }

    private void initLoadingAndErrorViews(View view) {
        // loading & error views
        tvVolleyErrorSection = (TextView) view.findViewById(R.id.tvVolleyError);
        ivRefresh = (ImageView) view.findViewById(R.id.ivRefresh);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
    }

    //endregion

    // region LOAD & SHOW ARTICLES of selected section

    private void tryLoadArticles(){
        tvVolleyErrorSection.setVisibility(View.GONE);
        ivRefresh.setVisibility(View.GONE);

        startLoadingAnimation();

        loadArticles(); //creates listeners and sends the request
    }

    private void loadArticles() {
        recyclerViewClickHandler.setPagesNum(1); // set the default page number

        // create listeners
        Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                SectionFragment.this.onResponse(response);
            }

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError){
                    showNoConnection(getResources().getString(R.string.no_connection_err_msg));
                } else {
                    showNoConnection(getResources().getString(R.string.connection_error_msg));
                }
            } //end of onErrorResponse
        };

        //send the request
        volleySingleton.createGetClankyArrayRequestGET(sectionID, 20, 1, responseLis, errorLis);
    }

    private void onResponse(JSONArray response) {
        //make UI changes
        hideErrorAndLoadingViews();
        recyclerViewSection.setVisibility(View.VISIBLE);

        // logic
        articlesOfSection = Parser.parseJsonArrayResponse(response);
        if (articlesOfSection.size() < MainActivity.ART_NUM){
            arvaSection.setNoMoreArticles();
        }
        arvaSection.setArticlesList(articlesOfSection);
    }

    // endregion

    private void handleClick(View view, int position) {
        if (position == articlesOfSection.size()) { // ak bolo kliknute na button nacitaj viac

            arvaSection.startAnim();

            pagesNum++;                                // !!! zvysenie poctu nacitanych stran !!!
            //nacitanie dalsej stranky
            Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    tvVolleyErrorSection.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                    //page-ovanie
                    if (pagesNum == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                        articlesOfSection = Parser.parseJsonArrayResponse(response);
                        if (articlesOfSection.size() < MainActivity.ART_NUM){
                            arvaSection.setNoMoreArticles();
                        }

                    } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                        ArrayList<ArticleObj> moreArticles = Parser.parseJsonArrayResponse(response);
                        if (moreArticles.size() < MainActivity.ART_NUM){
                            arvaSection.setNoMoreArticles();
                        }
                        articlesOfSection.addAll(moreArticles);
                    }
                    arvaSection.setArticlesList(articlesOfSection);
                }

            };

            Response.ErrorListener errorLis = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    arvaSection.setError();
                    pagesNum--;                                // !!!  reducing of loaded pages number - because page was not loaded !!!
                    Toast.makeText(getActivity().getApplicationContext(), "Chyba pri načítavaní ďalších článkov", Toast.LENGTH_SHORT).show();
                } //end of onErrorResponse
            };

            volleySingleton.createGetClankyArrayRequestGET(sectionID, 20, pagesNum, responseLis, errorLis);
            //Toast.makeText(CustomApplication.getCustomAppContext(), "Načítavam stránku číslo " + pagesNum, Toast.LENGTH_SHORT).show();

        } else { // ak bolo kliknute na clanok
            Util.startArticleOrBaterkaActivity(this, KEY_MAIN_ACTIVITY, articlesOfSection.get(position));
        }
    } //end handleClick()

    // region SUPPORT METHODS

    private void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setIndeterminate(true); // we don't need this, because we set indeterminateOnly="true" in layout
    }

    private void hideErrorAndLoadingViews() {
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)
        tvVolleyErrorSection.setVisibility(View.GONE);
        ivRefresh.setVisibility(View.GONE);
    }

    private void showNoConnection(String msg) {
        recyclerViewSection.setVisibility(View.GONE);

        //stop loading animation !
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

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

    // endregion

    private void getRecyclerViewAdapterType() {
        // in FRAGMENT (here): first parameter - context MUST be only getACTIVITY, and not getActivity().getAPPLICATIONContext()
        // - because Calligraphy wraps around ACTIVITY and not APPLICATION
        arvaSection = Util.getCrvaType(getActivity(), false); // false = doesn't have header

        // if used in ACTIVITY:
        //      first parameter - context MUST be 'this' (as activity), and not getApplicationContext()
        //      - because Calligraphy wraps around ACTIVITY and not APPLICATION
    }

    // region SAVE & RESTORE STATE

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList(KEY_SAVED_SECTION, articlesOfSection); // Save the current state of articlesOfSection
        outState.putInt("pagesNum", pagesNum);

        // Always call the superclass so it can save the view hierarchy state !!
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        articlesOfSection = savedInstanceState.getParcelableArrayList(KEY_SAVED_SECTION);
        pagesNum = savedInstanceState.getInt("pagesNum", 1);
    }

    // endregion

    // region LISTENERS METHODS

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(KEY_PREF_LIST_STYLE)) {
            Util.refreshRecyclerViewWithoutHeader(session, getActivity().getApplicationContext(), arvaSection, recyclerViewSection, articlesOfSection);
        }
    }//end onSharedPreferenceChanged

    @Override
    public void handleListStyleSelection(DialogInterface dialog, int listStyle) {
        session.setListStyle(listStyle); //save list style

        getRecyclerViewAdapterType();
        arvaSection.setArticlesList(articlesOfSection);
        recyclerViewSection.setAdapter(arvaSection);

        dialog.dismiss(); //dismiss the dialog
    }

    @Override
    public int getPagesNum() {
        return pagesNum;
    }

    @Override
    public ArrayList<ArticleObj> getArticles() {
        return articlesOfSection;
    }

    @Override
    public ArticleRecyclerViewAdapter getAdapter() {
        return arvaSection;
    }

    // endregion

    public interface SectionFragmentInteractionListener {

    }
}
