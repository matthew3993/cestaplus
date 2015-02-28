package bc.cestaplus;

import android.media.Image;
import java.util.Date;

/**
 * Created by Matej on 28.2.2015.
 */
public class ClanokObj {

    private String title;       // nadpis
    private int id;             // id clanku                            // kvoli nacitaniu textu konkretneho clanku // ci pouzijem link??
    //private String link;        // odkaz na dany clanok na webe
    private String description; // popis zobrazeny v listView
    private Date pubDate;       // datum a cas vydania                  // kvoli aktualizacii
    //private int ImageID;      // obrazok - nejakym sposobom           // akym??

    // private String type;        // typ / druh clanku, rubrika        // ?? bude treba??
    // private String text;        // vnutro clanku


    /**
     * bezparametricky konstruktor - vytvara prazdne clanky
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

    public Date getPubDate() {
        return pubDate;
    }

    public int getId() {
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


} //end class ClanokObj
