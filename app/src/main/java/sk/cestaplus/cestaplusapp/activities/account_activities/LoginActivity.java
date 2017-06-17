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
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import sk.cestaplus.cestaplusapp.objects.UserInfo;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.LoginManager;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Util;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_NOT_LOGGED;

public class LoginActivity
    extends Activity
    implements LoginManager.LoginManagerInteractionListener {

    //data
    private LoginManager loginManager;
    private SessionManager session;
    private boolean isShowedKeyboard; //helper flag meaning if soft keyboard is showed or not

    // UI components
    //body
    private ImageView ivMainLogo;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnUseAsNotLoggedIn;

    // loading & error views
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        initActivityDefaultFont(); // set up default font of activity

        // Check if user is already logged in or not
        if (Util.isLoggedIn()) {
            launchMainActivity(); // User is already logged in. Take him to main activity
        }

        // initialisations
        loginManager = LoginManager.getInstance(getApplicationContext()); //login manager initialisation !!
        session = new SessionManager(getApplicationContext());

        // keyboard is hidden at the start of activity
        // (using android:focusableInTouchMode="true" on root layout to prevent EditText from receiving focus)
        isShowedKeyboard = false;

        // init OnGlobalLayoutListener
        final View activityRootView = findViewById(R.id.login_RootLinearLayout);
        final Context context = this;

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            /**
             * This method is called EVERY time when something changes on the screen - mainly when entire soft keyboard is shown/hidden,
             * but also when only part of soft keyboard is shown/hidden (for example top row with numbers),
             * AND it's called 2 times when something changes on the screen (for some reason, found out by debugging) => therefore
             * helper flag "isShowedKeyboard" and it check is needed to prevent initialisation of same views multiple times
             * when it's not needed and requesting focus multiple times even we don't want it.
             */
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                if (heightDiff > Util.pxFromDp(context, 200)) { // if more than 200 dp, it's probably a keyboard...
                    // soft keyboard is SHOWED
                    //Toast.makeText(context, "Keyboard SHOWED", Toast.LENGTH_SHORT).show();

                    if (!isShowedKeyboard) { //check comment of method
                        // keyboard WAS hidden and NOW it is SHOWED
                        isShowedKeyboard = true;

                        setGoneChangableViews();
                        initViewsWithKeyboard(); // re-init views
                        setVisibleChangableViews();

                        etEmail.requestFocus(); //!! - SOURCE: https://stackoverflow.com/a/8080621 - check the comments of this answer
                    }
                } else {
                    // soft keyboard is HIDDEN
                    //Toast.makeText(context, "Keyboard HIDDEN", Toast.LENGTH_SHORT).show();

                    if (isShowedKeyboard) { //check comment of method
                        // keyboard WAS showed and NOW it is HIDDEN
                        isShowedKeyboard = false;

                        setGoneChangableViews();
                        initViewsWithOutKeyboard(); // re-init views
                        setVisibleChangableViews();
                    }
                }
            }
        });

        initLoginControls();
        initViewsWithOutKeyboard();

        // Progress dialog init
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

    }// end onCreate

    private void setGoneChangableViews() {
        ivMainLogo.setVisibility(View.GONE);
        btnLogin.setVisibility(View.GONE);
        btnUseAsNotLoggedIn.setVisibility(View.GONE);
    }

    private void setVisibleChangableViews() {
        ivMainLogo.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        btnUseAsNotLoggedIn.setVisibility(View.VISIBLE);
    }

    private void initViewsWithOutKeyboard() {
        initViews(R.id.ivMainLogo, R.id.btnLogin, R.id.btnUseAsNotLoggedIn);
    }

    private void initViewsWithKeyboard() {
        initViews(R.id.ivMainLogoWithKeyboard, R.id.btnLoginWithKeyboard, R.id.btnUseAsNotLoggedIn_WithKeyboard);
    }

    private void initViews(int mainLogoId, int btnLoginId, int btnUseAsNotLoggedInId) {
        ivMainLogo = (ImageView) findViewById(mainLogoId);
        btnLogin = (Button) findViewById(btnLoginId);
        btnUseAsNotLoggedIn = (Button) findViewById(btnUseAsNotLoggedInId);

        setButtonsListeners();
    }

    private void initLoginControls() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    private void setButtonsListeners() {
        // Login button OnClickListener
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                checkForm();
            }//end onClick

        });

        // Button 'use as not logged in' OnClickListener
        if (btnUseAsNotLoggedIn != null) {
            btnUseAsNotLoggedIn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    setUseAsNotLoggedIn();
                }
            });

            // underline text
            // SOURCES:
            //  https://stackoverflow.com/a/31718887
            //  https://stackoverflow.com/a/31719008
            btnUseAsNotLoggedIn.setPaintFlags(btnUseAsNotLoggedIn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            // underlining this way for some reason makes text look like without antialiasing :(
            // btnUseAsNotLoggedIn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    //region FONTS INIT - CALLIGRAPHY

    /**
     * Set up default font of activity Activity (not entire Application!) using Calligraphy lib
     * SOURCE: https://github.com/chrisjenx/Calligraphy
     */
    private void initActivityDefaultFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LatoLatin-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    /**
     * We NEED to override this method, because we MUST wrap the Activity! (not Application!) context
     * for Calligraphy to get working
     * SOURCE: https://github.com/chrisjenx/Calligraphy
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase)); // IMPORTANT for Calligraphy to get working
    }

    //endregion

    /**
     * This method is called after login button click.
     */
    private void checkForm() {
        String email = etEmail.getText().toString();
        String password = computeHash( etPassword.getText().toString() );

        // Check for empty data in the form
        if (email.trim().length() > 0 && password.trim().length() > 0) {
            showLoggingDialog();

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

    private void showLoggingDialog() {
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
