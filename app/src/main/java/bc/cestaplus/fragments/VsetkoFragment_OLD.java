package bc.cestaplus.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.activities.ArticleActivity;
import bc.cestaplus.activities.MainActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter;
import bc.cestaplus.listeners.RecyclerItemClickListener;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.network.requests.JsonArrayCustomUtf8Request;
import bc.cestaplus.utilities.CustomApplication;

import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_CLANKY;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ID;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_IMAGE_URL;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_LOCKED;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_PUB_DATE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SECTION;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SHORT_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TITLE;

//staticke importy


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link bc.cestaplus.fragments.VsetkoFragment_OLD#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VsetkoFragment_OLD
    extends Fragment
    //implements RecyclerView.OnClickListener
    {

    public static final String URL_CESTA_PLUS = "http://www.cestaplus.sk/_android/getAndroidData.php";
    private static final String ULOZENE_VSETKO = "ulozeny_vsetko";

//networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

// data
    private ArrayList<ArticleObj> zoznamVsetko; // konkretne pomenovanie vo FragmenteVsetko
    private int pocSrt;                        // pocet nacitanych stranok
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

// recyclerView
    private RecyclerView recyclerViewVsetko; // konkretne pomenovanie vo FragmentePrehlad
    private ClanokRecyclerViewAdapter crvaVsetko;

// vypis chyb
    private TextView tvVolleyError;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VsetkoFragment.
     */

    public static VsetkoFragment_OLD newInstance() {
        VsetkoFragment_OLD fragment = new VsetkoFragment_OLD();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public VsetkoFragment_OLD() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first

        /*if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }*/

        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //prístup ku kontextu main aktivity
        requestQueue = volleySingleton.getRequestQueue();
        zoznamVsetko = new ArrayList<>();

        Log.i("LIFECYCLE", "Vsetko.onCreate() was called");
    } //end onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("LIFECYCLE", "Vsetko.onCreateVIEW() was called");

    // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prehlad2, container, false);
        tvVolleyError = (TextView) view.findViewById(R.id.tvVolleyError);

    //inicializacia RecyclerView
        recyclerViewVsetko = (RecyclerView) view.findViewById(R.id.rvVsetko);
        recyclerViewVsetko.setLayoutManager(new LinearLayoutManager(getActivity()) );
        recyclerViewVsetko.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                if (position == zoznamVsetko.size()){
                                    pocSrt++;                                           // !!! zvysenie poctu nacitanych stran !!!
                                    sendGetClankyArrayRequestGET("all", 20, pocSrt);
                                    Toast.makeText(getActivity().getApplicationContext(), "Load more in VsetkoFragment, page " + pocSrt, Toast.LENGTH_SHORT).show();

                                } else {
                                    //Intent intent = new Intent(MainActivity.context, ArticleActivity_OtherWay.class);
                                    Intent intent = new Intent(MainActivity.context, ArticleActivity.class);
                                    intent.putExtra("clanok", zoznamVsetko.get(position));

                                    //ActivityCompat.startActivity(ArticleActivity_OtherWay, intent, null);
                                    //view.getContext().startActivity(intent);
                                    startActivity(intent);
                                }
                            }
        }));

        crvaVsetko = new ClanokRecyclerViewAdapter(getActivity());

        // tu to bolo
        if (savedInstanceState != null){ //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamVsetko = savedInstanceState.getParcelableArrayList(ULOZENE_VSETKO);
            crvaVsetko.setClanky(zoznamVsetko);
            pocSrt = savedInstanceState.getInt("pocSrt", 1);

        } else { // nove spustenie
            pocSrt = 1;
            //nove nacitanie

            sendGetClankyArrayRequestGET("all", 20, 1);
        }

        recyclerViewVsetko.setAdapter(crvaVsetko);

        return view;
    } // end onCreateView


    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.i("LIFECYCLE", "Vsetko.onSaveInstanceState() was called");

        // Save the current state of zoznamVsetko
        outState.putParcelableArrayList(ULOZENE_VSETKO, zoznamVsetko);

        //ulozenie poctu nacitanych stran
        outState.putInt("pocSrt", pocSrt);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }


// ======================================== NETWORKING =====================================================================================

    /*private void sendGetClankyObjectRequestGET(String section, int limit, int page){

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                //getRequestUrl(section, limit, page),
                "http://vaii.fri.uniza.sk/~mahut8/bc/vsetkoTest3.json",
                //"http://vaii.fri.uniza.sk/~mahut8/bc/jsonTest.php",
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
    } //end sendGetClankyObjectRequestGET
    */

    private void sendGetClankyArrayRequestGET(String section, int limit, int page){

        JsonArrayCustomUtf8Request request = new JsonArrayCustomUtf8Request(
                Request.Method.GET,
                getRequestUrl(section, limit, page),
                //"http://vaii.fri.uniza.sk/~mahut8/bc/vsetkoTest5.json",
                //"http://vaii.fri.uniza.sk/~mahut8/bc/jsonTest.php",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                        tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                    //page-ovanie
                        if (pocSrt == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                            zoznamVsetko = parseJsonArrayResponse(response);
                        } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                            zoznamVsetko.addAll(parseJsonArrayResponse(response));
                        }
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
    } //end sendGetClankyObjectRequestGET


    /**
     * Metóda vzskladá url adresu dopytu.
     * K základnej adrese prilepí podľa zadaných parametrov atribúty do GET metódy
     * @param section
     * @param limit
     * @param page
     * @return
     */
    private String getRequestUrl(String section, int limit, int page){
        //return URL_CESTA_PLUS_ANDROID+"?apikey="+ MainActivity.API_KEY+"&limit="+limit;
        return URL_CESTA_PLUS + "?section="+section + "&limit="+limit + "&page="+page;
    }


    private ArrayList<ArticleObj> parseJsonResponse(JSONObject response) {
        ArrayList<ArticleObj> tempArticles = new ArrayList<>();

        if (response != null && response.length() > 0){
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String title;// = "NA";
            String short_text;// = "NA";
            String imageUrl;// = "NA";
            String pubDate;
            String section;
            String ID;
            boolean locked;

            try {
                JSONArray jsonArrayAll = response.getJSONArray(KEY_CLANKY);

                for(int i = 0; i < jsonArrayAll.length(); i++){
                    JSONObject actArticle = jsonArrayAll.getJSONObject(i); //vrati clanok na aktualnej pozicii

                    //kontrola title
                    if (actArticle.has(KEY_TITLE) && !actArticle.isNull(KEY_TITLE)){
                        title = actArticle.getString(KEY_TITLE);
                    } else {
                        title = "NA"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }
                    // kontrola short_text
                    if (actArticle.has(KEY_SHORT_TEXT) && !actArticle.isNull(KEY_SHORT_TEXT)) {
                        short_text = actArticle.getString(KEY_SHORT_TEXT);
                    } else {
                        short_text = "Popis nedostuný"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }

                    //spracovanie obrazka - ostrenie v pripade, ze orazok nie je dostupny
                    imageUrl = null;
                    if(actArticle.has(KEY_IMAGE_URL) && !actArticle.isNull(KEY_IMAGE_URL)){
                        imageUrl = actArticle.getString(KEY_IMAGE_URL);
                    } else {
                        imageUrl = "NA";
                    }

                    //spracovanie datumu        //otazka co robit ak nie je?!?! dat sem try - catch ??
                    if(actArticle.has(KEY_PUB_DATE) && !actArticle.isNull(KEY_PUB_DATE)){
                        pubDate = actArticle.getString(KEY_PUB_DATE);
                    } else {
                        pubDate = "NA";
                    }

                    //spracovanie rubriky
                    if (actArticle.has(KEY_SECTION) && !actArticle.isNull(KEY_SECTION)) {
                        section = actArticle.getString(KEY_SECTION);
                    } else {
                        section = "Nezaradené"; // Článok
                    }

                    //spracovanie id
                    if (actArticle.has(KEY_ID) && !actArticle.isNull(KEY_ID)){
                        ID = actArticle.getString(KEY_ID);
                    } else {
                        ID = "NA"; // akoze chyba
                    }

                    //spracovanie locked
                    if (actArticle.has(KEY_LOCKED) && !actArticle.isNull(KEY_LOCKED)) {
                        locked = actArticle.getBoolean(KEY_LOCKED);
                    } else {
                        locked = false; // zatial !!!
                    }

                    //kontrola, ci bude clanok pridany do zoznamu
                    if (/*id != -1 && */ !title.equals("NA")) {
                        if (!pubDate.equals("NA")) {
                            try { //clanok ma v poriadku nadpis aj datum zverejnenia
                                tempArticles.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Toast.makeText(getActivity(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Toast.makeText(getActivity(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Toast.makeText(getActivity(), "CHYBA PARSOVANIA JSON" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } //end if response !=null ...

        Toast.makeText(getActivity().getApplicationContext(), "Načítaných " + tempArticles.size() + " článkov.", Toast.LENGTH_LONG).show();
        return tempArticles;
    }//end parseJsonObjectResponse


    private ArrayList<ArticleObj> parseJsonArrayResponse(JSONArray response) {
        ArrayList<ArticleObj> tempArticles = new ArrayList<>();

        if (response != null && response.length() > 0){
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String title;// = "NA";
            String short_text;// = "NA";
            String img;// = "NA";
            String pubDate;
            String section;
            String ID;
            boolean locked;

            try {
                //JSONArray jsonArrayVsetko = response.getJSONArray(KEY_CLANKY);

                for(int i = 0; i < response.length(); i++){
                    JSONObject actArticle = response.getJSONObject(i); //vrati clanok na aktualnej pozicii

                    //kontrola title
                    if (actArticle.has(KEY_TITLE) && !actArticle.isNull(KEY_TITLE)){
                        title = actArticle.getString(KEY_TITLE);
                    } else {
                        title = "NA"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }
                    // kontrola short_text
                    if (actArticle.has(KEY_SHORT_TEXT) && !actArticle.isNull(KEY_SHORT_TEXT)) {
                        short_text = actArticle.getString(KEY_SHORT_TEXT);
                    } else {
                        short_text = "Popis nedostuný"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }

                    //spracovanie obrazka - ostrenie v pripade, ze orazok nie je dostupny
                    img = null; //title image URL
                    if(actArticle.has(KEY_IMAGE_URL) && !actArticle.isNull(KEY_IMAGE_URL)){
                        img = actArticle.getString(KEY_IMAGE_URL);
                    } else {
                        img = "NA";
                    }

                    //spracovanie datumu        //otazka co robit ak nie je?!?! dat sem try - catch ??
                    if(actArticle.has(KEY_PUB_DATE) && !actArticle.isNull(KEY_PUB_DATE)){
                        pubDate = actArticle.getString(KEY_PUB_DATE);
                    } else {
                        pubDate = "NA";
                    }

                    //spracovanie rubriky
                    if (actArticle.has(KEY_SECTION) && !actArticle.isNull(KEY_SECTION)) {
                        section = actArticle.getString(KEY_SECTION);
                    } else {
                        section = "Nezaradené"; // Článok
                    }

                    //spracovanie id
                    if (actArticle.has(KEY_ID) && !actArticle.isNull(KEY_ID)){
                        ID = actArticle.getString(KEY_ID);
                    } else {
                        ID = "NA"; // akoze chyba
                    }

                    //spracovanie locked
                    if (actArticle.has(KEY_LOCKED) && !actArticle.isNull(KEY_LOCKED)) {
                        locked = actArticle.getBoolean(KEY_LOCKED);
                    } else {
                        locked = false; // zatial !!!
                    }

                    //kontrola, ci bude clanok pridany do zoznamu
                    if (/*id != -1 && */ !title.equals("NA")) {
                        if (!pubDate.equals("NA")) {
                            try { //clanok ma v poriadku nadpis aj datum zverejnenia
                                tempArticles.add(new ArticleObj(title, short_text, img, dateFormat.parse(pubDate), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Toast.makeText(getActivity(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, img, dateFormat.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Toast.makeText(getActivity(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } //end if response !=null ...

        Toast.makeText(CustomApplication.getCustomAppContext(), "Načítaných " + tempArticles.size() + " článkov.", Toast.LENGTH_LONG).show();
        return tempArticles;
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
    }//end handleVolleyError

    /*
    @Override
    public void onClick(View v) {
        if (v instanceof Button){
            Toast.makeText(CustomApplication.getCustomAppContext(), "Load more", Toast.LENGTH_SHORT).show();

        } else {
            Intent intent = new Intent(MainActivity.context, ArticleActivity_OtherWay.class);
            intent.putExtra(EXTRA_NAZOV_RUBRIKY, zoznamVsetko.get( crvaVsetko.getItemId() ));
            intent.putExtra(EXTRA_NAZOV_RUBRIKY, rubriky.get(getPosition()).getSection());
            v.get
            //ActivityCompat.startActivity(ArticleActivity_OtherWay, intent, null);
            v.getContext().startActivity(intent);
        }
    }*/
} // end class FragmentVsetko
