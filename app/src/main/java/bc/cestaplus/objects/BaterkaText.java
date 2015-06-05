package bc.cestaplus.objects;

/**
 * Created by Matej on 27. 5. 2015.
 */
public class BaterkaText {

    private String coordinates;// = "NA";
    private String scripture;// = "NA";
    private String title;// = "NA";
    private String author;
    private String imageUrl;
    private String text;
    private String quote;
    private String depth1;
    private String depth2;
    private String depth3;
    private String hint;

       /**
     * Full Constructor
     * @param coordinates
     * @param scripture
     * @param title
     * @param author
     * @param imageUrl
     * @param text
     * @param quote
     * @param depth1
     * @param depth2
     * @param depth3
     * @param hint
     */
    public BaterkaText(String coordinates, String scripture, String title, String author, String imageUrl, String text, String quote, String depth1, String depth2, String depth3, String hint) {
        this.coordinates = coordinates;
        this.scripture = scripture;
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.text = text;
        this.quote = quote;
        this.depth1 = depth1;
        this.depth2 = depth2;
        this.depth3 = depth3;
        this.hint = hint;
    }// end full Constructor

    /**
     * Empty Constructor
     */
    public BaterkaText() {
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getScripture() {
        return scripture;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getQuote() {
        return quote;
    }

    public String getDepth1() {
        return depth1;
    }

    public String getDepth2() {
        return depth2;
    }

    public String getDepth3() {
        return depth3;
    }

    public String getHint() {
        return hint;
    }
} //end class BaterkaText
