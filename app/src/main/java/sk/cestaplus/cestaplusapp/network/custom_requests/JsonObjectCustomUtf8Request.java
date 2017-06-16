package sk.cestaplus.cestaplusapp.network.custom_requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import sk.cestaplus.cestaplusapp.utilities.CustomHttpHeaderParser;

/**
 * Created by Matej on 22. 4. 2015.
 */
public class JsonObjectCustomUtf8Request
    extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public JsonObjectCustomUtf8Request(String url, Map<String, String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    public JsonObjectCustomUtf8Request(int method, String url, Map<String, String> params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    };

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers)); //default
            String jsonString = new String(response.data, "UTF-8");

            //return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            return Response.success(new JSONObject(jsonString), CustomHttpHeaderParser.parseIgnoreCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));

        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}
