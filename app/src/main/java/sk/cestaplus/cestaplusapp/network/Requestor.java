package sk.cestaplus.cestaplusapp.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sk.cestaplus.cestaplusapp.network.custom_requests.JsonArrayCustomUtf8Request;
import sk.cestaplus.cestaplusapp.network.custom_requests.JsonArrayUtf8FutureRequest;
import sk.cestaplus.cestaplusapp.network.custom_requests.JsonObjectCustomUtf8Request;
import sk.cestaplus.cestaplusapp.network.custom_requests.JsonObjectUtf8FutureRequest;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;

import static sk.cestaplus.cestaplusapp.extras.Constants.VOLLEY_DEBUG;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_API_KEY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_EMAIL;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_PASSWORD;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Requestor {

    public static JSONObject sendUpdateRequest(RequestQueue requestQueue) throws InterruptedException, ExecutionException, TimeoutException {
        JSONObject response = null;

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();

        JsonObjectUtf8FutureRequest request = new JsonObjectUtf8FutureRequest(
                Request.Method.GET,
                Endpoints.getUpdateRequestUrl(),
                null,
                requestFuture,
                requestFuture); //end of JsonObjectUtf8FutureRequest

        request.setShouldCache(false); //disable caching
        requestQueue.add(request);

        response = requestFuture.get(30, TimeUnit.SECONDS); //blocking code - never mind, we are in a background thread

        return response;
    } //end createGetArticlesObjectRequestGET

    public static void createBaterkaRequest(RequestQueue requestQueue, Date pubDate,
                                     Response.Listener<JSONObject> responseList, Response.ErrorListener errList){

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Endpoints.getBaterkaUrl(pubDate),
                (JSONObject) null,
                responseList,
                errList);

        requestQueue.add(request);
    } //end createBaterkaRequest

    public static void createGetArticlesArrayRequestGET(RequestQueue requestQueue, String section, int limit, int page,
                                                        Response.Listener<JSONArray> responseList, Response.ErrorListener errList){

        JsonArrayCustomUtf8Request request = new JsonArrayCustomUtf8Request(
                Request.Method.GET,
                Endpoints.getListOfArticlesRequestUrl(section, limit, page),
                null,
                responseList,
                errList);

        requestQueue.add(request);
    } //end createGetArticlesObjectRequestGET

    public static void createGetArticlesObjectRequestGET(RequestQueue requestQueue, String section, int limit, int page,
                                                         Response.Listener<JSONObject> responseList, Response.ErrorListener errList){

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Endpoints.getListOfArticlesRequestUrl(section, limit, page),
                (JSONObject) null,
                responseList,
                errList);

        requestQueue.add(request);
    } //end createGetArticlesObjectRequestGET

    /**
     * Creates request for text of defined article
     */
    public static void createGetArticleRequest(RequestQueue requestQueue, SessionManager sessionManager,
                                               String articleId, boolean locked,
                                               Response.Listener<JSONObject> responseLis, Response.ErrorListener errLis,
                                               boolean withPictures){

        Request<JSONObject> request;
        String url = Endpoints.getConcreteArticleRequestUrl(articleId, withPictures);
        Log.d(VOLLEY_DEBUG, "Request url: " + url);

        if (!locked){ //not locked = public article
            request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    (JSONObject) null,
                    responseLis,
                    errLis);

        } else { //locked article
            if (sessionManager.getRole() == ROLE_LOGGED_SUBSCRIPTION_OK){
                //init POST parameters
                Map<String, String> params = new HashMap<>();
                params.put(KEY_PARAMS_API_KEY, sessionManager.getAPI_key());

                //CustomNotificationManager.issueNotification("Loading article, API_KEY: " + session.getAPI_key(), NOTIFICATION_API_KEY_TEST); // debug notification
                Log.d(VOLLEY_DEBUG, "Loading article, API_KEY: " + sessionManager.getAPI_key());

                //create request
                 request = new JsonObjectCustomUtf8Request(
                        Request.Method.POST,
                        url,
                        params,
                        responseLis,
                        errLis);

            } else { // Other roles
                 request = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        (JSONObject) null,
                        responseLis,
                        errLis);

            } // if role == ROLE_LOGGED_SUBSCRIPTION_OK
        }// if !locked

        request.setShouldCache(true);
        requestQueue.add(request);
    }//end createGetArticleRequest

    public static void createLoginRequestPOST(RequestQueue requestQueue, final Map<String, String> parameters,
                                              Response.Listener<JSONObject> responseList,
                                              Response.ErrorListener errList){

        JsonObjectCustomUtf8Request request = new JsonObjectCustomUtf8Request(
                Request.Method.POST,
                Endpoints.getLoginUrl(),
                parameters,
                responseList,
                errList
        );

        request.setShouldCache(false); //disable caching!!!
        requestQueue.add(request);

    } //end createLoginRequestPOST

    public static void createReLoginRequest(RequestQueue requestQueue, SessionManager sessionManager,
                                            Response.Listener<JSONObject> responseList,
                                            Response.ErrorListener errList){

        //load the credentials from session manager
        Map<String, String> params = new HashMap<>();
        params.put(KEY_PARAMS_EMAIL, sessionManager.getEmail());
        params.put(KEY_PARAMS_PASSWORD, sessionManager.getPassword());

        createLoginRequestPOST(requestQueue, params, responseList, errList);

    } //end createReLoginRequest
} //end class Requestor
