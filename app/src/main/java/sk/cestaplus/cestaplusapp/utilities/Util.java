package sk.cestaplus.cestaplusapp.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter_All;
import sk.cestaplus.cestaplusapp.adapters.ArticleRecyclerViewAdapter_PicturesAndTitles;
import sk.cestaplus.cestaplusapp.listeners.ListStyleChangeListener;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_FROM_NOTIFICATION;
import static sk.cestaplus.cestaplusapp.utilities.SessionManager.LIST_STYLE_ALL;
import static sk.cestaplus.cestaplusapp.utilities.SessionManager.LIST_STYLE_PICTURES_AND_TITLES;

/**
 * Created by Matej on 30. 3. 2015.
 */
public class Util {

    public static void issueNotification(String text, int id){

        NotificationManager notMngr = CustomApplication.getCustomAppNotMngr();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(CustomApplication.getCustomAppContext()); //create a builder

        builder.setAutoCancel(true);
        builder.setContentTitle("cesta+");
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.notif_icon_5);
        //builder.setLargeIcon(BitmapFactory.decodeFile("new_icon.png"));
        /*builder.setLargeIcon(BitmapFactory.decodeResource(Context.getResources(),
                R.drawable.new_icon));*/
        builder.setColor(Color.parseColor("#9cddf1")); //conversion from hex string to argb int

        Intent notificationIntent = new Intent(CustomApplication.getCustomAppContext(), MainActivity.class);
        notificationIntent.putExtra(KEY_FROM_NOTIFICATION, true);
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

    public static void checkScreenSize(Context context) {
        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String toastMsg;
        switch(screenSize) { // screenSize
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                toastMsg = "Extra Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = "Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = "Normal screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                toastMsg = "Small screen";
                break;
            default:
                toastMsg = "Nedá sa určiť veľkosť obrazovky!";
        }

        int density= context.getResources().getDisplayMetrics().densityDpi;
        switch(density)
        {
            case DisplayMetrics.DENSITY_LOW:
                toastMsg += " LDPI";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                toastMsg += " MDPI";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                toastMsg += " HDPI";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                toastMsg += " XHDPI";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                toastMsg += " XXHDPI";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                toastMsg += " XXXHDPI";
                break;
        }

        Toast.makeText(context.getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
    }//end check screensize()

    private static String[] getListStyles() {
        return new String[] {"Zobraziť okrem nadpisov aj popisy k článkom", "Zobraziť len nadpisy"};
    }

    public static ArticleRecyclerViewAdapter getCrvaType(Context context, boolean hasHeader){
        SessionManager session = new SessionManager(context);

        switch (session.getListStyle()){
            case LIST_STYLE_ALL:{ //LIST_STYLE_ALL = 0
                return new ArticleRecyclerViewAdapter_All(context, hasHeader);
            }
            case LIST_STYLE_PICTURES_AND_TITLES:{ //LIST_STYLE_PICTURES_AND_TITLES = 1
                return new ArticleRecyclerViewAdapter_PicturesAndTitles(context, hasHeader);
            }
            default:{
                return new ArticleRecyclerViewAdapter_All(context, hasHeader);
            }

        } //end switch getListStyle()
    }

    public static void showListStyleDialog(final ListStyleChangeListener listener,
                                           final SessionManager session, final Context context) {
        String [] items = Util.getListStyles();

        new AlertDialog.Builder(context)
                .setTitle("Vyberte štýl zoznamu: ")
                .setCancelable(true)
                .setSingleChoiceItems(items, session.getListStyle(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item_num) {
                                switch (item_num) {
                                    case 0:
                                        listener.handleListStyleSelection(dialog, LIST_STYLE_ALL); // = 0
                                        break;

                                    case 1:
                                        listener.handleListStyleSelection(dialog, LIST_STYLE_PICTURES_AND_TITLES); // = 1
                                        break;
                                }// end switch
                            }
                        })
                .show();
    }//end showListStyleDialog()

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /**
     * Removes HTML tags from input string
     * SOURCE: http://stackoverflow.com/questions/6502759/how-to-strip-or-escape-html-tags-in-android
     */
    public static String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    /**
     * SOURCES: http://stackoverflow.com/questions/7896615/android-how-to-get-value-of-an-attribute-in-code
     *          http://stackoverflow.com/questions/13719103/how-to-retrieve-style-attributes-programatically-from-styles-xml
     *          http://stackoverflow.com/questions/17277618/get-color-value-programmatically-when-its-a-reference-theme
     *          http://stackoverflow.com/questions/9398610/how-to-get-the-attr-reference-in-code
     */
    public static int getActionBarSize(Context context){
        int[] attrs = new int[] { android.R.attr.actionBarSize };
        TypedValue typedValue = new TypedValue();
        int indexOfAttrActionBarSize = 0;
        TypedArray at = context.obtainStyledAttributes(typedValue.data, attrs);
        int actionBarSize = at.getDimensionPixelSize(indexOfAttrActionBarSize, -1);
        at.recycle();

        return actionBarSize;
    }

    /**
     * SOURCE: http://stackoverflow.com/a/23900692
     * Take look: http://ingenious-camel.blogspot.sk/2012/04/how-to-get-width-and-height-in-android.html
     * @return the usable display height in dp (dip) (minus the status bar etc)
     */
    public static int getUsableScreenHeightDp(Activity activity){
        Configuration configuration = activity.getResources().getConfiguration();
        return configuration.screenHeightDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.

    }

    public static int getUsableScreenHeightPixels(Context context){
        Configuration configuration = context.getResources().getConfiguration();
        int screenHeightDp = configuration.screenHeightDp; //The current width of the available screen space, in dp units, corresponding to screen width resource qualifier.

        return Math.round(pxFromDp(context, (float) screenHeightDp));
    }

    /**
     * SOURCE: http://stackoverflow.com/a/12147550
     */
    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    /**
     * SOURCE: http://stackoverflow.com/a/12147550
     */
    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int getScreenSize(Context context){
        return context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    /**
     * SOURCE: http://stackoverflow.com/questions/3663665/how-can-i-get-the-current-screen-orientation
     * @return true if screen orientation is landscape, false otherwise
     */
    public static boolean inLandscapeOrientation(Context context){
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        } else {
            return false;
        }
    }
}//end Util class
