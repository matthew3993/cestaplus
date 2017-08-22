package sk.cestaplus.cestaplusapp.utilities.navDrawer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.fragments.AllFragment;
import sk.cestaplus.cestaplusapp.fragments.SectionFragment;

import static sk.cestaplus.cestaplusapp.extras.IKeys.TAG_ALL_FRAGMENT;

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

        Fragment newFragment = AllFragment.newInstance(false); //false - do not wait for order to load articles

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.mainActivityMainFragmentContainer, newFragment, TAG_ALL_FRAGMENT)
                .commit();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            // clear the back stack
            // SOURCE: http://stackoverflow.com/a/20591748
            FragmentManager manager = activity.getSupportFragmentManager();
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        // set title
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.mainActivityToolbar);
        toolbar.setTitle(R.string.app_name);
    }
}
