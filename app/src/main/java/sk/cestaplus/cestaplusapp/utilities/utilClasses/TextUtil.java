package sk.cestaplus.cestaplusapp.utilities.utilClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.widget.TextView;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.extras.IErrorCodes;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_EXPIRED;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK;

/**
 * Created by matth on 21.02.2017.
 */

public class TextUtil {
    public static void adjustHeaderTitleTextSize(TextView textView) {
        int lineCount = textView.getLineCount();
    }

    /**
     * SOURCES:
     *      http://stackoverflow.com/a/28347404
     *      http://stackoverflow.com/questions/32298853/reduce-imagespan-height-and-width
     *      https://blog.stylingandroid.com/introduction-to-spans/
     *      https://developer.android.com/studio/write/vector-asset-studio.html
     *
     *      Other to look at:
     *          http://inloop.github.io/svg2android/
     *          http://flavienlaurent.com/blog/2014/01/31/spans/
     */
    @NonNull
    public static void setTextWithImageAtEnd(Context context, String text, TextView textView, int drawableId) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text).append("  ");

        Drawable lockDrawable = context.getResources().getDrawable(drawableId);

        int lineHeight = textView.getLineHeight();

        int height = (int) Math.round(( (double) lineHeight / 100) * 65); // * 75
        int width = (int) Math.round(( (double) height / 20) * 16);
        lockDrawable.setBounds(0,0, width, height);

        //ImageSpan is = getImageSpan(context, lockDrawable);
        ImageSpan is = getImageSpanAPI(lockDrawable);
        //ImageSpan is = new ImageSpan(lockDrawable);

        builder.setSpan(is, builder.length() - 1, builder.length(), 0); // 0

        textView.setTransformationMethod(null);
        textView.setText(builder);
    }

    /**
     *  Solves the problem with ImageSpan in API 21 & 22 (LOLLIPOP & LOLLIPOP_MR1)
     *  Acording to: http://stackoverflow.com/a/30560233
     *  textView.setTransformationMethod(null); - did NOT help
     **/
    private static ImageSpan getImageSpanAPI(Drawable lockDrawable) {

        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            return new ImageSpan(lockDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
        }

        return new ImageSpan(lockDrawable);
    }

    @NonNull
    private static ImageSpan getImageSpanDensity(Context context, Drawable lockDrawable) {

        int density= context.getResources().getDisplayMetrics().densityDpi;
        switch(density)
        {
            case DisplayMetrics.DENSITY_LOW:
            case DisplayMetrics.DENSITY_MEDIUM:
                return new ImageSpan(lockDrawable, DynamicDrawableSpan.ALIGN_BASELINE);

            case DisplayMetrics.DENSITY_HIGH:
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_XXXHIGH:
                return new ImageSpan(lockDrawable);

            default:
                return new ImageSpan(lockDrawable);
        }
    }

    public static void setTitleText(Context context, boolean showLock, String text, TextView textView, int drawableId) {
        String title = text.toUpperCase(); //DON'T FORGET UPPER CASE

        if (showLock){
            setTextWithImageAtEnd(context, title, textView, drawableId);
        } else {
            textView.setText(title);
        }
    }

    public static boolean showLock(int role, boolean isLocked){
        return !Util.isSubscriptionValid(role) && isLocked;
    }

    public static void setTextWithImageAtStart(Context context, String text, TextView textView, int drawableId) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("  ").append(text);

        Drawable lockDrawable = context.getResources().getDrawable(drawableId);

        int lineHeight = textView.getLineHeight();

        int height = (int) Math.round(( (double) lineHeight / 100) * 75);
        int width = (int) Math.round(( (double) height / 20) * 16);
        lockDrawable.setBounds(0,0, width, height);

        ImageSpan is = new ImageSpan(lockDrawable);
        builder.setSpan(is, 0, 1, 0);

        textView.setText(builder);
    }

    /**
     * SOURCES:
     *      https://stackoverflow.com/questions/4602902/how-to-set-the-text-color-of-textview-in-code
     *      https://stackoverflow.com/questions/6200533/set-textview-style-bold-or-italic
     * @param textView TextView with text we want to make red and bold
     */
    public static void setTextBoldAndRed(TextView textView, Context context){
        textView.setTextColor(ContextCompat.getColor(context, R.color.red));
        textView.setTypeface(null, Typeface.BOLD);
    }

    public static String getDaysString(long daysNum) {
        if (daysNum == 1){
            return "deň";

        }

        if (daysNum == 2 || daysNum == 3 || daysNum == 4){
            return "dni";

        }

        return "dní";
    }
}
