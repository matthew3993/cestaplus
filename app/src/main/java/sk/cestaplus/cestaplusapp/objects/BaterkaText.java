package sk.cestaplus.cestaplusapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matej on 27. 5. 2015.
 * Naming according to API.
 */
public class BaterkaText
    implements Parcelable {

    private String coordinates;
    private String scripture;
    private String title;
    private String author;
    private String imageUrl;
    private String text;
    private String quote;
    private String quoteAuthor;
    private String depth1;
    private String depth2;
    private String depth3;
    private String tip;
    //private String url;  // link on this article on web site //TODO: ??

    /**
     * Full constructor
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
     * @param tip
     */
    public BaterkaText(String coordinates, String scripture, String title,
                       String author, String imageUrl, String text,
                       String quote, String quoteAuthor, String depth1,
                       String depth2, String depth3, String tip) {
        this.coordinates = coordinates;
        this.scripture = scripture;
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.text = text;
        this.quote = quote;
        this.quoteAuthor = quoteAuthor;
        this.depth1 = depth1;
        this.depth2 = depth2;
        this.depth3 = depth3;
        this.tip = tip;
    }// end of full constructor

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

    public String getQuoteAuthor() {
        return quoteAuthor;
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

    public String getTip() {
        return tip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(coordinates);
        parcel.writeString(scripture);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(imageUrl);
        parcel.writeString(text);
        parcel.writeString(quote);
        parcel.writeString(quoteAuthor);
        parcel.writeString(depth1);
        parcel.writeString(depth2);
        parcel.writeString(depth3);
        parcel.writeString(tip);
    }

    /**
     * Constructor to restore from parcelable
     * @param input
     */
    public BaterkaText(Parcel input) {
        coordinates = input.readString();
        scripture = input.readString();
        title = input.readString();
        author = input.readString();
        imageUrl = input.readString();
        text = input.readString();
        quote = input.readString();
        quoteAuthor = input.readString();
        depth1 = input.readString();
        depth2 = input.readString();
        depth3 = input.readString();
        tip = input.readString();
    }

    public static final Parcelable.Creator<BaterkaText> CREATOR
            = new Parcelable.Creator<BaterkaText>() {

        public BaterkaText createFromParcel(Parcel in) {
            return new BaterkaText(in);
        }

        public BaterkaText[] newArray(int size) {
            return new BaterkaText[size];
        }
    };
} //end class BaterkaText
