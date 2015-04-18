package bc.cestaplus.utilities;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;

/**
 * Created by Matej on 12.3.2015.
 */
public class CustomApplication
    extends Application {

    private static Context context;
    private static NotificationManager notMngr;

    public void onCreate(){
        context = getApplicationContext();
        notMngr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public static Context getCustomAppContext(){
        return context;
    }

    public static NotificationManager getCustomAppNotMngr(){
        return notMngr;
    }
}
