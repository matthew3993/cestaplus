package bc.cestaplus.utilities;

/**
 * Created by Matej on 25. 9. 2015.
 */
public enum Sections {

    TEMA                (0, "Téma mesiaca", "tema", false),
    STO80STUPNOV        (1, "180 stupňov", "180stupnov", true),
    NACESTE             (2,"Na ceste","naceste", true),
    RODICOVSKESKRATKY   (3,"Rodičovské skratky", "rodicovskeskratky", true),
    NAPULZE             (4,"Na pulze","napulze", true),
    UMATUSA             (5,"U Matúša","umatusa", true),
    NORMALNARODINKA     (6,"Normálna rodinka","normalnarodinka", true),
    TABULE              (7,"Tabule","tabule", true),
    ANIMAMEA            (8,"Anima Mea","animamea", true),
    KUCHYNSKATEOLOGIA   (9,"Kuchynská teológia","kuchynskateologia", true),
    KAZATELNICAZIVOT    (10,"Kazateľnica život","kazatelnicazivot", true),
    ZAHRANICAMI         (11,"Za hranicami","zahranicami", true),
    FEJTON              (12,"Fejtón", "fejton", true),
    POBOXNEBO           (13,"P.O.BOX Nebo", "poboxnebo", true),
    ZPARLAMENTU         (14,"Z parlamentu",  "zparlamentu", true),
    BATERKA             (15,"Baterka", "baterka", false);

    //atributes
    private int numID;
    private String fullName;
    private String stringID;
    private boolean needShortTemplate;

    //constructor
    Sections(int numID, String fullName, String stringID, boolean needShortTemplate) {
        this.numID = numID;
        this.fullName = fullName;
        this.stringID = stringID;
        this.needShortTemplate = needShortTemplate;
    }
}
