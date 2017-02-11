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

import static meteo.Downloader.*;

/**
 *
 * @author karim
 */
public class Model {

    /**
     * TODO: methode de telechargement décompréssion
     *
     */

    /*
    verifier si le fichier du mois voulu existe
    verifier si le jour existe
    verifier si il y a connexion
    telecharger 
    decompresser
     */
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
}
