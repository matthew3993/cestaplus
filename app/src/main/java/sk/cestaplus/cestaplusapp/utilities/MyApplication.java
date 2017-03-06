package sk.cestaplus.cestaplusapp.utilities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import sk.cestaplus.cestaplusapp.database.ArticleDatabase;


/**
 * Created by Windows on 30-01-2015.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    private static ArticleDatabase mDatabase;

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    public synchronized static ArticleDatabase getWritableDatabase() {
        if (mDatabase == null) {
            mDatabase = new ArticleDatabase(CustomApplication.getCustomAppContext());
        }
        return mDatabase;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        mDatabase = new ArticleDatabase(this);
    }

    public static void saveToPreferences(Context context, String preferenceName, Long preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(preferenceName, preferenceValue);
        editor.apply();
    }

    public static long readFromPreferences(Context context, String preferenceName, long defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getLong(preferenceName, defaultValue);
    }

}//end MyApplication
