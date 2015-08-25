package bc.cestaplus.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import bc.cestaplus.activities.konto_activities.LoggedActivity;
import bc.cestaplus.activities.konto_activities.NotLoggedActivity;
import bc.cestaplus.network.Parser;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.objects.ArticleText;
import bc.cestaplus.R;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Templator;
import bc.cestaplus.utilities.Util;

//staticke importy
import static bc.cestaplus.extras.IKeys.KEY_O_PORTALI_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_PARENT_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_MAIN_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_ARTICLE_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_BATERKA_ACTIVITY;

/**
 * Created by Matej on 19. 3. 2015.
 */
public class OPortaliActivity
        extends ActionBarActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    //data
    private String parentActivity;

    //UI
    private WebView mWebView;
    private TextView tvVolleyErrorArticle; // vypis chyb so sieťou


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article); //layout is same as for ArticleActivity

        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);

        setTitle("O portáli"); // nastavenie nadpisu aktivity

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //ak by nesla navigacia UP, resp. sa nezobrazila šípka

        mWebView = (WebView) findViewById(R.id.webViewArticle);
        tvVolleyErrorArticle = (TextView) findViewById(R.id.tvVolleyErrorArticle);

        //mWebView.getSettings().setBuiltInZoomControls(true);

        //zobrazenie do webView
        mWebView.loadDataWithBaseURL(null, createOPortaliHtmlString(),
                "text/html", "utf-8", null);

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    } //end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oportali, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this); //standart way

                }
                return true;*/

            case R.id.action_text_size:
                showTextSizeDialog();
                return true;

            case R.id.account: {
                // Session manager
                final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRola() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_O_PORTALI_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_O_PORTALI_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_settings:
                // Launching the Settings activity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_O_PORTALI_ACTIVITY);
                startActivity(intent);
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
        }

        return i;
    }

    private String createOPortaliHtmlString() {
        return "<html>\n" +
                "    <head>\n" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/articleStyle.css\"/>\n" +
                "        <style>" +
                            Templator.getInternalArticleStyle() +
                "        </style>" +
                "        <style>" +
                "           h1 {color: black;" +
                "               font-weight: bold;" +
                "               margin-bottom: 10px;" +
                "           }" +
                "        </style>" +
                "    </head>\n" +
                "    <body>\n" +
                "        <div class=\"articleText\">\n" +
                "            <h1>O nás</h1>\n" +
                "            <font style=\"font-weight: bold; color: #005494;\">Cesta+</font> vznikla veľmi jednoducho." +
                "            Chceli sme mať kresťanské médium, ktoré bude hovoriť <b>zrozumiteľne a jednoducho Evanjelium</b>" +
                "            a prinášať <b>svedectvá a životné osudy ľudí</b>, ktorí toto Evanjelium žijú a <b>bojujú za dobro</b>." +
                "            <br />" +
                "            <br />" +
                "            <h1>O čo nám ide?</h1>" +
                "            <ul style=\"list-style-type:square\">" +
                "                <li>vyhľadávať a prinášať príbehy ľudí, ktorí nemusia byť známi, no žijú svoju vieru opravdivo a poctivo" +
                "                </li>" +
                "                <li>" +
                "                    prinášať Evanjelium zrozumiteľne, prakticky a moderným jazykom" +
                "                </li>" +
                "                <li>" +
                "                    prinášať témy zo života človeka (spoločenského, kultúrneho i politického) v optike Evanjelia" +
                "                </li>" +
                "            </ul>" +
                "            <br />" +
                "            <center>" +
                "               <img src=\"http://www.cestaplus.sk/images/skica_logo.jpg\">" +
                "            </center>" +
                "            <br />" +
                "            Názov <font style=\"font-weight: bold; color: #005494;\">cesta+ (cestaplus)</font>" +
                "            vznikol spomedzi mnohých návrhov, v ktorých nám išlo o rovnaké myšlienky: " +
                "            <b>byť sprievodcom človeka</b>, ktorý verí a zároveň žije v súčasnom svete a " +
                "            <b>ponúknúť mu obohatenie</b> na tejto ceste. Niečo, čím bude jeho cesta výnimočná a plus." +
                "        </div>" +
                "    </body>" +
                "</html>";
    }

    // ======================================== VLASTNÉ METÓDY =====================================================================================
    public void showTextSizeDialog() {
        // Session manager
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        String [] items = Util.getTextSizes();

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
        mWebView.loadDataWithBaseURL(null, createOPortaliHtmlString(),
                "text/html", "utf-8", null);

        dialog.dismiss(); //dismiss the dialog

    } //end handleSelection()

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_text_size")) {
            //reload webview
            mWebView.loadDataWithBaseURL(null, createOPortaliHtmlString(),
                    "text/html", "utf-8", null);
        }
    }//end onSharedPreferenceChanged

}//end ArticleActivity
