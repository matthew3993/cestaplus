package bc.cestaplus.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import bc.cestaplus.R;
import bc.cestaplus.activities.konto_activities.LoggedActivity;
import bc.cestaplus.activities.konto_activities.NotLoggedActivity;
import bc.cestaplus.network.Parser;
import bc.cestaplus.network.Requestor;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.objects.BaterkaText;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Templator;
import bc.cestaplus.utilities.Util;

import static bc.cestaplus.extras.IKeys.KEY_BATERKA_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_MAIN_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_PARENT_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_RUBRIKA_ACTIVITY;

/**
 * Created by Matej on 19. 3. 2015.
 */
public class BaterkaActivity
    extends ActionBarActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener{

    //data
    private ArticleObj article;
    private BaterkaText baterkaText;
    private String parentActivity;

    //UI
    private WebView mWebView;
    private TextView tvVolleyErrorArticle; // vypis chyb so sieťou
    private ImageView ivRefresh;

    //networking
    private VolleySingleton volleySingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baterka);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext()); //inicializácia volleySingleton - dôležité !!!
        article = getIntent().getParcelableExtra("baterka");
        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);

        getSupportActionBar().setTitle("Baterka"); // nastavenie nadpisu aktivity

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //aby nesla sa zobrazila šípka UP

        mWebView = (WebView) findViewById(R.id.webViewArticle);
        tvVolleyErrorArticle = (TextView) findViewById(R.id.tvVolleyErrorArticle);
        ivRefresh = (ImageView) findViewById(R.id.ivRefreshArticle);

        //mWebView.getSettings().setBuiltInZoomControls(true);

        tryLoadBaterka();

        //register On Shared Preference Change Listener
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    } //end onCreate()

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

                if (session.getRola() > 0) {
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

    private void tryLoadBaterka(){
        if (Util.isNetworkAvailable(this)) {
            //vytvorí listenery a odošle request
            loadBaterka(); //naplní article text zobrazení do webView

        } else {
            showNoConnection("Nie ste pripojený k sieti!");
        }
    }

    private void showNoConnection(String msg) {
        //Toast.makeText(getApplicationContext(), "ERROR ", Toast.LENGTH_LONG).show();
        mWebView.setVisibility(View.GONE);

        tvVolleyErrorArticle.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);

        tvVolleyErrorArticle.setText(msg);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoadBaterka();
            }
        });
    }

    private void loadBaterka(){ //loading a text of Baterka

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                mWebView.setVisibility(View.VISIBLE);

                tvVolleyErrorArticle.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                ivRefresh.setVisibility(View.GONE);

                baterkaText = Parser.parseBaterka(response); //uloženie stiahnutého textu do atribútu baterkaText

                //zobrazenie do webView
                mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, article.getPubDate()),
                        "text/html", "utf-8", null);
            }//end of onResponse

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showNoConnection("Chyba pripojenia na server!");
            } //end of onErrorResponse
        };

        //odoslanie requestu
        Requestor.createBaterkaRequest(volleySingleton.getRequestQueue(), article.getPubDate(), responseLis, errorLis); //boolean = či aj z obrázkami

    }//end loadBaterka

    public void showTextSizeDialog() {
        // Session manager
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        String [] items = Util.getTextSizes();

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

        //reload webview
        mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, article.getPubDate()),
                "text/html", "utf-8", null);

        dialog.dismiss(); //dismiss the dialog

    } //end handleSelection()

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_text_size")) {
            //reload webview
            mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, article.getPubDate()),
                    "text/html", "utf-8", null);
        }
    }//end onSharedPreferenceChanged

}//end BaterkaActivity
