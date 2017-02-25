package sk.cestaplus.cestaplusapp.utilities.navDrawer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.fragments.AllFragment;
import sk.cestaplus.cestaplusapp.fragments.SectionFragment;

/**
 * Created by matth on 24.02.2017.
 */

public class AllFragmentSwapper
    implements IAction{

    private AppCompatActivity activity;

    public AllFragmentSwapper(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void execute(){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.mainActivityMainFragmentContainer);

        if (actualFragment instanceof AllFragment){
            return;// do not swap
        }

        Fragment newFragment = AllFragment.newInstance();

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.mainActivityMainFragmentContainer, newFragment)
                .commit();

        // set title
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.mainActivityToolbar);
        toolbar.setTitle(R.string.app_name);
    }
}
