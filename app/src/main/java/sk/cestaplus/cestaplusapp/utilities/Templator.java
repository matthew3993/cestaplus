package sk.cestaplus.cestaplusapp.utilities;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.objects.ArticleObj;
import sk.cestaplus.cestaplusapp.objects.ArticleText;
import sk.cestaplus.cestaplusapp.utilities.utilClasses.TextSizeUtil;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class Templator {

    private static final String PREFIX_ARTICLE = "http://www.cestaplus.sk/cestaplus/clanok/";

    public static String createHtmlString(Context context, ArticleObj article, ArticleText articleText, int articleErrorCode) {

        /*
        if (SectionsUtil.needsShortTemplate(article.getSection())){
            return getShortTeplate(article, articleText, articleErrorCode);
        } else {
            return getFullTeplate(context, article, articleText, articleErrorCode);
        }*/

        return getFullTeplate(context, article, articleText, articleErrorCode);
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

        Elements cestaplushRefs = doc.select("a");
        for (int i = 0; i < cestaplushRefs.size(); i++) {
            Element ref = cestaplushRefs.get(i);

            String href = ref.attr("href"); // get the value from href attribute

            if (href.startsWith(PREFIX_ARTICLE)){
                // this is link to ordinary cesta plus article
                //TODO: do something with links on cestaplus articles
                //Attributes attrs = new Attributes();
                //attrs.put(new Attribute("href", src));
            }

            //iframes.get(i).replaceWith(new Element(Tag.valueOf("a"), "", attrs).html("Video"));
        }

        return doc.html();
    }//end checkVideos()

    private static String getFullTeplate(Context context, ArticleObj article, ArticleText articleText, int articleErrorCode) {
        return "<html>" +
                "    <head>" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/articleStyle.css\"/>\n" +

                "        <style>" +
                            getInternalArticleStyle(context) +
                "        </style>" +

                "    </head>" +
                "    <body>" +
                "       <div class=\"articleText\">" +
                "            <div id=\"text_clanok\">" +
                                checkVideos(articleText) +
                "               <p> <br/> </p>" +
                "            </div>        \n" +
                "       </div>\n" +
                "    </body>\n" +
                "</html>"
                ;
    }//end getShortTemplate()

    public static String getInternalArticleStyle(Context context) {
        SessionManager session = new SessionManager(CustomApplication.getCustomAppContext());
        int textSizeConstant = session.getTextSize();

        //SOURCE: http://stackoverflow.com/a/26382502
        int valueInPixels = (int) context.getResources().getDimension(R.dimen.article_font_size_normal);

        switch (textSizeConstant){
            case TextSizeUtil.TEXT_SIZE_BIG:{
                valueInPixels = (int) context.getResources().getDimension(R.dimen.article_font_size_big);
                break;
            }
            case TextSizeUtil.TEXT_SIZE_NORMAL:{
                valueInPixels = (int) context.getResources().getDimension(R.dimen.article_font_size_normal);
                break;
            }
            case TextSizeUtil.TEXT_SIZE_SMALL:{
                valueInPixels = (int) context.getResources().getDimension(R.dimen.article_font_size_small);
                break;
            }
        }

        String ret = "div.articleText   {font-size: " + valueInPixels + "px;} ";
        return ret;
    }
}//end class Templator
