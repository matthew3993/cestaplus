package sk.cestaplus.cestaplusapp.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.MainActivity;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_FROM_NOTIFICATION;

/**
 * Created by matth on 03.03.2017.
 * Custom, because NotificationManager already exists in Android SDK
 * TODO: for now this class is just used as container for static methods about notification, but what about SINGLETON??
 */
public class CustomNotificationManager {

    public static void issueNotification(String text, int id){

        NotificationManager notMngr = CustomApplication.getCustomAppNotMngr();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(CustomApplication.getCustomAppContext()); //create a builder

        builder.setAutoCancel(true);
        builder.setContentTitle("cesta+");
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.notification_icon);
        //builder.setLargeIcon(BitmapFactory.decodeFile("new_icon.png"));
        /*builder.setLargeIcon(BitmapFactory.decodeResource(Context.getResources(),
                R.drawable.new_icon));*/
        builder.setColor(Color.parseColor("#9cddf1")); //conversion from hex string to argb int

        Intent notificationIntent = new Intent(CustomApplication.getCustomAppContext(), MainActivity.class);
        notificationIntent.putExtra(KEY_INTENT_FROM_NOTIFICATION, true);
        /*
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        */

        /*
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);*/

        // The stack notificationBuilder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(CustomApplication.getCustomAppContext()); //Create a stack notificationBuilder

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class); //Add the back stack to the stack notificationBuilder

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent =
            stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            );

        /*
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        CustomApplication.getCustomAppContext(), //The Context in which this PendingIntent should start the activity.
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT //0
                );
        */
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        notMngr.notify(id, notification);
    } //end issueNotification()

        /*
    Intent notificationIntent = new Intent(CustomApplication.getCustomAppContext(), MainActivity.class);
        notificationIntent.putExtra("fromNotification", true);

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        CustomApplication.getCustomAppContext(), //The Context in which this PendingIntent should start the activity.
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT //0
                );
     */
}
