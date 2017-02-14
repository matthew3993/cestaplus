package sk.cestaplus.cestaplusapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.SettingsActivity;
import sk.cestaplus.cestaplusapp.activities.konto_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.konto_activities.NotLoggedActivity;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.Requestor;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.BaterkaText;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Templator;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static java.lang.System.currentTimeMillis;

public class BaterkaFragment_old
    extends Fragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    //data
    private BaterkaText baterkaText;

    //UI
    private WebView mWebView;
    private TextView tvVolleyErrorArticle; // vypis chyb so sieťou
    private ImageView ivRefresh;
    private ProgressBar progressBar;     //loading animation

    //session
    private SessionManager session;

    //networking
    private VolleySingleton volleySingleton;

    /**
     * factory method
     */
    public static BaterkaFragment_old newInstance() {
        BaterkaFragment_old fragment = new BaterkaFragment_old();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BaterkaFragment_old() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
        }

        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //inicializácia volleySingleton - dôležité !!!
        session = new SessionManager(CustomApplication.getCustomAppContext());

        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .registerOnSharedPreferenceChangeListener(this);
    }//end onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_baterka, container, false);

    //set up UI
        mWebView = (WebView) view.findViewById(R.id.webViewBaterka);
        tvVolleyErrorArticle = (TextView) view.findViewById(R.id.tvVolleyErrorBaterkaFragment);
        ivRefresh = (ImageView) view.findViewById(R.id.ivRefreshBaterkaFragment);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarBaterkaFragment); //find progressBar
        progressBar.setIndeterminate(true);

        //mWebView.getSettings().setBuiltInZoomControls(true);

        //check date
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        /*if ( fmt.format(currentTimeMillis()).equals(fmt.format(session.getLastBaterkaDate())) ) {
            // if dates are equal - load baterka from file

        } else {*/
            // if dates are NOT equal - load new baterka from API
            tryLoadBaterkaFromAPI();

        //}

        return view;
    }//en onCreateView

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_baterka_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_text_size:
                showTextSizeDialog();
                return true;

            case R.id.account: {
                // Session manager
                final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRola() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getActivity().getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_settings: {
                // Launching the Settings activity
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                startActivity(intent);
                return true;
            }

            case R.id.action_o_portali:
                // Launching the O portáli activity
                Intent intent = new Intent(getActivity().getApplicationContext(), OPortaliActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                startActivity(intent);

                //getActivity().finish();
                return true;
        } // end switch

        return super.onOptionsItemSelected(item);
    }

    private void tryLoadBaterkaFromAPI(){
        tvVolleyErrorArticle.setVisibility(View.GONE);
        ivRefresh.setVisibility(View.GONE);

        startLoadingAnimation();

        //vytvorí listenery a odošle request
        loadBaterkaFromAPI(); //naplní article text zobrazení do webView
    }

    private void showNoConnection(String msg) {
        //Toast.makeText(getApplicationContext(), "ERROR ", Toast.LENGTH_LONG).show();
        mWebView.setVisibility(View.GONE);
        //stop loading animation !
        progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

        tvVolleyErrorArticle.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.VISIBLE);

        tvVolleyErrorArticle.setText(msg);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoadBaterkaFromAPI();
            }
        });
    }

    private void loadBaterkaFromAPI() {
        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            //make UI changes
                //stop loading animation !
                progressBar.setVisibility(View.GONE); //this should automatically stop animation (based on visibility state of the progress bar)

                tvVolleyErrorArticle.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                ivRefresh.setVisibility(View.GONE);

                mWebView.setVisibility(View.VISIBLE);

            //logic
                baterkaText = Parser.parseBaterka(response); //uloženie stiahnutého textu do atribútu baterkaText

                //zobrazenie do webView
                mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, new Date(currentTimeMillis())),
                        "text/html", "utf-8", null);
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

        //odoslanie requestu
        Requestor.createBaterkaRequest(volleySingleton.getRequestQueue(), new Date(currentTimeMillis()), responseLis, errorLis); //boolean = či aj z obrázkami
    } //end loadBaterkaTextFromAPI()

    private void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        //progressBar.setIndeterminate(true); // we don't need this, because we set intermediateOnly = true in layout
    }

    public void showTextSizeDialog() {
        // Session manager
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        String [] items = Util.getTextSizes(getActivity().getApplicationContext());

        new AlertDialog.Builder(getActivity()/*.getApplicationContext()*/)// it don't use application context, but activity context !
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
        mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, new Date(currentTimeMillis())), //TODO check problem with date
                "text/html", "utf-8", null);

        dialog.dismiss(); //dismiss the dialog

    } //end handleListStyleSelection()

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("pref_text_size")) {
            //reload webview
            mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, new Date(currentTimeMillis())), //TODO check problem with date
                    "text/html", "utf-8", null);
        }
    }//end onSharedPreferenceChanged

}//end BaterkaFragment_old
