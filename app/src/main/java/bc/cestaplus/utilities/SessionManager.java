package bc.cestaplus.utilities;

/**
 * Created by Matej on 22. 4. 2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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

    private static final String KEY_REMEMBERED = "remembered";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "pass";

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

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public boolean isRemembered(){
        return pref.getBoolean(KEY_REMEMBERED, false);
    }

    public void prihlasAzapamataj(String API_key, String email, String password) {

        zapamatajPrihlasenie(email, password);

        setLogin(true);
        setAPI_key(API_key);
    }

    public void zapamatajPrihlasenie(String email, String pass) {

        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASS, pass);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public void prihlas(String API_key) {
        setLogin(true);
        setAPI_key(API_key);
    }

    public void odhlas() {
        setLogin(false);
        clearAPI_key();
    }

    public String getEmail(){
        return pref.getString(KEY_EMAIL, "NA");
    }

    public String getPassword(){
        return pref.getString(KEY_PASS, "NA");
    }

}//end sesion manager
