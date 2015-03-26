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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import bc.cestaplus.R;
import bc.cestaplus.network.VolleySingleton;
import bc.cestaplus.network.requests.JsonArrayCustomUtf8Request;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link postTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class postTestFragment extends Fragment {

//networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment postTestFragment.
     */
    public static postTestFragment newInstance() {
        postTestFragment fragment = new postTestFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public postTestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        volleySingleton = VolleySingleton.getInstance(getActivity().getApplicationContext()); //prístup ku kontextu main aktivity
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baterka, container, false);

        //TextView textView = view.findViewById(R.layout.);
        //textView.setText(R.string.hello_blank_fragment);

        //sendStringRequest();
        sendTestJSONRequest();

        return view;
    }


























    private void sendTestJSONRequest() {
        String url = "http://vaii.fri.uniza.sk/~mahut8/bc/jsonTest.php";

        Map<String, String> params = new HashMap<String, String>();
        params.put("pocet", Integer.toString(20));
        params.put("stranka", Integer.toString(1));

        /*
        JsonRequestCustom jsonRequestCustom = new JsonRequestCustom(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String responseString = response.toString();
                        Toast.makeText(getActivity(), "RESPONSE " + responseString, Toast.LENGTH_LONG).show();
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorString = error.toString();
                        Toast.makeText(getActivity(), "ERROR " + errorString, Toast.LENGTH_LONG).show();
                    }
                }
        );
        */

        JsonArrayCustomUtf8Request request = new JsonArrayCustomUtf8Request(
                Request.Method.POST,
                url,
                params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String responseString = response.toString();
                        Toast.makeText(getActivity().getApplicationContext(), "RESPONSE " + responseString, Toast.LENGTH_LONG).show();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorString = error.toString();
                Toast.makeText(getActivity().getApplicationContext(), "ERROR " + errorString, Toast.LENGTH_LONG).show();
            }
        }
        );

        Toast.makeText(getActivity().getApplicationContext(), "Sending TEST request ...", Toast.LENGTH_SHORT).show();
        requestQueue.add(request);
    }

    private void sendStringRequest() {

        StringRequest myReq = new StringRequest(
                Request.Method.POST,
                "http://vaii.fri.uniza.sk/~mahut8/bc/volleyPostTest.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String responseString = response.toString();
                        Toast.makeText(getActivity().getApplicationContext(), "RESPONSE " + responseString, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorString = error.toString();
                        Toast.makeText(getActivity().getApplicationContext(), "ERROR " + errorString, Toast.LENGTH_LONG).show();
                    } //end of onErrorResponse
                }){
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("pocet", "20");
                        params.put("stranka", "1");
                        return params;
                    };
        };

        Toast.makeText(getActivity().getApplicationContext(), "Sending TEST request ...", Toast.LENGTH_SHORT).show();
        requestQueue.add(myReq);
    }




}
