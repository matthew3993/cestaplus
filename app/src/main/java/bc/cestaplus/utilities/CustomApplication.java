package bc.cestaplus.utilities;

import android.app.Application;
import android.content.Context;

/**
 * Created by Matej on 12.3.2015.
 */
public class CustomApplication
    extends Application {

    private static Context context;

    public void onCreate(){
        context = getApplicationContext();
    }

    public static Context getCustomAppContext(){
        return context;
    }
}
