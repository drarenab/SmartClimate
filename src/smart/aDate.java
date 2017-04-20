/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coordonnee;

/**
 *
 * @author SEIF
 */
//verification de validité de la date!
//comparer deux dates
public class aDate {
    private String day;//dd
    private String month;//mm
    private String year;//yyyy
    private String time; // hh

    public aDate(String annee,String mois, String jour,String heure) {
        this.day = jour;
        this.month = mois;
        this.year = annee;
        this.time = heure;
    }
    public String getDate(){
        return year+month+day;
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String toString(){
        String annee,mois,jour;
        mois = ("00" + month).substring(String.valueOf(month).length());
        jour = ("00" + day).substring(String.valueOf(day).length());
        return("Le "+ jour+"/"+ mois +"/"+ year  +" a "+ Integer.parseInt(time)*3+" heure");
    }
}
