package sk.cestaplus.cestaplusapp.utilities;

/**
 * Created by Matej on 22. 4. 2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_DEFAULT_VALUE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PREF_LIST_STYLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PREF_POST_NOTIFICATIONS;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PREF_TEXT_SIZE;

public class SessionManager {

    private static String SESSION_TAG = SessionManager.class.getSimpleName(); // LogCat tag

    private SharedPreferences sharedPreferences;

    private Editor editor;
    private Context context;

    //private static final String PREF_NAME = "AndroidHiveLogin"; // Shared preferences file name
    //int PRIVATE_MODE = 0; // Shared sharedPreferences mode

    // shared preferences keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_API_KEY = "API_key";

    private static final String KEY_ROLE = "role";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "pass";

    private static final String KEY_LAST_HEADER_ARTICLE_ID = "lastHeaderArticleId";
    private static final String KEY_LAST_BATERKA_DATE = "lastBaterkaDate";

    public SessionManager(Context context) {
        this.context = context;
        //sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(SESSION_TAG, "User login session modified!");
    }

    public void setAPI_key(String API_key){
        editor.putString(KEY_API_KEY, API_key);

        // commit changes
        editor.commit();

        Log.d(SESSION_TAG, "API_key modified!");
    }

    public void clearAPI_key(){
        editor.remove(KEY_API_KEY);

        // commit changes
        editor.commit();

        Log.d(SESSION_TAG, "API_key removed!");
    }

    public void setRole(int role){
        editor.putInt(KEY_ROLE, role);

        // commit changes
        editor.commit();

        Log.d(SESSION_TAG, "ROLE modified!");
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void loginAndRememberPassword(String API_key, String email, String password) {

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

    public void login(String API_key) {
        setAPI_key(API_key);
        setLogin(true);
    }

    public void logout() {
        setLogin(false);
        clearAPI_key();
        setRole(ROLE_DEFAULT_VALUE); //default value
    }

    public String getEmail(){
        return sharedPreferences.getString(KEY_EMAIL, "NA");
    }

    public String getPassword(){
        return sharedPreferences.getString(KEY_PASS, "NA");
    }

    public String getAPI_key(){
        return sharedPreferences.getString(KEY_API_KEY, "");
    }

    public int getRole(){
        return sharedPreferences.getInt(KEY_ROLE, ROLE_DEFAULT_VALUE);
    }

    public long getLastBaterkaDate(){
        return sharedPreferences.getLong(KEY_LAST_BATERKA_DATE, 0);
    }

    public void setLastBaterkaDate(long lastBaterkaDate){
        editor.putLong(KEY_LAST_BATERKA_DATE, lastBaterkaDate);

        // commit changes
        editor.commit();
    }

    public int getTextSize(){
        return Integer.parseInt(sharedPreferences.getString(KEY_PREF_TEXT_SIZE, "1"));
    }

    public void setTextSize(int textSize){
        editor.putString(KEY_PREF_TEXT_SIZE, String.valueOf(textSize));

        // commit changes
        editor.commit();
    }

    public String getLastHeaderArticleId(){
        return sharedPreferences.getString(KEY_LAST_HEADER_ARTICLE_ID, "NA");
    }

    public void setLastHeaderArticleId(String headerArticleId){
        editor.putString(KEY_LAST_HEADER_ARTICLE_ID, headerArticleId);

        // commit changes
        editor.commit();
    }

    public int getListStyle(){
        return Integer.parseInt(sharedPreferences.getString(KEY_PREF_LIST_STYLE, "0"));
    }

    public void setListStyle(int listStyle){
        editor.putString(KEY_PREF_LIST_STYLE, String.valueOf(listStyle));

        // commit changes
        editor.commit();
    }

    public boolean getPostNotificationStatus(){
        return sharedPreferences.getBoolean(KEY_PREF_POST_NOTIFICATIONS, true); // true = notifications are on by default
    }

}//end class SessionManager