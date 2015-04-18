package bc.cestaplus.objects;

/**
 * Created by Matej on 27. 3. 2015.
 */
public class ArticleText {

    private String short_text;
    private String autor;
    private String text;

    public ArticleText(String short_text, String autor, String text) {
        this.short_text = short_text;
        this.autor = autor;
        this.text = text;
    }

    public ArticleText() {
    }

    public String getShort_text() {
        return short_text;
    }

    public String getAutor() {
        return autor;
    }

    public String getText() {
        return text;
    }

}// end AtricleText class
