package bc.cestaplus.utilities;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import bc.cestaplus.R;
import bc.cestaplus.activities.MainActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter_All;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter_PicturesAndTitles;
import bc.cestaplus.objects.ArticleObj;

import static bc.cestaplus.utilities.SessionManager.LIST_STYLE_ALL;
import static bc.cestaplus.utilities.SessionManager.LIST_STYLE_PICTURES_AND_TITLES;

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
        notificationIntent.putExtra("fromNotification", true);
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

    public static String[] getListStyles() {
        return new String[] {"Zobraziť okrem nadpisov aj popisy k článkom", "Zobraziť len nadpisy"};
    }

    public static ClanokRecyclerViewAdapter getCrvaType(SessionManager session, Context context){
        switch (session.getListStyle()){
            case LIST_STYLE_ALL:{ //LIST_STYLE_ALL = 0
                return new ClanokRecyclerViewAdapter_All(context);
            }
            case LIST_STYLE_PICTURES_AND_TITLES:{ //LIST_STYLE_PICTURES_AND_TITLES = 1
                return new ClanokRecyclerViewAdapter_PicturesAndTitles(context);
            }
            default:{
                return new ClanokRecyclerViewAdapter_All(context);
            }

        } //end switch getListStyle()
    }

    public static void showListStyleDialog(final SessionManager session, final Context context,
                                     final ClanokRecyclerViewAdapter crva, final RecyclerView recyclerView, final ArrayList<ArticleObj> articleList) {
        String [] items = Util.getListStyles();

        new AlertDialog.Builder(context)
                .setTitle("Vyberte štýl zoznamu: ")
                .setCancelable(true)
                .setSingleChoiceItems(items, session.getListStyle(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item_num) {
                                switch (item_num) {
                                    case 0:
                                        handleSelection(dialog, LIST_STYLE_ALL, session, context, crva, recyclerView, articleList); // = 0
                                        break;

                                    case 1:
                                        handleSelection(dialog, LIST_STYLE_PICTURES_AND_TITLES, session, context, crva, recyclerView, articleList); // = 1
                                        break;
                                }// end switch
                            }
                        })
                .show();
    }//end showListStyleDialog()

    private static void handleSelection(DialogInterface dialog, int listStyle, SessionManager session, Context context,
                                 ClanokRecyclerViewAdapter crva, RecyclerView recyclerView, ArrayList<ArticleObj> articleList) {
        session.setListStyle(listStyle); //save list style

        crva = getCrvaType(session, context);
        crva.setClanky(articleList);
        recyclerView.setAdapter(crva);

        dialog.dismiss(); //dismiss the dialog

    } //end handleSelection()

    public static void refreshRecyclerView(SessionManager session, Context context,
                            ClanokRecyclerViewAdapter crva, RecyclerView recyclerView, ArrayList<ArticleObj> articleList){

        crva = getCrvaType(session, context);
        crva.setClanky(articleList);
        recyclerView.setAdapter(crva);
    } //end refreshRecyclerView()



}//end Util class
