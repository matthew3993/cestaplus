package bc.cestaplus.network.custom_requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Matej on 14.3.2015.
 */
public class JsonArrayUtf8FutureRequest
    extends Request<JSONArray>{

    private Listener<JSONArray> listener;
    private Map<String, String> params;

    public JsonArrayUtf8FutureRequest(String url, Map<String, String> params, Listener<JSONArray> responseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    /**
     * Zmeneny response listener na RequestFuture !!!
     */
    public JsonArrayUtf8FutureRequest(int method, String url, Map<String, String> params, RequestFuture<JSONArray> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            //toto bolo povodne
            //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            // toto som zmenil ja
            String jsonString = new String(response.data, "UTF-8");

            return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));

        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONArray response) {
        listener.onResponse(response);
    }
} //end classCustomJsonRequest
