package bc.cestaplus.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

import bc.cestaplus.R;
import bc.cestaplus.network.Parser;
import bc.cestaplus.network.Requestor;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.objects.BaterkaText;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Templator;
import bc.cestaplus.utilities.Util;

/**
 * Created by Matej on 19. 3. 2015.
 */
public class BaterkaActivity
    extends ActionBarActivity {

    //data
    private ArticleObj article;
    private BaterkaText baterkaText;

    //UI
    private WebView mWebView;
    private TextView tvVolleyErrorArticle; // vypis chyb so sieťou

    //networking
    private VolleySingleton volleySingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article); //layout is same as for ArticleActivity

        volleySingleton = VolleySingleton.getInstance(getApplicationContext()); //inicializácia volleySingleton - dôležité !!!
        article = getIntent().getParcelableExtra("baterka");

        getSupportActionBar().setTitle("Baterka"); // nastavenie nadpisu aktivity

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); ak by nesla navigacia UP, resp. sa nezobrazila šípka

        mWebView = (WebView) findViewById(R.id.webViewArticle);
        tvVolleyErrorArticle = (TextView) findViewById(R.id.tvVolleyErrorArticle);

        mWebView.getSettings().setBuiltInZoomControls(true);

        loadBaterka(); //vytvorí listenery a request

    } //end onCreate()

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
            case R.id.action_text_size:
                showTextSizeDialog();
                return true;

            case R.id.action_settings:
                return true;

        } // end switch

        return super.onOptionsItemSelected(item);
    }


// ======================================== VLASTNÉ METÓDY =====================================================================================

    private void loadBaterka(){ //loading a text of Baterka

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                tvVolleyErrorArticle.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat

                baterkaText = Parser.parseBaterka(response); //uloženie stiahnutého textu do atribútu baterkaText

                //zobrazenie do webView
                mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, article.getPubDate()),
                        "text/html", "utf-8", null);
            }//end of onResponse

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                mWebView.setVisibility(View.GONE);
                volleySingleton.handleVolleyError(error, tvVolleyErrorArticle);
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
        mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, article.getPubDate()),
                "text/html", "utf-8", null);

        dialog.dismiss(); //dismiss the dialog

    } //end handleSelection()

}//end BaterkaActivity
