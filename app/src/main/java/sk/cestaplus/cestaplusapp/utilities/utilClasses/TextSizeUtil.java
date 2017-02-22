package sk.cestaplus.cestaplusapp.utilities.utilClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;

/**
 * Created by matth on 13.02.2017.
 */
public class TextSizeUtil {

    public static final int TEXT_SIZE_SMALL = 0;
    public static final int TEXT_SIZE_NORMAL = 1;
    public static final int TEXT_SIZE_BIG = 2;

    private TextSizeUtil(){}

    /**
     * SOURCE: http://stackoverflow.com/a/26382502
     * @param activity
     */
    public static void showTextSizeDialog(Activity activity) {
        // Session manager
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        String [] items = getTextSizes(activity.getApplicationContext());

        new AlertDialog.Builder(activity)
                .setTitle(R.string.select_text_size_dialog_title)
                .setCancelable(false)
                .setSingleChoiceItems(items, session.getTextSize(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item_num) {
                                switch (item_num) {
                                    case 0:
                                        handleTextSizeSelection(dialog, session, TEXT_SIZE_SMALL); // 0
                                        break;

                                    case 1:
                                        handleTextSizeSelection(dialog, session, TEXT_SIZE_NORMAL); // 1
                                        break;

                                    case 2:
                                        handleTextSizeSelection(dialog, session, TEXT_SIZE_BIG); // 2
                                        break;
                                }
                            }
                        })
                .show();
    }//end showTextSizeDialog()

    private static void handleTextSizeSelection(DialogInterface dialog, SessionManager session, int textSize) {
        // save chosen size
        // - will trigger onSharedPreferenceChanged() in SharedPreferences.OnSharedPreferenceChangeListener
        // with KEY_PREF_TEXT_SIZE key
        session.setTextSize(textSize);

        dialog.dismiss(); //dismiss the dialog
    } //end handleTextSizeSelection()

    public static String[] getTextSizes(Context context) {
        //SOURCE: http://stackoverflow.com/a/26382502
        int smallTextSize = (int) context.getResources().getDimension(R.dimen.article_font_size_small);
        int normalTextSize = (int) context.getResources().getDimension(R.dimen.article_font_size_normal);
        int bigTextSize = (int) context.getResources().getDimension(R.dimen.article_font_size_big);

        String [] ret = new String[]{"Malá (" + smallTextSize + ")", "Normálna (" + normalTextSize + ")", "Veľká (" + bigTextSize + ")"};

        return ret;
    }
}
