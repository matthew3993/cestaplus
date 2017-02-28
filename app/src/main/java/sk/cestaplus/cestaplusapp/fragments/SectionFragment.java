package sk.cestaplus.cestaplusapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.adapters.SimpleDividerItemDecoration;
import sk.cestaplus.cestaplusapp.extras.Constants;
import sk.cestaplus.cestaplusapp.listeners.ListStyleChangeListener;
import sk.cestaplus.cestaplusapp.listeners.RecyclerTouchListener;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.listeners.CustomRecyclerViewClickHandler;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_SECTION;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SECTION_ID;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SECTION_NAME;

public class SectionFragment
    extends Fragment
    implements
        ListStyleChangeListener,
        CustomRecyclerViewClickHandler.CustomRecyclerViewClickHandlerDataProvider{

    // ======================================= ATTRIBUTES ================================================================================
    // data
    private ArrayList<ArticleObj> articlesOfSection;
    private int pagesNum;           // number of loaded pages
    private int overallYScroll = 0; // actual Y scroll position - updated manually by RecyclerView.OnScrollListener!!

    //passed by arguments
    private String sectionName;
    private String sectionID;

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

    public static SectionFragment newInstance(String sectionName, String sectionID) {
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SECTION_NAME, sectionName);
        args.putString(KEY_SECTION_ID, sectionID);

        fragment.setArguments(args);
        return fragment;
    }

    //region LIFECYCLE METHODS

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first

        // get parameters from Arguments
        if (getArguments() != null) {
            sectionName = getArguments().getString(KEY_SECTION_NAME);
            sectionID = getArguments().getString(KEY_SECTION_ID);
        }

        // init data & utils
        // initialisations
        articlesOfSection = new ArrayList<>();
        pagesNum = 1; //!!
        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext());
        session = new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_section, container, false);

        // title is changed in SectionFragmentSwapper
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

        setRecyclerViewAdapterType();

        //SOURCE: http://stackoverflow.com/a/27546142
        recyclerViewSection.setOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // dy = change in scroll - NOT actual scroll position
                // overallYScroll = is actual Y scroll position

                overallYScroll += dy; // update actual Y scroll position

                if (overallYScroll < 0) {
                    overallYScroll = 0;
                }

                //Log.i("scrolling","Scroll Y  = " + dy);
                //Log.i("scrolling","Overall scroll Y  = " + overallYScroll);

                if (overallYScroll == 0) {
                    listener.setSwipeRefreshLayoutEnabled(true);
                } else {
                    listener.setSwipeRefreshLayoutEnabled(false);
                }
            }
        });

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

    public void tryLoadArticles(){
        tvVolleyErrorSection.setVisibility(View.GONE);
        ivRefresh.setVisibility(View.GONE);

        startLoadingAnimation();

        loadArticles(); //creates listeners and sends the request
    }

    private void loadArticles() {
        pagesNum = 1; // set the default page number

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
        volleySingleton.createGetArticlesArrayRequestGET(sectionID, Constants.ART_NUM, 1, responseLis, errorLis);
    }

    private void onResponse(JSONArray response) {
        //make UI changes
        hideErrorAndLoadingViews();
        listener.stopRefreshingAnimation();
        recyclerViewSection.setVisibility(View.VISIBLE);

        // logic
        articlesOfSection = Parser.parseJsonArrayResponse(response);
        if (articlesOfSection.size() < Constants.ART_NUM){
            arvaSection.setNoMoreArticles();
        }
        arvaSection.setArticlesList(articlesOfSection);
    }

    // endregion

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

    private void setRecyclerViewAdapterType() {
        // in FRAGMENT (here): first parameter - context MUST be only getACTIVITY, and not getActivity().getAPPLICATIONContext()
        // - because Calligraphy wraps around ACTIVITY and not APPLICATION
        arvaSection = Util.getCrvaType(getActivity(), false); // false = doesn't have header

        // if used in ACTIVITY:
        //      first parameter - context MUST be 'this' (as activity), and not getApplicationContext()
        //      - because Calligraphy wraps around ACTIVITY and not APPLICATION
    }

    public String getSectionID() {
        return sectionID;
    }

    public int getRecyclerViewScroll(){
        return overallYScroll;
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


    public void recyclerViewAdapterTypeChanged() {
        setRecyclerViewAdapterType();
        arvaSection.setArticlesList(articlesOfSection);
        recyclerViewSection.setAdapter(arvaSection);;
    }//end onSharedPreferenceChanged

    @Override
    public void handleListStyleSelection(DialogInterface dialog, int listStyle) {
        session.setListStyle(listStyle); //save list style

        recyclerViewAdapterTypeChanged();

        dialog.dismiss(); //dismiss the dialog
    }

    @Override
    public int getPagesNum() {
        return pagesNum;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
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
        void stopRefreshingAnimation();

        void setSwipeRefreshLayoutEnabled(boolean enabled);
    }
}
