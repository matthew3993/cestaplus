package bc.cestaplus.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Matej on 28.2.2015.
 */
public class ArticleObj
    implements Parcelable{

    // veci do itemListu
    private String title;       // nadpis v listView
    private String short_text; // popis zobrazeny v listView
    private int ImageID;
    private String imageUrl;

    private Date pubDate;       // datum a cas vydania                  // kvoli notifikáciam a aktualizacii
    private String section;        // typ / druh clanku, section           // kvoli notifikaciam len na vybranu rubriku a nadpisu v ArticleActivity_OtherWay

    private String ID;             // id clanku                            // kvoli nacitaniu textu konkretneho clanku
    private boolean locked;     // ci ide o zamknuty clanok alebo nie
    //private String link;        // odkaz na dany clanok na webe

    // private String text;        // vnutro clanku

    /**
     * NA TESTOVANIE
     * @param title
     * @param short_text
     * @param section
     */
    public ArticleObj(String title, String short_text, int imageID, String section) {
        this.title = title;
        this.short_text = short_text;
        this.ImageID = imageID;
        this.section = section;
    }

    /**
     *  PLNY KONSTRUKTOR
     */
    public ArticleObj(String title, String short_text, String imageUrl, Date pubDate, String section, String ID, boolean locked) {
        this.title = title;
        this.short_text = short_text;
        this.imageUrl = imageUrl;
        this.pubDate = pubDate;
        this.section = section;
        this.ID = ID;
        this.locked = locked;
    }

    /**
     * Konstruktor na obnovu z parcelable
     * @param input
     */
    public ArticleObj(Parcel input){
        //pozor na poradie!!!
        title = input.readString();
        short_text = input.readString();
        imageUrl = input.readString();

        pubDate = new Date(input.readLong()); //prevod casu v milisekundach (long) na datum
        section = input.readString();

        ID = input.readString();

        if (input.readInt() == 0){
            locked = false;
        } else {
            locked = true;
        }

    }

    /**
     * bezparametricky konstruktor - vytvara prazdne vsetko
     */
    public ArticleObj() {

    }


    // ===================== GETTERY =======================================================================================================
    public String getTitle() {
        return title;
    }

    public String getShort_text() {
        return short_text;
    }

    public String getImageUrl() {
        return imageUrl;
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


    // ===================== SETTERY ==========================================================================================================
    public void setTitle(String title) {
        this.title = title;
    }

    public void setShort_text(String short_text) {
        this.short_text = short_text;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

// ===================== Ostatne metody ==========================================================================================================



    @Override
    public String toString() {
        return "ArticleObj{" +
                "title='" + title + '\'' +
                ", short_text='" + short_text + '\'' +
                ", ImageUrl=" + imageUrl +
                ", pubDate=" + pubDate +
                ", section='" + section + '\'' +
                ", id=" + ID +
                ", locked=" + locked +
                '}';
    }


    public int getImageID() {
        return ImageID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //pozor na poradie!!!
        dest.writeString(title);
        dest.writeString(short_text);
        dest.writeString(imageUrl);
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
            //Toast.makeText(MainActivity.context, "create from parcel :clanok", Toast.LENGTH_LONG).show();
            return new ArticleObj(in);
        }

        public ArticleObj[] newArray(int size) {
            return new ArticleObj[size];
        }
    };
} //end class ArticleObj
