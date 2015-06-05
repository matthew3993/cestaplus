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
import java.util.HashMap;
import java.util.Map;

import bc.cestaplus.network.custom_requests.JsonObjectCustomUtf8Request;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.network.custom_requests.JsonArrayCustomUtf8Request;
import bc.cestaplus.utilities.CustomApplication;
import bc.cestaplus.utilities.SessionManager;

// importy IKeys
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_CLANKY;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ID;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_IMAGE_URL;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_LOCKED;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_PUB_DATE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SECTION;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SHORT_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TITLE;
import static com.android.volley.Request.*;

/**
 * Created by Matej on 3.3.2015.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private SessionManager session;

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

        // Session manager
        session = new SessionManager(CustomApplication.getCustomAppContext());
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
                Method.GET,
                Endpoints.getRequestUrl(section, limit, page),
                null,
                responseList,
                errList);

        mRequestQueue.add(request);
    } //end sendGetClankyObjectRequestGET


    public void sendGetClankyObjectRequestGET(String section, int limit, int page,
                                              Response.Listener<JSONObject> responseList, Response.ErrorListener errList){

        JsonObjectRequest request = new JsonObjectRequest(
                Method.GET,
                Endpoints.getRequestUrl(section, limit, page),
                (JSONObject) null,
                responseList,
                errList);

        mRequestQueue.add(request);
    } //end sendGetClankyObjectRequestGET


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
    public void sendGetArticleRequest(String id, boolean locked,
                                      Response.Listener<JSONObject> responseLis, Response.ErrorListener errLis, boolean withPictures){

        if (!locked){ //nezamknuty = verejny clanok
            JsonObjectRequest request = new JsonObjectRequest(
                    Method.GET,
                    Endpoints.getArticleRequestUrl(id, withPictures, null),
                    (JSONObject) null,
                    responseLis,
                    errLis);

            mRequestQueue.add(request);

        } else { //zamknuty  clanok
            if (session.isLoggedIn()){ //sme prihlásení
                //vytvorenie parametrov
                Map<String, String> params = new HashMap<>();
                params.put("apikey", session.getAPI_key());

                //vytvorenie requestu
                JsonObjectCustomUtf8Request request = new JsonObjectCustomUtf8Request(
                        Method.POST,
                        Endpoints.getArticleRequestUrl(id, withPictures, session.getAPI_key()),
                        params,
                        responseLis,
                        errLis);

                mRequestQueue.add(request);

            } else { // NIE sme prihlásení
                JsonObjectRequest request = new JsonObjectRequest(
                        Method.GET,
                        Endpoints.getArticleRequestUrl(id, withPictures, null),
                        (JSONObject) null,
                        responseLis,
                        errLis);

                mRequestQueue.add(request);
            } // if isLoggedIn
        }// if !locked

    }//end sendGetArticleRequest

    //login
    public void sendLoginRequestPOST(final Map<String, String> parametre,
                                     Response.Listener<JSONObject> responseList,
                                     Response.ErrorListener errList){

    // create a login request
        /*JsonObjectRequest request = new JsonObjectRequest(
                Method.POST,
                "http://vaii.fri.uniza.sk/~mahut8/bc/loginTest/login.php",
                responseList,
                errList) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = parametre;
                return params;
            }

        };*/
        JsonObjectCustomUtf8Request request = new JsonObjectCustomUtf8Request(
                Method.POST,
                Endpoints.getLoginUrl(),
                parametre,
                responseList,
                errList
        );

        request.setShouldCache(false); //disable caching
        mRequestQueue.add(request);
    } //end sendLoginRequestPOST

    //TODO zakomponovat tuto metodu do kodu vyssie - preburat kod!!
    private boolean contains(JSONObject jsonObject, String key){
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

} // end of VolleySingleton class
