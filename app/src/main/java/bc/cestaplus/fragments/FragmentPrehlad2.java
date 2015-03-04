package bc.cestaplus.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment; // musi byt .v4.app.Fragment a nie len .Fragment
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import bc.cestaplus.ClanokObj;
import bc.cestaplus.R;
import bc.cestaplus.activities.MainActivity;
import bc.cestaplus.extras.IKeys;
import bc.cestaplus.network.VolleySingleton;

//staticke importy
import static bc.cestaplus.extras.IKeys.IPrehlad.*;



public class FragmentPrehlad2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String URL_CESTA_PLUS_VSETKO = "";
    //public static final String URL_CESTA_PLUS_VSETKO = ""; //doplnit

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//networking
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

// data
    private ArrayList<ClanokObj> zoznamVsetko= new ArrayList<>();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPrehlad2.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPrehlad2 newInstance(String param1, String param2) {
        FragmentPrehlad2 fragment = new FragmentPrehlad2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentPrehlad2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        sendJsonRequest();

    } // end onCreate

    private void sendJsonRequest(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, /*getRequestUrl(10)*/ "http://vaii.fri.uniza.sk/~mahut8/vsetkoTest.json" ,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_LONG).show();
                parseJsonResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }

    private void parseJsonResponse(JSONObject response) {
        if (response == null || response.length() == 0){
            return;
        }

        try {
            //StringBuilder data = new StringBuilder();

            JSONArray jsonArrayVsetko = response.getJSONArray(KEY_CLANKY);

            for(int i = 0; i < jsonArrayVsetko.length(); i++){
                JSONObject aktualnyClanok = jsonArrayVsetko.getJSONObject(i); //vrati clanok na aktualnej pozicii

                String title = aktualnyClanok.getString(KEY_TITLE);
                String description = aktualnyClanok.getString(KEY_DESCRIPTION);

            //spracovanie obrazka - ostrenie v pripade, ze orazok nie je dostupny
                String imageUrl = null;
                if(aktualnyClanok.has(KEY_IMAGE_URL)){
                    imageUrl = aktualnyClanok.getString(KEY_IMAGE_URL);
                }

            /*
            //spracovanie datumu
                String pubDate = null;
                if(aktualnyClanok.has(KEY_PUB_DATE)){
                    pubDate = aktualnyClanok.getString(KEY_PUB_DATE);
                }
            */

                String rubrika = aktualnyClanok.getString(KEY_RUBRIKA);
                boolean locked = aktualnyClanok.getBoolean(KEY_LOCKED);

                //long id = aktualnyClanok.getLong(KEY_ID);

                //data.append(title + "\n"); // \n = novy riadok

                //zoznamVsetko.add(new ClanokObj(title, description, imageUrl, dateFormat.parse(pubDate), rubrika));
                zoznamVsetko.add(new ClanokObj(title, description, imageUrl, rubrika));
            }
            //Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_LONG).show();



        } catch (JSONException jsonEx){

        //} catch (ParseException pEx){

        }

        Toast.makeText(getActivity(), zoznamVsetko.toString(), Toast.LENGTH_LONG).show();
    }//end parseJsonResponse


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //networking test
        /*RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

        StringRequest request = new StringRequest(Request.Method.GET, "http://php.net/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), "RESPONSE " + response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "ERROR " + error, Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
        */

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prehlad2, container, false);
    }

    public static String getRequestUrl(int limit){
        return URL_CESTA_PLUS_VSETKO+"?apikey="+ MainActivity.API_KEY+"&limit="+limit;
    }

} //end of FragmentPrehlad2
