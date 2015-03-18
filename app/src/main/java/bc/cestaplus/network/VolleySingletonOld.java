package bc.cestaplus.network;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import bc.cestaplus.utilities.CustomApplication;

/**
 * Created by Matej on 3.3.2015.
 */
public class VolleySingletonOld {
    private static VolleySingletonOld sInstance = null;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    private VolleySingletonOld(){
        mRequestQueue = Volley.newRequestQueue(CustomApplication.getCustomAppContext()/*MainActivity.getAPPContext()*/);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() { //anonymna trieda ImageLoader

            private LruCache<String, Bitmap> cache = new LruCache<>((int)Runtime.getRuntime().maxMemory()/1024/8);
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

    public static VolleySingletonOld getInstance(){
        if (sInstance == null){
            sInstance = new VolleySingletonOld();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        return mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }
} // end of VolleySingleton class
