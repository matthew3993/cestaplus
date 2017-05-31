package sk.cestaplus.cestaplusapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matej on 27. 3. 2015.
 * Naming according to API.
 */
public class ArticleText
    implements Parcelable{

    private String short_text;
    private String author;
    private String text; // HTML code of text of article
    private String url;  // link on this article on web site

    public ArticleText(String short_text, String author, String text, String url) {
        this.short_text = short_text;
        this.author = author;
        this.text = text;
        this.url = url;
    }

    public ArticleText() {
    }

    public ArticleText(Parcel in) {
        short_text = in.readString();
        author = in.readString();
        text = in.readString();
        url = in.readString();
    }

    public String getShort_text() {
        return short_text;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(short_text);
        parcel.writeString(author);
        parcel.writeString(text);
        parcel.writeString(url);
    }

    public static final Parcelable.Creator<ArticleText> CREATOR
            = new Parcelable.Creator<ArticleText>() {

        public ArticleText createFromParcel(Parcel in) {
            return new ArticleText(in);
        }

        public ArticleText[] newArray(int size) {
            return new ArticleText[size];
        }
    };
}// end AtricleText class
