package sk.cestaplus.cestaplusapp.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.account_activities.LoginActivity;
import sk.cestaplus.cestaplusapp.extras.IErrorCodes;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.Requestor;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.objects.UserInfo;

import static sk.cestaplus.cestaplusapp.extras.Constants.VOLLEY_DEBUG;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.LOGIN_PARTIALLY_SUCCESSFUL;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.LOGIN_SUCCESSFUL;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_DEFAULT_VALUE;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_EXPIRED;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.SERVER_INTERNAL_ERROR;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_EMAIL;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARAMS_PASSWORD;

/**
 * Created by matth on 26.04.2017.
 *
 * Problem with context SOURCE:
 * http://stackoverflow.com/questions/36817412/singleton-with-context-as-a-variable-memory-leaks
 */
public class LoginManager {

    private static LoginManager instance;

    private static SessionManager session;
    private static VolleySingleton volleySingleton; // networking

    /**
     * About Context: check class comment.
     */
    public static LoginManager getInstance(Context context) {
        if (instance == null){
            instance = new LoginManager(context);
        }
        return instance;
    }

    private LoginManager(Context context) {
        session = new SessionManager(context);
        volleySingleton = VolleySingleton.getInstance(context);
    }

    public void tryLoginWithSavedCredentials(final LoginManagerInteractionListener listener) {
        String email = session.getEmail();
        String password = session.getPassword();

        tryLogin(email, password, true, listener);
    }

    /**
     * Used by:
     *  -   LoginActivity = new login
     * This method IS saving new UserInfo and also credentials.
     */
    public void tryLogin(final String email, final String password, final boolean remember, final LoginManagerInteractionListener listener) {

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){

                int login_error_code = Parser.parseErrorCode(response);

                if (login_error_code == LOGIN_SUCCESSFUL) {
                    String API_key = Parser.parseAPI_key(response);

                    //if (remember) {
                    session.saveCredentialsAndApiKey(API_key, email, password);
                    session.setRole(ROLE_LOGGED_SUBSCRIPTION_OK);
                    UserInfo userInfo = Parser.parseUserInfo(response);
                    session.saveUserInfo(userInfo);
                    //} else {
                    //session.login(API_key);
                    //}

                    listener.onLoginSuccessful(userInfo);

                } else if (login_error_code == LOGIN_PARTIALLY_SUCCESSFUL){
                    //if (remember) {
                    session.saveCredencials(email, password);
                    session.setRole(ROLE_LOGGED_SUBSCRIPTION_EXPIRED);
                    UserInfo userInfo = Parser.parseUserInfo(response);
                    session.saveUserInfo(userInfo);
                    //}

                    listener.onLoginPartiallySuccessful(userInfo);

                } else { //login_error_code != 0
                    listener.onLoginError(login_error_code);
                }

            }//end onResponse
        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // volley error means problem with internet connection
                listener.onLoginNetworkError();
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put(KEY_PARAMS_EMAIL, email);
        params.put(KEY_PARAMS_PASSWORD, password);

        Requestor.createLoginRequestPOST(volleySingleton.getRequestQueue(), params, responseLis, errorLis);
    }

    /**
     * Used by ArticleActivity.
     * Called ONLY in case that user was logged with subscription OK (ROLE_LOGGED_SUBSCRIPTION_OK),
     * but there was a problem with api key.
     * This method is NOT saving new UserInfo !!!
     */
    public void tryRelogin(final int count, final LoginManagerInteractionListener listener) {

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){

                int login_error_code = Parser.parseErrorCode(response);

                switch (login_error_code){
                    case LOGIN_SUCCESSFUL:{ //relogin successful - subscription still OK
                        //parse and save new API key
                        String API_key = Parser.parseAPI_key(response);
                        session.setAPI_key(API_key);

                        //CustomNotificationManager.issueNotification("New API key: " + session.getAPI_key(), NOTIFICATION_API_KEY_TEST+3); // debug notification
                        Log.d(VOLLEY_DEBUG, "New API key: " + session.getAPI_key());

                        listener.onLoginSuccessful(null); // null = user info is not needed in this case
                        break;
                    }
                    case LOGIN_PARTIALLY_SUCCESSFUL: { // relogin not successful - subscription has EXPIRED
                        session.clearAPI_key();
                        session.setRole(ROLE_LOGGED_SUBSCRIPTION_EXPIRED); //change role !!

                        listener.onLoginPartiallySuccessful(null); // null = user info is not needed in this case
                        break;
                    }
                    case SERVER_INTERNAL_ERROR:{
                        // in case of server error - try to login again (totally 3 times)
                        if (count < 4){
                            //try to login again
                            tryRelogin(count + 1, listener);

                        } else {
                            //unable to login for 3 times
                            listener.onLoginNetworkError();
                        }
                        break;
                    }
                    default:{
                        // EMAIL_OR_PASSWORD_MISSING or WRONG_EMAIL_OR_PASSWORD
                        listener.onLoginError(login_error_code);
                    }

                }
            }//end onResponse
        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            if (count < 4){
                //try to login again
                tryRelogin(count + 1, listener);

            } else {
                //unable to login for 3 times
                listener.onLoginNetworkError();
            }
            }
        };

        //CustomNotificationManager.issueNotification("Trying to relogin...", NOTIFICATION_API_KEY_TEST+2); // debug notification
        Log.d(VOLLEY_DEBUG, "Trying to relogin...");

        Requestor.createReLoginRequest(volleySingleton.getRequestQueue(), session, responseLis, errorLis);
    }//end relogin

    public void checkDefaultRole(Activity activity) {
        //application mode check
        int role = session.getRole();

        if (role == ROLE_DEFAULT_VALUE) { //for example: first app start
            // Launching the login activity
            Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
            activity.startActivity(intent);

            activity.finish(); // finish actual activity
        }

    }

    private void loginTry(final Activity activity, final ProgressDialog pDialog) {
        // Tag used to cancel the request
        //String tag_string_req = "req_login";

        pDialog.setMessage(activity.getString(R.string.login_loading_dialog_msg));
        showDialog(pDialog);

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){

                int error_code = Parser.parseErrorCode(response);

                if (error_code == 0){
                    String API_key = Parser.parseAPI_key(response);

                    session.setAPI_key(API_key);
                    session.setRole(IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK); //používame aplikáciu v prihlásenom móde

                    //inform the user
                    hideDialog(pDialog);
                    Toast.makeText(CustomApplication.getCustomAppContext(), "Prihlásenie s uloženými údajmi bolo úspešné!", Toast.LENGTH_LONG).show();

                } else {
                    hideDialog(pDialog);
                    Parser.handleLoginError(error_code);
                }

            }//end onResponse
        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(),
                        "CHYBA PRIHLASOVANIA " + error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog(pDialog);
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put(KEY_PARAMS_EMAIL, session.getEmail());
        params.put(KEY_PARAMS_PASSWORD, session.getPassword());

        //volleySingleton.createLoginRequestPOST(params, responseLis, errorLis);
    }

    private void showDialog(ProgressDialog pDialog) {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog(ProgressDialog pDialog) {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public interface LoginManagerInteractionListener{
        void onLoginSuccessful(UserInfo userInfo);

        void onLoginPartiallySuccessful(UserInfo userInfo);

        void onLoginError(int error_code); // errors according to error_codes (network ok)

        /**
         * Only network errors (e.g. no connection) - volley errors
         */
        void onLoginNetworkError();
    }
}
