package bc.cestaplus.utilities;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Matej on 12.3.2015.
 */
public class CustomApplication
    extends Application {

    private static Context context;
    private static NotificationManager notMngr;
    private static int screenSize;

    public void onCreate(){
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
