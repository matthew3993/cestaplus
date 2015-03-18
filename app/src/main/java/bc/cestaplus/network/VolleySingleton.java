package bc.cestaplus.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import bc.cestaplus.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.utilities.CustomApplication;

import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_CLANKY;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ID;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_IMAGE_URL;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_LOCKED;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_PUB_DATE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SECTION;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SHORT_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TITLE;

/**
 * Created by Matej on 3.3.2015.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    public static final String URL_CESTA_PLUS = "http://www.cestaplus.sk/_android/getAndroidData.php";

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private VolleySingleton(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() { //anonymna trieda ImageLoader

            private LruCache<String, Bitmap> cache = new LruCache<>((int)Runtime.getRuntime().maxMemory()/1024/8);
            // runtime.maxMemory - komplet vsetko, co kedy mozeme mat v bajtoch
            // deleno 1024 = kBajty, dalej deleno 8 - 1/8 vsetkeho, co mozeme mat
            // musime pretypovat z long na int

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static VolleySingleton getInstance(Context context){
        if (sInstance == null){
            sInstance = new VolleySingleton(context);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

// ======================================== VLASTNÉ METÓDY =====================================================================================

    public void sendGetClankyRequestGET(String section, int limit, int page,
                                         Response.Listener<JSONObject> responseList, Response.ErrorListener errList){

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "http://vaii.fri.uniza.sk/~mahut8/bc/vsetkoTest6.json",
                (JSONObject) null,
                responseList,
                errList);

        mRequestQueue.add(request);
        //volleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
        //Toast.makeText(getActivity(), "Sending request...", Toast.LENGTH_SHORT).show();
    } //end sendGetClankyRequestGET


    private String getRequestUrl(String section, int limit, int page){
        //return URL_CESTA_PLUS+"?apikey="+ MainActivity.API_KEY+"&limit="+limit;
        return URL_CESTA_PLUS + "?section="+section + "&limit="+limit + "&page="+page;
    }


    public ArrayList<ArticleObj> parseJsonResponse(JSONObject response) {
        ArrayList<ArticleObj> tempArticles = new ArrayList<>();

        if (response != null && response.length() > 0){
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String title;// = "NA";
            String short_text;// = "NA";
            String imageUrl;// = "NA";
            String pubDate;
            String section;
            long id = 0;
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
                        id = actArticle.getLong(KEY_ID);
                    } else {
                        id = -1; // akoze chyba
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
                                tempArticles.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section, id, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse("2000-01-01 00:00:00"), section, id, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA JSON" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } //end if response !=null ...

        Toast.makeText(CustomApplication.getCustomAppContext().getApplicationContext(), "Načítaných " + tempArticles.size() + " článkov.", Toast.LENGTH_LONG).show();
        return tempArticles;
    }//end parseJsonResponse

    public void handleVolleyError(VolleyError error, TextView tvVolleyError){
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



} // end of VolleySingleton class
