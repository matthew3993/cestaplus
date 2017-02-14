package sk.cestaplus.cestaplusapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.konto_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.konto_activities.NotLoggedActivity;
import sk.cestaplus.cestaplusapp.extras.IKeys;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.objects.ArticleText;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SectionsUtil;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Templator;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ARTICLE_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_RUBRIKA_ACTIVITY;

//staticke importy

/**
 * Created by Matej on 19. 3. 2015.
 */
public class ArticleActivity_old
    extends ActionBarActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener{

    //data
    private ArticleObj article;
    private ArticleText articleText;
    private int articleErrorCode;
    private String parentActivity;

    //UI
    private WebView mWebView;
    private TextView tvVolleyErrorArticle; // vypis chyb so sieťou
    private ImageView ivRefresh;
    private ProgressBar progressBar;     //loading animation

    //networking
    private VolleySingleton volleySingleton;

    // Session manager
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext()); //inicializácia volleySingleton - dôležité !!!
        article = getIntent().getParcelableExtra(IKeys.KEY_INTENT_EXTRA_ARTICLE);
        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);

        session = new SessionManager(getApplicationContext());

        getSupportActionBar().setTitle(SectionsUtil.getSectionTitle(article.getSection())); //nastavenie label-u konkretnej aktivity

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //ak by nesla navigacia UP, resp. sa nezobrazila šípka

        mWebView = (WebView) findViewById(R.id.webViewArticle); //find webView - important!!
        tvVolleyErrorArticle = (TextView) findViewById(R.id.tvVolleyErrorArticle);
        ivRefresh = (ImageView) findViewById(R.id.ivRefreshArticle);
        progressBar = (ProgressBar) findViewById(R.id.progressBarArticle); //find progressBar
        progressBar.setIndeterminate(true);

        //mWebView.getSettings().setBuiltInZoomControls(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.setWebViewClient(new WebViewClient(){
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null/* && url.startsWith("http://")*/) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        tryLoadArticle();

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    } //end onCreate

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

            case R.id.action_text_size:
                showTextSizeDialog();
                return true;

            case R.id.account: {
                // Session manager
                final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRola() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_settings:{
                // Launching the Settings activity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
                startActivity(intent);
                return true;
            }

            case R.id.action_o_portali:
                // Launching the O portáli activity
                Intent intent = new Intent(getApplicationContext(), OPortaliActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_ARTICLE_ACTIVITY);
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

            case KEY_RUBRIKA_ACTIVITY:{
                i = new Intent(this, SectionActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }

        }

        return i;
    }

    // ======================================== VLASTNÉ METÓDY =====================================================================================
    private void tryLoadArticle(){
        //if (Util.isNetworkAvailable(this)) {
            tvVolleyErrorArticle.setVisibility(View.GONE);
            ivRefresh.setVisibility(View.GONE);

            startLoadingAnimation();

            //vytvorí listenery a odošle request
            loadArticle(); //naplní article text zobrazení do webView

        //} else {
        //    showNoConnection("Nie ste pripojený k sieti!");
        //}
    }

    private void showNoConnection(String msg) {
        mWebView.setVisibility(View.GONE);
        //stop loading animation !
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

        tvVolleyErrorArticle.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);

        tvVolleyErrorArticle.setText(msg);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoadArticle();
            }
        });
    }

    private void loadArticle() {
        //nacitanie short text-u, autora a textu článku
        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                createResponseListener(response);
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
        volleySingleton.createGetArticleRequest(article.getID(), article.isLocked(), responseLis, errorLis, true); //boolean = či aj z obrázkami
    }//end loadArticle()

    private void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setIndeterminate(true); // we don't need this, because we set intermediateOnly = true in layout
    }

    private void createResponseListener(JSONObject response) {
    //make UI changes
        //stop loading animation !
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

        tvVolleyErrorArticle.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
        ivRefresh.setVisibility(View.GONE);

        mWebView.setVisibility(View.VISIBLE);

    //logic
        if (article.isLocked()){ //zamknuté články
            articleErrorCode = Parser.parseErrorCode(response);//get error code from response

        } else { //verejné články
            articleErrorCode = 0;
        }

        if ((articleErrorCode > 0) && (session.getRola() > 0)){//user is logged in, but there is a problem with api key
            //Toast.makeText(this, "Prihlasujem...", Toast.LENGTH_SHORT).show();
            tryRelogin(1);

        } else {//user is logged and there is no problem, or user is not logged
            articleText = Parser.parseArticleTextResponse(response); //uloženie stiahnutého textu do atribútu articleText

            //zobrazenie do webView
            mWebView.loadDataWithBaseURL(null, Templator.createHtmlString(this, article, articleText, articleErrorCode),
                    "text/html", "utf-8", null);

            //load ad
            //loadAd();
        }
    }//end createResponseListener

    /*
    private void loadAd() {
        //load ads only if app is NOT logged mode
        AdView adView = (AdView) findViewById(R.id.adView);

        if (session.getRola() == 0) { //user use apllication in NOT logged mode

            AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("EFA584B4E2545BEBE8DC114EBD032C8B")
                    .build();

            adView.loadAd(adRequest);

        } else {
            adView.setVisibility(View.GONE);
        }//end if
    }//end loadAd()
    */

    public void showTextSizeDialog() {
        // Session manager
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        String [] items = Util.getTextSizes(this);

        new AlertDialog.Builder(this)
                .setTitle("Vyberte veľkosť písma: ")
                .setCancelable(false)
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

        //reload webview
        mWebView.loadDataWithBaseURL(null, Templator.createHtmlString(this, article, articleText, articleErrorCode),
                "text/html", "utf-8", null);

        dialog.dismiss(); //dismiss the dialog

    } //end handleListStyleSelection()

    private void tryRelogin(final int count) {

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){

                int error_code = Parser.parseErrorCode(response);

                if (error_code == 0){ //relogin succesfull
                    //inform the user
                    //Toast.makeText(CustomApplication.getCustomAppContext(), "Prihlásenie úspešné!", Toast.LENGTH_LONG).show();

                    //parse and save new API key
                    String API_key = Parser.parseAPI_key(response);
                    session.setAPI_key(API_key);

                    //load article
                    tryLoadArticle();

                } else { //error_code != 0
                    //TODO: check errorcodes - ak plati aj chyba pripojenia na databazu, tak upravit tento kod, aby ak nastane chyba pripojenia na databazu urobil opat reloging
                    //relogin NOT succesfull, propable changed password or account expired

                    // inform the user - show error
                    Parser.handleLoginErrorCode(error_code);

                    //logout
                    session.logout();

                    // Launch the login activity
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }//end onResponse
        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (count < 4){
                    //try to login again
                    tryRelogin(count + 1);

                } else {
                    //unable to login for 3 times = logout and show login activity

                    // inform the user - show error
                    Toast.makeText(getApplicationContext(),
                        "CHYBA PRIHLASOVANIA " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    //logout
                    session.logout();

                    // Launch the login activity
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        volleySingleton.createReLoginRequest(responseLis, errorLis);
    }//end relogin

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_text_size")) {
            //reload webview
            mWebView.loadDataWithBaseURL(null, Templator.createHtmlString(this, article, articleText, articleErrorCode),
                    "text/html", "utf-8", null);
        }
    }//end onSharedPreferenceChanged

}//end ArticleActivity
