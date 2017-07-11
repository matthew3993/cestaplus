package sk.cestaplus.cestaplusapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.Date;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.account_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.account_activities.NotLoggedActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.SettingsActivity;
import sk.cestaplus.cestaplusapp.extras.IKeys;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.Requestor;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.objects.BaterkaText;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.ImageUtil;
import sk.cestaplus.cestaplusapp.utilities.Util;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.DateUtil;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextSizeUtil;
import sk.cestaplus.cestaplusapp.views.CircularTextView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static java.lang.System.currentTimeMillis;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_BATERKA_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_LOAD_BATERKA_ON_TODAY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_ARTICLE_OBJ;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SAVED_STATE_BATERKA_TEXT;

/**
 * Created by Matej on 19. 3. 2015.
 */
public class BaterkaActivity
    extends AppCompatActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener{

    //data
    private ArticleObj articleObj;
    private BaterkaText baterkaText;
    private String parentActivity;
    private int attrActionBarSize;
    private boolean loadBaterkaOnToday;

    // utils
    private VolleySingleton volleySingleton; // networking

    // UI components
    // layout views
    private CoordinatorLayout rootCoordinatorLayout;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    // data views
    // header
    private RelativeLayout rlHeader;
    private NetworkImageView nivBaterkaImage;
    private TextView tvAuthor;
    private TextView tvTitle;

    //body
    private NestedScrollView nestedScrollView;
    private TextView tvCoordinates;
    private TextView tvScripture;

    //private NetworkImageView ivAuthor;
    private TextView tvDayInWeek;
    private TextView tvDate;

    private TextView tvMeditationText;

    private TextView tvQuotation;
    private TextView tvQuotationAuthor;

    //Button btnThankForMeditation;
    //TextView tvThanksCount;

    private CircularTextView circtvDepth1;
    private TextView tvDepth1;
    private CircularTextView circtvDepth2;
    private TextView tvDepth2;
    private CircularTextView circtvDepth3;
    private TextView tvDepth3;

    private TextView tvTipText;

    // loading & error views
    private TextView tvVolleyErrorBaterka; // network error
    private ImageView ivRefresh;
    private ProgressBar progressBar;     //loading animation


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_baterka); //set up the main view
        initActivityDefaultFont(); // set up default font of activity

        // initialisations
        volleySingleton = VolleySingleton.getInstance(getApplicationContext()); // !!!

        // load data from intent
        articleObj = getIntent().getParcelableExtra(IKeys.KEY_INTENT_EXTRA_BATERKA);
        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);
        loadBaterkaOnToday = getIntent().getExtras().getBoolean(KEY_INTENT_LOAD_BATERKA_ON_TODAY, false); // false == default value

        initToolbar();
        initLayoutViews();
        initDataViews();
        initLoadingAndErrorViews();

        //try to load saved state from bundle
        if (savedInstanceState != null) { //if is not null = change of state - for example rotation of device
            restoreState(savedInstanceState);
            showBaterkaText();
        } else {
            //new start of activity
            tryLoadBaterka();
        }

        //register On Shared Preference Change Listener
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    } //end onCreate()


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
        toolbar = (Toolbar) findViewById(R.id.baterkaActivityToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to show up arrow
    }

    private void initLayoutViews() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.baterkaCollapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(" "); // ! empty space !

        appBarLayout = (AppBarLayout) findViewById(R.id.baterkaAppBarLayout);
        attrActionBarSize = Util.getActionBarSize(getApplicationContext());
    }

    private void initDataViews(){
        // header
        rlHeader = (RelativeLayout) findViewById(R.id.collapsingToolbarRelativeLayoutBaterka);
        nivBaterkaImage = (NetworkImageView) findViewById(R.id.nivBaterka);
        tvAuthor = (TextView) findViewById(R.id.tvBaterkaAuthor);
        tvTitle = (TextView) findViewById(R.id.tvBaterkaTitle);

        // clicking header image will collapse the appbar
        nivBaterkaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setExpanded(false); // SOURCE: http://stackoverflow.com/questions/30655939/programmatically-collapse-or-expand-collapsingtoolbarlayout
            }
        });

        ImageUtil.resolveAdjustBaterkaImageHeightToScreenHeight(this, nivBaterkaImage); // BATERKA!!!

        // body
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollViewBaterka);

        tvCoordinates = (TextView) findViewById(R.id.tvCoordinates);
        tvScripture = (TextView) findViewById(R.id.tvScripture);

        // ivAuthor = (TextView) findViewById(R.id.);
        tvDayInWeek = (TextView) findViewById(R.id.tvDayWeek);
        tvDate = (TextView) findViewById(R.id.tvDateBaterka);

        tvMeditationText = (TextView) findViewById(R.id.tvMeditationText);

        tvQuotation = (TextView) findViewById(R.id.tvQuotationText);
        tvQuotationAuthor = (TextView) findViewById(R.id.tvQuotationAuthor);

        //Button btnThankForMeditation;
        //TextView tvThanksCount;

        circtvDepth1 = (CircularTextView) findViewById(R.id.circtvDepth1);
        tvDepth1 = (TextView) findViewById(R.id.tvDepth1);
        circtvDepth2 = (CircularTextView) findViewById(R.id.circtvDepth2);
        tvDepth2 = (TextView) findViewById(R.id.tvDepth2);
        circtvDepth3 = (CircularTextView) findViewById(R.id.circtvDepth3);
        tvDepth3 = (TextView) findViewById(R.id.tvDepth3);

        tvTipText = (TextView) findViewById(R.id.tvTipText);
    }

    private void initLoadingAndErrorViews() {
        // loading & error views
        tvVolleyErrorBaterka = (TextView) findViewById(R.id.tvVolleyError);
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
    }

    //endregion

    // region MENU & NAVIGATION METHODS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article_activity, menu); //Article activity has the same menu options
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            */
            case R.id.action_text_size:
                showTextSizeDialog();
                return true;

            case R.id.account: {
                // Session manager
                final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRole() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_BATERKA_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_BATERKA_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_settings: {
                // Launching the Settings activity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_BATERKA_ACTIVITY);
                startActivity(intent);
                return true;
            }

            case R.id.action_o_portali:
                // Launching the O portáli activity
                Intent intent = new Intent(getApplicationContext(), OPortaliActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_BATERKA_ACTIVITY);
                startActivity(intent);
                //getActivity().finish();
                return true;

        } // end switch

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
            case KEY_MAIN_ACTIVITY:{
                i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }
        }

        return i;
    }

    // endregion

// ======================================== OWN METHODS =====================================================================================

    // region LOAD & SHOW BATERKA TEXT

    /**
     * Starts loading animation and calls method to load baterka
     */
    private void tryLoadBaterka(){
        hideErrorViews();

        startLoadingAnimation();

        loadBaterka(); //creates listeners and sends the request
    }

    private void loadBaterka(){ //loading a text of Baterka

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                baterkaText = Parser.parseBaterka(response); //parse response
                showBaterkaText(); //show parsed response
            }//end of onResponse
        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // show error views
                if (error instanceof NoConnectionError){
                    showNoConnection(getResources().getString(R.string.no_connection_err_msg));
                } else {
                    showNoConnection(getResources().getString(R.string.connection_error_msg));
                }
            } //end of onErrorResponse
            };

        //send the request
        Date dateToLoad = getDateToLoad();

        Requestor.createBaterkaRequest(volleySingleton.getRequestQueue(), dateToLoad, responseLis, errorLis);

    }//end loadBaterka

    private Date getDateToLoad() {
        Date dateToLoad;
        if (loadBaterkaOnToday){
            dateToLoad = new Date(currentTimeMillis());
        } else {
            dateToLoad = articleObj.getPubDate();
        }
        return dateToLoad;
    }

    /**
     * Fills the data vies with data in articleObj and baterkaText objects.
     * These object have to be created and filled with data before calling this method.
     */
    private void showBaterkaText(){
    //set the data
        nivBaterkaImage.setImageUrl(baterkaText.getImageUrl(), volleySingleton.getImageLoader());
        nivBaterkaImage.setErrorImageResId(R.drawable.baterka_vseobecna);

        tvAuthor.setText(baterkaText.getAuthor());
        tvTitle.setText(baterkaText.getTitle());

        tvCoordinates.setText(baterkaText.getCoordinates());
        tvScripture.setText(Util.stripHtml(baterkaText.getScripture()));

        //private NetworkImageView ivAuthor;
        Date date = getDateToLoad();

        tvDayInWeek.setText(DateUtil.resolveDayInWeek(getApplicationContext(), date));
        String dateString = DateUtil.getDateString(getApplicationContext(), date);
        tvDate.setText(dateString);

        tvMeditationText.setText(Util.stripHtml(baterkaText.getText()));

        tvQuotation.setText(baterkaText.getQuote());
        tvQuotationAuthor.setText(baterkaText.getQuoteAuthor());

        //Button btnThankForMeditation;
        //TextView tvThanksCount;

        tvDepth1.setText(baterkaText.getDepth1());
        tvDepth2.setText(baterkaText.getDepth2());
        tvDepth3.setText(baterkaText.getDepth3());

        tvTipText.setText(baterkaText.getTip());

    //make UI changes
        hideErrorAndLoadingViews();
        showDataViews();
    }

    private void showDataViews() {
        rlHeader.setVisibility(View.VISIBLE);

        // OnOffsetChangedListener MUST be set AFTER rlHeader is set Visible - to "show collapsing toolbar
        // layout title ONLY when collapsed" work properly
        Util.setOnOffsetChangedListener(appBarLayout, attrActionBarSize, collapsingToolbarLayout, baterkaText.getTitle());

        nestedScrollView.setVisibility(View.VISIBLE);
    }

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

        tvVolleyErrorBaterka.setText(msg);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoadBaterka();
            }
        });
    }

    private void showErrorViews() {
        //stop loading animation !
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

        // show error views
        tvVolleyErrorBaterka.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);
    }

    private void hideErrorAndLoadingViews() {
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)
        hideErrorViews();
    }

    private void hideErrorViews() {
        tvVolleyErrorBaterka.setVisibility(View.GONE);
        ivRefresh.setVisibility(View.GONE);
    }

    // endregion

    // TODO: similar method is in Util class - try to make changes to get only one
    public void showTextSizeDialog() {
        // Session manager
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        String [] items = TextSizeUtil.getTextSizes(getApplicationContext());

        new AlertDialog.Builder(this)
                .setTitle("Vyberte veľkosť písma: ")
                .setCancelable(true)
                .setSingleChoiceItems(items, session.getTextSize(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item_num) {
                                switch (item_num) {
                                    case 0:
                                        handleSelection(dialog, session, 0); //TEXT_SIZE_SMALL
                                        break;

                                    case 1:
                                        handleSelection(dialog, session, 1); //TEXT_SIZE_NORMAL
                                        break;

                                    case 2:
                                        handleSelection(dialog, session, 2); //TEXT_SIZE_BIG
                                        break;
                                }
                            }
                        })
                .show();
    }//end showTextSizeDialog()

    private void handleSelection(DialogInterface dialog, SessionManager session, int textSize) {
        session.setTextSize(textSize); //save chosen size

        //reload views
        showBaterkaText();

        dialog.dismiss(); //dismiss the dialog

    } //end handleListStyleSelection()

    // region SAVE & RESTORE STATE

    // SOURCE: https://developer.android.com/guide/components/activities/activity-lifecycle.html#saras
    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putParcelable(KEY_SAVED_STATE_ARTICLE_OBJ, articleObj);
        outState.putParcelable(KEY_SAVED_STATE_BATERKA_TEXT, baterkaText);

        // !!! Always call the superclass so it can save the view hierarchy state !!
        super.onSaveInstanceState(outState);
    }

    private void restoreState(Bundle savedInstanceState) {
        articleObj = savedInstanceState.getParcelable(KEY_SAVED_STATE_ARTICLE_OBJ);
        baterkaText = savedInstanceState.getParcelable(KEY_SAVED_STATE_BATERKA_TEXT);
    }

    // endregion

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getString(R.string.pref_text_size_key))) {
            // TODO: implement this feature - issue #8, todo #4
            // change of TEXT SIZE
            showBaterkaText(); //reload views
        }
    }//end onSharedPreferenceChanged

}//end BaterkaActivity
