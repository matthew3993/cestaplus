package sk.cestaplus.cestaplusapp.utilities.utilClasses;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageView;

import sk.cestaplus.cestaplusapp.utilities.Util;

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
}
