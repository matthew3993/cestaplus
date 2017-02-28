package sk.cestaplus.cestaplusapp.activities.account_activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.ArticleActivity;
import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.SettingsActivity;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ARTICLE_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_BATERKA_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_NOT_LOGGED_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_O_PORTALI_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;

public class NotLoggedActivity
    extends ActionBarActivity {

    //data
    private String parentActivity;

    //UI
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_logged);

        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);

        setTitle("Konto");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //ak by nesla navigacia UP, resp. sa nezobrazila šípka

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
    }//end onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_not_logged, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Launching the Settings activity
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra(KEY_PARENT_ACTIVITY, KEY_NOT_LOGGED_ACTIVITY);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method implements what should happen when Up button is pressed
     * @return
     */
    @Override
    public Intent getSupportParentActivityIntent() {
        return getCustomParentActivityIntent();
    }

    private Intent getCustomParentActivityIntent() {
        Intent i = null;

        switch (parentActivity){
            case KEY_ARTICLE_ACTIVITY:{
                i = new Intent(this, ArticleActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }

            case KEY_BATERKA_ACTIVITY:{
                i = new Intent(this, BaterkaActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }

            case KEY_MAIN_ACTIVITY:{
                i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }

            case KEY_O_PORTALI_ACTIVITY:{
                i = new Intent(this, OPortaliActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            }
        }

        return i;
    }

    private void doLogin(){
        // Launching the login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}//end NotLoggedActivity
