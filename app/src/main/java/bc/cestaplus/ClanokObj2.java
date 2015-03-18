package bc.cestaplus;

import android.media.Image;

import java.util.Date;

/**
 * Created by Matej on 28.2.2015.
 */
public class ClanokObj2 {
    //atributy z RSS
    private String title;       // nadpis
    private String link;        // odkaz na dany clanok na webe
    private String description; // popis zobrazeny v listView
    private Date pubDate;       // datum a cas vydania
    private Image enclosure;    // priloha obrazok - titulny obrazok
    private int ImageID;
    private String creator;     // autor

    //moje pridane atributy
    private int id;             // id clanku
    private String type;        // typ / druh clanku, rubrika
    private String text;        // vnutro clanku

    /**
     * konstruktor na nacitavanie z RSS (inicializuje len RSS atributy)
     * @param title
     * @param link
     * @param description
     * @param pubDate
     * @param enclosure
     * @param creator
     */
    public ClanokObj2(String title, String link, String description, Date pubDate, Image enclosure, String creator) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.enclosure = enclosure;
        this.creator = creator;
    }

    /**
     * plny konstruktor - inicializuje vsetky atributy
     * @param title
     * @param link
     * @param description
     * @param pubDate
     * @param enclosure
     * @param creator
     * @param id
     * @param type
     * @param text
     */
    public ClanokObj2(String title, String link, String description, Date pubDate, Image enclosure, String creator, int id, String type, String text) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.enclosure = enclosure;
        this.creator = creator;
        this.id = id;
        this.type = type;
        this.text = text;
    }

    /**
     * bezparametricky konstruktor - vytvara prazdne vsetko
     */
    public ClanokObj2() {

    }

    // testing
    public ClanokObj2(String title, String link, String description, Date pubDate, int imageID, String creator) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        ImageID = imageID;
        this.creator = creator;
    }

    // ===================== GETTERY =======================================================================================================
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public Image getEnclosure() {
        return enclosure;
    }

    public String getCreator() {
        return creator;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getImageID() {
        return ImageID;
    }


    // ===================== SETTERY ==========================================================================================================
    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public void setEnclosure(Image enclosure) {
        this.enclosure = enclosure;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

// ===================== Ostatne metody ==========================================================================================================


} //end class ArticleObj
