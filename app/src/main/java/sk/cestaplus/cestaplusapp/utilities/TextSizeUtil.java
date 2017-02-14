package sk.cestaplus.cestaplusapp.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import sk.cestaplus.cestaplusapp.R;

import static sk.cestaplus.cestaplusapp.utilities.Util.TEXT_SIZE_BIG;
import static sk.cestaplus.cestaplusapp.utilities.Util.TEXT_SIZE_NORMAL;
import static sk.cestaplus.cestaplusapp.utilities.Util.TEXT_SIZE_SMALL;

/**
 * Created by matth on 13.02.2017.
 */
public class TextSizeUtil {

    private TextSizeUtil(){}

    /**
     * SOURCE: http://stackoverflow.com/a/26382502
     * @param activity
     */
    public static void showTextSizeDialog(Activity activity) {
        // Session manager
        final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

        String [] items = Util.getTextSizes(activity.getApplicationContext());

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
}
