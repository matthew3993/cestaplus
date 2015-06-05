package bc.cestaplus.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Requestor {

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
