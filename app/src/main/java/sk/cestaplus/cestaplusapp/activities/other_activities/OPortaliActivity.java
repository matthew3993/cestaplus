package sk.cestaplus.cestaplusapp.activities.other_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.ArticleActivity;
import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.activities.account_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.account_activities.NotLoggedActivity;
import sk.cestaplus.cestaplusapp.fragments.other_fragments.OPortaliFragment;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ARTICLE_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_BATERKA_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_O_PORTALI_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.TAG_O_PORTALI_FRAGMENT;

/**
 * Created by Matej on 19. 3. 2015.
 */
public class OPortaliActivity
        extends AppCompatActivity {

    //data
    private String parentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_portali);

        parentActivity = getIntent().getExtras().getString(KEY_PARENT_ACTIVITY);

        setTitle(R.string.title_activity_oportali);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //for "UP" navigation to work, in case if back arrow wasn't displayed

        initFragment(savedInstanceState);
    } //end onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_oportali, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this); //standart way

                }
                return true;*/

            case R.id.account: {
                // Session manager
                final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRole() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_O_PORTALI_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_O_PORTALI_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_settings:
                // Launching the Settings activity
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_O_PORTALI_ACTIVITY);
                startActivity(intent);
                return true;

        } // end switch

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
        }

        return i;
    }

    private void initFragment(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager(); //!! not only getFragmentManager()!!

        if (savedInstanceState != null) {//if is not null = change of state - for example rotation of device
            // find previously added fragment by TAG
            // SOURCE: http://stackoverflow.com/questions/31743695/how-can-i-get-fragment-from-view
            OPortaliFragment fragment = (OPortaliFragment) fragmentManager.findFragmentByTag(TAG_O_PORTALI_FRAGMENT);



        } else {// new start of application
            // create and add AllFragment - don't forget TAG
            // SOURCE: https://developer.android.com/guide/components/fragments.html#Adding
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            OPortaliFragment fragment = OPortaliFragment.newInstance();
            fragmentTransaction.add(R.id.oportaliActivityMainFragmentContainer, fragment, TAG_O_PORTALI_FRAGMENT);
            fragmentTransaction.commit();
        }
    }

}//end ArticleActivity
