package bc.cestaplus.fragments;

/**
 * Created by Matej on 14. 4. 2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import bc.cestaplus.R;
import bc.cestaplus.activities.RubrikaAktivity;
import bc.cestaplus.adapters.RubrikyAdapter;

/**
 * fragment Rubriky
 */
public class RubrikyFragment
        extends Fragment
        implements AdapterView.OnItemClickListener{

    public static final String EXTRA_NAZOV_RUBRIKY = "nazov_rubriky";
    public static final String EXTRA_ID_RUBRIKY = "id_rubriky";

    String [] rubriky = {"Téma mesiaca",    //id = 0
                         "Tabule",          //id = 1
                         "Baterka",         //id = 2
                         "Za hranicami",    //id = 3
                         "Kazatenica život",//id = 4
                         "Anima Mea"};      //id = 5

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //TextView txtV = (TextView) view;
        //Toast.makeText(MainActivity.context/*MainActivity.getContext()*/, "Klikli ste na rubriku " + txtV.getText(), Toast.LENGTH_LONG).show();

        // potrebne nastavenia na spustenie novej aktivity Rubrika + preposlanie informacie ktora rubrika
        Intent intent = new Intent(getActivity().getApplicationContext(), RubrikaAktivity.class);
        intent.putExtra(EXTRA_NAZOV_RUBRIKY, rubriky[position]);
        intent.putExtra(EXTRA_ID_RUBRIKY, position);

        startActivity(intent);
    }

}// end class RubrikyFragment