package sk.cestaplus.cestaplusapp.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import sk.cestaplus.cestaplusapp.extras.IErrorCodes;
import sk.cestaplus.cestaplusapp.network.custom_requests.JsonObjectCustomUtf8Request;
import sk.cestaplus.cestaplusapp.network.custom_requests.JsonArrayCustomUtf8Request;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.CustomNotificationManager;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;

// importy IKeys
import static com.android.volley.Request.*;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.NOTIFICATION_API_KEY_TEST;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_API_KEY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_EMAIL;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_PASSWORD;

/**
 * Created by Matej on 3.3.2015.
 * SOURCE: https://developer.android.com/training/volley/requestqueue.html#singleton
 */
public class VolleySingleton {

    private static VolleySingleton sInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context context; //TODO: try remove context

    private SessionManager session;

    private VolleySingleton(Context context){
        VolleySingleton.context = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() { //anonymna trieda ImageLoader

            private final LruCache<String, Bitmap> cache = new LruCache<>((int)Runtime.getRuntime().maxMemory()/1024/8);
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

    public static synchronized VolleySingleton getInstance(Context context){
        if (sInstance == null){
            sInstance = new VolleySingleton(context);
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());

            //SOURCES: https://developer.android.com/reference/android/content/Context.html#getCacheDir()
            /*
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024 * 10); // 10MB cap
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
            */
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


    /**
     * Creates request for text of defined article
     */
    public void createGetArticleRequest(String articleId, boolean locked,
                                        Response.Listener<JSONObject> responseLis, Response.ErrorListener errLis,
                                        boolean withPictures){

        Request<JSONObject> request;
        String url = Endpoints.getConcreteArticleRequestUrl(articleId, withPictures);

        if (!locked){ //not locked = public article
            request = new JsonObjectRequest(
                    Method.GET,
                    url,
                    (JSONObject) null,
                    responseLis,
                    errLis);

        } else { //locked article
            if (session.getRole() == ROLE_LOGGED_SUBSCRIPTION_OK){
                //init POST parameters
                Map<String, String> params = new HashMap<>();
                params.put(KEY_PARAMS_API_KEY, session.getAPI_key());

                //CustomNotificationManager.issueNotification("Loading article, API_KEY: " + session.getAPI_key(), NOTIFICATION_API_KEY_TEST); // debug notification

                //create request
                 request = new JsonObjectCustomUtf8Request(
                        Method.POST,
                        url,
                        params,
                        responseLis,
                        errLis);

            } else { // Other roles
                 request = new JsonObjectRequest(
                        Method.GET,
                        url,
                        (JSONObject) null,
                        responseLis,
                        errLis);

            } // if role == ROLE_LOGGED_SUBSCRIPTION_OK
        }// if !locked

        request.setShouldCache(true);
        mRequestQueue.add(request);
    }//end createGetArticleRequest

    public void createLoginRequestPOST(final Map<String, String> parameters,
                                       Response.Listener<JSONObject> responseList,
                                       Response.ErrorListener errList){

        JsonObjectCustomUtf8Request request = new JsonObjectCustomUtf8Request(
                Method.POST,
                Endpoints.getLoginUrl(),
                parameters,
                responseList,
                errList
        );

        request.setShouldCache(false); //disable caching!!!
        mRequestQueue.add(request);

    } //end createLoginRequestPOST

    public void createReLoginRequest(Response.Listener<JSONObject> responseList,
                                     Response.ErrorListener errList){

        //load the credentials from session manager
        Map<String, String> params = new HashMap<>();
        params.put(KEY_PARAMS_EMAIL, session.getEmail());
        params.put(KEY_PARAMS_PASSWORD, session.getPassword());

        createLoginRequestPOST(params, responseList, errList);

    } //end createReLoginRequest

} // end of class VolleySingleton
