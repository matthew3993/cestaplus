package sk.cestaplus.cestaplusapp.network;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

import static sk.cestaplus.cestaplusapp.extras.Constants.CACHE_ENTRY_SOFT_TTL_MIN;
import static sk.cestaplus.cestaplusapp.extras.Constants.CACHE_ENTRY_TTL_MIN;

/**
 * Created by matth on 14.06.2017.
 *
 * Info about TTL (Time to live): https://en.wikipedia.org/wiki/Time_to_live
 * Difference between ttl and soft ttl:
 *      https://stackoverflow.com/questions/28523435/what-s-the-different-of-entry-softttl-and-entry-ttl-in-volley
 *      https://groups.google.com/forum/#!topic/volley-users/70CVZdsLX0w
 *
 * Info about Cache-Control headers:
 *      https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control
 *      https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html (14.9)
 */
public class CustomHttpHeaderParser {
    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        //difference between softTtl and ttl: https://stackoverflow.com/a/31587979
        // "in" here ==> read as 'after' // 'in' == 'ZA'
        // in 3 minutes cache will be hit, but also refreshed on background (validated),
        // but if during refreshing is no network available, cache entry will not be loaded
        final long cacheHitButRefreshed = CACHE_ENTRY_SOFT_TTL_MIN * 60 * 1000;
        final long cacheExpired = CACHE_ENTRY_TTL_MIN * 60 * 1000;   // in 24 hours this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }
}
