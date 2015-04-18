package bc.cestaplus.fragments;


import android.content.Intent;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.R;
import bc.cestaplus.activities.ArticleActivity;
import bc.cestaplus.adapters.ClanokRecyclerViewAdapter;
import bc.cestaplus.listeners.RecyclerItemClickListener;
import bc.cestaplus.network.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TemaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TemaFragment extends Fragment {

    private static final String ULOZENE_TEMA = "ulozene_tema";

//networking
    private VolleySingleton volleySingleton;

// data
    private ArrayList<ArticleObj> zoznamTema; // konkretne pomenovanie vo FragmenteVsetko
    private int pocSrt;                        // pocet nacitanych stranok

// recyclerView
    private RecyclerView recyclerViewTema; // konkretne pomenovanie vo FragmentePrehlad
    private ClanokRecyclerViewAdapter crvaTema;

 // vypis chyb
    private TextView tvVolleyError;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TemaFragment.
     */
    public static TemaFragment newInstance() {
        TemaFragment fragment = new TemaFragment();
        return fragment;
    }

    public TemaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //inicializácia atribútov
        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //prístup ku kontextu main aktivity
        zoznamTema = new ArrayList<>();
        pocSrt = 1;

    } //end onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prehlad2, container, false);   //!!!!
        tvVolleyError = (TextView) view.findViewById(R.id.tvVolleyError);

        //inicializacia RecyclerView
        recyclerViewTema = (RecyclerView) view.findViewById(R.id.rvVsetko);
        recyclerViewTema.setLayoutManager(new LinearLayoutManager(getActivity()));

  // ======= RecyclerView Touch Listener ====================================================================
        recyclerViewTema.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(),
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                if (position == zoznamTema.size()){ // ak bolo kliknute na button nacitaj viac

                                    pocSrt++;                                           // !!! zvysenie poctu nacitanych stran !!!
                                    //nacitanie dalsej stranky
                                    Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                                            tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                                        //page-ovanie
                                            if (pocSrt == 1) {  // ak ide o prvu stranku, zoznam je prepisany
                                                zoznamTema = volleySingleton.parseJsonArrayResponse(response);
                                            } else {            // ak ide o stranky nasledujuce, nove rubriky su pridane k existujucemu zoznamu
                                                zoznamTema.addAll(volleySingleton.parseJsonArrayResponse(response));
                                            }
                                            crvaTema.setClanky(zoznamTema);
                                        }

                                    };

                                    Response.ErrorListener errorLis = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                                            volleySingleton.handleVolleyError(error, tvVolleyError);
                                    } //end of onErrorResponse
                                    };

                                    volleySingleton.sendGetClankyArrayRequestGET("tema", 20, pocSrt, responseLis, errorLis);
                                    Toast.makeText(getActivity().getApplicationContext(), "Load more in TEMAFragment" + pocSrt, Toast.LENGTH_SHORT).show();

                                // ak bolo kliknute na clanok
                                } else {
                                    //Intent intent = new Intent(getActivity().getApplicationContext(), ArticleActivity_OtherWay.class);
                                    Intent intent = new Intent(getActivity().getApplicationContext(), ArticleActivity.class);
                                    intent.putExtra("clanok", zoznamTema.get(position));

                                    //ActivityCompat.startActivity(ArticleActivity_OtherWay, intent, null);
                                    //view.getContext().startActivity(intent);
                                    startActivity(intent);
                                }
                            }
                        }));

        crvaTema = new ClanokRecyclerViewAdapter(getActivity());

        // tu to bolo
        if (savedInstanceState != null){ //ak nie je null = nastala zmena stavu, napr. rotacia obrazovky
            //obnovenie ulozeneho stavu
            zoznamTema = savedInstanceState.getParcelableArrayList(ULOZENE_TEMA);
            crvaTema.setClanky(zoznamTema);

        } else {
            //nove nacitanie
            Response.Listener<JSONArray> responseLis = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                    tvVolleyError.setVisibility(View.GONE); //ak sa vyskytne chyba tak sa toto TextView zobrazi, teraz ho teda treba schovat
                    zoznamTema = volleySingleton.parseJsonArrayResponse(response);
                    crvaTema.setClanky(zoznamTema);
                }

            };

            Response.ErrorListener errorLis = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getActivity(), "ERROR " + error.toString(), Toast.LENGTH_LONG).show();
                    volleySingleton.handleVolleyError(error, tvVolleyError);
            } //end of onErrorResponse
            };

            volleySingleton.sendGetClankyArrayRequestGET("tema", 20, 1, responseLis, errorLis);
        }

        recyclerViewTema.setAdapter(crvaTema);

        return view;
    } //end onCreateView

    @Override
    public void onSaveInstanceState(Bundle outState){
        Log.i("LIFECYCLE", "Vsetko.onSaveInstanceState() was called");

        // Save the current state of zoznamVsetko
        outState.putParcelableArrayList(ULOZENE_TEMA, zoznamTema);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

} //end of class TemaFragment
