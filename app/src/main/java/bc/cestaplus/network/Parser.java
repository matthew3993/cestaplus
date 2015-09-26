package bc.cestaplus.network;

import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.objects.ArticleText;
import bc.cestaplus.objects.BaterkaText;
import bc.cestaplus.utilities.CustomApplication;

import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_APY_KEY;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_AUTOR;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_CLANKY;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_COORDINATES;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_DEPTH_1;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_DEPTH_2;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_DEPTH_3;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ERROR_CODE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_HINT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_ID;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_IMAGE_URL;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_LOCKED;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_PUB_DATE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_QUOTE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SCRIPTURE;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SECTION;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_SHORT_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TEXT;
import static bc.cestaplus.extras.IKeys.IPrehlad.KEY_TITLE;

/**
 * Created by Matej on 26. 5. 2015.
 */
public class Parser {

    //dates formats
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat baterkaShortDateFormat = new SimpleDateFormat("dd.MM.");

    public static BaterkaText parseBaterka(JSONObject response) {
        BaterkaText baterkaTextTemp = null;

        if (response != null && response.length() > 2) {
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String coordinates;// = "NA";
            String scripture;// = "NA";
            String title;// = "NA";
            String author;
            String imageUrl;
            String text;
            String quote;
            String depth1;
            String depth2;
            String depth3;
            String hint;

            try {
                    //kontrola coordinates
                    if (contains(response, KEY_COORDINATES)){
                        coordinates = response.getString(KEY_COORDINATES);
                    } else {
                        coordinates = "NA"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }
                    // kontrola scripture
                    if (contains(response, KEY_SCRIPTURE)) {
                        scripture = response.getString(KEY_SCRIPTURE);
                    } else {
                        scripture = "NA"; // akoze chyba // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }

                    //spracovanie nadpisu
                    if(contains(response, KEY_TITLE)){
                        title = response.getString(KEY_TITLE);
                    } else {
                        title = "NA"; // akoze chyba
                    }

                    //spracovanie autora
                    if(contains(response, KEY_AUTOR)){
                        author = response.getString(KEY_AUTOR);
                    } else {
                        author = "NA";
                    }

                    //spracovanie obrázku
                    if (contains(response, KEY_IMAGE_URL)) {
                        imageUrl = response.getString(KEY_IMAGE_URL);
                    } else {
                        imageUrl = "NA"; // akoze chyba
                    }

                    //spracovanie text
                    if (contains(response, KEY_TEXT)){
                        text = response.getString(KEY_TEXT);
                    } else {
                        text = "NA"; // akoze chyba
                    }

                    //spracovanie quote
                    if (contains(response, KEY_QUOTE)){
                        quote = response.getString(KEY_QUOTE);
                    } else {
                        quote = "NA"; // akoze chyba
                    }

                    //spracovanie depth1
                    if (contains(response, KEY_DEPTH_1)){
                        depth1 = response.getString(KEY_DEPTH_1);
                    } else {
                        depth1 = "NA"; // akoze chyba
                    }

                    //spracovanie depth2
                    if (contains(response, KEY_DEPTH_2)){
                        depth2 = response.getString(KEY_DEPTH_2);
                    } else {
                        depth2 = "NA"; // akoze chyba
                    }

                    //spracovanie depth3
                    if (contains(response, KEY_DEPTH_3)){
                        depth3 = response.getString(KEY_DEPTH_3);
                    } else {
                        depth3 = "NA"; // akoze chyba
                    }

                    //spracovanie hint
                    if (contains(response, KEY_HINT)){
                        hint = response.getString(KEY_HINT);
                    } else {
                        hint = "NA"; // akoze chyba
                    }

                baterkaTextTemp = new BaterkaText(coordinates, scripture, title, author, imageUrl,
                                                  text, quote, depth1, depth2, depth3, hint);

            } catch (JSONException jsonEx){
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();
            } //end catch

        } //end if response !=null ...

        return baterkaTextTemp;
    } //end parseBaterka

    public static ArrayList<ArticleObj> parseJsonArrayResponse(JSONArray response) {
        ArrayList<ArticleObj> tempArticles = new ArrayList<>();

        if (response != null && response.length() > 0){
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String title;// = "NA";
            String short_text;// = "NA";
            String img;// = "NA";
            String pubDate;
            String section;
            String ID;
            boolean locked;

            try {
                //JSONArray jsonArrayVsetko = response.getJSONArray(KEY_CLANKY);

                for(int i = 0; i < response.length(); i++){
                    JSONObject actArticle = response.getJSONObject(i); //vrati clanok na aktualnej pozicii

                    //spracovanie ID
                    if (actArticle.has(KEY_ID) && !actArticle.isNull(KEY_ID)){
                        ID = actArticle.getString(KEY_ID);
                    } else {
                        ID = "NA"; // akoze chyba
                    }

                    //kontrola title
                    if (actArticle.has(KEY_TITLE) && !actArticle.isNull(KEY_TITLE)){
                        title = actArticle.getString(KEY_TITLE);
                    } else {
                        title = "NA"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }
                    // kontrola short_text
                    if (actArticle.has(KEY_SHORT_TEXT) && !actArticle.isNull(KEY_SHORT_TEXT)) {
                        short_text = actArticle.getString(KEY_SHORT_TEXT);
                    } else {
                        short_text = "Popis nedostuný"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }

                    //spracovanie obrazka - ostrenie v pripade, ze obrazok nie je dostupny
                    img = null; //title image URL
                    if(actArticle.has(KEY_IMAGE_URL) && !actArticle.isNull(KEY_IMAGE_URL)){
                        img = actArticle.getString(KEY_IMAGE_URL);
                    } else {
                        img = "NA";
                    }

                    //spracovanie datumu        //otazka co robit ak nie je?!?! dat sem try - catch ??
                    if(actArticle.has(KEY_PUB_DATE) && !actArticle.isNull(KEY_PUB_DATE)){
                        pubDate = actArticle.getString(KEY_PUB_DATE);
                    } else {
                        pubDate = "NA";
                    }

                    //spracovanie rubriky
                    if (actArticle.has(KEY_SECTION) && !actArticle.isNull(KEY_SECTION)) {
                        section = actArticle.getString(KEY_SECTION);
                    } else {
                        section = "Nezaradené"; // Článok
                    }

                    //spracovanie locked
                    if(section.equalsIgnoreCase("baterka")){ //if this article is Baterka
                        locked = false; //Baterka is never locked

                        title = baterkaShortDateFormat.format(dateFormat.parse(pubDate)) + ": " + title; //add the date of this Baterka into it's title

                    } else {
                        if (actArticle.has(KEY_LOCKED) && !actArticle.isNull(KEY_LOCKED)) {
                            locked = actArticle.getBoolean(KEY_LOCKED);
                        } else {
                            locked = false; // zatial !!!
                        }
                    }

                    //unescaping short_text for some sections
                    switch (section) {
                        case "180stupnov":
                        case "naceste":
                        case "rodicovskeskratky":
                        case "napulze":
                        case "umatusa":
                        case "normalnarodinka":
                        case "tabule":
                        case "animamea":
                        case "kuchynskateologia":
                        case "kazatelnicazivot":
                        case "zahranicami":
                        case "fejton":
                        case "poboxnebo":
                        case "zparlamentu":{
                            //short_text = HtmlEscape.unescapeHtml(short_text); //cez externu kniznicu
                            short_text = Html.fromHtml(short_text).toString();
                            break;
                        }//end case
                    }//end switch

                    //kontrola, ci bude clanok pridany do zoznamu
                    if (/*id != -1 && */ !title.equals("NA")) {
                        if (!pubDate.equals("NA")) {
                            try { //clanok ma v poriadku nadpis aj datum zverejnenia
                                tempArticles.add(new ArticleObj(title, short_text, img, dateFormat.parse(pubDate), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, img, dateFormat.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Log.e("error", "CHYBA PARSOVANIA DATUMU" + pEx.getMessage());
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Log.e("error", "CHYBA JSON PARSOVANIA" + jsonEx.getMessage());

            } //end catch
            catch (ParseException e) {
                Log.e("error", "CHYBA PARSOVANIA DATUMU BATERKY" + e.getMessage());
            }

        } //end if response !=null ...

        return tempArticles;
    } //end parseJsonArrayResponse

    public static ArticleText parseArticleTextResponse(JSONObject response) {
        ArticleText articleTextTemp = null;

        if (response != null && response.length() > 2) {
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String short_text;
            String autor;
            String text;

            try {
                // kontrola short_text
                if (response.has(KEY_SHORT_TEXT) && !response.isNull(KEY_SHORT_TEXT)) {
                    short_text = response.getString(KEY_SHORT_TEXT);
                } else {
                    short_text = "Chyba st"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                //kontrola autora
                if (response.has(KEY_AUTOR) && !response.isNull(KEY_AUTOR)) {
                    autor = response.getString(KEY_AUTOR);
                } else {
                    autor = "Chyba aut"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                //kontrola text
                if (response.has(KEY_TEXT) && !response.isNull(KEY_TEXT)) {
                    text = response.getString(KEY_TEXT);
                } else {
                    text = "<p>Chyba txt<p>"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                articleTextTemp = new ArticleText(short_text, autor, text);

            } catch (JSONException jsonEx) {
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } else {
            Toast.makeText(CustomApplication.getCustomAppContext(), "Príliš krátka response", Toast.LENGTH_LONG).show();
            //end if response !=null ...
        }

        if (articleTextTemp == null) { //osetrenie, aby sa nikdy nevratilo null
            return new ArticleText("Chyba short_textu", "Chyba autora", "<p>Chyba textu<p>");
        } else {
            return articleTextTemp;
        }
    } //end parseArticleTextResponse

    public static int parseErrorCode(JSONObject response) {

        int error_code = 5; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota

        if (response != null/* && response.length() > 2*/) {
            try {
                // kontrola error_code
                if (response.has(KEY_ERROR_CODE) && !response.isNull(KEY_ERROR_CODE)) {
                    error_code = response.getInt(KEY_ERROR_CODE);
                }

            } catch (JSONException jsonEx) {
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();
            } //end catch
        } // end if

        return error_code;
    }

    public static String parseAPI_key(JSONObject response) {
        String API_key = "NA";
        try {
            //kontrola API_key
            if (response.has(KEY_APY_KEY) && !response.isNull(KEY_APY_KEY)) {
                API_key = response.getString(KEY_APY_KEY);
            }

        } catch (JSONException e) {
            Toast.makeText(CustomApplication.getCustomAppContext(), "Chyba parsovnania API_key!", Toast.LENGTH_LONG).show();
        }
        return API_key;
    }

    public static ArrayList<ArticleObj> parseJsonObjectResponse(JSONObject response) {
        ArrayList<ArticleObj> tempArticles = new ArrayList<>();

        if (response != null && response.length() > 0){
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String title;// = "NA";
            String short_text;// = "NA";
            String imageUrl;// = "NA";
            String pubDate;
            String section;
            String ID;
            boolean locked;

            try {
                JSONArray jsonArrayAll = response.getJSONArray(KEY_CLANKY);

                for(int i = 0; i < jsonArrayAll.length(); i++){
                    JSONObject actArticle = jsonArrayAll.getJSONObject(i); //vrati clanok na aktualnej pozicii

                    //kontrola title
                    if (actArticle.has(KEY_TITLE) && !actArticle.isNull(KEY_TITLE)){
                        title = actArticle.getString(KEY_TITLE);
                    } else {
                        title = "NA"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }
                    // kontrola short_text
                    if (actArticle.has(KEY_SHORT_TEXT) && !actArticle.isNull(KEY_SHORT_TEXT)) {
                        short_text = actArticle.getString(KEY_SHORT_TEXT);
                    } else {
                        short_text = "Popis nedostuný"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }

                    //spracovanie obrazka - ostrenie v pripade, ze orazok nie je dostupny
                    imageUrl = null;
                    if(actArticle.has(KEY_IMAGE_URL) && !actArticle.isNull(KEY_IMAGE_URL)){
                        imageUrl = actArticle.getString(KEY_IMAGE_URL);
                    } else {
                        imageUrl = "NA";
                    }

                    //spracovanie datumu        //otazka co robit ak nie je?!?! dat sem try - catch ??
                    if(actArticle.has(KEY_PUB_DATE) && !actArticle.isNull(KEY_PUB_DATE)){
                        pubDate = actArticle.getString(KEY_PUB_DATE);
                    } else {
                        pubDate = "NA";
                    }

                    //spracovanie rubriky
                    if (actArticle.has(KEY_SECTION) && !actArticle.isNull(KEY_SECTION)) {
                        section = actArticle.getString(KEY_SECTION);
                    } else {
                        section = "Nezaradené"; // Článok
                    }

                    //spracovanie id
                    if (actArticle.has(KEY_ID) && !actArticle.isNull(KEY_ID)){
                        ID = actArticle.getString(KEY_ID);
                    } else {
                        ID = "NA"; // akoze chyba
                    }

                    //spracovanie locked
                    if (actArticle.has(KEY_LOCKED) && !actArticle.isNull(KEY_LOCKED)) {
                        locked = actArticle.getBoolean(KEY_LOCKED);
                    } else {
                        locked = false; // zatial !!!
                    }

                    //kontrola, ci bude clanok pridany do zoznamu
                    if (/*id != -1 && */ !title.equals("NA")) {
                        if (!pubDate.equals("NA")) {
                            try { //clanok ma v poriadku nadpis aj datum zverejnenia
                                tempArticles.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Log.e("error", "CHYBA PARSOVANIA DATUMU" + pEx.getMessage());
                            }
                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                                //zoznamVsetko.add(new ArticleObj(title, short_text, imageUrl, dateFormat.parse(pubDate), section));
                            } catch (ParseException pEx){
                                Log.e("error", "CHYBA PARSOVANIA DATUMU" + pEx.getMessage());
                            }
                        }
                    }
                } // end for

            } catch (JSONException jsonEx){
                Log.e("error", "CHYBA PARSOVANIA JSON" + jsonEx.getMessage());

            } //end catch

        } //end if response !=null ...

        Toast.makeText(CustomApplication.getCustomAppContext().getApplicationContext(), "Načítaných " + tempArticles.size() + " článkov.", Toast.LENGTH_LONG).show();
        return tempArticles;
    }//end parseJsonObjectResponse

    public static void handleLoginErrorCode(int error_code){
        switch (error_code){
            case 1:{
                Toast.makeText(CustomApplication.getCustomAppContext(), "Chýba email alebo heslo!", Toast.LENGTH_LONG).show();
                break;
            }
            case 2:{
                Toast.makeText(CustomApplication.getCustomAppContext(), "Zadaný email nie je v databáze!", Toast.LENGTH_LONG).show();
                break;
            }
            case 3:{
                Toast.makeText(CustomApplication.getCustomAppContext(), "Nesprávne heslo!", Toast.LENGTH_LONG).show();
                break;
            }
            case 4:{
                Toast.makeText(CustomApplication.getCustomAppContext(), "Chyba pripojenia na databázu!", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                Toast.makeText(CustomApplication.getCustomAppContext(), "Chyba parsovania!", Toast.LENGTH_LONG).show();
                break;
            }
        } // end switch
    }//end handleLoginErrorCode()

    //TODO zakomponovat tuto metodu do kodu vyssie - preburat kod!!
    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }// end contains()

} // end class Parser
