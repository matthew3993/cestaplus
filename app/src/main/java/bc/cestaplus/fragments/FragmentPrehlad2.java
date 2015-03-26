package bc.cestaplus.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import bc.cestaplus.activities.MainActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.network.requests.JsonArrayCustomUtf8Request;
import bc.cestaplus.utilities.CustomApplication;

//staticke importy
import static bc.cestaplus.extras.IKeys.IPrehlad.*;


public class FragmentPrehlad2 extends Fragment {

    public static final String URL_CESTA_PLUS_VSETKO = "";
    private static final String ULOZENE_VSETKO = "ulozeny_vsetko";
    //public static final String URL_CESTA_PLUS = "";                //doplnit

//networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

// data
    private ArrayList<ArticleObj> zoznamVsetko; // konkretne pomenovanie vo FragmentePrehlad
    private int pocSrt;                        // pocet nacitanych stranok
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

// recyclerView
    private RecyclerView recyclerViewVsetko; // konkretne pomenovanie vo FragmentePrehlad
    private ClanokRecyclerViewAdapter crvaVsetko;

// vypis chyb
    private TextView tvVolleyError;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentPrehlad2.
     */
    public static FragmentPrehlad2 newInstance() {
        FragmentPrehlad2 fragment = new FragmentPrehlad2();
        //Bundle args = new Bundle();
        //fragment.setArguments(args);
        return fragment;
    }

    public FragmentPrehlad2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first

        //if (getArguments() != null) {
        //}

        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //prístup ku kontextu main aktivity
        requestQueue = volleySingleton.getRequestQueue();
        zoznamVsetko = new ArrayList<>();
        pocSrt = 1;

        Log.i("LIFECYCLE", "Prehlad 2.onCreate() was called");

    } // end onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("LIFECYCLE", "Prehlad 2.onCreateVIEW() was called");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prehlad2, container, false);
        tvVolleyError = (TextView) view.findViewById(R.id.tvVolleyError);

        recyclerViewVsetko = (RecyclerView) view.findViewById(R.id.rvVsetko);
        recyclerViewVsetko.setLayoutManager(new LinearLayoutManager(getActivity()) );

        crvaVsetko = new ClanokRecyclerViewAdapter(getActivity());

        // tu to bolo
        if (savedInstanceState != null){ //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamVsetko = savedInstanceState.getParcelableArrayList(ULOZENE_VSETKO);
            crvaVsetko.setClanky(zoznamVsetko);

        } else {
            //nove nacitanie
            //sendJsonRequest();
            sendGetClankyRequestPOST("vsetko", 20, 1);
        }

        recyclerViewVsetko.setAdapter(crvaVsetko);

        return view;
    } // end onCreateView

    /*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i("LIFECYCLE", "Prehlad 2.onActivityCreated() was called");

        //tu to bolo
    }
    */

    public static String getRequestUrl(int limit){
        return URL_CESTA_PLUS_VSETKO+"?apikey="+ MainActivity.API_KEY+"&limit="+limit;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.i("LIFECYCLE", "Prehlad 2.onSaveInstanceState() was called");

        // Save the current state of zoznamVsetko
        outState.putParcelableArrayList(ULOZENE_VSETKO, zoznamVsetko);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }



// ======================================== NETWORKING =====================================================================================

    public void sendGetClankyRequestPOST(String druhDopytu, int limit, final int stranka){

        String url = "http://vaii.fri.uniza.sk/~mahut8/bc/jsonTest.php"; // testovaci script na pageovanie
        //String url = "http://vaii.fri.uniza.sk/~mahut8/bc/vsetkoTest.json"; //treba opravit parsovaciu metodu

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

                        if (stranka == 1) { // v pripade ze ide o 1 stranku je cely zoznam prepisany
                            zoznamVsetko = parseJsonArrayResponse(response);
                        } else { // v pripade dalsich stranok su clanky pridavane
                            zoznamVsetko.addAll(parseJsonArrayResponse(response));
                        }

                        crvaVsetko.setClanky(zoznamVsetko);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //String errorString = error.toString();
                //Toast.makeText(CustomApplication.getCustomAppContext(), "ERROR " + errorString, Toast.LENGTH_LONG).show();
                handleVolleyError(error);
            }
        }
        );

        //Toast.makeText(CustomApplication.getCustomAppContext(), "Sending TEST request ...", Toast.LENGTH_SHORT).show();
        requestQueue.add(request);
    } // end sendGetClankyRequestPOST()

    private ArrayList<ArticleObj> parseJsonArrayResponse(JSONArray response) {
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


    private void handleVolleyError(VolleyError error){
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







        /*
    private void sendJsonRequest(){

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                //getRequestUrl(10)
                "http://vaii.fri.uniza.sk/~mahut8/bc/vsetkoTest.json",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                        tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                        zoznamVsetko = parseJsonObjectResponse(response);
                        crvaVsetko.setClanky(zoznamVsetko);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                        handleVolleyError(error);
                    } //end of onErrorResponse
                }); //end of JsonObjectRequest

        requestQueue.add(request);
        //volleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
        //Toast.makeText(getActivity(), "Sending request...", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<ArticleObj> parseJsonObjectResponse(JSONObject response) {
        ArrayList<ArticleObj> pomClanky = new ArrayList<>();

        if (response != null && response.length() > 0){
            //defaultne hodnoty
                //urobit cez rozhranie Constants v Extras
            String title;// = "NA";
            String description;// = "NA";
            String imageUrl;// = "NA";
            String pubDate;
            String rubrika;
            long id = 0;
            boolean locked;

            try {
                JSONArray jsonArrayVsetko = response.getJSONArray(KEY_CLANKY);

                for(int i = 0; i < jsonArrayVsetko.length(); i++){
                    JSONObject aktualnyClanok = jsonArrayVsetko.getJSONObject(i); //vrati clanok na aktualnej pozicii

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
                        id = aktualnyClanok.getLong(KEY_ID);
                    } else {
                        id = -1; // akoze chyba
                    }

                //spracovanie locked
                    if (aktualnyClanok.has(KEY_LOCKED) && !aktualnyClanok.isNull(KEY_LOCKED)) {
                        locked = aktualnyClanok.getBoolean(KEY_LOCKED);
                    } else {
                        locked = false; // zatial !!!
                    }

                //kontrola, ci bude clanok pridany do zoznamu
                    if (/*id != -1 &&  !title.equals("NA")) {
                        if (pubDate.equals("NA")) {
                            try {
                                pomClanky.add(new ArticleObj(title, description, imageUrl, dateFormat.parse("01.01.2000"), rubrika, id, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, description, imageUrl, dateFormat.parse(pubDate), rubrika));
                            } catch (ParseException pEx){
                                Toast.makeText(getActivity(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Toast.makeText(getActivity(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } //end if response !=null ...

        Toast.makeText(getActivity().getApplicationContext(), "Načítaných " + pomClanky.size() + " článkov.", Toast.LENGTH_LONG).show();
        return pomClanky;
    }//end parseJsonObjectResponse

    private void handleVolleyError(VolleyError error){
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
    }
    */

} //end of FragmentPrehlad2
