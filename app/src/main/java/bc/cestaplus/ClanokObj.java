package bc.cestaplus;

import java.util.Date;

/**
 * Created by Matej on 28.2.2015.
 */
public class ClanokObj {

    // veci do itemListu
    private String title;       // nadpis v listView
    private String description; // popis zobrazeny v listView
    //private int ImageID;        // obrazok - nejakym sposobom           // akym?? - zatiaľ imageID
    private String imageUrl;

    private Date pubDate;       // datum a cas vydania                  // kvoli notifikáciam a aktualizacii
    private String rubrika;        // typ / druh clanku, rubrika           // kvoli notifikaciam len na vybranu rubriku a nadpisu v ClanokActivity

    private int id;             // id clanku                            // kvoli nacitaniu textu konkretneho clanku // ci pouzijem link??
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
} //end class ClanokObj
