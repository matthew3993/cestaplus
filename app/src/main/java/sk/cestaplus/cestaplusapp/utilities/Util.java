package sk.cestaplus.cestaplusapp.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import sk.cestaplus.cestaplusapp.activities.account_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.account_activities.NotLoggedActivity;
import sk.cestaplus.cestaplusapp.adapters.ArticlesRecyclerViewAdapter;
import sk.cestaplus.cestaplusapp.adapters.ArticlesRecyclerViewAdapter_All;
import sk.cestaplus.cestaplusapp.adapters.ArticlesRecyclerViewAdapter_PicturesAndTitles;
import sk.cestaplus.cestaplusapp.listeners.ListStyleChangeListener;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;

import static sk.cestaplus.cestaplusapp.extras.Constants.LIST_STYLE_ALL;
import static sk.cestaplus.cestaplusapp.extras.Constants.LIST_STYLE_PICTURES_AND_TITLES;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_EXPIRED;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK;

/**
 * Created by Matej on 30. 3. 2015.
 */
public class Util {

    public static int getScreenSize(Context context){
        return context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
    }

    public static int getScreenDensity(Context context){
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static String checkScreenSizeAndDensity(Context context) {
        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String msg = "Veľkosť displeja: ";
        switch(screenSize) { // screenSize
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                msg += "Extra Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                msg += "Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                msg += "Normal screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                msg += "Small screen";
                break;
            default:
                msg += "Nedá sa určiť veľkosť displeja!";
        }

        msg += "\nRozlíšenie: ";

        int density= context.getResources().getDisplayMetrics().densityDpi;
        switch(density)
        {
            case DisplayMetrics.DENSITY_LOW:
                msg += " LDPI";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                msg += " MDPI";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                msg += " HDPI";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                msg += " XHDPI";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                msg += " XXHDPI";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                msg += " XXXHDPI";
                break;
        }

        return msg;
    }//end check checkScreenSizeAndDensity()

    public static void checkScreenSizeAndDensityToast(Context context) {
        String toastMsg = checkScreenSizeAndDensity(context);

        Toast.makeText(context.getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
    }//end check checkScreenSizeAndDensityToast()

    private static String[] getListStyles() {
        return new String[] {"Zobraziť okrem nadpisov aj popisy k článkom", "Zobraziť len nadpisy"};
    }

    public static ArticlesRecyclerViewAdapter getArvaType(Context context, boolean hasHeader){
        SessionManager session = new SessionManager(context);

        switch (session.getListStyle()){
            case LIST_STYLE_ALL:{ //LIST_STYLE_ALL = 0
                return new ArticlesRecyclerViewAdapter_All(context, hasHeader);
            }
            case LIST_STYLE_PICTURES_AND_TITLES:{ //LIST_STYLE_PICTURES_AND_TITLES = 1
                return new ArticlesRecyclerViewAdapter_PicturesAndTitles(context, hasHeader);
            }
            default:{
                return new ArticlesRecyclerViewAdapter_All(context, hasHeader);
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

    public static Class getAccountActivityToStart(){
        if (isLoggedIn()) {
            return LoggedActivity.class;
        } else {
            return NotLoggedActivity.class;
        }
    }

    /**
     * Should be used ONLY (!) when choosing right account activity to start.
     * (LoginActivity check is exception)
     * Warning: This method does not answer question: "Is subscription valid?"
     */
    public static boolean isLoggedIn(){
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());
        int role = session.getRole();

        if ((role == ROLE_LOGGED_SUBSCRIPTION_OK) || (role == ROLE_LOGGED_SUBSCRIPTION_EXPIRED)){
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSubscriptionValid(int role){
        if ((role == ROLE_LOGGED_SUBSCRIPTION_OK)){
            return true;
        } else {
            return false;
        }
    }


    public static void setOnOffsetChangedListener(AppBarLayout appBarLayout, final int attrActionBarSize,
                                                  final CollapsingToolbarLayout collapsingToolbarLayout, final String title) {
        //show collapsing toolbar layout title ONLY when collapsed
        //SOURCE: http://stackoverflow.com/questions/9398610/how-to-get-the-attr-reference-in-code
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < attrActionBarSize + 1) {
                    collapsingToolbarLayout.setTitle(title);
                    isShow = true;

                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }
}//end Util class
