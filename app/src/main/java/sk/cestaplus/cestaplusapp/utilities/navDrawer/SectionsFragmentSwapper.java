package sk.cestaplus.cestaplusapp.utilities.navDrawer;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.fragments.AllFragment;
import sk.cestaplus.cestaplusapp.fragments.SectionFragment;

/**
 * Created by matth on 24.02.2017.
 */

public class SectionsFragmentSwapper
    implements IAction{

    private AppCompatActivity activity;
    private Class classToStart;
    private String sectionName;
    private String sectionId;
    //private Fragment newFragment;

    public SectionsFragmentSwapper(AppCompatActivity activity, String sectionName, String sectionId) {
        this.activity = activity;
        //this.newFragment = newFragment;
        this.sectionName = sectionName;
        this.sectionId = sectionId;
    }

    public void execute(){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment actualFragment = fragmentManager.findFragmentById(R.id.mainActivityMainFragmentContainer);

        /*
        Class actualFragmentClass = actualFragment.getClass();
        if (actualFragmentClass == AllFragment.class && newFragment.getClass() == AllFragment.class){
            return; // do not swap
        }*/
        if ((actualFragment instanceof SectionFragment) && (((SectionFragment)actualFragment).getSectionID().equalsIgnoreCase(sectionId))){
            return;// do not swap
        }

        Fragment newFragment = SectionFragment.newInstance(sectionName, sectionId);

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction()
                .replace(R.id.mainActivityMainFragmentContainer, newFragment)
                .commit();

        // hide app bar
        AppBarLayout appBarLayout = (AppBarLayout) activity.findViewById(R.id.mainActivityAppBarLayout);
        appBarLayout.setVisibility(View.GONE);
        //appBarLayout.setExpanded(false, false);

        //CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.mainActivityCollapsingToolbarLayout);
        //collapsingToolbarLayout.setVisibility(View.GONE);
        //collapsingToolbarLayout.setEnabled(false);

        RelativeLayout relativeLayout = (RelativeLayout) activity.findViewById(R.id.collapsingToolbarRelativeLayoutMainActivity);
        relativeLayout.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.mainActivityToolbar);
        toolbar.setTitle(sectionName);

        appBarLayout.setExpanded(true, false);
        appBarLayout.setVisibility(View.VISIBLE);



        /*
        if (fragment.getClass() == AllFragment.class){
            return;
        }

        if (classToStart == AllFragment.class){

        }*/
    }
}
