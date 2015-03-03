package bc.cestaplus.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import bc.cestaplus.R;
import bc.cestaplus.network.VolleySingleton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPrehlad2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPrehlad2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private VolleySingleton volleySingleton;

    private ImageLoader imageLoader;
    private RequestQueue requestQueue;


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

        //volleySingleton = VolleySingleton.getInstance();
        //requestQueue = volleySingleton.getRequestQueue();

        //JsonObjectRequest request = JsonObjectRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_prehlad2, container, false);
    }


}
