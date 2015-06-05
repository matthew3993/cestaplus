package bc.cestaplus.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import bc.cestaplus.activities.BaterkaActivity;
import bc.cestaplus.network.Parser;
import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.activities.ArticleActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter;
import bc.cestaplus.extras.ArticlesLoadedListener;
import bc.cestaplus.listeners.RecyclerItemClickListener;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.tasks.UpdateTask;

//staticke importy


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VsetkoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VsetkoFragment
    extends Fragment
    implements ArticlesLoadedListener
    //implements RecyclerView.OnClickListener
    {

    private static final String ULOZENE_VSETKO = "ulozeny_vsetko";

//networking
    private VolleySingleton volleySingleton;

// data
    private ArrayList<ArticleObj> zoznamVsetko; // konkretne pomenovanie vo FragmenteVsetko
    private int pocSrt;                        // pocet nacitanych stranok

// recyclerView
    private RecyclerView recyclerViewVsetko; // konkretne pomenovanie vo FragmentePrehlad
    private ClanokRecyclerViewAdapter crvaVsetko;

// vypis chyb
    private TextView tvVolleyError;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VsetkoFragment.
     */

    public static VsetkoFragment newInstance() {
        VsetkoFragment fragment = new VsetkoFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public VsetkoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Always call the superclass first

        /*if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }*/

        zoznamVsetko = new ArrayList<>();
        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //prístup ku kontextu main aktivity

        Log.i("LIFECYCLE", "Vsetko.onCreate() was called");
    } //end onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("LIFECYCLE", "Vsetko.onCreateVIEW() was called");

    // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prehlad2, container, false);
        tvVolleyError = (TextView) view.findViewById(R.id.tvVolleyError);

    //inicializacia RecyclerView
        recyclerViewVsetko = (RecyclerView) view.findViewById(R.id.rvVsetko);
        recyclerViewVsetko.setLayoutManager(new LinearLayoutManager(getActivity()) );

        // ======= RecyclerView Touch Listener ====================================================================
        recyclerViewVsetko.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                if (position == zoznamVsetko.size()){ // ak bolo kliknute na button nacitaj viac

                                    pocSrt++;                                // !!! zvysenie poctu nacitanych stran !!!
                                    //nacitanie dalsej stranky
                                    Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                                            tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                                            //page-ovanie
                                            if (pocSrt == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                                                zoznamVsetko = Parser.parseJsonArrayResponse(response);
                                            } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                                                zoznamVsetko.addAll(Parser.parseJsonArrayResponse(response));
                                            }
                                            crvaVsetko.setClanky(zoznamVsetko);
                                        }

                                    };

                                    Response.ErrorListener errorLis = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                                            volleySingleton.handleVolleyError(error, tvVolleyError);
                                        } //end of onErrorResponse
                                    };

                                    volleySingleton.sendGetClankyArrayRequestGET("all", 20, pocSrt, responseLis, errorLis);
                                    Toast.makeText(getActivity().getApplicationContext(), "Load more in VSETKOFragment" + pocSrt, Toast.LENGTH_SHORT).show();

                                } else { // ak bolo kliknute na clanok
                                    if (zoznamVsetko.get(position).getSection().equalsIgnoreCase("baterka")){ //if baterka was clicked
                                        Intent intent = new Intent(getActivity().getApplicationContext(), BaterkaActivity.class);
                                        intent.putExtra("baterka", zoznamVsetko.get(position));

                                        startActivity(intent);

                                    } else { // if other sections was clicked
                                        Intent intent = new Intent(getActivity().getApplicationContext(), ArticleActivity.class);
                                        intent.putExtra("clanok", zoznamVsetko.get(position));

                                        startActivity(intent);
                                    }
                                }
                            }
                        }));

        crvaVsetko = new ClanokRecyclerViewAdapter(getActivity().getApplicationContext());

        if (savedInstanceState != null){ //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamVsetko = savedInstanceState.getParcelableArrayList(ULOZENE_VSETKO);
            pocSrt = savedInstanceState.getInt("pocSrt", 1);

            //ošetrenie prípadu, keď po rýchlom otočení po spustení ostal zoznam prázdny
            if (zoznamVsetko.isEmpty()){ //ak je zoznam clankov prazdny,
                //start the update task - will trigger onArticlesLoaded
                Toast.makeText(getActivity(), "executing update task from VSETKOfragment ORIENTATION CHANGES", Toast.LENGTH_LONG).show();
                new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time

            } else { //v pripade, ze nie je prazdny
                crvaVsetko.setClanky(zoznamVsetko);
            }

        } else { // nove spustenie
            pocSrt = 1; // set the page number

            //start the update task - will trigger onArticlesLoaded
                Toast.makeText(getActivity(), "executing update task from VSETKOfragment FIRST", Toast.LENGTH_LONG).show();
            new UpdateTask(this, false).execute(); //false = we DON'T want to issue notifications this time

        } //end else savedInstanceState

        recyclerViewVsetko.setAdapter(crvaVsetko);

        return view;
    } // end onCreateView


    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.i("LIFECYCLE", "Vsetko.onSaveInstanceState() was called");

        // Save the current state of zoznamVsetko
        outState.putParcelableArrayList(ULOZENE_VSETKO, zoznamVsetko);

        //ulozenie poctu nacitanych stran
        outState.putInt("pocSrt", pocSrt);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    private void handleVolleyError(VolleyError error){
        tvVolleyError.setVisibility(View.VISIBLE);

        if (error instanceof TimeoutError || error instanceof NoConnectionError){ // lebo tieto dve pre pouzivatela su skoro rovnake
            tvVolleyError.setText(R.string.no_connection_error);

        } else if (error instanceof AuthFailureError){
            tvVolleyError.setText(R.string.authentification_error);

        } else if (error instanceof ServerError){
            tvVolleyError.setText(R.string.server_error);

        } else if (error instanceof NetworkError){
            tvVolleyError.setText(R.string.network_error);

        } else if (error instanceof ParseError){
            tvVolleyError.setText(R.string.parse_error);
        }
    }//end handleVolleyError

     @Override
     public void onArticlesLoaded(ArrayList<ArticleObj> listArticles) {
         // !!!!!!!!! TODO: solve problem with paging !!!
         zoznamVsetko = listArticles;
         crvaVsetko.setClanky(listArticles);
     }

} // end class FragmentVsetko
