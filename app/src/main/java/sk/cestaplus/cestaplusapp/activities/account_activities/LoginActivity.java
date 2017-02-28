package sk.cestaplus.cestaplusapp.activities.account_activities;

/**
 * Created by Matej on 22. 4. 2015.
 */
import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.network.Parser;
import sk.cestaplus.cestaplusapp.network.VolleySingleton;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import sk.cestaplus.cestaplusapp.utilities.SessionManager;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.LOGIN_SUCCESSFUL;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_NOT_LOGGED;


public class LoginActivity extends Activity {

    private Button btnLogin;
    private Button btnUseAsNotLoggedIn;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    private VolleySingleton volleySingleton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        volleySingleton = VolleySingleton.getInstance(getApplicationContext()); //volleySingleton initialisation !!!

        // init views
        inputEmail = (EditText) findViewById(R.id.txtvNotLoggedIn);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnUseAsNotLoggedIn = (Button) findViewById(R.id.btnUseAsNotLoggedIn);

        // Progress dialog init
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            launchMainActivity();            // User is already logged in. Take him to main activity
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                checkForm();
            }//end onClick

        });
        
        // Button use as not logged in Click Event
        btnUseAsNotLoggedIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setUseAsNotLoggedIn();
            }
        });

    }// end onCreate

    private void checkForm() {
        String email = inputEmail.getText().toString();
        String password = "";

        try {
            password = computeHash( inputPassword.getText().toString() );

        } catch (NoSuchAlgorithmException e1) {
            Log.e("hash", "HASH_ERROR - NoSuchAlgorithmException: " + e1);
        } catch (UnsupportedEncodingException e) {
            Log.e("hash", "HASH_ERROR - UnsupportedEncodingException: " + e);
        }

        // Check for empty data in the form
        if (email.trim().length() > 0 && password.trim().length() > 0) {
            // try to login
            tryLogin(email, password, true); // true = remember password

        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    R.string.enter_credentials_msg, Toast.LENGTH_LONG)
                    .show();
        }
    }//end checkForm

    private void tryLogin(final String email, final String password, final boolean remember) {
        pDialog.setMessage(getString(R.string.login_loading_dialog_msg));
        showDialog();

        Response.Listener<JSONObject> responseLis = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){

                int error_code = Parser.parseErrorCode(response);
                hideDialog();

                if (error_code == LOGIN_SUCCESSFUL){
                    String API_key = Parser.parseAPI_key(response);

                    //if (remember) {
                        session.loginAndRememberPassword(API_key, email, password);
                        session.setRole(ROLE_LOGGED); //app is used in LOGGED mode
                    //} else {
                        //session.login(API_key);
                    //}

                    //inform the user
                    Toast.makeText(CustomApplication.getCustomAppContext(), R.string.login_successful_msg, Toast.LENGTH_LONG).show();

                    launchMainActivity();

                } else { //error_code != 0
                    Parser.handleLoginError(error_code);
                }

            }//end onResponse
        };

        Response.ErrorListener errorLis = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // volley error means problem with internet connection
                Toast.makeText(getApplicationContext(),
                        R.string.login_connection_error, Toast.LENGTH_LONG).show();
                hideDialog();
            }
        };

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        volleySingleton.createLoginRequestPOST(params, responseLis, errorLis);
    }

    private void setUseAsNotLoggedIn() {
        session.setRole(ROLE_NOT_LOGGED); //we use app in NOT logged mode
        launchMainActivity(); // Launch main activity
    }

    private void launchMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public String computeHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        byte[] byteData = digest.digest(input.getBytes("UTF-8"));
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < byteData.length; i++){
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}// end Login Activity
