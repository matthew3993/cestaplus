package sk.cestaplus.cestaplusapp.activities.account_activities;

/**
 * Created by Matej on 22. 4. 2015.
 */
import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.network.Parser;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sk.cestaplus.cestaplusapp.objects.UserInfo;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.LoginManager;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Util;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_NOT_LOGGED;


public class LoginActivity
    extends Activity
    implements LoginManager.LoginManagerInteractionListener {

    private Button btnLogin;
    private Button btnUseAsNotLoggedIn;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    private LoginManager loginManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginManager = LoginManager.getInstance(getApplicationContext()); //login manager initialisation !!

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
        if (Util.isLoggedIn()) {
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

    /**
     * This method is called after login button click.
     */
    private void checkForm() {
        String email = inputEmail.getText().toString();
        String password = computeHash( inputPassword.getText().toString() );

        // Check for empty data in the form
        if (email.trim().length() > 0 && password.trim().length() > 0) {
            showDialog();

            // try to login - will trigger one of LoginManagerInteractionListener methods
            loginManager.tryLogin(email, password, true, this); // true = remember password

        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    R.string.enter_credentials_msg, Toast.LENGTH_LONG)
                    .show();
        }
    }//end checkForm

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

    public String computeHash(String input){
        StringBuffer sb = new StringBuffer();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();

            byte[] byteData = digest.digest(input.getBytes("UTF-8"));

            for (int i = 0; i < byteData.length; i++){
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e1) {
            Log.e("hash", "HASH_ERROR - NoSuchAlgorithmException: " + e1);
        } catch (UnsupportedEncodingException e) {
            Log.e("hash", "HASH_ERROR - UnsupportedEncodingException: " + e);
        }

        return sb.toString();
    }

    //region LoginManagerInteractionListener methods

    @Override
    public void onLoginSuccessful(UserInfo userInfo) {
        //inform the user
        Toast.makeText(CustomApplication.getCustomAppContext(), R.string.login_successful_msg, Toast.LENGTH_LONG).show();

        hideDialog();
        launchMainActivity();
    }

    @Override
    public void onLoginPartiallySuccessful(UserInfo userInfo) {
        //inform the user
        Toast.makeText(CustomApplication.getCustomAppContext(), R.string.login_partially_successful_msg, Toast.LENGTH_LONG).show();

        hideDialog();
        launchMainActivity();
    }

    @Override
    public void onLoginError(int error_code) {
        hideDialog();
        Parser.handleLoginError(error_code);
    }

    @Override
    public void onLoginNetworkError() {
        hideDialog();
        // volley error means problem with internet connection
        Toast.makeText(getApplicationContext(),
                R.string.login_network_error, Toast.LENGTH_LONG).show();
    }

    // endregion

}// end Login Activity
