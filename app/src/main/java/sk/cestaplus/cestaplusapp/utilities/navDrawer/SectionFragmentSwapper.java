package sk.cestaplus.cestaplusapp.utilities.navDrawer;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.fragments.AllFragment;
import sk.cestaplus.cestaplusapp.fragments.SectionFragment;

import static sk.cestaplus.cestaplusapp.extras.IKeys.TAG_SECTION_FRAGMENT;

/**
 * Created by matth on 24.02.2017.
 */

public class SectionFragmentSwapper
    implements IAction{

    private AppCompatActivity activity;
    private String sectionName;
    private String sectionId;
    //private Fragment newFragment;

    public SectionFragmentSwapper(AppCompatActivity activity, String sectionName, String sectionId) {
        this.activity = activity;
        this.sectionName = sectionName;
        this.sectionId = sectionId;
    }

    public void execute(){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.mainActivityMainFragmentContainer);

        if ((actualFragment instanceof SectionFragment) && (((SectionFragment)actualFragment).getSectionID().equalsIgnoreCase(sectionId))){
            return;// do not swap
        }

        Fragment newFragment = SectionFragment.newInstance(sectionName, sectionId);

        // Insert the fragment by replacing any existing fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.mainActivityMainFragmentContainer, newFragment, TAG_SECTION_FRAGMENT);

        transaction.addToBackStack(null); // we add fragment every time

        /*if (!(actualFragment instanceof SectionFragment)){
            //transaction.addToBackStack(null);
        } else {
            //fragmentManager.popBackStack();
        }*/

        transaction.commit();

        // hide app bar
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.mainActivityAppBarLayout);
        appBarLayout.setVisibility(View.GONE);
        //appBarLayout.setExpanded(false, false);

        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.mainActivityCollapsingToolbarLayout);
        //collapsingToolbarLayout.setVisibility(View.GONE);
        //collapsingToolbarLayout.setEnabled(false);

        RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.collapsingToolbarRelativeLayoutMainActivity);
        relativeLayout.setVisibility(View.GONE);

        // set toolbar title
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.mainActivityToolbar);
        toolbar.setTitle(sectionName);

        appBarLayout.setExpanded(true, false);
        appBarLayout.setVisibility(View.VISIBLE);
    }
}
