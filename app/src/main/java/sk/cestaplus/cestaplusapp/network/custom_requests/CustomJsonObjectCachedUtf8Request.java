package sk.cestaplus.cestaplusapp.network.custom_requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import sk.cestaplus.cestaplusapp.network.CustomHttpHeaderParser;

/**
 * Created by Matej on 22. 4. 2015.
 *
 * NOTE about VOLLEY CACHING:
 * {@see https://stackoverflow.com/a/23883980}
 * Volley sets caching policy for it's built in Request classes ( for example {@link com.android.volley.toolbox.JsonObjectRequest} ),
 * according to Cache-Control headers of response from server (see StackOverflow questions and web pages about
 * Cache-Control headers below), if we don't disable caching with {@link Request#setShouldCache(boolean)}.
 *
 * In other words, Volley's DEFAULT behavior is, that caching is ENABLED (we do NOT NEED to setShouldCache(true) manually) =>
 * BUT if request is really cached (and how), is set according to Cache-Control headers of response from server - it can, but don't
 * have to be cached. This means that if server don't send any Cache-Control headers with response, response will NOT be cached.
 * And if we DISABLE caching with setShouldCache(false), request will NOT be cached anyway (even if server allows caching).
 *
 * Cache-Control headers are parsed in {@link Request#parseNetworkResponse(NetworkResponse)} by
 * {@link com.android.volley.toolbox.HttpHeaderParser#parseCacheHeaders(NetworkResponse)}.
 *
 * If we want request to be always cached (independently on Cache-Control headers), we must override / change
 * cache headers parsing method. This is done it this request class.
 * SO this request is always cached (independently on cache headers), unless setShouldCache() is set to false.
 * And 'Charset' of response is always set to 'UTF-8'.
 *
 * SOURCES:
 *      https://stackoverflow.com/a/23883980
 *      https://stackoverflow.com/questions/16783177/set-expiration-policy-for-cache-using-googles-volley (answer https://stackoverflow.com/a/16852462)
 *      https://stackoverflow.com/questions/16781244/android-volley-jsonobjectrequest-caching
 *      https://stackoverflow.com/questions/31897189/android-setup-volley-to-use-from-cache
 *      https://stackoverflow.com/a/25666063
 *
 * Info about Cache-Control headers:
 *      https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
 *      https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html (14.9)
 */
public class CustomJsonObjectCachedUtf8Request
    extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public CustomJsonObjectCachedUtf8Request(String url, Map<String, String> params, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = responseListener;
        this.params = params;
    }

    public CustomJsonObjectCachedUtf8Request(int method, String url, Map<String, String> params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
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
            //Default behavior - parse charset from response headers
            //String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            // do not parse charset form response headers, but set it to 'UTF-8'
            String jsonString = new String(response.data, "UTF-8");

            //Default behavior - cache according Cache-Control headers of response - see class comment for more info
            //return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));

            // PARSE HEADERS BY CUSTOM METHOD - see class comment for more info
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
