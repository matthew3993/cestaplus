package sk.cestaplus.cestaplusapp.utilities.utilClasses;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static sk.cestaplus.cestaplusapp.extras.Constants.DIMEN_A;
import static sk.cestaplus.cestaplusapp.extras.Constants.DIMEN_B;
import static sk.cestaplus.cestaplusapp.extras.Constants.DIMEN_C;
import static sk.cestaplus.cestaplusapp.extras.Constants.DIMEN_D;
import static sk.cestaplus.cestaplusapp.extras.Constants.IMAGES;
import static sk.cestaplus.cestaplusapp.extras.Constants.SMALL;
import static sk.cestaplus.cestaplusapp.extras.Constants.URL_CESTA_PLUS;

/**
 * Created by matth on 21.02.2017.
 */
public class ImageUtil {

    /**
     * Adjust image size to size of the screen.
     * Used for article header images when device is in landscape orientation (no matter screen size),
     * or if size of screen is 'NORMAL' (both orientations).
     */
    public static void resolveAdjustImageHeightToScreenHeight(Context context, ImageView imageView){
        if(Util.inLandscapeOrientation(context)){
            adjustImageHeightToScreenHeight(context, imageView);
            return;
        }

        /*
        // no in landscape
        if (Util.getScreenSize(context) == Configuration.SCREENLAYOUT_SIZE_NORMAL){
            adjustImageHeightToScreenHeight(context, imageView);
            return;
        }
        */
    }

    /**
     * This method is used ONLY for baterka header images.
     * Baterka header images are adjusted only in landscape orientation
     */
    public static void resolveAdjustBaterkaImageHeightToScreenHeight(Context context, ImageView imageView){
        if(Util.inLandscapeOrientation(context)){
            adjustImageHeightToScreenHeight(context, imageView);
            return;
        }
    }

    /**
     *
     * @param imageView which height will be adjusted (no matter screen size)
     * Take a look:
     *      http://stackoverflow.com/questions/24523000/how-to-set-height-width-of-imageview-programmatically-in-android
     */
    public static void adjustImageHeightToScreenHeight(Context context, ImageView imageView){
        int screenHeightPixels = Util.getUsableScreenHeightPixels(context);

        //SOURCES:
        //  http://stackoverflow.com/questions/3144940/set-imageview-width-and-height-programmatically
        //  https://developer.android.com/reference/android/view/ViewGroup.LayoutParams.html#ViewGroup.LayoutParams(int, int)
        imageView.getLayoutParams().height = screenHeightPixels;

        //If you're setting the height after the layout has already been 'laid out', make sure you also call:
        //imageView.requestLayout();
    }

    public static String getImageDimenUrl(Context context, ArticleObj article){

        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        String dimenUrl = URL_CESTA_PLUS + IMAGES + SMALL;


        switch(screenSize) { // screenSize
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                dimenUrl = article.getImageDefaulUrl();   //image url from API
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE: // 7" tablets
                dimenUrl += getImgDimenUrlLarge(context);
                dimenUrl += getFullImageName(article);
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL: // mobile phones
                dimenUrl += getImgDimenUrlNormal(context);
                dimenUrl += getFullImageName(article);
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL: // mostly wearables ?
                dimenUrl = article.getImageDefaulUrl();   //image url from API
                break;
            default:
                dimenUrl = article.getImageDefaulUrl();   //image url from API
                break;
        }

        //Log.d(IMAGE_DEBUG, "Image dime url: " + dimenUrl);
        return dimenUrl;
    }

    private static String getFullImageName(ArticleObj article){
        return String.format("%s_%s", article.getSection(), article.getImageName());
    }

    /**
     * According table in docs - VERSION 1
     * @return Dimen for used device
     */
    public static String getImgDimenUrlNormal(Context context){
        String dimenUrl;
        int density= context.getResources().getDisplayMetrics().densityDpi;

        switch(density)
        {
            case DisplayMetrics.DENSITY_LOW: //not in docs
                dimenUrl = DIMEN_A;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                dimenUrl = DIMEN_B;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                dimenUrl = DIMEN_B;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                dimenUrl = DIMEN_C;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                dimenUrl = DIMEN_C;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                dimenUrl = DIMEN_D;
                break;
            default:
                dimenUrl = DIMEN_D;
        }

        return dimenUrl;
    }

    /**
     * According table in docs - VERSION 1
     * @return Dimen for used device
     */
    public static String getImgDimenUrlLarge(Context context){
        String dimenUrl;
        int density= context.getResources().getDisplayMetrics().densityDpi;

        switch(density)
        {
            case DisplayMetrics.DENSITY_LOW: //not in docs
                dimenUrl = DIMEN_B;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                dimenUrl = DIMEN_B;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                dimenUrl = DIMEN_C;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                dimenUrl = DIMEN_C;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                dimenUrl = DIMEN_D;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                dimenUrl = DIMEN_D;
                break;
            default:
                dimenUrl = DIMEN_D;
        }

        return dimenUrl;
    }
}
