package bc.cestaplus.utilities;

/**
 * Created by Matej on 22. 4. 2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class SessionManager {

    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    //private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_API_KEY = "API_key";

    private static final String KEY_ROLA = "rola";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "pass";

    private static final String KEY_LAST_BATERKA_DATE = "lastBaterkaDate";
    private static final String KEY_TEXT_SIZE = "pref_text_size"; //also preference
    private static final String KEY_LIST_STYLE = "pref_list_style"; //also preference
    private static final String KEY_POST_NOTIFICATION = "pref_post_notifications"; //also preference

    public static final int LIST_STYLE_ALL = 0;
    public static final int LIST_STYLE_PICTURES_AND_TITLES = 1;

    public SessionManager(Context context) {
        this._context = context;
        //pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void setAPI_key(String API_key){
        editor.putString(KEY_API_KEY, API_key);

        // commit changes
        editor.commit();

        Log.d(TAG, "API_key modified!");
    }

    public void clearAPI_key(){
        editor.remove(KEY_API_KEY);

        // commit changes
        editor.commit();

        Log.d(TAG, "API_key removed!");
    }

    public void setRola(int rola){
        editor.putInt(KEY_ROLA, rola);

        // commit changes
        editor.commit();

        Log.d(TAG, "ROLA modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void prihlasAzapamataj(String API_key, String email, String password) {

        saveCredencials(email, password);

        setLogin(true);
        setAPI_key(API_key);
    }

    public void saveCredencials(String email, String pass) {

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASS, pass);

        // commit changes
        editor.commit();
    }

    public void prihlas(String API_key) {
        setAPI_key(API_key);
        setLogin(true);
    }

    public void logout() {
        setLogin(false);
        clearAPI_key();
        setRola(50); //pôvodná hodnota
    }

    public String getEmail(){
        return pref.getString(KEY_EMAIL, "NA");
    }

    public String getPassword(){
        return pref.getString(KEY_PASS, "NA");
    }

    public String getAPI_key(){
        return pref.getString(KEY_API_KEY, "");
    }

    public int getRola(){
        return pref.getInt(KEY_ROLA, 50); //50 = ľubovolne zvolená hodnota znamenajúca 1. spustenie
    }

    public long getLastBaterkaDate(){
        return pref.getLong(KEY_LAST_BATERKA_DATE, 0);
    }

    public void setLastBaterkaDate(long lastBaterkaDate){
        editor.putLong(KEY_LAST_BATERKA_DATE, lastBaterkaDate);

        // commit changes
        editor.commit();
    }

    public int getTextSize(){
        return Integer.parseInt(pref.getString(KEY_TEXT_SIZE, "1"));
    }

    public void setTextSize(int textSize){
        editor.putString(KEY_TEXT_SIZE, String.valueOf(textSize));

        // commit changes
        editor.commit();
    }

    public int getListStyle(){
        return Integer.parseInt(pref.getString(KEY_LIST_STYLE, "0"));
    }

    public void setListStyle(int listStyle){
        editor.putString(KEY_LIST_STYLE, String.valueOf(listStyle));

        // commit changes
        editor.commit();
    }

    public boolean getPostNotificationStatus(){
        return pref.getBoolean(KEY_POST_NOTIFICATION, true);
    }
}//end class SessionManager
