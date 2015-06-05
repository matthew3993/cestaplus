package bc.cestaplus.utilities;

import android.content.res.Configuration;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import bc.cestaplus.objects.ArticleObj;
import bc.cestaplus.objects.ArticleText;
import bc.cestaplus.objects.BaterkaText;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Templator {

    private static DateFormat dateFormatBaterkaPrint = new SimpleDateFormat("dd. MM");

    public static String createHtmlString(ArticleObj article, ArticleText articleText, int articleErrorCode) {

        String ret = "";

        switch (article.getSection()) {
            case "baterka": {
                Toast.makeText(CustomApplication.getCustomAppContext(), "Ľutujeme, ale tento článok patrí do rubriky, ktorá nie je podporovaná!", Toast.LENGTH_SHORT).show();
                break;
            }
            case "normalnarodinka":
            case "tabule":
            case "animamea":
            case "kuchynskateologia":
            case "kazatelnicazivot":
            case "zahranicami":
            case "fejton":
            case "poboxnebo":
            case "zparlamentu": {
                ret = getShortTeplate(article, articleText, articleErrorCode);
                break;
            } //end case skrátená šablóna

            default: {
                ret = getFullTeplate(article, articleText, articleErrorCode);
            } //end defaut - full template

        }//end switch

        return ret;
    }//end createHtmlString

    private static String checkVideos(ArticleText articleText) {
        Document doc = Jsoup.parse(articleText.getText());

        Elements iframes = doc.select("iframe");

        String src = "";
        for (int i = 0; i < iframes.size(); i++) {
            src = iframes.get(i).attr("src"); // get the value from src attribute
            Attributes attrs = new Attributes();
            attrs.put(new Attribute("href", src));
            iframes.get(i).replaceWith(new Element(Tag.valueOf("a"), "", attrs).html("Video"));
        }

        return doc.html();
    }//end checkVideos()

    private static String getShortTeplate(ArticleObj article, ArticleText articleText, int articleErrorCode) {
        return "<html>\n" +
                "    <head>\n" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/articleStyle.css\"/>\n" +
                "        <style>" +
                getInternalArticleStyle() +
                "        </style>" +
                "    </head>\n" +
                "    \n" +
                "    <body>\n" +
                "        <div class=\"body1_left_pisuinde\" style=\" height: 100%;\">                    \n" +
                "            <h1>\n" +
                article.getTitle() +
                "            </h1>\n" +
                "\n" +
                "            <div id=\"text_clanok\">" +
                checkVideos(articleText) +
                "            </div>        \n" +

                (articleErrorCode == 0 ? "" : (
                        "<div id=\"info\">" +
                                "Zvyšok článku je prístupný iba predplatiteľom.<br>" +
                                "<font style=\"color: #005494\">Staňte sa našim <b>predplatiteľom</b> a získajte aj iné <b>výhody</b>.</font>\n" +
                                "\t\t\t\t\t<br />Informácie o predplatnom na <a href=\"http://www.cestaplus.sk/predplatne\">www.cestaplus.sk/predplatne</a>.\n" +
                                "\t\t\t\t\t<br />\t\n" +
                                "\t\t\t\t\t<br />\t\n" +
                                "\t\t\t\t\t<b>cesta+</b>\n" +
                                "\t\t\t\t\t<br />\n" +
                                "</div>        \n")) +
                "           " +
                "            <div id=\"end\">" +
                "               <p> <br/> </p>" +
                "            </div>        \n" +
                "        </div>\n" +
                "    </body>\n" +
                "</html>"
                ;
    }//end getFullTemplate()

    private static String getFullTeplate(ArticleObj article, ArticleText articleText, int articleErrorCode) {
        return "<html>" +
                "    <head>" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/articleStyle.css\"/>\n" +
                "        <style>" +
                            getInternalArticleStyle() +
                "        </style>" +
                "    </head>" +
                "    <body>" +
                "        <div class=\"body1_left_pisuinde\" style=\" height: 100%;\">" +
                "            <h1>" +
                                article.getTitle() +
                "            </h1>" +
                "            <div>" +
                                "<p id=\"short_text\">" + articleText.getShort_text() + "</p>" +
                "            </div>" +
                "            <div>" +
                "                <img src='" + article.getImageUrl() + "'>" +
                "            </div>" +
                "            <div id=\"text_clanok\">" +
                                checkVideos(articleText) +
                "            </div>        \n" +

                (articleErrorCode == 0 ? "" : (
                        "   <div id=\"info\">" +
                                "Zvyšok článku je prístupný iba predplatiteľom.<br>" +
                                "<font style=\"color: #005494\">Staňte sa našim <b>predplatiteľom</b> a získajte aj iné <b>výhody</b>.</font>\n" +
                                "\t\t\t\t\t<br />Informácie o predplatnom na <a href=\"http://www.cestaplus.sk/predplatne\">www.cestaplus.sk/predplatne</a>.\n" +
                                "\t\t\t\t\t<br />\t\n" +
                                "\t\t\t\t\t<br />\t\n" +
                                "\t\t\t\t\t<b>cesta+</b>\n" +
                                "\t\t\t\t\t<br />\n" +
                        "   </div>        \n")) +
                "            <div id=\"end\">" +
                "               <p> <br/> </p>" +
                "            </div>        \n" +
                "       </div>\n" +
                "    </body>\n" +
                "</html>"
                ;
    }//end getShortTemplate()

    public static String createBaterkaHtmlString(BaterkaText baterkaText, Date pubDate) {
        return  "<html>" +
                "    <head>" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/baterkaStyle.css\"/>" +
                "        <style>" +
                            getInternalBaterkaStyle() +
                "        </style>" +
                "    </head>" +
                "    <body>" +
                "        <div class=\"baterka_body\" style=\" height: 100%;\">" +
                "            <div id=\"scripture_div\">" +
                "                <img id=\"scripture_ul\" src=\"http://www.cestaplus.sk/images/site_images/ul_evanjelium.png\">" +
                "                <h1 id=\"coordinates\"> "+ baterkaText.getCoordinates() + "</h1>" +
                "                <p id=\"scripture\">" + baterkaText.getScripture() + "</p>" +
                "            </div>" +
                "            <div class=\"smallGap\"></div>" +
                "            <div>" +
                "                <h1 id=\"meditation_title\">" + baterkaText.getTitle() + "</h1>" +
                "            </div>" +
                "            <div>" +
                "                <p id=\"date\">na " + dateFormatBaterkaPrint.format(pubDate) + "</p>" +
                "                <p id=\"author\">" + baterkaText.getAuthor() + "</p>\t\t\t\t" +
                "            </div>" +
                "            <div>\t" +
                "                <img id=\"main_image\" src='" + baterkaText.getImageUrl() + "'>" +
                "                <p id=\"meditation_text\">" +
                baterkaText.getText() +
                "                </p>" +
                "            </div>" +
                "            " +
                "            <!-- gap -->" +
                "            <div class=\"middleGap\"></div>" +
                "            " +
                "            <div id=\"quote\">" +
                "                <table id=\"table_style\">" +
                "                    <tr>" +
                "                        <td rowspan=\"2\" style=\"vertical-align: top; width: 20px\"><img src=\"http://www.cestaplus.sk//images/site_images/uvodzovka_1.png\"></td>" +
                "                        <td style=\"text-align: left\"> <p>" + baterkaText.getQuote() + "</p> </td>" +
                "                        <td  rowspan=\"2\" style=\"vertical-align: bottom;  width: 20px\" ><img src=\"http://www.cestaplus.sk/images/site_images/uvodzovka_2.png\"></td>" +
                "                    </tr>" +
                "                    <tr>" +
                "                        <td id=\"quote_author\">(" + baterkaText.getAuthor() +")\t\t\t\t\t\t</td>" +
                "                    </tr>\t" +
                "                </table>\t\t\t\t" +
                "            </div>" +
                "         " +
                "            <div class=\"gap\"></div>" +
                "            <p id=\"pod_na_hlbinu_title\">POĎ NA HLBINU</p>" +
                "            <div class=\"gap\"></div>" +
                "            <div>" +
                "                <div class=\"liImg\"> <img src=\"http://www.cestaplus.sk/images/site_images/lampas_1.png\"></div>" +
                "                <div class=\"depthLi\">" + baterkaText.getDepth1() + "</div>" +
                "            </div>" +
                "            <div class=\"gap\"></div>" +
                "            " +
                "            <div>" +
                "                <div class=\"liImg\"> <img src=\"http://www.cestaplus.sk/images/site_images/lampas_2.png\"> </div>" +
                "                <div class=\"depthLi\">" + baterkaText.getDepth2() + "</div>" +
                "            </div>" +
                "            <div class=\"gap\"></div>" +
                "            " +
                "            <div>" +
                "                <div class=\"liImg\"> <img src=\"http://www.cestaplus.sk/images/site_images/lampas_3.png\"> </div>" +
                "                <div class=\"depthLi\">" + baterkaText.getDepth3() + "</div>" +
                "            </div>" +
                "            <div class=\"gap\"></div>" +
                "            " +
                "            <div id=\"hint_outer_box\">" +
                "                <div id=\"hint_inner_box\">" +
                "                    <p id=\"hint_title\">{ TIP PRE TEBA }</p>" +
                "                    <p id=\"hint_text\">" + baterkaText.getHint() + "</p>" +
                "                </div>\t" +
                "            </div> " +
                "        " +
                "        </div>" +
                "        <div id=\"end\">" +
                "           <p> <br/> <br/> </p>" +
                "        </div>        " +
                "    </body>" +
                "</html>"
                ;
    }//end createBaterkaHtmlString()

    private static String getInternalBaterkaStyle() {

        SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());
        int textSizeConstant = session.getTextSize();
        String ret = "";

        switch (CustomApplication.getCustomAppScreenSize()) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                ret = "div.depthLi{width: 90%}";
                switch (textSizeConstant){
                    case Util.TEXT_SIZE_BIG:{
                        ret += "h1   {font-size: 35px;}" +
                              "p    {font-size: 25px;} " +
                              "p#date            {font-size: 22px;} " +
                              "p#author          {font-size: 27px;} " +
                              " #quote_author    {font-size: 22px;} " +
                              "p#pod_na_hlbinu_title    {font-size: 30px;} " +
                              "div.depthLi       {font-size: 25px;} " +
                              "p#hint_title      {font-size: 27px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_NORMAL:{
                        ret += "h1   {font-size: 30px;}" +
                              "p    {font-size: 20px;} "+
                              "p#date            {font-size: 17px;} " +
                              "p#author          {font-size: 22px;} " +
                              " #quote_author    {font-size: 17px;} " +
                              "p#pod_na_hlbinu_title    {font-size: 25px;} " +
                              "div.depthLi       {font-size: 20px;} " +
                              "p#hint_title      {font-size: 22px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_SMALL:{
                        ret += "h1                {font-size: 25px;}" +
                              "p                 {font-size: 15px;} " +
                              "p#date            {font-size: 12px;} " +
                              "p#author          {font-size: 17px;} " +
                              " #quote_author    {font-size: 12px;} " +
                              "p#pod_na_hlbinu_title    {font-size: 20px;} " +
                              "div.depthLi       {font-size: 15px;} " +
                              "p#hint_title      {font-size: 17px;} ";
                        break;
                    }
                }
                break;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                ret = "div.depthLi{width: 85%}";
                switch (textSizeConstant){
                    case Util.TEXT_SIZE_BIG:{
                        ret += "h1   {font-size: 30px;}" +
                              "p    {font-size: 20px;} "+
                              "p#date            {font-size: 17px;} " +
                              "p#author          {font-size: 22px;} " +
                              " #quote_author    {font-size: 17px;} " +
                              "p#pod_na_hlbinu_title    {font-size: 25px;} " +
                              "div.depthLi       {font-size: 20px;} " +
                              "p#hint_title      {font-size: 22px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_NORMAL:{
                        ret += "h1                {font-size: 25px;}" +
                              "p                 {font-size: 15px;} " +
                              "p#date            {font-size: 12px;} " +
                              "p#author          {font-size: 17px;} " +
                              " #quote_author    {font-size: 12px;} " +
                              "p#pod_na_hlbinu_title    {font-size: 20px;} " +
                              "div.depthLi       {font-size: 15px;} " +
                              "p#hint_title      {font-size: 17px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_SMALL:{
                        ret += "h1   {font-size: 20px;}" +
                              "p    {font-size: 10px;} " +
                              "p#date            {font-size: 7px;} " +
                              "p#author          {font-size: 12px;} " +
                              " #quote_author    {font-size: 7px;} " +
                              "p#pod_na_hlbinu_title    {font-size: 15px;} " +
                              "div.depthLi       {font-size: 10px;} " +
                              "p#hint_title      {font-size: 12px;} ";
                        break;
                    }
                }
                break;
        }

        return ret;
    }//end getInternalBaterkaStyle()

    private static String getInternalArticleStyle() {

        SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());
        int textSizeConstant = session.getTextSize();
        String ret = "";

        switch (CustomApplication.getCustomAppScreenSize()) {
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                switch (textSizeConstant){
                    case Util.TEXT_SIZE_BIG:{
                        ret = "h1   {font-size: 35px;}" +
                              "p    {font-size: 25px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_NORMAL:{
                        ret = "h1   {font-size: 30px;}" +
                              "p    {font-size: 20px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_SMALL:{
                        ret = "h1   {font-size: 25px;}" +
                              "p    {font-size: 15px;} ";
                        break;
                    }
                }
                break;

            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                switch (textSizeConstant){
                    case Util.TEXT_SIZE_BIG:{
                        ret = "h1   {font-size: 30px;}" +
                              "p    {font-size: 20px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_NORMAL:{
                        ret = "h1   {font-size: 25px;}" +
                              "p    {font-size: 15px;} ";
                        break;
                    }
                    case Util.TEXT_SIZE_SMALL:{
                        ret = "h1   {font-size: 20px;}" +
                              "p    {font-size: 10px;} ";
                        break;
                    }
                }
                break;
        }

        return ret;
    }//end getInternalArticleStyle()

}//end class Templator
