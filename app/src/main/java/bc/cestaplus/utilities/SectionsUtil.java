package bc.cestaplus.utilities;

import android.widget.Toast;

/**
 * Created by Matej on 26. 10. 2015.
 */
public class SectionsUtil {

    public static String [] getSectionsList(){
        String [] sections = {"Téma mesiaca",           //id =  0
                              "180 stupňov",            //id =  1
                              "Na ceste",               //id =  2
                              "Rodičovské skratky",     //id =  3
                              "Na pulze",               //id =  4
                              "U Matúša",               //id =  5
                              "Evanjelium podľa lotra", //id =  6
                              "Editoriál",              //id =  7
                              "Normálna rodinka",       //id =  8
                              "Kuchynská teológia",     //id =  9
                              "Tabule",                 //id =  10
                              "Anima Mea",              //id =  11
                              "Kazateľnica život",      //id =  12
                              "Za hranicami",           //id =  13
                              "Fejtón",                 //id =  14
                              "P.O.BOX Nebo",           //id =  15
                              "Z parlamentu",           //id =  16
                              "Baterka"};               //id =  17

        return sections;
    }//end getSectionsList(){

    public static String translateSectionId(int sectionId){
        switch (sectionId){
            case  0: return "tema";
            case  1: return "180stupnov";
            case  2: return "naceste";
            case  3: return "rodicovskeskratky";
            case  4: return "napulze";
            case  5: return "umatusa";
            case  6: return "evanjeliumpodlalotra";
            case  7: return "editorial";
            case  8: return "normalnarodinka";
            case  9: return "kuchynskateologia";
            case 10: return "tabule";
            case 11: return "animamea";
            case 12: return "kazatelnicazivot";
            case 13: return "zahranicami";
            case 14: return "fejton";
            case 15: return "poboxnebo";
            case 16: return "zparlamentu";
            case 17: return "baterka";
            default: return "all"; //in case of some error will return all articles
        } //end switch
    }//end translateSectionId

    public static String getSectionTitle(String section) {
        String title = "";

        switch (section) {
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
                title = "U matúša";
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
