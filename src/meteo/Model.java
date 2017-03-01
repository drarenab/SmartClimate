
package meteo;

import coordonnee.VilleTemp;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import static meteo.Downloader.*;

/**
 *
 * @author karim
 */
public class Model implements Serializable{
    ArrayList<VilleTemp> data;
    
    /*
    Constructeur privé 
    methode charger 
    */
    private Model() {
        this.data =new ArrayList ();
    }
    
    public  void  chargerListe(String date, String stationName,String online) {
        if(data==null){
           data=new ArrayList();
        }
        int k = getIdFromNameVille(stationName);
        String t = Integer.toString(k);
        /*
        si la date voulu n'existe pas alors telecharger
        */
        String lastDate = getLatestAvailableDateOnFile(date);

        if (lastDate == null || Integer.parseInt(lastDate.substring(6, 8)) < Integer.parseInt(date.substring(6, 8))) {
            telechargerETdecompresser(date, online);
        }
        /*
        charger la liste par la date desiré
        */
        data = date.length()==4 ? 
                                Downloader.getDataForYearByCity(date,  t.length() == 5 ? t : '0' + t):
                                Downloader.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);
    
       } 
       
       
    
    /*****************************Partie Interface Principale*******************************/
    
    
    /*****************************Partie Interface Setting**********************************/
    
    
    /*****************************Partie Interface Comparaison******************************/
    
    
    
    
     /**
     *
     * @param date
     * @param stationName
     * @return observableList for Chart
     */
       
     public static ArrayList<VilleTemp> getListForChart(String date, String stationName) {
        int k = getIdFromNameVille(stationName);
        String t = Integer.toString(k);
        
        ArrayList<VilleTemp> Resultat = date.length()==4 ? 
                                Downloader.getDataForYearByCity(date,  t.length() == 5 ? t : '0' + t):
                                Downloader.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);
                //Downloader.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);
                

        return Resultat;
    }

    /**
     *
     * @param date
     * @param stationName
     * @return ArrayList of series that are parameters to ChartLine
     *
     */
    public static ArrayList<XYChart.Series> ConstructChart(String date, String stationName) {
        ArrayList<XYChart.Series> S = new ArrayList<>();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();

        ArrayList<VilleTemp> Resultat = getListForChart(date, stationName);
        
        for (int i = 0; i < Resultat.size(); i++) {

            series.getData().add(new XYChart.Data<>(i, Resultat.get(i).getTemperature()));
            series1.getData().add(new XYChart.Data<>(i, Resultat.get(i).getHumidite()));
            series2.getData().add(new XYChart.Data<>(i, Resultat.get(i).getNebulosite()));

        }
        S.add(series);
        S.add(series1);
        S.add(series2);
        return S;
        //return false;
    }

    /*Verification des textField Date correct*/
    /**
     *
     * @param year
     * @param month
     * @param day
     * @return Tableau d'erreur or null if any errors
     */
    public static Map validateDate(String year, String month, String day) {
        /*
        si année vide retourner null
        verifier format année , verifier format jour et mois
         */
       Map <String,String> errors=new HashMap();
       String errorYear="",errorMonth="",errorDay="";
        if (year.length() ==0) {
            
            errorYear="Year must be defined";
        } 
        else {

            if (!day.matches("(0[1-9]|[12][0-9]|3[01])?")) {
               errorDay="Day in incorrect format ";
            }
            if (month.length() != 0 & !month.matches("(0[1-9]|1[012])?")) {
                errorMonth= "Month in incorrect format ";
            }
            if (year.length() != 0 & !year.matches("((19|20)\\d\\d)")) {
               errorYear="Year in incorrect format";
            }

        }
        errors.put("Year",errorYear );
        errors.put("Month",errorMonth );
        errors.put("Day", errorDay);
        return errors;
    }
    
    
    

   
    

   
public static boolean IsOnline(String online_offline){
    return online_offline.equals("onLine");
}
}
