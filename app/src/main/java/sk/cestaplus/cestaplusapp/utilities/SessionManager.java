package sk.cestaplus.cestaplusapp.utilities;

/**
 * Created by Matej on 22. 4. 2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.objects.UserInfo;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_DEFAULT_VALUE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_NAME;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SUBSCRIPTION_END;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SUBSCRIPTION_NAME;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SUBSCRIPTION_START;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SURNAME;

public class SessionManager {

    private static String SESSION_TAG = SessionManager.class.getSimpleName(); // LogCat tag

    private SharedPreferences sharedPreferences;

    private Editor editor;
    private Context context;

    //private static final String PREF_NAME = "AndroidHiveLogin"; // Shared preferences file name
    //int PRIVATE_MODE = 0; // Shared sharedPreferences mode

    // shared preferences keys
    //private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
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

    /*
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(SESSION_TAG, "User login session modified!");
    }*/

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

    public void saveCredentialsAndApiKey(String API_key, String email, String password) {

        saveCredencials(email, password);

        //setLogin(true);
        setAPI_key(API_key);
    }

    public void saveCredencials(String email, String pass) {

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASS, pass);

        editor.commit(); // commit changes
    }

    public void saveUserInfo(UserInfo userInfo){
        editor.putString(KEY_NAME, userInfo.getName());
        editor.putString(KEY_SURNAME, userInfo.getSurname());
        editor.putString(KEY_SUBSCRIPTION_START, DateFormats.dateFormatJSON.format(userInfo.getSubscription_start()));
        editor.putString(KEY_SUBSCRIPTION_END, DateFormats.dateFormatJSON.format(userInfo.getSubscription_end()));
        editor.putString(KEY_SUBSCRIPTION_NAME, userInfo.getSubscription_name());

        editor.commit(); // commit changes
    }

    public UserInfo getUserInfo(){
        String name = sharedPreferences.getString(KEY_NAME, "name");
        String surname = sharedPreferences.getString(KEY_SURNAME, "surname");
        String subscription_start_string = sharedPreferences.getString(KEY_SUBSCRIPTION_START, "subscription_start");
        String subscription_end_string = sharedPreferences.getString(KEY_SUBSCRIPTION_END, "subscription_end");
        String subscription_name = sharedPreferences.getString(KEY_SUBSCRIPTION_NAME, "subscription_name");

        Date subscription_start = new Date();
        Date subscription_end = new Date();

        try {
            subscription_start = DateFormats.dateFormatJSON.parse(subscription_start_string);
            subscription_end = DateFormats.dateFormatJSON.parse(subscription_end_string);

        } catch (ParseException pEx){
            Log.e("error", "CHYBA PARSOVANIA DATUMU" + pEx.getMessage());
        }
        return new UserInfo(name, surname, subscription_start, subscription_end, subscription_name);
    }

    public String getFullName(){
        String name = sharedPreferences.getString(KEY_NAME, "name");
        String surname = sharedPreferences.getString(KEY_SURNAME, "surname");

        return name + " " + surname;
    }

    public void logout() {
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
        return Integer.parseInt(sharedPreferences.getString(context.getString(R.string.pref_text_size_key), "1"));
    }

    public void setTextSize(int textSize){
        editor.putString(context.getString(R.string.pref_text_size_key), String.valueOf(textSize));

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
        return Integer.parseInt(sharedPreferences.getString(context.getString(R.string.pref_list_style_key), "0"));
    }

    public void setListStyle(int listStyle){
        editor.putString(context.getString(R.string.pref_list_style_key), String.valueOf(listStyle));

        // commit changes
        editor.commit();
    }

    public boolean getPostNotificationStatus(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_post_notifications_key), true); // true = notifications are on by default
    }

}//end class SessionManager
