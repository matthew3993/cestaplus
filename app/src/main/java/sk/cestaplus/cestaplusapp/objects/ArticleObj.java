package sk.cestaplus.cestaplusapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import sk.cestaplus.cestaplusapp.extras.Constants;

/**
 * Created by Matej on 28.2.2015.
 * Naming according to API.
 */
public class ArticleObj
    implements Parcelable{

    private String title;
    private String short_text;  // shortened version of description, showed only in recycler view

    private String author;
    private String imageDefaulUrl;

    private Date pubDate;       // publish time & date
    private String section;     // rubrika

    private String ID;
    private boolean locked;
    //private String link;     // link on article on webpage

    //helper attributes
    private String imageName;
    private int imageID;

    private boolean wasErrorDimenImage; // if there was an error during loading a dimen image

    /**
     * NA TESTOVANIE
     * @param title
     * @param short_text
     * @param section
     */
    public ArticleObj(String title, String short_text, int imageID, String section) {
        this.title = title;
        this.short_text = short_text;
        this.imageID = imageID;
        this.section = section;
    }

    /**
     *  PLNY KONSTRUKTOR
     */
    public ArticleObj(String title, String short_text, String author, String imageDefaulUrl, Date pubDate, String section, String ID, boolean locked) {
        this.title = title;
        this.short_text = short_text;
        this.author = author;
        this.imageDefaulUrl = imageDefaulUrl;
        this.pubDate = pubDate;
        this.section = section;
        this.ID = ID;
        this.locked = locked;

        //parse imageName from imageDefaulUrl
        String urlWithoutName = Constants.URL_CESTA_PLUS + Constants.IMAGES + section + "/";
        this.imageName = imageDefaulUrl.replace(urlWithoutName, ""); //SOURCE: https://stackoverflow.com/questions/8694984/remove-part-of-string

        //Log.d(IMAGE_DEBUG, "Parsed image name: " + this.imageName);
    }

    /**
     * Konstruktor na obnovu z parcelable
     * @param input
     */
    public ArticleObj(Parcel input){
        //pozor na poradie!!!
        title = input.readString();
        short_text = input.readString();
        author = input.readString();
        imageDefaulUrl = input.readString();
        imageName = input.readString();

        pubDate = new Date(input.readLong()); //prevod casu v milisekundach (long) na datum
        section = input.readString();

        ID = input.readString();

        if (input.readInt() == 0){
            locked = false;
        } else {
            locked = true;
        }

    }

    public ArticleObj() {

    }

    // ===================== GETTERS =======================================================================================================
    public String getTitle() {
        return title;
    }

    public String getShort_text() {
        return short_text;
    }

    public String getAuthor() {
        return author;
    }

    public String getImageDefaulUrl() {
        return imageDefaulUrl;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getSection() {
        return section;
    }

    public String getID() {
        return ID;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean wasErrorDimenImage() {
        return wasErrorDimenImage;
    }

    // ===================== SETTERS ==========================================================================================================
    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public void setWasErrorDimenImage(boolean wasErrorDimenImage) {
        this.wasErrorDimenImage = wasErrorDimenImage;
    }

// ===================== Other methods ==========================================================================================================


    @Override
    public String toString() {
        return "ArticleObj{" +
                "title='" + title + '\'' +
                ", short_text='" + short_text + '\'' +
                ", ImageUrl=" + imageDefaulUrl +
                ", pubDate=" + pubDate +
                ", section='" + section + '\'' +
                ", id=" + ID +
                ", locked=" + locked +
                '}';
    }

    public int getImageID() {
        return imageID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // warning! use SAME order here and in constructor from Parcelable
        dest.writeString(title);
        dest.writeString(short_text);
        dest.writeString(author);
        dest.writeString(imageDefaulUrl);
        dest.writeString(imageName);
        dest.writeLong(pubDate.getTime()); //prevod datumu na cas v milisekundach (long)
        dest.writeString(section);
        dest.writeString(ID);
        if (locked){            // neexistuje dest.writeBoolean metoda
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }

    } // end writeToParcel

    public static final Parcelable.Creator<ArticleObj> CREATOR
            = new Parcelable.Creator<ArticleObj>() {

        public ArticleObj createFromParcel(Parcel in) {

            return new ArticleObj(in);
        }

        public ArticleObj[] newArray(int size) {
            return new ArticleObj[size];
        }
    };

} //end class ArticleObj
