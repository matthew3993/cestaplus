package sk.cestaplus.cestaplusapp.network;

import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.objects.ArticleText;
import sk.cestaplus.cestaplusapp.objects.BaterkaText;
import sk.cestaplus.cestaplusapp.objects.UserInfo;
import sk.cestaplus.cestaplusapp.utilities.ResponseCrate;
import sk.cestaplus.cestaplusapp.utilities.CustomApplication;
import sk.cestaplus.cestaplusapp.utilities.DateFormats;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.SectionsUtil;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.EMAIL_OR_PASSWORD_MISSING;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.JSON_ERROR;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.SERVER_INTERNAL_ERROR;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.WRONG_EMAIL_OR_PASSWORD;
import static sk.cestaplus.cestaplusapp.extras.IKeys.DEFAULT_URL;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_API_KEY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ARRAY_ARTICLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_AUTOR_OBJ;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_AUTOR_TEXT;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_COORDINATES;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_DEPTH_1;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_DEPTH_2;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_DEPTH_3;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ERROR_CODE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_HEADER_ARTICLE_ID;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_HINT;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_ID;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_IMAGE_URL;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_LOCKED;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_NAME;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PUB_DATE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_QUOTE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_QUOTE_AUTHOR;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SCRIPTURE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SECTION;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SHORT_TEXT;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SUBSCRIPTION_END;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SUBSCRIPTION_NAME;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SUBSCRIPTION_START;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_SURNAME;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_TEXT;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_TITLE;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_URL;

/**
 * Created by Matej on 26. 5. 2015.
 */
public class Parser {

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
            String quoteAuthor;
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
                if(contains(response, KEY_AUTOR_TEXT)){
                    author = response.getString(KEY_AUTOR_TEXT);
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

                //spracovanie quoteAuthor
                if (contains(response, KEY_QUOTE_AUTHOR)){
                    quoteAuthor = response.getString(KEY_QUOTE_AUTHOR);
                } else {
                    quoteAuthor = "NA"; // akoze chyba
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

                baterkaTextTemp = new BaterkaText(
                        coordinates, scripture, title,
                        author, imageUrl, text,
                        quote, quoteAuthor, depth1,
                        depth2, depth3, hint
                );

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
            String author;
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

                    // author
                    if (actArticle.has(KEY_AUTOR_OBJ) && !actArticle.isNull(KEY_AUTOR_OBJ)) {
                        author = actArticle.getString(KEY_AUTOR_OBJ);
                    } else {
                        author = "Petra Babulíková"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                    }

                    //spracovanie obrazka - ostrenie v pripade, ze obrazok nie je dostupny
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

                        title = DateFormats.dateFormatBaterkaShort.format(DateFormats.dateFormatJSON.parse(pubDate)) + ": " + title; //add the date of this Baterka into it's title

                    } else {
                        if (actArticle.has(KEY_LOCKED) && !actArticle.isNull(KEY_LOCKED)) {
                            locked = actArticle.getBoolean(KEY_LOCKED);
                        } else {
                            locked = false; // zatial !!!
                        }
                    }

                    //unescaping short_text for some sections
                    if (SectionsUtil.needsShortTemplate(section)){
                            short_text = Html.fromHtml(short_text).toString();
                    }//end if needsShortTemplate

                    //kontrola, ci bude clanok pridany do zoznamu
                    if (/*id != -1 && */ !title.equals("NA")) {
                        if (!pubDate.equals("NA")) {
                            try { //clanok ma v poriadku nadpis aj datum zverejnenia
                                tempArticles.add(new ArticleObj(title, short_text, author,img, DateFormats.dateFormatJSON.parse(pubDate), section, ID, locked)); // pridanie do docasneho zoznamu clankov
                            } catch (ParseException pEx){
                                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA PARSOVANIA DATUMU" + pEx.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        } else { // tu je rieseny pripad, ze clanok nema v poriadku datum zverejnenia
                            try {
                                tempArticles.add(new ArticleObj(title, short_text, author, img, DateFormats.dateFormatJSON.parse("2000-01-01 00:00:00"), section, ID, locked)); // pridanie do docasneho zoznamu clankov

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

    public static ResponseCrate parseJsonObjectResponse(JSONObject response) {

        String headerArticleId = "NA";
        ArrayList<ArticleObj> articles = new ArrayList<>();

        try {
            if (contains(response, KEY_HEADER_ARTICLE_ID)){
                headerArticleId = response.getString(KEY_HEADER_ARTICLE_ID);
            }

            if (contains(response, KEY_ARRAY_ARTICLE)){
                JSONArray jsonArray = response.getJSONArray(KEY_ARRAY_ARTICLE);
                articles = parseJsonArrayResponse(jsonArray);
            }

        } catch (JSONException jsonEx) {
            Log.e("JSON error", "CHYBA JSON PARSOVANIA " + jsonEx.getMessage());
        }

        ResponseCrate responseCrate = new ResponseCrate(headerArticleId, articles);
        return responseCrate;
    }//end parseJsonObjectResponse

    public static ArticleText parseArticleTextResponse(JSONObject response) {
        ArticleText articleTextTemp = null;

        if (response != null && response.length() > 2) {
            //defaultne hodnoty
            //urobit cez rozhranie Constants v Extras
            String short_text;
            String autor;
            String text;
            String url;

            try {
                // kontrola short_text
                if (response.has(KEY_SHORT_TEXT) && !response.isNull(KEY_SHORT_TEXT)) {
                    short_text = response.getString(KEY_SHORT_TEXT);
                } else {
                    short_text = "Chyba st"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                //kontrola autora
                if (response.has(KEY_AUTOR_TEXT) && !response.isNull(KEY_AUTOR_TEXT)) {
                    autor = response.getString(KEY_AUTOR_TEXT);
                } else {
                    autor = "Chyba aut"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                //kontrola text
                if (response.has(KEY_TEXT) && !response.isNull(KEY_TEXT)) {
                    text = response.getString(KEY_TEXT);
                } else {
                    text = "<p>Chyba txt<p>"; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                //kontrola text
                if (contains(response, KEY_URL)) {
                    url = response.getString(KEY_URL);
                } else {
                    url = DEFAULT_URL; // ak JSON feed nie je v poriadku, nastavi sa tato hodnota
                }

                articleTextTemp = new ArticleText(short_text, autor, text, url);

            } catch (JSONException jsonEx) {
                Toast.makeText(CustomApplication.getCustomAppContext(), "CHYBA JSON PARSOVANIA" + jsonEx.getMessage(), Toast.LENGTH_LONG).show();

            } //end catch

        } else {
            Toast.makeText(CustomApplication.getCustomAppContext(), "Príliš krátka response", Toast.LENGTH_LONG).show();
            //end if response !=null ...
        }

        if (articleTextTemp == null) { //osetrenie, aby sa nikdy nevratilo null
            return new ArticleText("Chyba short_textu", "Chyba autora", "<p>Chyba textu<p>", DEFAULT_URL);
        } else {
            return articleTextTemp;
        }
    } //end parseArticleTextResponse

    public static int parseErrorCode(JSONObject response) {

        int error_code = JSON_ERROR; // if there is an error in JSON feed this value is set

        if (response != null/* && response.length() > 2*/) {
            try {
                if (contains(response, KEY_ERROR_CODE)){
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
            //parse API_key
            if (response.has(KEY_API_KEY) && !response.isNull(KEY_API_KEY)) {
                API_key = response.getString(KEY_API_KEY);
            }

        } catch (JSONException e) {
            Toast.makeText(CustomApplication.getCustomAppContext(), "Chyba parsovania API_key!", Toast.LENGTH_LONG).show();
        }
        return API_key;
    }

    public static UserInfo parseUserInfo(JSONObject response) {
        String name = "name";
        String surname = "surname";
        String subscription_start_string = "subscription_start";
        String subscription_end_string = "subscription_end";
        String subscription_name = "subscription_name";

        Date subscription_start = new Date();
        Date subscription_end = new Date();

        try {
            //parse name
            if (contains(response, KEY_NAME)) {
                name = response.getString(KEY_NAME);
            }

            //parse surname
            if (contains(response, KEY_SURNAME)) {
                surname = response.getString(KEY_SURNAME);
            }

            //parse subscription_start String
            if (contains(response, KEY_SUBSCRIPTION_START)) {
                subscription_start_string = response.getString(KEY_SUBSCRIPTION_START);
            }

            //parse subscription_end String
            if (contains(response, KEY_SUBSCRIPTION_END)) {
                subscription_end_string = response.getString(KEY_SUBSCRIPTION_END);
            }

            //parse subscription_name
            if (contains(response, KEY_SUBSCRIPTION_NAME)) {
                subscription_name = response.getString(KEY_SUBSCRIPTION_NAME);
            }

            subscription_start = DateFormats.dateFormatJSON.parse(subscription_start_string);
            subscription_end = DateFormats.dateFormatJSON.parse(subscription_end_string);

        } catch (JSONException e) {
            Toast.makeText(CustomApplication.getCustomAppContext(), "Chyba parsovania UserInfo!", Toast.LENGTH_LONG).show();
        } catch (ParseException pEx){
            Log.e("error", "CHYBA PARSOVANIA DATUMU" + pEx.getMessage());
        }

        return new UserInfo(name, surname, subscription_start, subscription_end, subscription_name);
    }

    public static void handleLoginError(int errorCode){
        switch (errorCode){
            case EMAIL_OR_PASSWORD_MISSING:{ // 11
                Toast.makeText(CustomApplication.getCustomAppContext(), R.string.email_or_password_missing_error, Toast.LENGTH_LONG).show();
                break;
            }
            case WRONG_EMAIL_OR_PASSWORD:{ //12
                Toast.makeText(CustomApplication.getCustomAppContext(), R.string.wrong_email_or_password_error, Toast.LENGTH_LONG).show();
                break;
            }
            case SERVER_INTERNAL_ERROR:{ //13
                Toast.makeText(CustomApplication.getCustomAppContext(), R.string.server_internal_error, Toast.LENGTH_LONG).show();
                break;
            }
            case JSON_ERROR:{
                Toast.makeText(CustomApplication.getCustomAppContext(), R.string.json_parsing_error, Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                Toast.makeText(CustomApplication.getCustomAppContext(), R.string.parsing_error, Toast.LENGTH_LONG).show();
                break;
            }
        } // end switch
    }//end handleLoginError()

    //TODO zakomponovat tuto metodu do kodu vyssie - preburat kod!!
    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }// end contains()

} // end class Parser
