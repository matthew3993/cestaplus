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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sk.cestaplus.cestaplusapp.network.custom_requests.JsonArrayUtf8FutureRequest;
import sk.cestaplus.cestaplusapp.network.custom_requests.JsonObjectUtf8FutureRequest;

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

} //end class Requestor
