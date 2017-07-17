package sk.cestaplus.cestaplusapp.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Matej on 3.3.2015.
 * SOURCE: https://developer.android.com/training/volley/requestqueue.html#singleton
 *
 * Volley lib docs: http://afzaln.com/volley/
 */
public class VolleySingleton {

    private static VolleySingleton sInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context context;

    private VolleySingleton(Context context){
        VolleySingleton.context = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

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
            // init request queue with default cache and default network
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());

            // init request queue with custom cache and custom network
            //SOURCES:
            //  https://stackoverflow.com/questions/25664627/android-volley-does-not-work-offline-with-cache
            //  https://developer.android.com/reference/android/content/Context.html#getCacheDir()
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

} // end of class VolleySingleton
