package sk.cestaplus.cestaplusapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.fragments.AllFragment;
import sk.cestaplus.cestaplusapp.fragments.SectionFragment;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.CustomJobManager;
import sk.cestaplus.cestaplusapp.utilities.LoginManager;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.navDrawer.NavigationalDrawerPopulator;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.ImageUtil;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextUtil;
import sk.cestaplus.cestaplusapp.views.AnimatedExpandableListView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_EXTRA_ARTICLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_FROM_NOTIFICATION;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_HEADER_ARTICLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.TAG_ALL_FRAGMENT;

public class MainActivity
    extends AppCompatActivity
    implements
        SwipeRefreshLayout.OnRefreshListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        AppBarLayout.OnOffsetChangedListener,
        AllFragment.AllFragmentInteractionListener,
        SectionFragment.SectionFragmentInteractionListener {

    // data
    private ArticleObj headerArticle;

    // utils
    private VolleySingleton volleySingleton; //networking
    private SessionManager session;
    private int role;
    private CustomJobManager customJobManager;
    private LoginManager loginManager; // to check role

    // UI components
    //header article views
    private RelativeLayout rlHeader;
    private NetworkImageView nivHeaderImage;
    private TextView tvHeaderAuthor;
    private TextView tvHeaderTitle;
    private TextView tvHeaderDescription;

    // navigational drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    // layout views
    private CoordinatorLayout coordinatorLayout; // we need this reference for snackbar
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    // swipe refresh layouts
    private SwipeRefreshLayout swipeRefreshLayoutAll; // this one wraps entire activity layout, it wraps root coordinator layout, this one is really USED
    private SwipeRefreshLayout swipeRefreshLayoutRecyclerView; //this one is disabled - it only wraps recycler view


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); //set up the main view
        initActivityDefaultFont(); // set up default font of activity

        // initialisations
        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        role = session.getRole();
        customJobManager = CustomJobManager.getInstance(getApplicationContext());
        loginManager = LoginManager.getInstance(getApplicationContext()); // to check role

        // init views
        initToolbar();
        initLayoutViews();
        initHeaderViews();
        initNavigationalDrawer();

        loginManager.checkRole(this);

        initFragments(savedInstanceState);

        //try to load saved state from bundle
        if (savedInstanceState != null){ //if is not null = change of state - for example rotation of device
            restoreState(savedInstanceState); //restore saved state

        } else {
            //new start of application
            if (session.getPostNotificationStatus()){ //if notifications are on
                // create an update job
                // now using FirebaseJobDispatcher it is not needed to delay the scheduling of job,
                // because in time when job is built, job is not executed - only scheduled to be executed in future
                customJobManager.constructAndSheduleUpdateJob();
                /*
                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() { customJobManager.constructAndSheduleUpdateJob(); }
                                          },
                        //DELAY
                        //(UPDATE_PERIOD_MIN/2)*60*1000); //half from update period
                        CREATE_JOB_DELAY_SEC*1000); //30 seconds
                */
            }

        } //end else savedInstanceState

        //register shared preferences change listener
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);

        //Util.checkScreenSizeAndDensityToast(this);
    } // end ActivityMain onCreate method

    //region FONTS INIT - CALLIGRAPHY

    /**
     * Set up default font of activity Activity (not entire Application!) using Calligraphy lib
     * SOURCE: https://github.com/chrisjenx/Calligraphy
     */
    private void initActivityDefaultFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LatoLatin-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    /**
     * We NEED to override this method, because we MUST wrap the Activity! (not Application!) context
     * for Calligraphy to get working
     * SOURCE: https://github.com/chrisjenx/Calligraphy
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase)); // IMPORTANT for Calligraphy to get working
    }

    //endregion

    /**
     * It's here because of drawerToggle
     * @param savedInstanceState
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    /**
     * It's here because of drawerToggle
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //region INITIALISATION METHODS

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);
    }

    private void initLayoutViews() {
        // init drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.rootDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainActivityCoordinatorLayout); // we need this reference for snackbar

        //init SwipeRefreshLayouts
        //
        swipeRefreshLayoutAll = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutMainActivity);
        swipeRefreshLayoutAll.setOnRefreshListener(this);

        swipeRefreshLayoutRecyclerView = (SwipeRefreshLayout) findViewById(R.id.swipeRecyclerViewMain);
        swipeRefreshLayoutRecyclerView.setEnabled(false);

        // init appbar & toolbar
        appBarLayout = (AppBarLayout) findViewById(R.id.mainActivityAppBarLayout);
        appBarLayout.addOnOffsetChangedListener(this); // don't forget to set listener !!!

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.mainActivityCollapsingToolbarLayout);
    }

    private void initHeaderViews() {
        rlHeader = (RelativeLayout) findViewById(R.id.collapsingToolbarRelativeLayoutMainActivity);
        nivHeaderImage = (NetworkImageView) findViewById(R.id.nivMainActivityHeaderImage);
        tvHeaderAuthor = (TextView) findViewById(R.id.tvHeaderAuthor);
        tvHeaderTitle = (TextView) findViewById(R.id.tvHeaderTitle);
        tvHeaderDescription = (TextView) findViewById(R.id.tvHeaderDescription);

        View.OnClickListener startActivityListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startArticleActivityWithHeaderArticle();
            }
        };
        tvHeaderAuthor.setOnClickListener(startActivityListener);
        tvHeaderTitle.setOnClickListener(startActivityListener);
        tvHeaderDescription.setOnClickListener(startActivityListener);

        // clicking header image will collapse the appbar
        nivHeaderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setExpanded(false); // SOURCE: http://stackoverflow.com/questions/30655939/programmatically-collapse-or-expand-collapsingtoolbarlayout
            }
        });

        ImageUtil.resolveAdjustImageHeightToScreenHeight(this, nivHeaderImage);
    }

    private void startArticleActivityWithHeaderArticle() {
        final Intent intent;
        intent = new Intent(getApplicationContext(), ArticleActivity.class);
        intent.putExtra(KEY_INTENT_EXTRA_ARTICLE, headerArticle);
        intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);

        // we don't need delay, because there is no animation when clicking header article
        startActivity(intent);
    }

    private void initNavigationalDrawer() {
        new NavigationalDrawerPopulator(this).populateSectionsExpandableList();
    }

    private void initFragments(Bundle savedInstanceState) {

        if (savedInstanceState != null) {//if is not null = change of state - for example rotation of device
            // find previously added fragment by TAG
            // SOURCE: http://stackoverflow.com/questions/31743695/how-can-i-get-fragment-from-view
            AllFragment allFragment = (AllFragment) getSupportFragmentManager().findFragmentByTag(TAG_ALL_FRAGMENT);

            // do something you need with allFragment !!! CAN BE NULL, IF SECTIONS FRAGMENT WAS ACTIVE LAST TIME !!

        } else {// new start of application
            // create and add AllFragment - don't forget TAG
            // SOURCE: https://developer.android.com/guide/components/fragments.html#Adding
            replaceAllFragment();
        }
    }

    private void replaceAllFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager(); //!! not only getFragmentManager()!!
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AllFragment allFragment = AllFragment.newInstance();
        fragmentTransaction.replace(R.id.mainActivityMainFragmentContainer, allFragment, TAG_ALL_FRAGMENT);
        fragmentTransaction.commit();
    }

    //endregion

    @Override
    public void onNewIntent(Intent intent) {
        //when onNewIntent() is called from notification super.onNewIntent() automatically stops and hides other activities
        super.onNewIntent(intent); // so this is important!!

        if (intent.getBooleanExtra(KEY_INTENT_FROM_NOTIFICATION, false)) {
            //restart activity
            this.finish();

            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    } // end onNewIntent()

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        int backStackEntryCount = manager.getBackStackEntryCount();

        if (backStackEntryCount > 0) {
            // clear the back stack
            // SOURCE: http://stackoverflow.com/a/20591748
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

            toolbar.setTitle(R.string.app_name);

            AnimatedExpandableListView navDrlistView = (AnimatedExpandableListView) findViewById(R.id.navDrListViewSections);
            navDrlistView.setItemChecked(0, true); //set home as checked

        } else {
            super.onBackPressed();
        }
    }

    @Override
    /**
     * This method is needed to navigational drawer to be shown
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // region COMMUNICATION WITH ALL FRAGMENT

    public void showNoConnection(String msg) {
        // create snackbar
        // SOURCE: https://inthecheesefactory.com/blog/android-design-support-library-codelab/en
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.connection_error_msg), Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.tryAgain, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tryConnectAgain();
                    }
                });
        snackbar.show(); //don't forget to show created snackbar

        // disable swipe-right-to-dismiss behavior of snackbar
        // SOURCE: http://stackoverflow.com/questions/32183509/snackbar-with-coordinatorlayout-disable-dismiss  ANSWER 3
        snackbar.getView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                snackbar.getView().getViewTreeObserver().removeOnPreDrawListener(this);
                ((CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams()).setBehavior(null);
                return true;
            }
        });
    }

    private void tryConnectAgain() {
        rlHeader.setVisibility(View.GONE);

        // find fragment by it's container ID
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivityMainFragmentContainer);

        if(fragment instanceof AllFragment){
            ((AllFragment) fragment).tryConnectAgain();
        }

        if (fragment instanceof SectionFragment){
            ((SectionFragment) fragment).tryLoadArticles(true); // true = yes, show loading animation (indeterminate progress bar)
        }
    }

    @Override
    public void showHeaderArticle(ArticleObj headerArticle) {
        this.headerArticle = headerArticle;

        updateHeaderArticleViews();
    }

    private void updateHeaderArticleViews(){
        nivHeaderImage.setImageUrl(headerArticle.getImageUrl(), volleySingleton.getImageLoader());
        tvHeaderAuthor.setText(headerArticle.getAuthor());
        TextUtil.setTitleText(getApplicationContext(), TextUtil.showLock(role, headerArticle.isLocked()), headerArticle.getTitle(), tvHeaderTitle, R.drawable.lock_white);
        tvHeaderDescription.setText(headerArticle.getShort_text());

        //Util.adjustHeaderTitleTextSize(tvHeaderTitle); // not used for now

        rlHeader.setVisibility(View.VISIBLE);
    }

    public void stopRefreshingAnimation(){
        if(swipeRefreshLayoutAll.isRefreshing()) {
            swipeRefreshLayoutAll.setRefreshing(false);
        }
    }
    // endregion

    @Override
    public void onRefresh() {
        //refreshing animation will start automatically

        // find fragment by it's container ID
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivityMainFragmentContainer);

        if(fragment instanceof AllFragment){
            ((AllFragment) fragment).startRefresh();

        } else if (fragment instanceof SectionFragment){
            // false = do NOT show loading animation (indeterminate progress bar) - because of refresh// animation
            ((SectionFragment) fragment).tryLoadArticles(false);

        } else {
            stopRefreshingAnimation();
        }

    }//end onRefresh()

    // endregion

    // region SAVE & RESTORE STATE

    // SOURCE: https://developer.android.com/guide/components/activities/activity-lifecycle.html#saras
    @Override
    protected void onSaveInstanceState(Bundle outState){
        Log.i("LIFECYCLE", "MainActivity.onSaveInstanceState() was called");

        outState.putParcelable(KEY_SAVED_STATE_HEADER_ARTICLE, headerArticle); //save actual header article

        // !!! Always call the superclass so it can save the view hierarchy state !!
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        headerArticle = savedInstanceState.getParcelable(KEY_SAVED_STATE_HEADER_ARTICLE);

        updateHeaderArticleViews();
    }

    // endregion

    // region LISTENERS METHODS

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getString(R.string.pref_post_notifications_key))) {

            if (customJobManager == null){
                customJobManager = CustomJobManager.getInstance(getApplicationContext());
            }

            if (session.getPostNotificationStatus()){
                // notifications are now allowed - construct JOB
                customJobManager.constructAndSheduleUpdateJob();
                Toast.makeText(this, R.string.notifications_allowed, Toast.LENGTH_SHORT).show();

            } else {
                // notifications are now forbidden - CANCEL job
                customJobManager.cancelUpdateJob(); //cancel update job
                Toast.makeText(this, R.string.notifications_forbidden, Toast.LENGTH_SHORT).show();
            }
        }//end if

        if (key.equalsIgnoreCase(getString(R.string.pref_list_style_key))) {
            // find fragment by it's container ID
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainActivityMainFragmentContainer);

            if(fragment instanceof AllFragment){
                ((AllFragment) fragment).recyclerViewAdapterTypeChanged();
            }

            if (fragment instanceof SectionFragment){
                ((SectionFragment) fragment).recyclerViewAdapterTypeChanged();
            }
        }

    }//end onSharedPreferenceChanged

    /**
     * SOURCES: https://gist.github.com/blackcj/001a90c7775765ad5212
     *          http://stackoverflow.com/questions/30833589/scrolling-down-triggers-refresh-instead-of-revealing-the-toolbar
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //Log.d("SWIPE", "onOffsetChanged(), offset: " + verticalOffset);

        if (rlHeader.getVisibility() != View.GONE) {
            // rlHeader VISIBLE means that AllFragment is active
            // rlHeader GONE means that SectionFragment is active - and also that SectionFragment handles swipeRefreshLayout enabling/disabling

            if (verticalOffset == 0) { // if app bar fully expanded
                swipeRefreshLayoutAll.setEnabled(true);
            } else {
                swipeRefreshLayoutAll.setEnabled(false);
            }

        }
    }

    /**
     * Allows SectionFragment to handle swipeRefreshLayout enabling/disabling
     */
    public void setSwipeRefreshLayoutEnabled(boolean enabled){
        swipeRefreshLayoutAll.setEnabled(enabled);
    }

    //endregion
} // end MainActivity
