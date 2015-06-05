package bc.cestaplus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import bc.cestaplus.R;
import bc.cestaplus.network.Parser;
import bc.cestaplus.network.Requestor;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.objects.BaterkaText;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Templator;

import static java.lang.System.currentTimeMillis;

public class BaterkaFragment
    extends Fragment {

    private WebView mWebView;
    private TextView tvVolleyErrorArticle; // vypis chyb so sieťou

    private SessionManager session;

 //networking
    private VolleySingleton volleySingleton;

    /**
     * factory method
     */
    public static BaterkaFragment newInstance() {
        BaterkaFragment fragment = new BaterkaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public BaterkaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //inicializácia volleySingleton - dôležité !!!
        session = new SessionManager(CustomApplication.getCustomAppContext());
    }//end onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_baterka2, container, false);

        mWebView = (WebView) view.findViewById(R.id.webViewBaterka);
        tvVolleyErrorArticle = (TextView) view.findViewById(R.id.tvVolleyErrorBaterka);

        mWebView.getSettings().setBuiltInZoomControls(true);

        //check date
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        /*if ( fmt.format(currentTimeMillis()).equals(fmt.format(session.getLastBaterkaDate())) ) {
            // if dates are equal - load baterka from file

        } else {*/
            // if dates are NOT equal - load new baterka from API
            loadBaterkaTextFromAPI();

        //}

        return view;
    }//en onCreateView

    private void loadBaterkaTextFromAPI() {
        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                tvVolleyErrorArticle.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat

                BaterkaText baterkaText = Parser.parseBaterka(response); //uloženie stiahnutého textu do atribútu baterkaText

                //zobrazenie do webView
                mWebView.loadDataWithBaseURL(null, Templator.createBaterkaHtmlString(baterkaText, new Date(currentTimeMillis())),
                        "text/html", "utf-8", null);
            }//end of onResponse

        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                mWebView.setVisibility(View.GONE);
                volleySingleton.handleVolleyError(error, tvVolleyErrorArticle);
            } //end of onErrorResponse
        };

        //odoslanie requestu
        Requestor.createBaterkaRequest(volleySingleton.getRequestQueue(), new Date(currentTimeMillis()), responseLis, errorLis); //boolean = či aj z obrázkami
    } //end loadBaterkaTextFromAPI()

}//end BaterkaFragment
