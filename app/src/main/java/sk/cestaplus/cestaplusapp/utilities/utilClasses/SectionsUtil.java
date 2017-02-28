package sk.cestaplus.cestaplusapp.utilities.utilClasses;

/**
 * Created by Matej on 26. 10. 2015.
 */
public class SectionsUtil {

    public static String getSectionTitle(String sectionId) {
        String title = " ";

        switch (sectionId) {
            case "clanok": {
                title = "Článok";
                break;
            }
            case "tema": { //id = 0
                title = "Téma mesiaca";
                break;
            }
            case  "180stupnov":{ //id = 1
                title = "180 stupňov";
                break;
            }
            case "naceste":{ //id = 2
                title = "Na ceste";
                break;
            }
            case "rodicovskeskratky":{ //id = 3
                title = "Rodičovské skratky";
                break;
            }
            case "napulze":{ //id = 4
                title = "Na pulze";
                break;
            }
            case "umatusa":{ //id = 5
                title = "U Matúša";
                break;
            }
            case "evanjeliumpodlalotra":{ //id = 6
                title = "Evanjelium podľa lotra";
                break;
            }
            case "editorial":{ //id = 7
                title = "Editoriál";
                break;
            }
            case "normalnarodinka": { //id = 8
                title = "Normálna rodinka";
                break;
            }
            case "kuchynskateologia": { //id = 9
                title = "Kuchynská teológia";
                break;
            }
            case "tabule": { //id = 10
                title = "Tabule";
                break;
            }
            case "animamea": { //id = 11
                title = "Anima mea";
                break;
            }
            case "kazatelnicazivot":{ //id = 12
                title = "Kazateľnica život";
                break;
            }
            case "zahranicami":{ //id = 13
                title = "Za hranicami";
                break;
            }
            case "fejton":{ //id = 14
                title = "Fejtón";
                break;
            }
            case "poboxnebo":{ //id = 15
                title = "P. O. Box Nebo";
                break;
            }
            case "zparlamentu": { //id = 16
                title = "Z parlamentu";
                break;
            }
            case "baterka": { // id =  17
                title = "Baterka";
                break;
            }
            default:
                title = "Článok";
        }//end switch
        return title;
    }//end getSectionTitle()

    public static boolean needsShortTemplate(String section){
        switch (section) {
            case "clanok":
            case "tema":{
                return false;
            } //end case full template
            case "180stupnov":
            case "naceste":
            case "rodicovskeskratky":
            case "napulze":
            case "umatusa":
            case "evanjeliumpodlalotra":
            case "editorial":
            //case "":
            case "normalnarodinka":
            case "kuchynskateologia":
            case "tabule":
            case "animamea":
            case "kazatelnicazivot":
            case "zahranicami":
            case "fejton":
            case "poboxnebo":
            case "zparlamentu": {
                return true;
            } //end case skrátená šablóna

            default: {
                return false;
            } //end defaut - full template

        }//end switch
    }//end needsShortTemplate()
}//end class SectionsUtil
