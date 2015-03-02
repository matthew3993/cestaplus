package bc.cestaplus;

import java.util.Date;

/**
 * Created by Matej on 28.2.2015.
 */
public class ClanokObj {

    // veci do itemListu
    private String title;       // nadpis v itemListe
    private String description; // popis zobrazeny v listView
    private int ImageID;        // obrazok - nejakym sposobom           // akym?? - zatiaľ imageID

    private Date pubDate;       // datum a cas vydania                  // kvoli notifikáciam a aktualizacii
    private String rubrika;        // typ / druh clanku, rubrika           // kvoli notifikaciam len na vybranu rubriku a nadpisu v ClanokActivity

    private int id;             // id clanku                            // kvoli nacitaniu textu konkretneho clanku // ci pouzijem link??
    //private String link;        // odkaz na dany clanok na webe

    // private String text;        // vnutro clanku


    /**
     *  PLNY KONSTRUKTOR
     */
    public ClanokObj(String title, String description, int imageID, Date pubDate, String rubrika, int id) {
        this.title = title;
        this.description = description;
        ImageID = imageID;
        this.pubDate = pubDate;
        this.rubrika = rubrika;
        this.id = id;
    }

    /**
     * NA TESTOVANIE
     * @param title
     * @param description
     * @param imageID
     * @param rubrika
     */
    public ClanokObj(String title, String description, int imageID, String rubrika) {
        this.title = title;
        this.description = description;
        ImageID = imageID;
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


    public int getImageID() {
        return ImageID;
    }

    public String getRubrika() {
        return rubrika;
    }
} //end class ClanokObj
