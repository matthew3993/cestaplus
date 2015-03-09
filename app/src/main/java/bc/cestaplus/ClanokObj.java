package bc.cestaplus;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import java.util.Date;

import bc.cestaplus.activities.MainActivity;

/**
 * Created by Matej on 28.2.2015.
 */
public class ClanokObj
    implements Parcelable{

    // veci do itemListu
    private String title;       // nadpis v listView
    private String description; // popis zobrazeny v listView
    private int ImageID;
    private String imageUrl;

    private Date pubDate;       // datum a cas vydania                  // kvoli notifikáciam a aktualizacii
    private String rubrika;        // typ / druh clanku, rubrika           // kvoli notifikaciam len na vybranu rubriku a nadpisu v ClanokActivity

    private long id;             // id clanku                            // kvoli nacitaniu textu konkretneho clanku // ci pouzijem link??
    private boolean locked;     // ci ide o zamknuty clanok alebo nie
    //private String link;        // odkaz na dany clanok na webe

    // private String text;        // vnutro clanku


    /**
     *  PLNY KONSTRUKTOR
     */


    /**
     * NA TESTOVANIE
     * @param title
     * @param description
     * @param imageUrl
     * @param rubrika
     */
    public ClanokObj(String title, String description, String imageUrl, String rubrika) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.rubrika = rubrika;
    }

    public ClanokObj(String title, String description, int imageID, String rubrika) {
        this.title = title;
        this.description = description;
        ImageID = imageID;
        this.rubrika = rubrika;
    }

    public ClanokObj(Parcel input){
        //pozor na poradie!!!
        title = input.readString();
        description = input.readString();
        imageUrl = input.readString();

        pubDate = new Date(input.readLong()); //prevod casu v milisekundach (long) na datum
        rubrika = input.readString();

        id = input.readLong();
        if (input.readInt() == 0){
            locked = false;
        } else {
            locked = true;
        }

    }

    /**
     * bezparametricky konstruktor - vytvara prazdne vsetko
     */
    public ClanokObj() {

    }


    // ===================== GETTERY =======================================================================================================
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public long getId() {
        return id;
    }

    // ===================== SETTERY ==========================================================================================================
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public void setId(int id) {
        this.id = id;
    }

// ===================== Ostatne metody ==========================================================================================================




    public String getRubrika() {
        return rubrika;
    }


    @Override
    public String toString() {
        return "ClanokObj{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", ImageUrl=" + imageUrl +
                ", pubDate=" + pubDate +
                ", rubrika='" + rubrika + '\'' +
                ", id=" + id +
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
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeLong(pubDate.getTime()); //prevod datumu na cas v milisekundach (long)
        dest.writeString(rubrika);
        dest.writeLong(id);
        if (locked){            // neexistuje dest.writeBoolean metoda
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
    }

    public static final Parcelable.Creator<ClanokObj> CREATOR
            = new Parcelable.Creator<ClanokObj>() {
        public ClanokObj createFromParcel(Parcel in) {
            Toast.makeText(MainActivity.context, "create from parcel :clanok", Toast.LENGTH_LONG).show();
            return new ClanokObj(in);
        }

        public ClanokObj[] newArray(int size) {
            return new ClanokObj[size];
        }
    };
} //end class ClanokObj
