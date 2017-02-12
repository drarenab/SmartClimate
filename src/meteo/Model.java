/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import coordonnee.VilleTemp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.XYChart;

import static meteo.Downloader.*;

/**
 *
 * @author karim
 */
public class Model {

    /**
     *
     * @param date sous forme yyyymmjj
     */
    //manque la fonction qui telecharge toute une année si la date est du format yyyy
    public static void telechargerETdecompresser(String date) {
        //verifier si le fichier du mois existe
        if (netIsAvailable()) {
            String lastDate = getLatestAvailableDateOnFile(date);
            //System.out.println(Integer.parseInt(lastDate) +"date" +Integer.parseInt(date));
            if (lastDate == null || Integer.parseInt(lastDate.substring(6, 8)) < Integer.parseInt(date.substring(6, 8))) {
                //telechargement
                try {

                    Downloader.downLoadCsvByDate(date.substring(0, 6));
                } catch (IOException ex) {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                }
                //decompression
                DecompresserGzip(getGzipFilePathFromDate(date));
            } else {
                System.out.println("pas besoin de telecharger les données nécessaires existes déja");

            }
        } else {
            System.out.println("No connection !");
        }

    }

    /**
     *
     * @param date
     * @param stationName
     * @return observableList for Chart
     */
    public static ArrayList<VilleTemp> getListForChart(String date, String stationName) {
        int k = getIdFromNameVille(stationName);
        String t = Integer.toString(k);
        ArrayList<VilleTemp> Resultat = Downloader.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);
        return Resultat;
    }
    /**
     * 
     * @param date
     * @param stationName
     * @return ArrayList of series that are parameters to ChartLine
     * 
     */
    public static ArrayList<XYChart.Series> ConstructChart(String date, String stationName){
        ArrayList<XYChart.Series> S=new ArrayList<>();
                
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        
        ArrayList<VilleTemp> Resultat=getListForChart(date, stationName);
        
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

    public static String[] validateDate(String year,String month,String day){
     /*
        textField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
        if (!newValue) { //when focus lost
            if(!textField.getText().matches("[1-5]\\.[0-9]|6\\.0")){
                //when it not matches the pattern (1.0 - 6.0)
                //set the textField empty
                textField.setText("");
            }
        }

    });
        */
     String[] errors = null;
     if(year.length()<4) {
         errors[0]="Must be in format yyyy";
     }
     if(month.length()<2) {
         errors[1]= "Must be in format mm";
     }
     if(day.length()<2) {
         errors[2]= "Must be in format dd";
     }        
    
     if(!year.matches("(0[1-9]|[12][0-9]|3[01])")) errors[0]="Not in correct format ";
     if(!month.matches("(0[1-9]|1[012])")) errors[1]="Not in correct format ";
     if(!day.matches("((19|20)\\d\\d)")) errors[2]="Not in correct format ";
     
     return errors;
    }
    
}
