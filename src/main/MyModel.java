/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 * TODO yearMode,MonthMode,DayMode
 * <p>
 * if data doesn't exist or not updated we download it , and it depends if it is
 * only a month , or a whole year !
 */

import coordonnee.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.scene.control.TableView;
import smart.*;

import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utilitaire.Utilitaire;

import javax.rmi.CORBA.Util;

/**
 * @author karim
 */
public class MyModel {

    /**
     * *****************************************PRIVATE
     * SECTION*******************************************************
     */
    private Map<Integer, Station> stationList;
    //for singelton
    private static volatile MyModel instance = null;
    //private Map<Integer, Ville> villes;

    private MyModel() {
        constructMapVilles();
    }

    /**
     * Construire la map qui contient la correspondance idVille => nomVille
     *
     * @return true if succesfully charged false if not
     */
    private boolean constructMapVilles() {
        FileReader fr = null;
        String line;
        Station station;
        int id, x, y;
        String city;

        try {
            File file = new File(Configuration.CITY_FILE_NAME);
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            line = br.readLine();

            stationList = new HashMap();

            while (line != null) {
                id = Integer.parseInt(line.split(";")[0]);
                city = line.split(";")[1];
                x = Integer.parseInt(line.split(";")[2]);
                y = Integer.parseInt(line.split(";")[3]);
                station = new Station(
                        id,
                        city,
                        new Point(x, y)
                );
                stationList.put(id, station);
                line = br.readLine();
            }

            br.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private boolean addReleve(int station, int annee, int mois, int jour, int ordre, float temperature, float humidite, float nebulosite) {
        Releve releve = new Releve(ordre, temperature, humidite, nebulosite);
        return (stationList
                .get(station)
                .getAndCreateAnnee(annee)
                .getAndCreateMois(mois)
                .getAndCreateJour(jour)
                .getAndCreateReleve(ordre, releve) == null);
    }

    private boolean downloadAndUncompress(String date) throws IOException {
        return (Utilitaire.downLoadCsvByDate(date)
                && Utilitaire.decompresserGzip(Utilitaire.getGzipFilePathFromDate(date)));
    }

    private String getStationNameFromId(int stationId) {
        return stationList.get(stationId).getNom();
    }

    private String getStationIdFromName(String stationName) {
        for (Station station : stationList.values()) {
            if (station.getNom().equals(stationName)) {
                return station.getId();
            }
        }
        return null;
    }

    /**
     * Return list of data for a station on a certain date This Method is used
     * by : -ConstructChartAffichage() -ConstructChartComparaison()
     * -ConstructTableView()
     *
     * @param stationName name of station given by the user
     *                    <<<<<<< HEAD
     * @param year        year of the data that will be returned
     * @param month       month of data that will be returned
     * @param day         day of data that will be returned
     * @param mode        2 get data for Year Only
     *                    1 get data for Year AND Month
     *                    0 get data for year AND Month AND Day
     *                    01 get data for year AND month AND day for ALL stations
     * @return list of data
     */
    public List<DataBean2> getData(String stationName,
                                   String year,
                                   String month,
                                   String day,
                                   int mode,
                                   int MinOrMaxOrMoy,
                                   boolean importOrNot
    ) throws IOException {

        Map<Integer, Mois> missingMonths;
        Map<Integer, Jour> missingDays;
        Map<Integer, Releve> missingReleve;
        List<DataBean2> listReleves = new ArrayList<DataBean2>();
        String stationIdTemp = getStationIdFromName(stationName);

        String stationId = ("00000" + stationIdTemp).substring(String.valueOf(stationIdTemp).length());
        System.out.println("stationId: " + stationId + " temp:" + stationIdTemp);

        String ordre;
        boolean result;
        int yearInt, monthInt, dayInt, stationIdInt, ordreInt, jourIdInt, moisIdInt, anneeIdInt;
        String jourId, moisId, anneeId;
        stationIdInt = Integer.parseInt(stationId);
        int x, y;
        x = stationList.get(stationIdInt).getPointObj().getX();
        y = stationList.get(stationIdInt).getPointObj().getY();
        switch (mode) {
            /**
             * *******************************************************************************************************
             */
            case 0://jour pas besoin de faire la moyenne
                dayInt = Integer.parseInt(day);
                monthInt = Integer.parseInt(month);
                yearInt = Integer.parseInt(year);
                stationIdInt = Integer.parseInt(stationId);
                missingReleve = stationList.get(stationIdInt)
                        .getAndCreateAnnee(yearInt)
                        .getAndCreateMois(monthInt)
                        .getAndCreateJour(dayInt)
                        .getMissingReleves(yearInt, monthInt);
                if (missingReleve.size() > 0) {
                    // si on a trouver des relevés qui manque, on essaye de les uploader depuis les fichiers en local
                    //ATTENTION LE CAST STRING INT
                    System.out.println("Missing relevées found , trying to upload them from local files size=" + missingReleve.size());
                    for (Releve releve : missingReleve.values()) {
                        ordreInt = releve.getOrdre() * 3;
                        ordre = ("00" + ordreInt).substring(String.valueOf(ordreInt).length());
                        getDataForDateByCity(
                                year
                                        + month
                                        + day
                                        + ordre,
                                stationId
                        );
                    }

                    missingReleve.clear();

                    missingReleve = stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getAndCreateMois(monthInt)
                            .getAndCreateJour(dayInt)
                            .getMissingReleves(yearInt, monthInt);
                    /*
                    stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getAndCreateMois(monthInt)
                            .getAndCreateJour(dayInt)
                            .showAll();
                     */

                    if (missingReleve.size() > 0) {
                        /**
                         * MANQUE SI APP EST EN LIGNE*
                         */

                        if (Utilitaire.netIsAvailable() != -1) {
                            //si encore on a trouver des relevés qui manque , et si app est enligne on lance le telechargement
                            System.out.println("Still have missing relevées , trying to download them! size=" + missingReleve.size());
                            downloadAndUncompress(String.valueOf(year) + String.valueOf(month));

                            System.out.println("Download is done , trying to upload local files ");
                        } else {
                            if( importOrNot){
                                DisplayAlertToImport();
                            }
                            System.out.println("No need to import");
                        }

                        for (Releve releve : missingReleve.values()) {
                            ordreInt = releve.getOrdre() * 3;
                            ordre = ("00" + ordreInt).substring(String.valueOf(ordreInt).length());
                            getDataForDateByCity(
                                    year
                                            + month
                                            + day
                                            + ordre,
                                    stationId
                            );
                        }

                        missingReleve = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getAndCreateMois(monthInt)
                                .getAndCreateJour(dayInt)
                                .getMissingReleves(yearInt, monthInt);

                        if (missingReleve.size() > 0) {
                            //si encore on a des relevés qui manque
                            System.out.println("Attention - Not completed data, returning data even though ! size=" + missingReleve.size());
                        }
                    }

                }

                System.out.println("Everything looks good, returning data now");
                listReleves = stationList.get(stationIdInt)
                        .getAndCreateAnnee(yearInt)
                        .getAndCreateMois(monthInt)
                        .getAndCreateJour(dayInt)
                        .getAllReleves(stationName,stationIdInt, yearInt, monthInt,x,y);

                System.out.println("List relevés");
                for (DataBean2 dataBean2 : listReleves) {
                    System.out.println("station:" + dataBean2.getIdStation() + "ordre: " + dataBean2.getDate().getTime() + " temperature:" + dataBean2.getTemperature());
                }

                if (listReleves.isEmpty()) {
                    System.out.println("Attention, Null data found  ! ");
                }
                return listReleves;

            /**
             * *******************************************************************************************************
             */
            case 1:// un mois on fait la moyenne ou le min ou le max
                monthInt = Integer.parseInt(month);
                yearInt = Integer.parseInt(year);
                missingDays = stationList.get(stationIdInt)
                        .getAndCreateAnnee(yearInt)
                        .getAndCreateMois(monthInt)
                        .getMissingData(yearInt);

                if (missingDays.size() > 0) {
                    // si on a trouver des jours qui manque, on essaye de les uploader depuis les fichiers en local
                    //ATTENTION LE CAST STRING INT
                    System.out.println("Missing days found , trying to upload them from local files size=" + missingDays.size());
                    for (Jour jour : missingDays.values()) {
                        jourIdInt = jour.getId();
                        jourId = ("00" + jourIdInt).substring(String.valueOf(jourIdInt).length());
                        getDataForDateByCity(
                                year
                                        + month
                                        + jourId,
                                stationId
                        );
                    }

                    missingDays.clear();

                    missingDays = stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getAndCreateMois(monthInt)
                            .getMissingData(yearInt);

                    /*
                    stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getAndCreateMois(monthInt)
                            .showAll();
                     */
                    if (missingDays.size() > 0) {
                        /**
                         * MANQUE SI APP EST EN LIGNE*
                         */
                        if (Utilitaire.netIsAvailable() != -1) {
                            //si encore on a trouver des jours qui manque , et si app est enligne on lance le telechargement
                            System.out.println("Still have missing days , trying to download them! size=" + missingDays.size());
                            downloadAndUncompress(String.valueOf(year) + String.valueOf(month));
                        } else {
                            if( importOrNot){
                                DisplayAlertToImport();
                            }
                            System.out.println("No need to import");
                        }
                        System.out.println("Download is done , trying to upload local files ");
                        for (Jour jour : missingDays.values()) {
                            jourIdInt = jour.getId();
                            jourId = ("00" + jourIdInt).substring(String.valueOf(jourIdInt).length());
                            getDataForDateByCity(
                                    year
                                            + month
                                            + jourId,
                                    stationId
                            );
                        }

                        missingDays = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getAndCreateMois(monthInt)
                                .getMissingData(yearInt);

                        if (missingDays.size() > 0) {
                            //si encore on a des jours qui manque
                            System.out.println("Attention - Not completed data, returning data even though ! size=" + missingDays.size());
                            /*for (Jour jour : missingDays.values()) {
                                System.out.println(jour.getId());
                            }
                             */
                        }

                    }
                }

                System.out.println("Everything looks good, returning data now");
//                listReleves = stationList.get(stationIdInt)
//                        .getAndCreateAnnee(yearInt)
//                        .getAndCreateMois(monthInt)
//                        .getAllReleves(stationIdInt, yearInt);
//                Array
                if (MinOrMaxOrMoy == 0) {//moy
                    listReleves = stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getAndCreateMois(monthInt)
                            .getMoyennesParJour(stationName,stationIdInt, year,x,y);
                } else {
                    if (MinOrMaxOrMoy == 1) {//min
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getAndCreateMois(monthInt)
                                .getMinParMois(stationName,stationIdInt, year,x,y);
                    } else {//max
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getAndCreateMois(monthInt)
                                .getMaxParMois(stationName,stationIdInt, year,x,y);
                    }
                }

                System.out.println("List jours .. size:" + listReleves.size());
                for (DataBean2 dataBean2 : listReleves) {
                    System.out.println("station:" + dataBean2.getIdStation() + "ordre: " + dataBean2.getDate().getTime() + " temperature:" + dataBean2.getTemperature());
                }
                if (listReleves.isEmpty()) {
                    System.out.println("Attention, Null data found  ! ");
                }
                return listReleves;

            /**
             * *******************************************************************************************************
             */
            case 2:
                yearInt = Integer.parseInt(year);
                missingMonths = stationList.get(stationIdInt)
                        .getAndCreateAnnee(yearInt)
                        .getMissingData();

                if (missingMonths.size() > 0) {
                    // si on a trouver des mois qui manque, on essaye de les uploader depuis les fichiers en local
                    //ATTENTION LE CAST STRING INT
                    System.out.println("Missing months found , trying to upload them from local files size=" + missingMonths.size());
                    for (Mois mois : missingMonths.values()) {
                        moisIdInt = mois.getId();
                        moisId = ("00" + moisIdInt).substring(String.valueOf(moisIdInt).length());
                        getDataForDateByCity(
                                year + moisId,
                                stationId
                        );
                    }

                    missingMonths.clear();

                    missingMonths = stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getMissingData();

                    stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .showAll();

                    if (missingMonths.size() > 0) {
                        /**
                         * MANQUE SI APP EST EN LIGNE*
                         */
                        if (Utilitaire.netIsAvailable() != -1) {
                            //si encore on a trouver des mois qui manque , et si app est enligne on lance le telechargement
                            System.out.println("Still have missing mois , trying to download them! size=" + missingMonths.size());

                            for (Mois mois : missingMonths.values()) {
                                moisIdInt = mois.getId();
                                moisId = ("00" + moisIdInt).substring(String.valueOf(moisIdInt).length());
                                downloadAndUncompress(year + moisId);
                            }

                            System.out.println("Download is done , trying to upload local files ");
                        } else {
                            if( importOrNot){
                                DisplayAlertToImport();
                            }
                            System.out.println("No need to import");
                        }
                        for (Mois mois : missingMonths.values()) {
                            moisIdInt = mois.getId();
                            moisId = ("00" + moisIdInt).substring(String.valueOf(moisIdInt).length());
                            getDataForDateByCity(
                                    year
                                            + moisId,
                                    stationId
                            );
                        }

                        missingMonths = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getMissingData();

                        if (missingMonths.size() > 0) {
                            //si encore on a des mois qui manque
                            System.out.println("Attention - Not completed data,returning data even though ! size=" + missingMonths.size());
                            /*System.out.println("missing months :");
                            for (Mois mois : missingMonths.values()) {
                                System.out.println(mois.getId());
                            }*/

                        }

                    }
                }

                System.out.println("Everything looks good, returning data now");
                listReleves = stationList.get(stationIdInt)
                        .getAndCreateAnnee(yearInt)
                        .getAllReleves(stationName,stationIdInt,x,y);

                if (MinOrMaxOrMoy == 0) {//moy
                    listReleves = stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getMoyenneParMois(stationName,stationIdInt,x,y);
                } else {
                    if (MinOrMaxOrMoy == 1) {//min
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getMinParMois(stationName,stationIdInt,x,y);
                    } else {//max
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getMaxParMois(stationName,stationIdInt,x,y);
                    }
                }

                System.out.println("List mois .. size:" + listReleves.size());
                for (DataBean2 dataBean2 : listReleves) {
                    System.out.println("station:" + dataBean2.getIdStation() + "ordre: " + dataBean2.getDate().getTime() + " temperature:" + dataBean2.getTemperature());
                }
                if (listReleves.isEmpty()) {
                    System.out.println("Attention , null data found ! ");
                }
                return listReleves;
        }

        return null;
    }


    /**********************************************************************************************************/


/**
 * PUBLIC SECTION
 * This method is a part if singleton design pattern that willl alow the other classes to instantiate This class
 * instead of the New keyword
 *
 * @return an instance of This class
 */

    /**
     * This method is a part if singleton design pattern that willl alow the other classes to instantiate This class
     * instead of the New keyword
     *
     * @return an instance of This class
     */
    public static MyModel getInstance() {
        if (instance == null) {
//premiere demande d'instanciation
//synchronized = laisser passer les threads (demandes) un par un
            synchronized (MyModel.class) {
                instance = new MyModel();
            }
        }
        return instance;
    }

    /**
     * This method returns station names
     *
     * @return List of String that contains all station names
     * =======
     * /**
     * *****************************************PUBLIC SECTION
     * *******************************************************
     * >>>>>>> 98d7682377e66f44011b9f0b8c3f877b069c100c
     */
    public List<String> getStationNames() {
        List<String> list = new ArrayList<>();
        for (Station station : stationList.values()) {
            list.add(station.getNom());
        }
        return list;
    }

    /**
     * this method constructs Affichage chart lists with the asked data
     *
     * @param station      station name
     * @param year         year given by the user
     * @param month        month given by the user
     * @param day          day given by the user
     * @param AfficheTemp  temperature observable list that will be written on
     * @param AfficheHum   humidite observable list that will be written on
     * @param AfficheNebul nebulosite observable list that will be written on
     * @return TRUE if charts were constructed successfully
     * FALSE if not
     * @throws IOException
     */
    public boolean constructChartAffichage(String station,
            String year,
            String month,
            String day,
            AreaChart<Number, Number> AfficheTemp,
            AreaChart<Number, Number> AfficheHum,
            AreaChart<Number, Number> AfficheNebul,
            int MinOrMaxOrMoy
            ,boolean importOrNot
    ) throws IOException {

        int mode = whichMode(year, month, day);

        ArrayList<XYChart.Series> S = new ArrayList<>();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();

        ArrayList<DataBean2> Resultat = (ArrayList<DataBean2>) getData(station, year, month, day, mode, MinOrMaxOrMoy, importOrNot);

        if (Resultat == null) {
            return false;
        }
        /*
            a partir de date : si c une année alors afficher la moyenne de chaque mois
                                si c un mois alors afficher les 30 jours
                                si c un jours afficher les 8 valeurs
         */

        for (int i = 0; i < Resultat.size(); i++) {

            series.getData().add(new XYChart.Data<>(i+1, Resultat.get(i).getTemperature()));
            series1.getData().add(new XYChart.Data<>(i+1, Resultat.get(i).getHumidite()));
            series2.getData().add(new XYChart.Data<>(i+1, Resultat.get(i).getNebulosite()));

        }
        S.add(series);
        S.add(series1);
        S.add(series2);

        if (S != null) {
            AfficheTemp.getData().setAll(S.get(0));
            AfficheHum.getData().setAll(S.get(1));
            AfficheNebul.getData().setAll(S.get(2));
            System.out.println("Chart constructed succefully ");
            return true;
        } else {
            System.out.println("Opps ,Chart cannot be constructed please submit a bug report ");
            return false;
        }
    }

    /**
     * this method constructs Comparaison chart lists using asked data
     *
     * @param station        station name
     * @param year1          given by the user for date 1
     * @param month1         given by the user  for date 1
     * @param day1           given by the user  for date1
     * @param year2          given by the user  for date2
     * @param month2         given by the user  for date2
     * @param day2           given by the user for date2
     * @param lineCharttemp  the observable list for temperature data that will be written on
     * @param lineCharthum   the observable list for humidite data that will be written on
     * @param lineChartnebul the observable list for nebelusite data that will be written on
     * @return TRUE IF Charts were constructed successfully b
     * FALSE if not
     * @throws IOException
     */
    public boolean constructChartComparaison(String station,
                                             String year1,
                                             String month1,
                                             String day1,
                                             String year2,
                                             String month2,
                                             String day2,
                                             LineChart<Number, Number> lineCharttemp,
                                             LineChart<Number, Number> lineCharthum,
                                             LineChart<Number, Number> lineChartnebul,
                                             int MinOrMaxOrMoy
    ) throws IOException {

        int mode = whichMode(year1, month1, day1);

        ArrayList<XYChart.Series> S1 = new ArrayList<>();
        ArrayList<XYChart.Series> S2 = new ArrayList<>();

        XYChart.Series<Number, Number> series0 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series01 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series02 = new XYChart.Series<>();

        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series11 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series12 = new XYChart.Series<>();

        ArrayList<DataBean2> Resultat1 = (ArrayList<DataBean2>) getData(station, year1, month1, day1, mode, MinOrMaxOrMoy, true);
        ArrayList<DataBean2> Resultat2 = (ArrayList<DataBean2>) getData(station, year2, month2, day2, mode, MinOrMaxOrMoy, true);
        if (Resultat1 == null || Resultat2 == null) {
            return false;
        }
        series0.setName(Resultat1.get(0).getDate().getYear());
        series01.setName(Resultat1.get(0).getDate().getYear());
        series02.setName(Resultat1.get(0).getDate().getYear());
        
        series1.setName(Resultat2.get(0).getDate().getYear());
        series11.setName(Resultat2.get(0).getDate().getYear());
        series12.setName(Resultat2.get(0).getDate().getYear());
        for (int i = 0; i < Resultat1.size(); i++) {

            series0.getData().add(new XYChart.Data<>(i, Resultat1.get(i).getTemperature()));
            
            series01.getData().add(new XYChart.Data<>(i, Resultat1.get(i).getHumidite()));
            series02.getData().add(new XYChart.Data<>(i, Resultat1.get(i).getNebulosite()));

        }

        S1.add(series0);
        S1.add(series01);
        S1.add(series02);

        for (int i = 0; i < Resultat2.size(); i++) {

            series1.getData().add(new XYChart.Data<>(i, Resultat2.get(i).getTemperature()));
            series11.getData().add(new XYChart.Data<>(i, Resultat2.get(i).getHumidite()));
            series12.getData().add(new XYChart.Data<>(i, Resultat2.get(i).getNebulosite()));

        }

        S2.add(series1);
        S2.add(series11);
        S2.add(series12);

        if (S1 != null && S2 != null) {
            lineCharttemp.getData().setAll(S1.get(0), S2.get(0));
            lineCharthum.getData().setAll(S1.get(1), S2.get(1));
            lineChartnebul.getData().setAll(S1.get(2), S2.get(2));
            System.out.println("Chart constructed succefully ");
            return true;
        } else {
            System.out.println("Opps ,Chart cannot be constructed please submit a bug report ");
            return false;
        }

        //return false;


    }

    /**
     * This method constructs a table view with the data asked for
     *
     * @param station   station given by the user
     * @param year      year as given by the user
     * @param month     month as given by the user
     * @param day       month as given by the user
     * @param tableView the tableView in which we are going to insert Data
     * @return TRUE if tableView was constructed successfuly
     * FALSE if not
     * @throws IOException
     */
    public boolean constructTableView(String station,
            String year,
            String month,
            String day,
            TableView<DataBean> tableView,
            int MinOrMaxOrMoy,
            boolean importOrNot

    ) throws IOException {
        int mode = whichMode(year, month, day);

        ArrayList<DataBean2> resultat = (ArrayList<DataBean2>) getData(station, year, month, day, mode, MinOrMaxOrMoy, importOrNot);
        ArrayList<DataBean> listDataBean = new ArrayList<DataBean>();
        if (resultat != null) {
            for (DataBean2 dataBean2 : resultat) {
                listDataBean.add(dataBean2.toDataBean());
            }
        }

        tableView.getItems().setAll(listDataBean);
        return true;
    }


    public List<DataBean2> getLatestDataForGraphicMap() throws IOException {
//        int[] currentDate = Utilitaire.getCurrentDate();
//        String year = String.valueOf(currentDate[3]);
//
//        String month = ("00" + currentDate[2]).substring(String.valueOf(currentDate[2]).length());
//        String day = ("00" + currentDate[1]).substring(String.valueOf(currentDate[1]).length());
//
//        String stationName;
//        List<DataBean2> dataList = new ArrayList<>();
//        List<DataBean2> tempList;
//        int mode = 0;
//        DataBean2 dataBean;
//        for (Station station : stationList.values()) {
//            stationName = station.getNom();
//            tempList = getData(stationName, year, month, day, mode);
//            if (tempList.size() > 0) {
//                dataBean = tempList.get(tempList.size() - 1);
//                dataList.add(dataBean);
//            }
//            System.out.println("getting data for : " + stationName + "--" + year + "/" + month + "/" + day);
//            //           dataList.addAll(getData(stationName,year,month,day,mode));
//        }
//        return dataList;

        int[] currentDate = Utilitaire.getCurrentDate();
        int intYear = currentDate[3];
        int intMonth = currentDate[2];
        int intDay = currentDate[1];

        String year = String.valueOf(currentDate[3]);
        String month = ("00" + currentDate[2]).substring(String.valueOf(currentDate[2]).length());
        String day = ("00" + currentDate[1]).substring(String.valueOf(currentDate[1]).length());

        String stationName;
        DataBean2 dataBean;
        List<DataBean2> dataList = new ArrayList<>();
        List<DataBean2> tempList;
        if (Utilitaire.netIsAvailable() != -1)
            downloadAndUncompress(year + month);

        getDataForDateByCity(year + month + day, "all");
        for (Station station : stationList.values()) {
            dataBean = station
                    .getAndCreateAnnee(intYear)
                    .getAndCreateMois(intMonth)
                    .getAndCreateJour(intDay)
                    .getLatestReleve(station.getNom()
                            , Integer.parseInt(station.getId())
                            , intYear
                            , intMonth
                            , station.getPointObj().getX()
                            , station.getPointObj().getY());
            //  System.out.println("loop:"+station.getNom()+" id:"+station.getId());
            dataList.add(dataBean);
        }

        return dataList;
    }


    /**
     * Cette methode Donne les donnée qui correspond a une date dans une liste,
     * ,n
     *
     * @param date   la date des donner quand veux recuperer dans la list : Si
     *               yyyymm fournit elle donne tout les donner du moi , Si yyyymmjj elle donne
     *               les donner du jour jj,
     * @param cityId la ville des donner quand veux recuperer dans la list, si
     *               elle contient "all" la mthode retourne les donner de tout les villes
     * @return une arrayList de type DataCity qui contient les donner demander
     */
    public boolean getDataForDateByCity(String date
            , String cityId
    ) {
        // EX: date=20140231 (31 fevrier 2014) ==> va chercher si le dossier 2014 exist et si'il contient le fichier 201402 , et si ce dernier fichier contient
        //les données de la date demander
        //System.out.println("Recuperation des donnée de la ville => " + cityId + " et la date =>" + date);
        String fileName = Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv";
        String idVille;
        float nebu, temperature, himudite;
        aDate adate;
        int jour, mois, annee, ordre;

        //si le fichier de donnée correspondant n'existe pas
        // System.out.println("file exist" + fileName);
        if (!Utilitaire.checkIfFileExists(fileName)) {
            System.out.println("file doens't exist");
            return false;
        }

        try {
            //parcourir le fichier correspondant et chercher si la date demander jour et heure
            File dateFile = new File(fileName);
            FileReader dataFR = new FileReader(dateFile);
            BufferedReader dataBR = new BufferedReader(dataFR);
            String line, dateLine, splitedLine[];

            ArrayList<DataCity> listDonnees = new ArrayList<DataCity>();

            Pattern pattern = Pattern.compile(date + ".*");
            line = dataBR.readLine();
            Ville ville;
            //sauter la premiere ligne si elle contient un string , pour éviter les erreurs
            if (line.startsWith("numer_sta")) {
                line = dataBR.readLine();
            }
            // System.out.println("Date=" + date + " station=" + cityId);
            while (line != null) {

                //diviser la line qu'on a lu selon la regex ";" en un tableau de string
                splitedLine = line.split(";");
                //recuperer l'id de la ville (premier champ)
                idVille = splitedLine[0];
                //si c'est la ville qu'on cherche
                if (idVille.equals(cityId) || cityId.equals("all")) {
                    dateLine = splitedLine[1];
                    Matcher match = pattern.matcher(dateLine);
                    //si on a trouver la date qu'on cherche
                    if (match.find()) {
                        //System.out.println("match date=>"+dateLine);
                        // si on a bien matcher une date
                        //recuperation des données apartir du fichier
                        //System.out.print("Match found, ");
                        temperature = (float) (!splitedLine[7].equals("mq") ? Float.parseFloat(splitedLine[7]) - 273.15 : 101.0);
                        nebu = !splitedLine[14].equals("mq") ? Float.parseFloat(splitedLine[14]) : 101;
                        himudite = !splitedLine[9].equals("mq") ? Float.parseFloat(splitedLine[9]) : 101;
                        //                                 annéé                           mois                            jour                            heure
                        //adate = new aDate(splitedLine[1].substring(0, 4), splitedLine[1].substring(4, 6), splitedLine[1].substring(6, 8), splitedLine[1].substring(8, 10));

                        annee = Integer.parseInt(splitedLine[1].substring(0, 4));
                        mois = Integer.parseInt(splitedLine[1].substring(4, 6));
                        jour = Integer.parseInt(splitedLine[1].substring(6, 8));
                        ordre = Integer.parseInt(splitedLine[1].substring(8, 10));

                        //System.out.print("Match found ordre=" + ordre);
                        //noter j'ai pa mis le nom de la ville a rajouter
                        //ville = getVilleFromId(Integer.parseInt(idVille));
                        //attention si anneee or mois or jour n'existe pas !
                        addReleve(Integer.parseInt(idVille), annee, mois, jour, ordre / 3, temperature, himudite, nebu);

                        //listDonnees.add(new DataCity(ville, temperature, himudite, nebu, adate));
                    }

                }
                line = dataBR.readLine();
            }
            dataBR.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Afficher une alert permettant d'importer un fichier
     *
     * @throws IOException
     */
    public void DisplayAlertToImport() throws IOException {
        /*
        Affichage de l'alert
         */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Import Dialog");
        alert.setHeaderText("On va imported un ou plusieurs fichiers");
        alert.setContentText("Clickez sur Import pour importer les données téléchargées");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK

            //Open new dialog for import data and display it
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");

            List<File> recupp = chooser.showOpenMultipleDialog(new Stage());
            if (recupp != null) {
                for (int i = 0; i < recupp.size(); i++) {
                    /*
                    get path of file selected
                     */
                    String pathOfFile = recupp.get(i).getPath();
                    // System.out.println(pathOfFile);
                    System.out.println(recupp.get(i));
                    if (utilitaire.Utilitaire.CopyFileImported(recupp.get(i))) {
                        System.out.println("File correctly Imported !");
                    } else {
                        System.out.println("Error when try to import File selected !");
                    }

                }

            } else {
                System.out.println("File selected not in correct Format !");
            }
        } else {
            // ... user chose CANCEL or closed the dialog
            /*
            Que faire dans ce cas la ????
            
             */

            System.out.println("user select cancel ! ");

        }

    }

    /**
     * Verification des textField Date correct
     *
     * @param year
     * @param month
     * @param day
     * @return Tableau d'erreur or null if any errors
     */

    public Map validateDate(String year, String month, String day) {
        /*
        si année vide retourner null
        verifier format année , verifier format jour et mois
         */

        Map<String, String> errors = new HashMap();
        String errorYear = "", errorMonth = "", errorDay = "";

        if (year.length() == 0) {

            errorYear = "Year must be defined";
        } else {
            //verification syntaxique
            if (!day.matches("(0[1-9]|[12][0-9]|3[01])?")) {
                errorDay = "Day in incorrect format ";
            }
            if (month.length() != 0 & !month.matches("(0[1-9]|1[012])?")) {
                errorMonth = "Month in incorrect format ";
            }
            if (year.length() != 0 & !year.matches("((19|20)\\d\\d)")) {
                errorYear = "Year in incorrect format";
            }

        }
        errors.put("Year", errorYear);
        errors.put("Month", errorMonth);
        errors.put("Day", errorDay);
        return errors;
    }

    /**
     * Verification que la date donner ne doit pas depasser la date courante
     *
     * @param year
     * @param month
     * @param day
     * @return true if valide , false if not
     */
    public boolean validateNotFuture(String year, String month, String day) {
        int currentDay, currentMonth, currentYear;
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        currentDay = localTime.getDayOfMonth();
        currentMonth = localTime.getMonthValue();
        currentYear = localTime.getYear();

        if (year.length() != 0 && Integer.parseInt(year) > currentYear) {
            return false;
        } else if (year.length() != 0 && Integer.parseInt(year) == currentYear && month.length() != 0 && Integer.parseInt(month) > currentMonth) {
            return false;
        } else if (month.length() != 0 && Integer.parseInt(month) == currentMonth && day.length() != 0 && Integer.parseInt(day) > currentDay) {
            return false;
        } else {
            return true;
        }

    }




    private int whichMode(String year, String month, String day) {
        if (year.length() > 0 && month.length() > 0 && day.length() > 0)
            return 0;
        else if (year.length() > 0 && month.length() > 0)
            return 1;
        else if (year.length() > 0)
            return 2;

        else return -1; // invalid date !
    }

    public void showEveryThing() {
        Iterator it = stationList.values().iterator();
        while (it.hasNext()) {
            Station station = (Station) it.next();
            System.out.println(station.getId() + ":" + station.getNom() + "  |" + station.getPoint());
        }
    }

    /**
     * @return ArrayList contenant les dates existantes en local
     */
    public ArrayList<String> getYearExists() {
        ArrayList<String> list = new ArrayList<>();
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME);
        for (File file : file1.listFiles()) {
            list.add(file.toString().substring(8));
        }

        return list;
    }

    /**
     * @param year
     * @return arrayList contenant les mois existants en local pour une année
     * mise en paramétre
     */
    public ArrayList<String> getMonthsExistsForYear(String year) {
        ArrayList<String> list = new ArrayList<>();
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME + "/" + year);
        for (File file : file1.listFiles()) {
            list.add(file.toString().substring(8));
        }

        return list;
    }


    public void Affichage(String station,
            String year,
            String month,
            String day,
            AreaChart<Number, Number> AfficheTemp,
            AreaChart<Number, Number> AfficheHum,
            AreaChart<Number, Number> AfficheNebul,
            TableView<DataBean> tableView,
            int MinOrMaxOrMoy,
            ImageView loading) throws IOException {

        loading.setVisible(false);
        constructChartAffichage(station,
                year,
                month,
                day,
                AfficheTemp,
                AfficheHum,
                AfficheNebul,
                MinOrMaxOrMoy,
                true
        );

        constructTableView(station,
                year,
                month,
                day,
                tableView,
                MinOrMaxOrMoy,
                false);
//        loading.setVisible(false);

    }

}
