package sk.cestaplus.cestaplusapp.activities.account_activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.ArticleActivity;
import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.SettingsActivity;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.UserInfo;
import sk.cestaplus.cestaplusapp.utilities.DateFormats;
import sk.cestaplus.cestaplusapp.utilities.LoginManager;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Util;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.DateUtil;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.ImageUtil;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sk.cestaplus.cestaplusapp.extras.Constants.RED_REMAINING_DAYS_LIMIT;
import static sk.cestaplus.cestaplusapp.extras.Constants.URL_SUBSCRIPTION_PROLONG;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ARTICLE_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_BATERKA_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_LOGGED_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_O_PORTALI_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_USER_INFO;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_WAS_NETWORK_ERROR;

public class LoggedActivity
    extends AppCompatActivity
    implements LoginManager.LoginManagerInteractionListener{

    //data
    private UserInfo userInfo;
    private String parentActivity;
    private int attrActionBarSize;

    private LoginManager loginManager;

    // UI components
    // layout views
    private CoordinatorLayout rootCoordinatorLayout;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    // data views
    // header
    private RelativeLayout rlHeader;
    private ImageView ivLoggedHeaderImg;

    //body
    private NestedScrollView nestedScrollView;
    private TextView tvLoggedMainName;
    private TextView tvActualStatus;

    private View activeArea;

    // account data
    private TextView tvFullNameText;
    private TextView tvSubscriptionNameText;
    private TextView tvSubscriptionStartText;
    private TextView tvSubscriptionEndText;
    private TextView tvSubscriptionRemainingText;

    private RelativeLayout rlProlongSubscription;

    // loading & error views
    private TextView tvLoggedNetworkError; // network error
    private RelativeLayout loggedNetworkErrorRelativeLayout;
    private ImageView ivRefresh;
    private ProgressBar progressBar;     //loading animation

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logged);
        initActivityDefaultFont(); // set up default font of activity

        // initialisations
        loginManager = LoginManager.getInstance(getApplicationContext());

        // load data from intent
        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);

        initToolbar();
        initLayoutViews();
        initDataViews();
        initLoadingAndErrorViews();

        //try to load saved state from bundle
        if (savedInstanceState != null) { //if is not null = change of state - for example rotation of device

            boolean wasNetworkError = savedInstanceState.getBoolean(KEY_SAVED_STATE_WAS_NETWORK_ERROR, true);
            if (wasNetworkError){
                tryLoadUserInfo();

            } else {
                //was not network error
                restoreState(savedInstanceState); // restores userInfo from bundle
                hideErrorAndLoadingViews();

                SessionManager session = new SessionManager(getApplicationContext());
                int role = session.getRole();

                showUserInfoAcordingRole(role);
            }

        } else {
            //new start of activity
            tryLoadUserInfo();
        }

    } // end onCreate

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

    //region INITIALISATION METHODS

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.loggedToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to show up arrow
    }

    private void initLayoutViews() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.loggedCollapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(" "); // ! empty space !

        appBarLayout = (AppBarLayout) findViewById(R.id.loggedAppBarLayout);
        attrActionBarSize = Util.getActionBarSize(getApplicationContext());
    }

    private void initDataViews(){
        // header
        rlHeader = (RelativeLayout) findViewById(R.id.collapsingToolbarRelativeLayoutLogged);
        ivLoggedHeaderImg = (ImageView) findViewById(R.id.ivLoggedHeaderImg);

        // clicking header image will collapse the appbar
        ivLoggedHeaderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setExpanded(false); // SOURCE: http://stackoverflow.com/questions/30655939/programmatically-collapse-or-expand-collapsingtoolbarlayout
            }
        });

        ImageUtil.resolveAdjustBaterkaImageHeightToScreenHeight(this, ivLoggedHeaderImg); // BATERKA!!!

        // body
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollViewLogged);
        tvLoggedMainName = (TextView) findViewById(R.id.tvLoggedMainName);
        tvActualStatus = (TextView) findViewById(R.id.tvActualStatus);
    }

    /**
     * SOURCE of idea activeArea.findViewById(): https://stackoverflow.com/questions/4355122/how-to-include-a-layout-twice-in-android
     */
    private void initButtons() {
        // "button" prolong subscription
        rlProlongSubscription = (RelativeLayout) activeArea.findViewById(R.id.rlProlongSubscription);
        rlProlongSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: don't show url, but something like ProlongSubscription Activity

                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(URL_SUBSCRIPTION_PROLONG)));
            }
        });

        // button logout
        btnLogout = (Button) activeArea.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
            }
        });
    }

    /**
     * SOURCE of idea activeArea.findViewById(): https://stackoverflow.com/questions/4355122/how-to-include-a-layout-twice-in-android
     */
    private void initAccountDataViews() {
        // !! activeArea.findViewById() !!
        tvFullNameText = (TextView) activeArea.findViewById(R.id.tvFullNameText);
        tvSubscriptionNameText = (TextView) activeArea.findViewById(R.id.tvSubscriptionNameText);
        tvSubscriptionStartText = (TextView) activeArea.findViewById(R.id.tvSubscriptionStartText);
        tvSubscriptionEndText = (TextView) activeArea.findViewById(R.id.tvSubscriptionEndText);
        tvSubscriptionRemainingText = (TextView) activeArea.findViewById(R.id.tvSubscriptionRemainingText);
    }

    private void initLoadingAndErrorViews() {
        // loading & error views
        progressBar = (ProgressBar) findViewById(R.id.loggedProgressBar);
        progressBar.setIndeterminate(true);

        loggedNetworkErrorRelativeLayout = (RelativeLayout) findViewById(R.id.loggedNetworkErrorRelativeLayout);
        tvLoggedNetworkError = (TextView) findViewById(R.id.tvLoggedNetworkError);
        ivRefresh = (ImageView) findViewById(R.id.ivLoggedRefresh);
    }

    //endregion

    // region MENU & NAVIGATION METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logged, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Launching the Settings activity
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra(KEY_PARENT_ACTIVITY, KEY_LOGGED_ACTIVITY);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method implements what should happen when Up button is pressed
     * @return
     */
    @Override
    public Intent getSupportParentActivityIntent() {
        return getCustomParentActivityIntent();
    }

    private Intent getCustomParentActivityIntent() {
        Intent i = null;

        switch (parentActivity){
            case KEY_ARTICLE_ACTIVITY:{
                i = new Intent(this, ArticleActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }

            case KEY_BATERKA_ACTIVITY:{
                i = new Intent(this, BaterkaActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }

            case KEY_MAIN_ACTIVITY:{
                i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }

            case KEY_O_PORTALI_ACTIVITY:{
                i = new Intent(this, OPortaliActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }
        }

        return i;
    }

    // endregion

// ======================================== OWN METHODS =====================================================================================

    // region LOAD & SHOW USER INFO

    /**
     * Starts loading animation and calls method to load baterka
     */
    private void tryLoadUserInfo(){
        hideErrorViews();

        nestedScrollView.setVisibility(View.GONE); // hide data views

        startLoadingAnimation();

        // try to login - will trigger one of LoginManagerInteractionListener methods
        loginManager.tryLoginWithSavedCredentials(this);
    }

    /**
     * Fills the data vies with data in userInfo object.
     * This object have to be created and filled with data before calling this method.
     * Also activeArea have to be initialized before calling ths method.
     */
    private void showUserInfo(String actualStatusMsg){
        //set the data
        tvLoggedMainName.setText(String.format("%s,", userInfo.getName()));
        tvActualStatus.setText(actualStatusMsg);

        initButtons();
        initAccountDataViews();
        showAccountData();

        //make UI changes
        activeArea.setVisibility(View.VISIBLE);
        showDataViews();
    }

    private void showAccountData() {
        tvFullNameText.setText(userInfo.getFullName());
        tvSubscriptionNameText.setText(userInfo.getSubscription_name());
        tvSubscriptionStartText.setText(DateFormats.dateFormatSlovakPrint.format(userInfo.getSubscription_start()));
        tvSubscriptionEndText.setText(DateFormats.dateFormatSlovakPrint.format(userInfo.getSubscription_end()));

        //remaining days
        long remainingDays = DateUtil.daysBetween(userInfo.getSubscription_end());
        if (remainingDays < 0){
            remainingDays = 0;
        } else {
            //remainingDays++;
        }

        if (remainingDays <= RED_REMAINING_DAYS_LIMIT){
            TextUtil.setTextViewBoldAndRed(tvSubscriptionRemainingText, this);
            TextUtil.setTextViewBoldAndRed(tvSubscriptionEndText, this);
        }

        //tvSubscriptionRemainingText.setText(String.format("%d dní", remainingDays));
        // SOURCE: https://stackoverflow.com/questions/2397613/are-parameters-in-strings-xml-possible
        tvSubscriptionRemainingText.setText(getString(R.string.logged_subscription_remaining_format,
                remainingDays, TextUtil.getDaysString(remainingDays)));
    }

    private void showDataViews() {
        rlHeader.setVisibility(View.VISIBLE);

        // OnOffsetChangedListener MUST be set AFTER rlHeader is set Visible - to "show collapsing toolbar
        // layout title ONLY when collapsed" work properly

        //show collapsing toolbar layout title ONLY when collapsed
        //SOURCE: http://stackoverflow.com/questions/9398610/how-to-get-the-attr-reference-in-code
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < attrActionBarSize + 1) {
                    collapsingToolbarLayout.setTitle(getString(R.string.title_activity_logged));
                    isShow = true;

                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it won't work
                    isShow = false;
                }
            }
        });

        nestedScrollView.setVisibility(View.VISIBLE);
    }

    //endregion

    // region SUPPORT METHODS

    private void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setIndeterminate(true); // we don't need this, because we set intermediateOnly = true in layout
    }

    private void showNoConnection(String msg) {
        showErrorViews();

        tvLoggedNetworkError.setText(msg);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoadUserInfo();
            }
        });
    }

    private void showErrorViews() {
        //stop loading animation !
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

        // show error views
        loggedNetworkErrorRelativeLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorAndLoadingViews() {
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)
        hideErrorViews();
    }

    private void hideErrorViews() {
        loggedNetworkErrorRelativeLayout.setVisibility(View.GONE);
    }

    // endregion

    //region LoginManagerInteractionListener methods

    @Override
    public void onLoginSuccessful(UserInfo userInfo) {
        this.userInfo = userInfo;
        hideErrorAndLoadingViews();

        activeArea = findViewById(R.id.layout_subscription_ok);
        showUserInfo(getString(R.string.logged_actual_status_subscription_ok));
    }

    @Override
    public void onLoginPartiallySuccessful(UserInfo userInfo) {
        this.userInfo = userInfo;
        hideErrorAndLoadingViews();

        activeArea = findViewById(R.id.layout_subscription_expired);
        showUserInfo(getString(R.string.logged_actual_status_subscription_expired));

        TextUtil.setTextViewBoldAndRed(tvSubscriptionEndText, this);
    }

    @Override
    public void onLoginError(int error_code) {
        // this SHOULD NOT HAPPEN, because this - "LOGGED" Activity should start,
        // only if user was logged in time of starting this activity

        Parser.handleLoginError(error_code);
    }

    @Override
    public void onLoginNetworkError() {
        // volley error means problem with internet connection
        //Toast.makeText(getApplicationContext(),
                //R.string.login_network_error, Toast.LENGTH_LONG).show();

        showNoConnection(getString(R.string.logged_connection_err_msg));

        //retrieve user info from session
        SessionManager session = new SessionManager(getApplicationContext());
        userInfo = session.getUserInfo();
        int role = session.getRole();

        showUserInfoAcordingRole(role);
    }

    private void showUserInfoAcordingRole(int role) {
        if (Util.isSubscriptionValid(role)){
            activeArea = findViewById(R.id.layout_subscription_ok);
            showUserInfo(getString(R.string.logged_actual_status_subscription_ok));

        } else {
            activeArea = findViewById(R.id.layout_subscription_expired);
            showUserInfo(getString(R.string.logged_actual_status_subscription_expired));
            TextUtil.setTextViewBoldAndRed(tvSubscriptionEndText, this);
        }
    }

    // endregion

    // region SAVE & RESTORE STATE

    // SOURCE: https://developer.android.com/guide/components/activities/activity-lifecycle.html#saras
    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putParcelable(KEY_SAVED_STATE_USER_INFO, userInfo);

        boolean wasError = false;
        if (loggedNetworkErrorRelativeLayout.getVisibility() == View.VISIBLE){
            wasError = true;
        }
        outState.putBoolean(KEY_SAVED_STATE_WAS_NETWORK_ERROR, wasError);

        // !!! Always call the superclass so it can save the view hierarchy state !!
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        userInfo = savedInstanceState.getParcelable(KEY_SAVED_STATE_USER_INFO);
    }

    // endregion

    private void doLogout() {
        Toast.makeText(this, R.string.toast_logging_out, Toast.LENGTH_SHORT).show();

        SessionManager session = new SessionManager(getApplicationContext());
        session.logout();

        //clear the entire cache
        VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        volleySingleton.getRequestQueue().getCache().clear();

        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }//end doLogout

} //end LoggedActivity
