package sk.cestaplus.cestaplusapp.utilities;

//import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;

//import com.facebook.stetho.Stetho;


/**
 * Created by Matej on 12.3.2015.
 */
public class CustomApplication
    extends MultiDexApplication {
    //extends Application {

    private static Context context;
    private static NotificationManager notMngr;
    private static int screenSize;

    // STETHO for DEBUGGING - SQLite database, network inspection, ...
    // SOURCES:
    //      http://facebook.github.io/stetho/
    //      https://code.tutsplus.com/tutorials/debugging-android-apps-with-facebooks-stetho--cms-24205
    //          (don't forget step 2 !)
    //      http://stackoverflow.com/a/32173974
    //      http://stackoverflow.com/a/31465301
    // Look also: http://stackoverflow.com/questions/19194576/how-do-i-view-the-sqlite-database-on-an-android-device
    public void onCreate(){
        super.onCreate();
        //Stetho.initializeWithDefaults(this);

        context = getApplicationContext();
        notMngr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    public static Context getCustomAppContext(){
        return context;
    }

    public static NotificationManager getCustomAppNotMngr(){
        return notMngr;
    }

    public static int getCustomAppScreenSize(){ return screenSize; }

} //end class CustomApplication
