package bc.cestaplus.network;

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

import bc.cestaplus.network.custom_requests.JsonArrayUtf8FutureRequest;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Requestor {

    public static JSONArray createUpdateRequest(RequestQueue requestQueue){
        JSONArray response = null;

        RequestFuture<JSONArray> requestFuture = RequestFuture.newFuture();

        JsonArrayUtf8FutureRequest request = new JsonArrayUtf8FutureRequest(
                Request.Method.GET,
                Endpoints.getUpdateRequestUrl(),
                null,
                requestFuture,
                requestFuture); //end of JsonArrayUtf8FutureRequest

        request.setShouldCache(false); //disable caching
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
    } //end createGetClankyObjectRequestGET

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
