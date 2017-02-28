package sk.cestaplus.cestaplusapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.adapters.SimpleDividerItemDecoration;
import sk.cestaplus.cestaplusapp.listeners.ArticlesLoadedListener;
import sk.cestaplus.cestaplusapp.listeners.ListStyleChangeListener;
import sk.cestaplus.cestaplusapp.listeners.RecyclerTouchListener;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.tasks.UpdateTask;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.DateFormats;
import sk.cestaplus.cestaplusapp.utilities.MyApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.listeners.CustomRecyclerViewClickHandler;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static java.lang.System.currentTimeMillis;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_LAST_TRY_TIME;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_ARTICLES_ALL;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_PAGES_NUM;

public class AllFragment
    extends Fragment
    implements
        ArticlesLoadedListener,
        ListStyleChangeListener,
        CustomRecyclerViewClickHandler.CustomRecyclerViewClickHandlerDataProvider {

    // data
    private ArrayList<ArticleObj> articlesAll;
    private int pagesNum;                        // number of loaded pages

    // utils
    private SessionManager session; // session manager

    private CustomRecyclerViewClickHandler recyclerViewClickHandler;
    private AllFragmentInteractionListener listener;

    // recyclerView
    private RecyclerView recyclerViewAll;
    private ArticleRecyclerViewAdapter arvaAll;

    // loading & error views
    private ProgressBar progressBar; //loading animation in activity, NOT in load more btn
    private ImageView ivNoConnection;

    /**
     * Required empty public constructor
     */
    public AllFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment AllFragment.
    */
    public static AllFragment newInstance() {
        AllFragment fragment = new AllFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first

        // get parameters from Arguments
        if (getArguments() != null) {
            //role = getArguments().getInt(KEY_ROLE, ROLE_NOT_LOGGED);
        }

        // init data & utils
        articlesAll = new ArrayList<>();
        pagesNum = 1; //!!
        session = new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all, container, false);

        // init views
        //initLayoutViews(view);
        initRecyclerView(view);
        initLoadingAndErrorViews(view);

        tryLoadAllArticles(savedInstanceState);

        return view;
    }

    //region LIFECYCLE METHODS

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AllFragmentInteractionListener) {
            listener = (AllFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AllFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO: check last try time and if needed start UpdateTask - probably isn't working
        long defaultVal = 0;
        try {
            defaultVal = DateFormats.dateFormatJSON.parse("2010-01-01 00:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long lastTryTime = MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(), KEY_LAST_TRY_TIME, defaultVal);

        if (currentTimeMillis() - lastTryTime > 60*60*1000){// 60 min in miliseconds
            //Toast.makeText(getActivity().getApplicationContext(), "Aktualizujem...", Toast.LENGTH_SHORT).show();

            startLoadingAnimation();
            //start the update task - will trigger onArticlesLoaded
            new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    //endregion

    //region INITIALISATION METHODS

    private void initRecyclerView(View view) {
        recyclerViewAll = (RecyclerView) view.findViewById(R.id.rvAll);
        recyclerViewAll.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerViewAll.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));

        // region NOT USED: ADD PARRALAX effect on header view of recycler view
        /*
        //SOURCE: http://stackoverflow.com/questions/26568087/parallax-header-effect-with-recyclerview  ! ANSWER 2 !
        recyclerViewAll.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View view = recyclerView.getChildAt(0);
                if(view != null && recyclerView.getChildAdapterPosition(view) == 0)  {
                    view.setTranslationY(-view.getTop() / 2);// or use view.animate().translateY();
                }
            }
        });*/
        // endregion

        recyclerViewClickHandler = new CustomRecyclerViewClickHandler(
                this, this, KEY_MAIN_ACTIVITY);

        recyclerViewAll.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity().getApplicationContext(),
                        recyclerViewAll, recyclerViewClickHandler));

        setRecyclerViewAdapterType();
    }

    private void initLoadingAndErrorViews(View view) {
        // loading & error views
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarFragmentAll);
        progressBar.setIndeterminate(true); //set the progress bar to be intermediate
        ivNoConnection = (ImageView) view.findViewById(R.id.ivNoConnectionMain);
    }

    // endregion

    // region LOAD & SHOW ARTICLE

    private void tryLoadAllArticles(Bundle savedInstanceState) {
        //try to load saved state from bundle
        if (savedInstanceState != null){ //if is not null = change of state - for example rotation of device
            restoreState(savedInstanceState); //restore saved state

            //ošetrenie prípadu, keď po rýchlom otočení po spustení ostal zoznam prázdny
            if (articlesAll.isEmpty()){ //ak je zoznam clankov prazdny,
                loadArticles();

            } else { //v pripade, ze nie je prazdny
                arvaAll.setArticlesList(articlesAll);
                progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)
                recyclerViewAll.setVisibility(View.VISIBLE);
            }

        } else {
            loadArticles();

        } //end else savedInstanceState

        //set adapter
        recyclerViewAll.setAdapter(arvaAll);
    }

    private void loadArticles() {
        pagesNum = 1; // set the page number
        startLoadingAnimation();

        //start the update task - will trigger onArticlesLoaded
        new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time
    }

    public void startRefresh(){
        //start the update task - will trigger onArticlesLoaded or void onLoadingError();
        new UpdateTask(this).execute(); //using SECONDARY constructor - setting refreshing to true
    }

    //endregion

    //region ArticlesLoadedListener METHODS

    /**
     * This method is called EVERY! time, even if there is problem with network.
     * It is because, even there is no network, it shows list of articles loaded from database.
     * And if network is ok, it shows updated list of articles.
     */
    @Override
    public void onArticlesLoaded(ArrayList<ArticleObj> listArticles) {
        // !!!!!!!!! TODO: solve memory problem with paging !!!

        //stop refreshing or loading animation -- important!
        stopLoadingOrRefreshingAnimation();

        articlesAll = listArticles;
        // if we don't want to change the reference
        //articlesAll.clear();
        //articlesAll.addAll(listArticles);

        ArticleObj headerArticle = getTestHeaderArticle(); //TODO: change this to real header article
        if (listener != null) {
            listener.showHeaderArticle(headerArticle);
        }

        arvaAll.setArticlesList(listArticles);
        pagesNum = 1; // !!
        recyclerViewAll.setVisibility(View.VISIBLE);
    }//end onArticlesLoaded

    public ArticleObj getTestHeaderArticle() {

        for (ArticleObj article : articlesAll) {
            if (!article.getSection().equalsIgnoreCase("baterka")){
                return article;
            }
        }
        return articlesAll.get(0);
    }

    @Override
    public void onLoadingError() {
        //stopLoadingOrRefreshingAnimation(); // this is called in onArticlesLoaded()
        listener.showNoConnection(getResources().getString(R.string.connection_error_msg));
    }

    @Override
    public void numNewArticles(int count) {
        Context context = getActivity().getApplicationContext();

        if (count == 0) {
            Toast.makeText(context, R.string.no_new_articles, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, getString(R.string.num_of_new_articles) + count, Toast.LENGTH_SHORT).show();
        }
    } // end numNewArticles

    // endregion

    // region SUPPORT METHODS

    public void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopLoadingOrRefreshingAnimation() {
        //stop refreshing or loading animation -- important!
        if (listener != null) {
            listener.stopRefreshingAnimation();
        }

        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)
    }

    // endregion

    private void setRecyclerViewAdapterType() {
        // in FRAGMENT (here): first parameter - context MUST be only getACTIVITY, and not getActivity().getAPPLICATIONContext()
        // - because Calligraphy wraps around ACTIVITY and not APPLICATION
        arvaAll = Util.getCrvaType(getActivity(), false); // false = doesn't have header

        // if used in ACTIVITY:
        //      first parameter - context MUST be 'this' (as activity), and not getApplicationContext()
        //      - because Calligraphy wraps around ACTIVITY and not APPLICATION
    }

    public void recyclerViewAdapterTypeChanged(){
        if (getActivity() != null){
            setRecyclerViewAdapterType();
        } else {
            arvaAll = Util.getCrvaType(CustomApplication.getCustomAppContext(), false); // doesn't have header
        }
        arvaAll.setArticlesList(articlesAll);
        recyclerViewAll.setAdapter(arvaAll);
    }

    // region SAVE & RESTORE STATE

    // SOURCE: https://developer.android.com/guide/components/activities/activity-lifecycle.html#saras
    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.i("LIFECYCLE", "MainActivity.onSaveInstanceState() was called");

        outState.putParcelableArrayList(KEY_SAVED_STATE_ARTICLES_ALL, articlesAll); // Save the current state of articlesAll
        outState.putInt(KEY_SAVED_STATE_PAGES_NUM, pagesNum); //save actual nubmer of actually loaded pages

        // !!! Always call the superclass so it can save the view hierarchy state !!
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {

        articlesAll = savedInstanceState.getParcelableArrayList(KEY_SAVED_STATE_ARTICLES_ALL);
        // if we don't want to change the reference = savedInstanceState.getParcelableArrayList(KEY_SAVED_STATE_ARTICLES_ALL);
        //ArrayList<ArticleObj> articlesList =
        //articlesAll.clear();
        //articlesAll.addAll(articlesList);

        pagesNum = savedInstanceState.getInt(KEY_SAVED_STATE_PAGES_NUM, 1);
    }

    // endregion

    public void tryConnectAgain() {
        new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time

        ivNoConnection.setVisibility(View.GONE);
        recyclerViewAll.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    // region LISTENERS METHODS

    @Override
    public void handleListStyleSelection(DialogInterface dialog, int listStyle) {
        session.setListStyle(listStyle); //save list style

        setRecyclerViewAdapterType();
        arvaAll.setArticlesList(articlesAll);
        recyclerViewAll.setAdapter(arvaAll);

        dialog.dismiss(); //dismiss the dialog
    }

    public int getPagesNum() {
        return pagesNum;
    }

    public void setPagesNum(int pagesNum) {
        this.pagesNum = pagesNum;
    }

    @Override
    public ArrayList<ArticleObj> getArticles() {
        return articlesAll;
    }

    @Override
    public ArticleRecyclerViewAdapter getAdapter() {
        return arvaAll;
    }

    // endregion

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface AllFragmentInteractionListener {

        void showNoConnection(String msg);

        void showHeaderArticle(ArticleObj headerArticle);

        void stopRefreshingAnimation();
    }
}