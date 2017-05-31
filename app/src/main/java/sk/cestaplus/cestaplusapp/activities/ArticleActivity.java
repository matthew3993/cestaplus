package sk.cestaplus.cestaplusapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.account_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.account_activities.LoginActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.SettingsActivity;
import sk.cestaplus.cestaplusapp.extras.IKeys;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.objects.ArticleText;
import sk.cestaplus.cestaplusapp.utilities.CustomNotificationManager;
import sk.cestaplus.cestaplusapp.utilities.LoginManager;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.SectionsUtil;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Templator;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.DateUtil;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.ImageUtil;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextSizeUtil;
import sk.cestaplus.cestaplusapp.utilities.Util;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//static imports
import static sk.cestaplus.cestaplusapp.extras.Constants.DELAY_TO_START_ACTIVITY_MILLIS;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.AEC_OK;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.NOTIFICATION_API_KEY_TEST;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ARTICLE_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_ARTICLE_URL;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PREF_TEXT_SIZE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_ARTICLE_ERROR_CODE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_SCROLL_PERC;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_ARTICLE_OBJ;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_ARTICLE_TEXT;

/**
 * Created by Matej on 19. 3. 2015.
 *
 * AlertDialogs SOURCES:
 *      Simple: http://stackoverflow.com/questions/26097513/android-simple-alert-dialog
 *      Theme.AppCompat Exception: http://stackoverflow.com/a/30181104
 *      TwoButtons: http://stackoverflow.com/a/8228190
 */
public class ArticleActivity
    extends AppCompatActivity
    implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoginManager.LoginManagerInteractionListener {

    //data
    private ArticleObj articleObj;
    private ArticleText articleText;
    private int articleErrorCode;
    private String parentActivity;
    private double scrollPercentage;
    private int loadPagesCount = 0;

    private int attrActionBarSize;
    private String sectionName;

    // utils
    private VolleySingleton volleySingleton; // networking
    private LoginManager loginManager;
    private SessionManager session;
    private int role;

    // UI components
    // layout views
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    // data views
    // header
    private RelativeLayout rlHeader;
    private NetworkImageView nivArticleImage;
    private TextView tvAuthor;
    private TextView tvTitle;
    private TextView tvDescription;

    // body
    private NestedScrollView nestedScrollView;
    private TextView tvDate;
    private WebView webView;
    private Button btnShowComments;

    // alert locked
    private LinearLayout llAlertLocked;
    private TextView tvAlertLockedTitle;

    // loading & error views
    private TextView tvVolleyErrorArticle; // network error
    private ImageView ivRefresh;
    private ProgressBar progressBar;       // loading animation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);
        initActivityDefaultFont(); // set up default font of activity

        // initialisations
        volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        loginManager = LoginManager.getInstance(getApplicationContext());
        articleObj = getIntent().getParcelableExtra(IKeys.KEY_INTENT_EXTRA_ARTICLE);
        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);

        session = new SessionManager(getApplicationContext());
        sectionName = SectionsUtil.getSectionTitle(articleObj.getSection());

        initToolbar();
        initLayoutViews();
        initDataViews();
        initLoadingAndErrorViews();

        //try to load saved state from bundle
        if (savedInstanceState != null) { //if is not null = change of state - for example rotation of device
            restoreState(savedInstanceState);

            // loading empty page solves the problem with blank space at the bottom of WebView
            // SOURCE: http://vision-apps.blogspot.sk/2012/08/android-webview-tips-tricks.html Point 4
            // load empty data to shrink the WebView instance
            Log.d("SCROLLING", "loading empty page");
            webView.loadUrl("file:///android_asset/Empty.html");

            hideErrorAndLoadingViews();
            showArticleText();
            loadAd();
        } else {
            //new start of activity
            tryLoadArticle();
        }

        //register On Shared Preference Change Listener
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    } //end onCreate

    private void scrollNestedScrollView() {
        final NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedScrollViewArticle);

        int totalHeight = nsv.getChildAt(0).getHeight();
        //int totalHeight = webView.getContentHeight();
        Log.d("SCROLLING", "totalheight: " + totalHeight);
        final int y = (int) Math.round( ((double)totalHeight / 100) * scrollPercentage );

        Log.d("SCROLLING", "percentage: " + scrollPercentage);
        Log.d("SCROLLING", "y: " + y);

        nsv.post(new Runnable() {
            public void run() {
                Log.d("SCROLLING", "scrolling nested scroll view to positions: 0 " + y);
                nsv.scrollTo(0, y);
            }
        });
    }

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
        toolbar = (Toolbar) findViewById(R.id.articleToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to show up arrow
    }

    private void initLayoutViews() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.articleCollapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(" ");

        appBarLayout = (AppBarLayout) findViewById(R.id.articleAppBarLayout);
        attrActionBarSize = Util.getActionBarSize(getApplicationContext());

        /*
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
                    collapsingToolbarLayout.setTitle(sectionName);
                    isShow = true;

                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it won't work
                    isShow = false;
                }
            }
        });
        */
    }

    private void initDataViews(){
        // header
        rlHeader = (RelativeLayout) findViewById(R.id.collapsingToolbarRelativeLayoutArticle);
        nivArticleImage = (NetworkImageView) findViewById(R.id.nivArticle);
        tvAuthor = (TextView) findViewById(R.id.tvArticleAuthor);
        tvTitle = (TextView) findViewById(R.id.tvArticleTitle);
        tvDescription = (TextView) findViewById(R.id.tvArticleDescription);

        // clicking header image will collapse the appbar
        nivArticleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setExpanded(false); // SOURCE: http://stackoverflow.com/questions/30655939/programmatically-collapse-or-expand-collapsingtoolbarlayout
            }
        });

        // adjust image height to screen height
        ImageUtil.resolveAdjustImageHeightToScreenHeight(this, nivArticleImage);

        // body
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollViewArticle);
        tvDate = (TextView) findViewById(R.id.tvDateArticle);
        webView = (WebView) findViewById(R.id.webViewArticle);
        btnShowComments = (Button) findViewById(R.id.btnShowCommentsArticle);

        btnShowComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), FacebookCommentsActivity.class);
                intent.putExtra(KEY_INTENT_ARTICLE_URL, articleText.getUrl());

                // delay the start of ArticleActivity because of onClick animation
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                }, DELAY_TO_START_ACTIVITY_MILLIS);
            }
        });

        // alert locked
        llAlertLocked = (LinearLayout) findViewById(R.id.alert_locked_layout);

        Log.d("SCROLLING", "setting web client");
        //webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        //webView.addJavascriptInterface(this, "MyApp");
    }

    private void initLoadingAndErrorViews() {
        // loading & error views
        tvVolleyErrorArticle = (TextView) findViewById(R.id.tvVolleyError);
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setIndeterminate(true);
    }

    //endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            /*
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            */
            case R.id.action_text_size: {
                TextSizeUtil.showTextSizeDialog(this);
                return true;
            }

            case R.id.account: {
                Class classToStart = Util.getAccountActivityToStart();

                Intent intent = new Intent(getApplicationContext(), classToStart);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                startActivity(intent);

                return true;
            }

            case R.id.action_settings: {
                // Launching the Settings activity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                startActivity(intent);
                return true;
            }

            case R.id.action_o_portali:{
                // Launching the O portáli activity
                Intent intent = new Intent(getApplicationContext(), OPortaliActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                startActivity(intent);
                //getActivity().finish();
                return true;
            }

            /*
            case R.id.action_database_manager: {
                // Launching the database manager
                Intent intent = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                startActivity(intent);

                //getActivity().finish();
                return true;
            }

            case R.id.action_delete_db: {
                // Launching the database manager
                MyApplication.getWritableDatabase().deleteArticlesAll();
                Toast.makeText(getApplicationContext(), "articles deleted!!", Toast.LENGTH_LONG).show();

                return true;
            }
            */
        } // end switch

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method implements what should happen when Up button is pressed
     */
    @Override
    public Intent getSupportParentActivityIntent() {
        return getCustomParentActivityIntent();
    }

    private Intent getCustomParentActivityIntent() {
        Intent i = null;

        switch (parentActivity){
            case KEY_MAIN_ACTIVITY:{
                i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }
        }

        return i;
    }

    // ======================================== OWN METHODS =====================================================================================

    // region LOAD & SHOW ARTICLE

    private void tryLoadArticle(){
        hideErrorViews();

        startLoadingAnimation();

        loadArticle(); //creates listeners and sends the request
    }

    private void loadArticle() {
        // create listeners
        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArticleActivity.this.onResponse(response);
            }//end of onResponse

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
        volleySingleton.createGetArticleRequest(articleObj.getID(), articleObj.isLocked(), responseLis, errorLis, true); //boolean = či aj z obrázkami
    }//end loadArticle()

    private void onResponse(JSONObject response) {
        role = session.getRole();

        // get articleErrorCode - AEC
        if (articleObj.isLocked()){
            //in case of locked articleObj, get error code from response
            articleErrorCode = Parser.parseErrorCode(response);

        } else {
            //in case of public articleObj error code is always OK
            articleErrorCode = AEC_OK;
        }

        articleText = Parser.parseArticleTextResponse(response); //parse response to articleText

        if ( (role == ROLE_LOGGED_SUBSCRIPTION_OK) && (articleErrorCode != AEC_OK) ){
            // role is "subscription ok", but there is a problem with api key
            // this means, that API_key is not valid => refresh API_key

            CustomNotificationManager.issueNotification("Problem with API key: " + session.getAPI_key(), NOTIFICATION_API_KEY_TEST+1); // debug notification

            loginManager.tryRelogin(1, this);

        } else { //role is "subscription ok" and there is no problem, or subscription is invalid
            showArticleText();

            loadAd();
        }
    }//end onResponse

    /**
     * Called in response listener.
     * Response listener sets the articleErrorCode and articleText.
     */
    private void showArticleText() {
        nivArticleImage.setImageUrl(articleObj.getImageUrl(), volleySingleton.getImageLoader());
        nivArticleImage.setErrorImageResId(R.drawable.baterka_vseobecna);

        tvAuthor.setText(articleText.getAuthor());
        TextUtil.setTitleText(getApplicationContext(), TextUtil.showLock(role, articleObj.isLocked()), articleObj.getTitle(), tvTitle, R.drawable.lock_white);
        tvDescription.setText(articleText.getShort_text()); //!!! not from articleObj object - because, there is shortened version of description

        tvDate.setText(DateUtil.getDateString(getApplicationContext(), articleObj.getPubDate()));

        //show text of articleObj in webView
        Log.d("SCROLLING", "loading REAL page");
        webView.loadDataWithBaseURL(null, Templator.createHtmlString(this, articleObj, articleText, articleErrorCode),
                "text/html", "utf-8", null); //will trigger onPageFinished() in MyWebViewClient

    //make UI changes
        hideErrorAndLoadingViews();
        showDataViews();
        //decideToShowAlertLocked(); // moved to --> MyWebViewClient onPageFinished
    }

    private void showDataViews() {
        rlHeader.setVisibility(View.VISIBLE);

        // OnOffsetChangedListener MUST be set after rlHeader is set Visible - to "show collapsing toolbar
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
                    collapsingToolbarLayout.setTitle(sectionName);
                    isShow = true;

                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it won't work
                    isShow = false;
                }
            }
        });

        nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void decideToShowAlertLocked() {
        if (articleErrorCode != AEC_OK) {
            showAlertLocked();
        }
    }

    private void showAlertLocked() {
        llAlertLocked.setVisibility(View.VISIBLE);
        tvAlertLockedTitle = (TextView) findViewById(R.id.tvAlertLockedTitle);

        TextUtil.setTextWithImageAtStart(getApplicationContext(), tvAlertLockedTitle.getText().toString().toUpperCase(), tvAlertLockedTitle, R.drawable.lock_alert_locked);

        Button btnAlertLocked = (Button) findViewById(R.id.btnAlertLocked);
        btnAlertLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: don't show url, but something like Subscription Activity

                String url = "http://www.cestaplus.sk/predplatne/info";
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    private void loadAd() {
        //load ads only if app is NOT logged mode
        if (session.getRole() == 0) { //user use apllication in NOT logged mode

            AdView adView = (AdView) findViewById(R.id.adView);

            if (adView != null) {
                adView.setVisibility(View.VISIBLE);

                AdRequest adRequest = new AdRequest.Builder()
                        //.addTestDevice("EFA584B4E2545BEBE8DC114EBD032C8B")
                        .build();

                adView.loadAd(adRequest);
            }
        }
    }//end loadAd()

    // endregion

    // region SUPPORT METHODS

    private void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setIndeterminate(true); // we don't need this, because we set intermediateOnly = true in layout
    }

    private void showNoConnection(String msg) {
        //hide data views
        rlHeader.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.GONE);

        showErrorViews();

        tvVolleyErrorArticle.setText(msg);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoadArticle();
            }
        });
    }

    private void showErrorViews() {
        //stop loading animation !
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

        tvVolleyErrorArticle.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);
    }

    private void hideErrorAndLoadingViews() {
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

        hideErrorViews();
    }

    private void hideErrorViews() {
        tvVolleyErrorArticle.setVisibility(View.GONE);
        ivRefresh.setVisibility(View.GONE);
    }

    // endregion

    // region SAVE & RESTORE STATE

    // SOURCE: https://developer.android.com/guide/components/activities/activity-lifecycle.html#saras
    @Override
    protected void onSaveInstanceState(Bundle outState){
        Log.d("SCROLLING", "onSaveInstanceState()");
        outState.putParcelable(KEY_SAVED_STATE_ARTICLE_OBJ, articleObj);
        outState.putParcelable(KEY_SAVED_STATE_ARTICLE_TEXT, articleText);
        outState.putInt(KEY_SAVED_ARTICLE_ERROR_CODE, articleErrorCode);

        NestedScrollView nsv = (NestedScrollView) findViewById(R.id.nestedScrollViewArticle);

        int totalHeight = nsv.getChildAt(0).getHeight();
        //int totalHeight = webView.getContentHeight();
        int y = nsv.getScrollY();
        double p = ((double)y/(double)totalHeight)*100;

        Log.d("SCROLLING", "totalheight: " + totalHeight);
        Log.d("SCROLLING", "y: " + y);
        Log.d("SCROLLING", "p: " + p);

        outState.putDouble(KEY_SAVED_SCROLL_PERC, p);

        // !!! Always call the superclass so it can save the view hierarchy state !!
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        Log.d("SCROLLING", "restoring state");

        articleObj = savedInstanceState.getParcelable(KEY_SAVED_STATE_ARTICLE_OBJ);
        articleText = savedInstanceState.getParcelable(KEY_SAVED_STATE_ARTICLE_TEXT);
        articleErrorCode = savedInstanceState.getInt(KEY_SAVED_ARTICLE_ERROR_CODE);

        scrollPercentage = savedInstanceState.getDouble(KEY_SAVED_SCROLL_PERC);
    }

    // endregion

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(KEY_PREF_TEXT_SIZE)) {
            showArticleText(); //reload views
        }
    }//end onSharedPreferenceChanged

    // region LoginManagerInterActionListener methods

    @Override
    public void onLoginSuccessful() {
        // relogin successful - subscription still OK
        // create new article request with NEW API_key
        tryLoadArticle();
    }

    @Override
    public void onLoginPartiallySuccessful() {
        // relogin not successful - subscription has EXPIRED

        //inform the user - show AlertDialog, after closing show LoggedActivity
        // SOURCE: check class comment
        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.alertDialog)).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(this.getString(R.string.login_partially_successful_msg));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    showArticleText();

                    // start LoggedActivity
                    Intent intent = new Intent(getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                    startActivity(intent);
                }
            });
        alertDialog.show();
    }

    @Override
    public void onLoginError(int login_error_code) {
        // case only if EMAIL_OR_PASSWORD_MISSING or WRONG_EMAIL_OR_PASSWORD
        // probably changed password

        // inform the user - show error toast
        Parser.handleLoginError(login_error_code);

        session.logout();

        //inform the user - show AlertDialog, after closing show LoginActivity
        // SOURCE: check class comment
        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.alertDialog)).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(this.getString(R.string.relogin_wrong_password));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    // start LoginActivity
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        alertDialog.show();
    }

    @Override
    public void onLoginNetworkError() {
        //unable to login for 3 times

        //inform the user - show AlertDialog, after closing show LoginActivity
        // SOURCE: check class comment
        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.alertDialog)).create();
        alertDialog.setTitle("Info");
        alertDialog.setMessage(this.getString(R.string.relogin_network_or_server_error));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        showArticleText();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.load_again),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        tryLoadArticle();
                    }
                });
        alertDialog.show();
    }

    //endregion

    // region private class MyWebViewClient

    /**
     * Class that handles scrolling during orientation changes and also clicks on url addresses
     * SOURCES: http://stackoverflow.com/questions/6855715/maintain-webview-content-scroll-position-on-orientation-change
     *          http://stackoverflow.com/a/16007049
     */
    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {

            Log.d("SCROLLING", "onPageLoaded()");
            // SOURCE: http://stackoverflow.com/a/26565217
            //webView.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");

            // if it is a new start of activity, first loaded page is the page we want to show.
            // During orientation changes first loaded page is always BLANK page
            if (loadPagesCount == 0) {
                Log.d("SCROLLING", "loadPagesCount == 0");
                loadPagesCount++;
                decideToShowAlertLocked();

            } else {
                Log.d("SCROLLING", "loadPagesCount == " + loadPagesCount);

                //SOURCES: http://stackoverflow.com/a/8927667
                //         http://stackoverflow.com/questions/23093513/android-webview-getcontentheight-always-returns-0
                if (view.getContentHeight() == 0) { //check if page is fully loaded
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("SCROLLING", "delay!!");
                            scrollNestedScrollView();
                            decideToShowAlertLocked();
                        }
                    }, 100);
                } else {
                    Log.d("SCROLLING", "non delay");
                    scrollNestedScrollView();
                    decideToShowAlertLocked();
                }
            }
            super.onPageFinished(view, url);
        }
        /*
        @Override
        public void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh); // don't forget this or things will break!
            Log.d(TAG, "WebView height" + getContentHeight());
        }*/


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (url != null/* && url.startsWith("http://")*/) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }// end of class MyWebClient

    // endregion

    // region JavascriptInterface - not used now

    // SOURCE: http://stackoverflow.com/a/26565217
    @JavascriptInterface
    public void resize(final float height) {
        ArticleActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int) (height * getResources().getDisplayMetrics().density)));
            }
        });
    }

    // endregion

}//end ArticleActivity
