package bc.cestaplus.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import bc.cestaplus.R;
import bc.cestaplus.activities.MainActivity;

/**
 * Created by Matej on 30. 3. 2015.
 */
public class Util {

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
    }
}//end Utii class
