package sk.cestaplus.cestaplusapp.fragments.other_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.utilities.Templator;


public class OPortaliFragment extends Fragment {

    // UI components
    private WebView webView;

    public OPortaliFragment() {
        // Required empty public constructor
    }

    public static OPortaliFragment newInstance() {
        OPortaliFragment fragment = new OPortaliFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_oportali, container, false);

        webView = (WebView) view.findViewById(R.id.webViewOPortali);

        //load page to webview
        webView.loadDataWithBaseURL(null, createOPortaliHtmlString(),
                "text/html", "utf-8", null);

        return view;
    }

    private String createOPortaliHtmlString() {
        return "<html>\n" +
                "    <head>\n" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/articleStyle.css\"/>\n" +
                "        <style>" +
                Templator.getInternalArticleStyle(getActivity()) +
                "        </style>" +
                "        <style>" +
                "           h1 {color: black;" +
                "               font-weight: bold;" +
                "               margin-bottom: 10px;" +
                "           }" +
                "        </style>" +
                "    </head>\n" +
                "    <body>\n" +
                "        <div class=\"articleText\">\n" +
                "            <h1>O nás</h1>\n" +
                "            <font style=\"font-weight: bold; color: #005494;\">Cesta+</font> vznikla veľmi jednoducho." +
                "            Chceli sme mať kresťanské médium, ktoré bude hovoriť <b>zrozumiteľne a jednoducho Evanjelium</b>" +
                "            a prinášať <b>svedectvá a životné osudy ľudí</b>, ktorí toto Evanjelium žijú a <b>bojujú za dobro</b>." +
                "            <br />" +
                "            <br />" +
                "            <h1>O čo nám ide?</h1>" +
                "            <ul style=\"list-style-type:square\">" +
                "                <li>vyhľadávať a prinášať príbehy ľudí, ktorí nemusia byť známi, no žijú svoju vieru opravdivo a poctivo" +
                "                </li>" +
                "                <li>" +
                "                    prinášať Evanjelium zrozumiteľne, prakticky a moderným jazykom" +
                "                </li>" +
                "                <li>" +
                "                    prinášať témy zo života človeka (spoločenského, kultúrneho i politického) v optike Evanjelia" +
                "                </li>" +
                "            </ul>" +
                "            <br />" +
                "            <center>" +
                "               <img src=\"http://www.cestaplus.sk/images/skica_logo.jpg\">" +
                "            </center>" +
                "            <br />" +
                "            Názov <font style=\"font-weight: bold; color: #005494;\">cesta+ (cestaplus)</font>" +
                "            vznikol spomedzi mnohých návrhov, v ktorých nám išlo o rovnaké myšlienky: " +
                "            <b>byť sprievodcom človeka</b>, ktorý verí a zároveň žije v súčasnom svete a " +
                "            <b>ponúknúť mu obohatenie</b> na tejto ceste. Niečo, čím bude jeho cesta výnimočná a plus." +
                "        </div>" +
                "    </body>" +
                "</html>";
    }
}
