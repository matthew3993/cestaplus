package sk.cestaplus.cestaplusapp.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sk.cestaplus.cestaplusapp.network.custom_requests.JsonObjectCustomUtf8Request;
import sk.cestaplus.cestaplusapp.network.custom_requests.JsonArrayCustomUtf8Request;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;

// importy IKeys
import static com.android.volley.Request.*;

/**
 * Created by Matej on 3.3.2015.
 */
public class VolleySingleton {

    private static VolleySingleton sInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context context; //TODO: try remove context

    private SessionManager session;

    private VolleySingleton(Context context){
        this.context = context;
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
            //mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
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

    public void createGetArticlesArrayRequestGET(String section, int limit, int page,
                                                 Response.Listener<JSONArray> responseList, Response.ErrorListener errList){

        JsonArrayCustomUtf8Request request = new JsonArrayCustomUtf8Request(
                Method.GET,
                Endpoints.getListOfArticlesRequestUrl(section, limit, page),
                null,
                responseList,
                errList);

        mRequestQueue.add(request);
    } //end createGetArticlesObjectRequestGET


    public void createGetArticlesObjectRequestGET(String section, int limit, int page,
                                                  Response.Listener<JSONObject> responseList, Response.ErrorListener errList){

        JsonObjectRequest request = new JsonObjectRequest(
                Method.GET,
                Endpoints.getListOfArticlesRequestUrl(section, limit, page),
                (JSONObject) null,
                responseList,
                errList);

        mRequestQueue.add(request);
    } //end createGetArticlesObjectRequestGET

// nacitavanie konkretneho clanku
    public void createGetArticleRequest(String id, boolean locked,
                                        Response.Listener<JSONObject> responseLis, Response.ErrorListener errLis, boolean withPictures){

        if (!locked){ //nezamknuty = verejny clanok
            JsonObjectRequest request = new JsonObjectRequest(
                    Method.GET,
                    Endpoints.getConcreteArticleRequestUrl(id, withPictures, null),
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
                        Endpoints.getConcreteArticleRequestUrl(id, withPictures, session.getAPI_key()),
                        params,
                        responseLis,
                        errLis);

                mRequestQueue.add(request);

            } else { // NIE sme prihlásení
                JsonObjectRequest request = new JsonObjectRequest(
                        Method.GET,
                        Endpoints.getConcreteArticleRequestUrl(id, withPictures, null),
                        (JSONObject) null,
                        responseLis,
                        errLis);

                mRequestQueue.add(request);
            } // if isLoggedIn
        }// if !locked

    }//end createGetArticleRequest

    public void createLoginRequestPOST(final Map<String, String> parametre,
                                       Response.Listener<JSONObject> responseList,
                                       Response.ErrorListener errList){

        JsonObjectCustomUtf8Request request = new JsonObjectCustomUtf8Request(
                Method.POST,
                Endpoints.getLoginUrl(),
                parametre,
                responseList,
                errList
        );

        request.setShouldCache(false); //disable caching!!!
        mRequestQueue.add(request);

    } //end createLoginRequestPOST

    public void createReLoginRequest(Response.Listener<JSONObject> responseList,
                                     Response.ErrorListener errList){

        //load the credentials from session
        Map<String, String> params = new HashMap<>();
        params.put("email", session.getEmail());
        params.put("password", session.getPassword());

        JsonObjectCustomUtf8Request request = new JsonObjectCustomUtf8Request(
                Method.POST,
                Endpoints.getLoginUrl(),
                params,
                responseList,
                errList
        );

        request.setShouldCache(false); //disable caching!!!
        mRequestQueue.add(request);

    } //end createLoginRequestPOST

} // end of class VolleySingleton
