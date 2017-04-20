/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abstraction;

import coordonnee.DataCity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ProgressBar;

/**
 *
 * @author SEIF
 */
public interface Model {

    
    public ArrayList<String> getYearExists();
            
    public ArrayList<String> getMonthsExistsForYear(String year);
    
    /**
     *
     * @param date
     * @param stationName
     * @return ArrayList of series that are parameters to ChartLine
     *
     */
    boolean constructChartAffichage(boolean onlineMode,String date, String stationName, 
                AreaChart<Number, Number> AfficheTemp, 
                AreaChart<Number, Number> AfficheHum, 
                AreaChart<Number, Number> AfficheNebul
                
    );

    /**
     *
     * @param date
     * @param stationName
     * @return ArrayList of series that are parameters to ChartLine
     *
     */
    boolean constructChartComparaison(boolean onlineMode,String date1, String date2, String stationName, 
            LineChart<Number, Number> lineCharttemp,
            LineChart<Number, Number> lineCharthum, 
            LineChart<Number, Number> lineChartnebul);

    /**
     * Cette methode Donne les donnée qui correspond a une date dans une liste,
     * ,n
     *
     * @param date la date des donner quand veux recuperer dans la list : Si
     * yyyymm fournit elle donne tout les donner du moi , Si yyyymmjj elle donne
     * les donner du jour jj,
     * @param cityId la ville des donner quand veux recuperer dans la list, si
     * elle contient "all" la mthode retourne les donner de tout les villes
     * @return une arrayList de type DataCity qui contient les donner demander
     */
    ArrayList<DataCity> getDataForDateByCity(String date, String cityId);

    ArrayList<DataCity> getDataForYearByCity(String date, String cityId);

    /**
     * This method returns the latest available data localy
     *
     * @return latest available data that we have localy if exists null if no
     * data found localy
     */
    ArrayList<DataCity> getLatestAvailableData();

    /**
     * Cette methode retourne la date des donner la plus recente inclut dans un
     * fichier
     *
     * @param date sous la forme de yyyymm correspond au nom du fichier qu'on
     * veux chercher dedans
     * @return la date la plus recente dans le fichier qui correspond a @date
     */
    String getLatestAvailableDateOnFile(String date);

    /**
     *
     * @param date
     * @param stationName
     * @return observableList for Chart
     */
    ArrayList<DataCity> getListForChart(String date, String stationName);

    //*********************************PUBLIC SECTION ********************************************************//
    /**
     * this method returns for a given year all month files that doesn't exist
     * EX: inside folder 2014 if we have all months files except 201401.csv,
     * then method will return it
     *
     * @param year corresponds to the year folder we'll look at
     * @return a list of missed months
     */
    ArrayList<String> getMissedMonthsFiles(String year);
    
    public void DisplayAlertToImport() throws IOException;
    
    public boolean downloadAndUncompress(String date) throws IOException;
            
    /**
     * Method classique qui retourne pour un mois donner le dernier jour de ce
     * moi EX: le mois JUIN(06) il contient 30 jours
     *
     * @return le nombre de jour de ce mois
     */
    int getNumberDaysOfMonth(int year, int month);

    /**
     * une methode qui prend en parametre un fichier de donnéer d'un mois sous
     * forme de yyyymm EX: 201405 et dis si ce fichier est a jour (contient tout
     * les donnée)
     *
     * @param month
     * @return TRUE si le fichier est a jour FALSE sinon
     */
    boolean isUpdatedMonth(String date);

    /**
     * *******************KARIM*************************
     */
    /**
     * Cette Methode retourne la date exacte des donnée les plus recents
     * yyyymmjjhh
     *
     * @param date sous la forme de yyyymm
     * @return date sous form yyyymmjjhh
     */
    double netIsAvailable();

    /**
     * Verification des textField Date correct
     *
     * @param year
     * @param month
     * @param day
     * @return Tableau d'erreur or null if any errors
     */
    Map validateDate(String year, String month, String day);

    /**
     * Verification que la date donner ne doit pas depasser la date courante
     *
     * @return true if valide , false if not
     */
    boolean validateNotFuture(String year, String month, String day);
    
}
