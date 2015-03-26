package bc.cestaplus.network;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bc.cestaplus.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter;
import bc.cestaplus.network.requests.JsonArrayCustomUtf8Request;
import bc.cestaplus.utilities.CustomApplication;

import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SHORT_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ID;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_IMAGE_URL;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_LOCKED;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_PUB_DATE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SECTION;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TITLE;

/**
 * Created by Matej on 14.3.2015.
 */
public class NetworkManager {

//networking
    private static VolleySingleton volleySingleton;
    private static RequestQueue requestQueue;

// data
    //private static ArrayList<ArticleObj> zoznamClankov; // konkretne pomenovanie vo FragmentePrehlad
    private static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    /**
     * Konstruktor
     */
    public NetworkManager() {
    }

    public static void sendGetClankyRequestPOST(String druhDopytu, int limit, int stranka,
                                                final ArrayList<ArticleObj> clanky, final ClanokRecyclerViewAdapter crva,
                                                final TextView tvVolleyError){

        String url = "http://vaii.fri.uniza.sk/~mahut8/bc/jsonTest.php"; // cielova adresa

    // vytvorenie Map-y parametrov
        Map<String, String> params = new HashMap<String, String>();
        params.put("pocet", Integer.toString(20)); //limit
        params.put("stranka", Integer.toString(1));

    // vytvorenie requestu
        JsonArrayCustomUtf8Request request = new JsonArrayCustomUtf8Request(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //String responseString = response.toString();
                        //Toast.makeText(CustomApplication.getCustomAppContext(), "RESPONSE " + responseString, Toast.LENGTH_LONG).show();
                        tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                        /*zoznamClankov = */
                        //clanky.set() parseJsonArrayResponse(response);
                        crva.setClanky(parseJsonArrayResponse(response));
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //String errorString = error.toString();
                        //Toast.makeText(CustomApplication.getCustomAppContext(), "ERROR " + errorString, Toast.LENGTH_LONG).show();
                        handleVolleyError(error, tvVolleyError);
                    }
                }
        );

        Toast.makeText(CustomApplication.getCustomAppContext(), "Sending TEST request ...", Toast.LENGTH_SHORT).show();
        requestQueue.add(request);

        //return zoznamClankov;
    } // end getClanky()

    private static ArrayList<ArticleObj> parseJsonArrayResponse(JSONArray response) {
        ArrayList<ArticleObj> pomClanky = new ArrayList<>();

        if (response != null && response.length() > 0){
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String title;// = "NA";
            String description;// = "NA";
            String imageUrl;// = "NA";
            String pubDate;
            String rubrika;
            String ID;
            boolean locked;

            try {
                //JSONArray jsonArrayVsetko = response.getJSONArray(KEY_CLANKY);

                for(int i = 0; i < response.length(); i++){
                    JSONObject aktualnyClanok = response.getJSONObject(i); //vrati clanok na aktualnej pozicii

                    //kontrola title
                    if (aktualnyClanok.has(KEY_TITLE) && !aktualnyClanok.isNull(KEY_TITLE)){
                        title = aktualnyClanok.getString(KEY_TITLE);
                    } else {
                        title = "NA"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }
                    // kontrola description
                    if (aktualnyClanok.has(KEY_SHORT_TEXT) && !aktualnyClanok.isNull(KEY_SHORT_TEXT)) {
                        description = aktualnyClanok.getString(KEY_SHORT_TEXT);
                    } else {
                        description = "Popis nedostuný"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }

                    //spracovanie obrazka - ostrenie v pripade, ze orazok nie je dostupny
                    imageUrl = null;
                    if(aktualnyClanok.has(KEY_IMAGE_URL) && !aktualnyClanok.isNull(KEY_IMAGE_URL)){
                        imageUrl = aktualnyClanok.getString(KEY_IMAGE_URL);
                    } else {
                        imageUrl = "NA";
                    }

                    //spracovanie datumu        //otazka co robit ak nie je?!?! dat sem try - catch ??
                    if(aktualnyClanok.has(KEY_PUB_DATE) && !aktualnyClanok.isNull(KEY_PUB_DATE)){
                        pubDate = aktualnyClanok.getString(KEY_PUB_DATE);
                    } else {
                        pubDate = "NA";
                    }

                    //spracovanie rubriky
                    if (aktualnyClanok.has(KEY_SECTION) && !aktualnyClanok.isNull(KEY_SECTION)) {
                        rubrika = aktualnyClanok.getString(KEY_SECTION);
                    } else {
                        rubrika = "Nezaradené"; // Článok
                    }

                    //spracovanie id
                    if (aktualnyClanok.has(KEY_ID) && !aktualnyClanok.isNull(KEY_ID)){
                        ID = aktualnyClanok.getString(KEY_ID);
                    } else {
                        ID = "NA"; // akoze chyba
                    }

                    //spracovanie locked
                    if (aktualnyClanok.has(KEY_LOCKED) && !aktualnyClanok.isNull(KEY_LOCKED)) {
                        locked = aktualnyClanok.getBoolean(KEY_LOCKED);
                    } else {
                        locked = false; // zatial !!!
                    }

                    //kontrola, ci bude clanok pridany do zoznamu
                    if (/*id != -1 && */ !title.equals("NA")) {
                        if (pubDate.equals("NA")) {
                            try {
                                pomClanky.add(new ArticleObj(title, description, imageUrl, dateFormat.parse("01.01.2000"), rubrika, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, description, imageUrl, dateFormat.parse(pubDate), rubrika));
                            } catch (ParseException pEx){
                                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } //end if response !=null ...

        Toast.makeText(CustomApplication.getCustomAppContext(), "Načítaných " + pomClanky.size() + " článkov.", Toast.LENGTH_LONG).show();
        return pomClanky;
    } //end parseJsonArrayResponse


    private static void handleVolleyError(VolleyError error, TextView tvVolleyError){
        tvVolleyError.setVisibility(View.VISIBLE);

        if (error instanceof TimeoutError || error instanceof NoConnectionError){ // lebo tieto dve pre pouzivatela su skoro rovnake
            tvVolleyError.setText(R.string.no_connection_error);

        } else if (error instanceof AuthFailureError){
            tvVolleyError.setText(R.string.authentification_error);

        } else if (error instanceof ServerError){
            tvVolleyError.setText(R.string.server_error);

        } else if (error instanceof NetworkError){
            tvVolleyError.setText(R.string.network_error);

        } else if (error instanceof ParseError){
            tvVolleyError.setText(R.string.parse_error);
        }
    } // end handleVolleyError
} //end class NetworkManager
