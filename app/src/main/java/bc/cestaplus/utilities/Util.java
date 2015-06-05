package bc.cestaplus.utilities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.v4.app.NotificationCompat;

import bc.cestaplus.R;
import bc.cestaplus.activities.MainActivity;

/**
 * Created by Matej on 30. 3. 2015.
 */
public class Util {

    public static final int TEXT_SIZE_SMALL = 0;
    public static final int TEXT_SIZE_NORMAL = 1;
    public static final int TEXT_SIZE_BIG = 2;


    public Util() {

    }

    public static void issueNotification(String text, int id){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(CustomApplication.getCustomAppContext()); //create a builder

        builder.setAutoCancel(true);
        builder.setContentTitle("cesta+");
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.icon);

        Notification notification = builder.build();

        NotificationManager notMngr = CustomApplication.getCustomAppNotMngr();

        notMngr.notify(id, notification);
    } //end issueNotification()

    /*
    public static int getScreenSize(){
        return CustomApplication.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
    }*/



    public static String[] getTextSizes() {
        String [] ret;

        switch(CustomApplication.getCustomAppScreenSize()) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                ret = new String[]{"Malá (15)", "Normálna (20)", "Veľká (25)"};
                break;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                ret = new String[]{"Malá (10)", "Normálna (15)", "Veľká (20)"};
                break;

            default:
                ret = new String[]{"Malá (10)", "Normálna (15)", "Veľká (20)"};
        }

        return ret;
    }
}//end Util class
