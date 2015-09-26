package bc.cestaplus.fragments;

/**
 * Created by Matej on 14. 4. 2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import bc.cestaplus.R;
import bc.cestaplus.activities.OPortaliActivity;
import bc.cestaplus.activities.RubrikaActivity;
import bc.cestaplus.activities.SettingsActivity;
import bc.cestaplus.activities.konto_activities.LoggedActivity;
import bc.cestaplus.activities.konto_activities.NotLoggedActivity;
import bc.cestaplus.adapters.RubrikyAdapter;
import bc.cestaplus.utilities.SessionManager;
import bc.cestaplus.utilities.Util;

import static bc.cestaplus.extras.IKeys.KEY_MAIN_ACTIVITY;
import static bc.cestaplus.extras.IKeys.KEY_PARENT_ACTIVITY;

/**
 * fragment Rubriky
 */
public class RubrikyFragment
        extends Fragment
        implements AdapterView.OnItemClickListener{

    // session
    private SessionManager session;

    // data
    public static final String EXTRA_NAZOV_RUBRIKY = "nazov_rubriky";
    public static final String EXTRA_ID_RUBRIKY = "id_rubriky";

    String [] rubriky = {"Téma mesiaca",        //id =  0
                         "180 stupňov",         //id =  1
                         "Na ceste",            //id =  2
                         "Rodičovské skratky",  //id =  3
                         "Na pulze",            //id =  4
                         "U Matúša",            //id =  5
                         "Normálna rodinka",    //id =  6
                         "Tabule",              //id =  7
                         "Anima Mea",           //id =  8
                         "Kuchynská teológia",  //id =  9
                         "Kazateľnica život",   //id =  10
                         "Za hranicami",        //id =  11
                         "Fejtón",              //id =  12
                         "P.O.BOX Nebo",        //id =  13
                         "Z parlamentu",        //id =  14
                         "Baterka"};            //id =  15

    //UI
    private static ArrayAdapter<String> adapterRubriky;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static RubrikyFragment newInstance() {
        return new RubrikyFragment();
    }

    /**
     * default empty constructor
     */
    public RubrikyFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //this fragment has it's own menu different from menu of activity

        session = new SessionManager(getActivity());
    }//end onCreate()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rubriky, container, false);

        //adapterRubriky = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, rubriky);
        adapterRubriky = new RubrikyAdapter(getActivity().getApplicationContext(), R.layout.rubrika_list_item, rubriky);

        ListView listViewRubriky = (ListView) rootView.findViewById(R.id.listViewRubriky);
        listViewRubriky.setAdapter(adapterRubriky);

        listViewRubriky.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sections_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //handler na kliknutie na itemy v action bar-e
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.account: {
                // Session manager
                //final SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());

                if (session.getRola() > 0) {
                    // Launching the LOGGED activity
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                } else {
                    // Launching the NOT Logged activity
                    Intent intent = new Intent(getActivity().getApplicationContext(), NotLoggedActivity.class);
                    intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                    startActivity(intent);
                    //getActivity().finish();

                }
                return true;
            }

            case R.id.action_settings: {
                // Launching the Settings activity
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                startActivity(intent);
                return true;
            }

            case R.id.action_o_portali: {
                // Launching the O portáli activity
                Intent intent = new Intent(getActivity().getApplicationContext(), OPortaliActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                startActivity(intent);
                //getActivity().finish();
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }// onOptionsItemSelected()

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TextView txtV = (TextView) view;
        //Toast.makeText(MainActivity.context/*MainActivity.getContext()*/, "Klikli ste na rubriku " + txtV.getText(), Toast.LENGTH_LONG).show();

        // potrebne nastavenia na spustenie novej aktivity Rubrika + preposlanie informacie ktora rubrika
        Intent intent = new Intent(getActivity().getApplicationContext(), RubrikaActivity.class);
        intent.putExtra(EXTRA_NAZOV_RUBRIKY, rubriky[position]);
        intent.putExtra(EXTRA_ID_RUBRIKY, position);

        startActivity(intent);
    }//end onItemClick()

}// end class RubrikyFragment