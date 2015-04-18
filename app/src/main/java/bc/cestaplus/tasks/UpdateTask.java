package bc.cestaplus.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.utilities.MyApplication;
import bc.cestaplus.extras.ArticlesLoadedListener;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.network.requests.JsonArrayUtf8FutureRequest;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.Util;

import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ID;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_IMAGE_URL;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_LOCKED;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_PUB_DATE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SECTION;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SHORT_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TITLE;
import static java.lang.System.currentTimeMillis;

/**
 * Created by Matej on 13. 4. 2015.
 */
public class UpdateTask
    extends AsyncTask<Void, Void, ArrayList<ArticleObj>> {

    //constats
    public static final String URL_CESTA_PLUS_ANDROID = "http://www.cestaplus.sk/_android/";
    public static final String GET_NEW_ARTICLES = "getAndroidNewArticle.php";

    //atributes
    private boolean issueNotification;
    private ArticlesLoadedListener myComponent;

    //dates formats
    private DateFormat dateFormatAPP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat dateFormatAPI = new SimpleDateFormat("yyyy-MM-dd%HH:mm:ss");

    //networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    /**
     * CONSTRUCTOR
     */
    public UpdateTask(ArticlesLoadedListener myComponent, boolean issueNotification) {
        //this.myService = myService;
        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance(CustomApplication.getCustomAppContext());
        requestQueue = volleySingleton.getRequestQueue();
        this.issueNotification = issueNotification;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected ArrayList<ArticleObj> doInBackground(Void... params) {

        long tryTime = currentTimeMillis(); //save current time before sending request

        JSONArray response = sentUpdateRequest(); // 1 - send request
        ArrayList<ArticleObj> listArticles = parseJsonArrayResponse(response); // 2 - parse the response //response null check inside this method
        //MyApplication.getWritableDatabase().insertArticlesAll(listArticles, false); // 3 - insert parsed articles into database

        //check if they are really new
        ArrayList<ArticleObj> newArticles = getNewArticles(listArticles);

        if (!newArticles.isEmpty()) { // if new articles are not empty
            MyApplication.getWritableDatabase().updateArticles(newArticles); // update database

            MyApplication.saveToPreferences(CustomApplication.getCustomAppContext(), "lastUpdate", tryTime); //change update time

            if (issueNotification){
                Util.issueNotification("Počet nových článkov: " + newArticles.size(), 1); // ak sú nové články id = 1
            }

        } else {
            if (issueNotification){
                Util.issueNotification("Žiadne nové články", 2); // ak nie sú žiadne nové články id = 2
            }
        }

        return MyApplication.getWritableDatabase().getAllArticles();
    }

    private ArrayList<ArticleObj> getNewArticles(ArrayList<ArticleObj> listArticles) {
        ArrayList<ArticleObj> newArticles = new ArrayList<ArticleObj>();

        long defaultVal = 0;
        try {
            defaultVal = dateFormatAPP.parse("2010-01-01 00:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date lastUpdate = new Date(MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(),"lastUpdate", defaultVal));

    //check
        for (int i = 0; i < listArticles.size(); i++){
            if ( ( listArticles.get(i).getPubDate().compareTo(lastUpdate) ) > 0 ){
                newArticles.add(listArticles.get(i));
            }
        }//end for

        return newArticles;
    }

    @Override
    protected void onPostExecute(ArrayList<ArticleObj> listArticles) {
        if ( myComponent != null){
            myComponent.onArticlesLoaded(listArticles);
        }
    } //end onPostExecute

    /*
    ======================= OTHER METHODS =============================================================================
     */
    private JSONArray sentUpdateRequest(){
        JSONArray response = null;


        //int seconds = Calendar.getInstance().get(Calendar.SECOND);

        RequestFuture<JSONArray> requestFuture = RequestFuture.newFuture();

        JsonArrayUtf8FutureRequest request = new JsonArrayUtf8FutureRequest(
                Request.Method.GET,
                getUpdateRequestUrl(),
                //getUpdateRequestUrl(MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(),"lastUpdate", (long) 1420088400000)),
                null,
                requestFuture,
                requestFuture); //end of JsonArrayUtf8FutureRequest

        requestQueue.add(request);

        try {
            response = requestFuture.get(30, TimeUnit.SECONDS); //blocking code - never mind, we are in a background thread

        } catch (InterruptedException e) {
            Log.e("error", e + "");

        } catch (ExecutionException e) {
            Log.e("error", e+"");

        } catch (TimeoutException e) {
            Log.e("error", e+"");
        }
        //Toast.makeText(getActivity(), "Sending request...", Toast.LENGTH_SHORT).show();

        return response;
    } //end sendGetClankyObjectRequestGET


    /**
     * Metóda vyskladá url adresu dopytu.
     * @return
     */
    private String getUpdateRequestUrl(){
        //return URL_CESTA_PLUS_ANDROID+"?apikey="+ MainActivity.API_KEY+"&limit="+limit;

        long defaultVal = 0;
        try {
            defaultVal = dateFormatAPP.parse("2010-01-01 00:00:00").getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String lastUpdateTime = dateFormatAPI.format(MyApplication.readFromPreferences(CustomApplication.getCustomAppContext(),"lastUpdate", defaultVal));

        //String lastUpdateTime = "2015-03-29%23:29:59";
        return URL_CESTA_PLUS_ANDROID + GET_NEW_ARTICLES + "?date=" + lastUpdateTime + "&limit="+20 + "&page="+1;
    }

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
                                tempArticles.add(new ArticleObj(title, short_text, img, dateFormatAPP.parse(pubDate), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormatAPP.parse(pubDate), section));
                            } catch (ParseException pEx){
                                //Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("error", pEx+"");
                            }
                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, img, dateFormatAPP.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormatAPP.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Log.e("error", pEx+"");
                                //Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Log.e("error", jsonEx+"");
                //Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } //end if response !=null ...

        //Toast.makeText(CustomApplication.getCustomAppContext(), "Načítaných " + tempArticles.size() + " článkov.", Toast.LENGTH_LONG).show();
        return tempArticles;
    } //end parseJsonArrayResponse
} //end inner class UpdateTask
