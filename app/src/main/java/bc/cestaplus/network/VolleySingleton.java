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

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.objects.ArticleText;
import bc.cestaplus.R;
import bc.cestaplus.network.requests.JsonArrayCustomUtf8Request;
import bc.cestaplus.utilities.CustomApplication;

// importy IKeys
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_CLANKY;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ID;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_IMAGE_URL;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_LOCKED;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_PUB_DATE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SECTION;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SHORT_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TITLE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_AUTOR;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TEXT;

/**
 * Created by Matej on 3.3.2015.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    public static final String URL_CESTA_PLUS_ANDROID = "http://www.cestaplus.sk/_android/";
    public static final String GET_ARTICLES = "getAndroidData.php";
    public static final String GET_CONCRETE_ARTICLE = "getAndroidData_article.php";

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

    public void sendGetClankyArrayRequestGET(String section, int limit, int page,
                                             Response.Listener<JSONArray> responseList, Response.ErrorListener errList){

        JsonArrayCustomUtf8Request request = new JsonArrayCustomUtf8Request(
                Request.Method.GET,
                getRequestUrl(section, limit, page),
                //"http://vaii.fri.uniza.sk/~mahut8/bc/vsetkoTest6.json",
                null,
                responseList,
                errList);

        mRequestQueue.add(request);
        //volleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
        //Toast.makeText(getActivity(), "Sending request...", Toast.LENGTH_SHORT).show();
    } //end sendGetClankyObjectRequestGET


    public void sendGetClankyObjectRequestGET(String section, int limit, int page,
                                              Response.Listener<JSONObject> responseList, Response.ErrorListener errList){

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                getRequestUrl(section, limit, page),
                //"http://vaii.fri.uniza.sk/~mahut8/bc/vsetkoTest6.json",
                (JSONObject) null,
                responseList,
                errList);

        mRequestQueue.add(request);
        //volleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request);
        //Toast.makeText(getActivity(), "Sending request...", Toast.LENGTH_SHORT).show();
    } //end sendGetClankyObjectRequestGET


    private String getRequestUrl(String section, int limit, int page){
        //return URL_CESTA_PLUS_ANDROID+"?apikey="+ MainActivity.API_KEY+"&limit="+limit;
        return URL_CESTA_PLUS_ANDROID + GET_ARTICLES  + "?section="+section + "&limit="+limit + "&page="+page;
    }


    public ArrayList<ArticleObj> parseJsonArrayResponse(JSONArray response) {
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

                    //spracovanie ID
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
                                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, img, dateFormat.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
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

        Toast.makeText(CustomApplication.getCustomAppContext(), "Načítaných " + tempArticles.size() + " článkov.", Toast.LENGTH_LONG).show();
        return tempArticles;
    } //end parseJsonArrayResponse


    public ArrayList<ArticleObj> parseJsonObjectResponse(JSONObject response) {
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
                                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov
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
    }//end parseJsonObjectResponse


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

// nacitavanie konkretneho clanku
    public void sendGetArticleRequest(String id, boolean withPictures,
                                      Response.Listener<JSONObject> responseLis, Response.ErrorListener errLis){

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                getArticleRequestUrl(id, withPictures),
                (JSONObject) null,
                responseLis,
                errLis);

        mRequestQueue.add(request);
    }

    private String getArticleRequestUrl(String id, boolean withPictures) {
        int wp = 1;
        if (!withPictures){
            wp = 0;
        }

        return URL_CESTA_PLUS_ANDROID + GET_CONCRETE_ARTICLE + "?id="+id + "&withPictures="+wp;
    }

    public ArticleText parseArticleTextResponse(JSONObject response) {
        ArticleText articleTextTemp = null;

        if (response != null && response.length() > 2) {
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String short_text;
            String autor;
            String text;

            try {
                // kontrola short_text
                if (response.has(KEY_SHORT_TEXT) && !response.isNull(KEY_SHORT_TEXT)) {
                    short_text = response.getString(KEY_SHORT_TEXT);
                } else {
                    short_text = "Chyba st"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                //kontrola autora
                if (response.has(KEY_AUTOR) && !response.isNull(KEY_AUTOR)) {
                    autor = response.getString(KEY_AUTOR);
                } else {
                    autor = "Chyba aut"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                //kontrola text
                if (response.has(KEY_TEXT) && !response.isNull(KEY_TEXT)) {
                    text = response.getString(KEY_TEXT);
                } else {
                    text = "<p>Chyba txt<p>"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                articleTextTemp = new ArticleText(short_text, autor, text);

            } catch (JSONException jsonEx) {
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } else {
            Toast.makeText(CustomApplication.getCustomAppContext(), "Príliš krátka response", Toast.LENGTH_LONG).show();
            //end if response !=null ...
        }

        if (articleTextTemp == null) { //osetrenie, aby sa nikdy nevratilo null
            return new ArticleText("Chyba short_textu", "Chyba autora", "<p>Chyba textu<p>");
        } else {
            return articleTextTemp;
        }
    } //end parseArticleTextResponse


    //TODO zakomponovat tuto metodu do kodu vyssie - preburat kod!!
    private boolean contains(JSONObject jsonObject, String key){
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }
} // end of VolleySingleton class
