/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import abstraction.Model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.scene.control.TableView;
import smart.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
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

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utilitaire.Utilitaire;

/**
 * This class represents the main point of the Model , it contains a Map of stations
 * and two parts of methods
 * Public part : they are the methods that can be accessed from the controller
 * Private part: they are the methods that are used by the public methods to insure the good operation of the app
 *
 * This class uses the design pattern singleton to insure that one instance of the
 * model can be available on all the app
 * @author karim
 */
public class MyModel implements Model {
    /*********************************PRIVATE_SECTION******************************************************************/
    private Map<Integer, Station> stationList;
    /*for singleton*/
    private static volatile Model instance = null;


    /*******************************************PRIVATE METHODS *******************************************************/
    private MyModel() {
        constructMapVilles();
    }

    /**
     * Construire la map qui contient la correspondance idVille => nomVille
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
        }
        return false;
    }

    /**
     * Ajouter un Objet de type relevée dans sa place exacte dans le model
     * la méthode est utiliser par la méthode getStationDataForDateByCity()
     * @param station station de releve
     * @param annee annee de la donne
     * @param mois mois de la donnee
     * @param jour jour de la donnee
     * @param ordre ordre du releve de la donnee
     * @param temperature temperature de la donnee
     * @param humidite humidite de la donnee
     * @param nebulosite nebulosite de la donnnee
     * @return boolean indiquant si un relevé a bien était ajouter a la map
     */
    private boolean addReleve(int station, int annee, int mois, int jour,
                              int ordre, float temperature, float humidite, float nebulosite) {
        Releve releve = new Releve(ordre, temperature, humidite, nebulosite);
        return (stationList
                .get(station)
                .getAndCreateAnnee(annee)
                .getAndCreateMois(mois)
                .getAndCreateJour(jour)
                .getAndCreateReleve(ordre, releve) == null);
    }

    /**
     * permet de télécharger et décompresser un fichier CSV
     *
     * @param date
     * @return true or false
     * @throws IOException
     */
    private boolean downloadAndUncompress(String date) throws IOException {
        return (Utilitaire.downLoadCsvByDate(date)
                && Utilitaire.decompresserGzip(Utilitaire.getGzipFilePathFromDate(date)));
    }

    /**
     *
     * @param stationName
     * @return l'id d'une station a partir de son nom
     */
    private String getStationIdFromName(String stationName) {
        for (Station station : stationList.values()) {
            if (station.getNom().equals(stationName)) {
                return station.getId();
            }
        }
        return null;
    }

    /**
     *Charger les donner qui se trovue localement et generer les objets du Model (Station,Annee,Mois,Jour,Relevee)
     * @param date la date des donner qu'on veux charger , Si date est de la forme :
                 * YYYYMM elle charge les donner tout les donner du moi
                 * Si YYYYMMJJ elle donne les donner du jour jj
     * @param cityId la ville des donner quand veux charger on passe le mot "all" la
     *               méthode retourne les donner de toutes les villes
     * @return TRUE si chargement réussit
     *         FALSE sinon
     */
    private boolean getDataForDateByCity(String date,
                                         String cityId
    ) {
        // EX: date=20140231 (31 fevrier 2014) ==> va chercher si le dossier 2014 exist et si'il contient le fichier 201402 , et si ce dernier fichier contient
        //les données de la date demander

        String fileName = Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv";
        String idVille;
        float nebu, temperature, himudite;
        aDate adate;
        int jour, mois, annee, ordre;

        //si le fichier de donnée correspondant n'existe pas

        if (!Utilitaire.checkIfFileExists(fileName)) {

            return false;
        }

        try {
            //parcourir le fichier correspondant et chercher si la date demander jour et heure
            File dateFile = new File(fileName);
            FileReader dataFR = new FileReader(dateFile);
            BufferedReader dataBR = new BufferedReader(dataFR);
            String line, dateLine, splitedLine[];
            Pattern pattern = Pattern.compile(date + ".*");
            line = dataBR.readLine();
            //sauter la premiere ligne si elle contient un string , pour éviter les erreurs
            if (line.startsWith("numer_sta")) {
                line = dataBR.readLine();
            }
            //
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

                        // si on a bien matcher une date
                        //recuperation des données apartir du fichier

                        temperature = (float) (!splitedLine[7].equals("mq") ? Float.parseFloat(splitedLine[7]) - 273.15 : 101.0);
                        nebu = !splitedLine[14].equals("mq") ? Float.parseFloat(splitedLine[14]) : 101;
                        himudite = !splitedLine[9].equals("mq") ? Float.parseFloat(splitedLine[9]) : 101;
                        //                                 annéé                           mois                            jour                            heure


                        annee = Integer.parseInt(splitedLine[1].substring(0, 4));
                        mois = Integer.parseInt(splitedLine[1].substring(4, 6));
                        jour = Integer.parseInt(splitedLine[1].substring(6, 8));
                        ordre = Integer.parseInt(splitedLine[1].substring(8, 10));


                        addReleve(Integer.parseInt(idVille), annee, mois, jour, ordre / 3, temperature, himudite, nebu);


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
     * permet de savoir si on est dans le mode mois, jour ou année dans le cas de l'affichage ou de la comparaison
     * @param year
     * @param month
     * @param day
     * @return 0 pour le mode Day 1 pour le mode month 2 pour le mode year
     */
    private int whichMode(String year, String month, String day) {
        if (year.length() > 0 && month.length() > 0 && day.length() > 0) {
            return 0;
        } else if (year.length() > 0 && month.length() > 0) {
            return 1;
        } else if (year.length() > 0) {
            return 2;
        } else {
            return -1; // invalid date !
        }
    }


    /**
     * Cette methode retourne la date des donner la plus recente inclut dans un
     * fichier
     *
     * @param date sous la forme de yyyymm correspond au nom du fichier qu'on
     * veux chercher dedans
     * @return la date la plus recente dans le fichier qui correspond a @date
     */
    private String getLatestAvailableDateOnFile(String date) {
        File f;
        FileReader fr;
        BufferedReader br;
        String line;
        int latestDate = 0;
        String dateLine;
        String filePath;

        try {
            filePath = utilitaire.Utilitaire.getCsvFilePathFromDate(date);
            if (!utilitaire.Utilitaire.checkIfFileExists(filePath)) {
                return null;
            }

            f = new File(filePath);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            line = br.readLine();

            if (line.startsWith("numer_sta")) {
                line = br.readLine();
            }

            while (line != null) {
                dateLine = line.split(";")[1].substring(0, 10);

                if (Integer.parseInt(dateLine) > latestDate) {
                    latestDate = Integer.parseInt(dateLine);
                }
                line = br.readLine();
            }

            fr.close();
            br.close();
            return String.valueOf(latestDate);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utilitaire.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(Utilitaire.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns a list of data for a station on a certain date This Method is used
     * by : -ConstructChartAffichage() -ConstructChartComparaison()
     * -ConstructTableView()
     *
     * @param stationName name of station given by the user
     * @param year year of the data that will be returned
     * @param month month of data that will be returned
     * @param day day of data that will be returned
     * @param mode 2 get data for Year Only 1 get data for Year AND Month 0 get
     * data for year AND Month AND Day 01 get data for year AND month AND day
     * for ALL stations
     * @return list of DataBean2 Objects , each Object represents data for one Releve
     */
    private List<DataBean2> getData(String stationName,
            String year,
            String month,
            String day,
            int mode,
            int MinOrMaxOrMoy,
            boolean importOrNot,
            boolean offlineMode,
            String kelvin_celcius
    ) throws IOException {

        Map<Integer, Mois> missingMonths;
        Map<Integer, Jour> missingDays;
        Map<Integer, Releve> missingReleve;
        List<DataBean2> listReleves = new ArrayList<DataBean2>();
        String stationIdTemp = getStationIdFromName(stationName);

        String stationId = ("00000" + stationIdTemp).substring(String.valueOf(stationIdTemp).length());

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

                        if ((Utilitaire.netIsAvailable() != -1) && !offlineMode) {
                            //si encore on a trouver des relevés qui manque , et si app est enligne on lance le telechargement
                            System.out.println("Still have missing relevées , trying to download them! size=" + missingReleve.size());
                            downloadAndUncompress(String.valueOf(year) + String.valueOf(month));

                            System.out.println("Download is done , trying to upload local files ");
                        } else {
                            if (importOrNot) {
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
                        .getAllReleves(kelvin_celcius, stationName, stationIdInt, yearInt, monthInt, x, y);

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
                        if ((Utilitaire.netIsAvailable() != -1) && !offlineMode) {
                            //si encore on a trouver des jours qui manque , et si app est enligne on lance le telechargement
                            System.out.println("Still have missing days , trying to download them! size=" + missingDays.size());
                            downloadAndUncompress(String.valueOf(year) + String.valueOf(month));
                        } else {
                            if (importOrNot) {
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

                        }

                    }
                }

                System.out.println("Everything looks good, returning data now");
                if (MinOrMaxOrMoy == 0) {//moy
                    listReleves = stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getAndCreateMois(monthInt)
                            .getMoyennesParJour(kelvin_celcius, stationName, stationIdInt, year, x, y);
                } else {
                    if (MinOrMaxOrMoy == 1) {//min
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getAndCreateMois(monthInt)
                                .getMinParMois(kelvin_celcius, stationName, stationIdInt, year, x, y);
                    } else {//max
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getAndCreateMois(monthInt)
                                .getMaxParMois(kelvin_celcius, stationName, stationIdInt, year, x, y);
                    }
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
                        if ((Utilitaire.netIsAvailable() != -1) && !offlineMode) {
                            //si encore on a trouver des mois qui manque , et si app est enligne on lance le telechargement
                            System.out.println("Still have missing mois , trying to download them! size=" + missingMonths.size());

                            for (Mois mois : missingMonths.values()) {
                                moisIdInt = mois.getId();
                                moisId = ("00" + moisIdInt).substring(String.valueOf(moisIdInt).length());
                                downloadAndUncompress(year + moisId);
                            }

                            System.out.println("Download is done , trying to upload local files ");
                        } else {
                            if (importOrNot) {
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

                        }

                    }
                }

                System.out.println("Everything looks good, returning data now");
                listReleves = stationList.get(stationIdInt)
                        .getAndCreateAnnee(yearInt)
                        .getAllReleves(kelvin_celcius, stationName, stationIdInt, x, y);

                if (MinOrMaxOrMoy == 0) {//moy
                    listReleves = stationList.get(stationIdInt)
                            .getAndCreateAnnee(yearInt)
                            .getMoyenneParMois(kelvin_celcius, stationName, stationIdInt, x, y);
                } else {
                    if (MinOrMaxOrMoy == 1) {//min
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getMinParMois(kelvin_celcius, stationName, stationIdInt, x, y);
                    } else {//max
                        listReleves = stationList.get(stationIdInt)
                                .getAndCreateAnnee(yearInt)
                                .getMaxParMois(kelvin_celcius, stationName, stationIdInt, x, y);
                    }
                }





                if (listReleves.isEmpty()) {
                    System.out.println("Attention , null data found ! ");
                }
                return listReleves;
        }

        return null;
    }

    /**
     * this method constructs Affichage chart lists with the asked data
     *
     * @param station station name
     * @param year year given by the user
     * @param month month given by the user
     * @param day day given by the user
     * @param AfficheTemp temperature observable list that will be written on
     * @param AfficheHum humidite observable list that will be written on
     * @param AfficheNebul nebulosite observable list that will be written on
     * @return TRUE if charts were constructed successfully FALSE if not
     * @throws IOException
     */
    private boolean constructChartAffichage(String station,
                                            String year,
                                            String month,
                                            String day,
                                            AreaChart<Number, Number> AfficheTemp,
                                            AreaChart<Number, Number> AfficheHum,
                                            AreaChart<Number, Number> AfficheNebul,
                                            int MinOrMaxOrMoy,
                                            boolean importOrNot,
                                            boolean offlineMode,
                                            String kelvin_celcius
    ) throws IOException {

        int mode = whichMode(year, month, day);

        ArrayList<XYChart.Series> S = new ArrayList<>();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();

        ArrayList<DataBean2> Resultat = (ArrayList<DataBean2>) getData(station, year, month, day,
                mode, MinOrMaxOrMoy, importOrNot, offlineMode, kelvin_celcius);

        if (Resultat == null) {
            return false;
        }
        /*
            a partir de date : si c une année alors afficher la moyenne de chaque mois
                                si c un mois alors afficher les 30 jours
                                si c un jours afficher les 8 valeurs
         */

        for (int i = 0; i < Resultat.size(); i++) {

            series.getData().add(new XYChart.Data<>(i + 1, Resultat.get(i).getTemperature()));
            series1.getData().add(new XYChart.Data<>(i + 1, Resultat.get(i).getHumidite()));
            series2.getData().add(new XYChart.Data<>(i + 1, Resultat.get(i).getNebulosite()));
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
     * This method constructs a table view with the data asked for
     *
     * @param station station given by the user
     * @param year year as given by the user
     * @param month month as given by the user
     * @param day month as given by the user
     * @param tableView the tableView in which we are going to insert Data
     * @return TRUE if tableView was constructed successfuly FALSE if not
     * @throws IOException
     */
    private boolean constructTableView(String station,
                                       String year,
                                       String month,
                                       String day,
                                       TableView<DataBean> tableView,
                                       int MinOrMaxOrMoy,
                                       boolean importOrNot,
                                       boolean offlineMode,
                                       String kelvin_celcius
    ) throws IOException {
        int mode = whichMode(year, month, day);

        ArrayList<DataBean2> resultat = (ArrayList<DataBean2>) getData(station, year, month, day, mode,
                MinOrMaxOrMoy, importOrNot, offlineMode, kelvin_celcius);
        ArrayList<DataBean> listDataBean = new ArrayList<DataBean>();
        if (resultat != null) {
            for (DataBean2 dataBean2 : resultat) {
                listDataBean.add(dataBean2.toDataBean());
            }
        }

        tableView.getItems().setAll(listDataBean);
        return true;
    }

    /*****************************PUBLIC SECTION***********************************************************************/
    /**
     * This method is a part if singleton design pattern that
     * willl alow the other classes to instantiate This class instead of the New
     * keyword
     *
     * @return an instance of This class
     */
    static Model getInstance() {
        if (MyModel.instance == null) {
    //premiere demande d'instanciation
    //synchronized = laisser passer les threads (demandes) un par un
            synchronized (MyModel.class) {
                MyModel.instance = new MyModel();
            }
        }
        return MyModel.instance;
    }


    /**
     * this method constructs Comparaison chart lists using asked data
     *
     * @param station station name
     * @param year1 given by the user for date 1
     * @param month1 given by the user for date 1
     * @param day1 given by the user for date1
     * @param year2 given by the user for date2
     * @param month2 given by the user for date2
     * @param day2 given by the user for date2
     * @param lineCharttemp the observable list for temperature data that will
     * be written on
     * @param lineCharthum the observable list for humidite data that will be
     * written on
     * @param lineChartnebul the observable list for nebelusite data that will
     * be written on
     * @return TRUE IF Charts were constructed successfully b FALSE if not
     * @throws IOException
     */
    @Override
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
                                             int MinOrMaxOrMoy,
                                             boolean offlineMode,
                                             String kelvin_celcius
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

        ArrayList<DataBean2> Resultat1 = (ArrayList<DataBean2>) getData(station, year1, month1, day1,
                mode, MinOrMaxOrMoy, true, offlineMode, kelvin_celcius);
        ArrayList<DataBean2> Resultat2 = (ArrayList<DataBean2>) getData(station, year2, month2, day2,
                mode, MinOrMaxOrMoy, true, offlineMode, kelvin_celcius);
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


    }

    /**
     * This method fill the latest data found into the model , then returns that data
     * If internet is available it will download the latest data, if no internet available it will ask
     * the user to give a file containing data
     * This Method is used by AffichierCarte method inside the controller to show France carte containing latest data
     * @param offlineMode indicates if the app is on ONLINE or OFFLINE MODE
     * @return List of DataBean2 object containing the latest(newest data found )
     * @throws IOException
     */
    @Override
    public List<DataBean2> getLatestDataForGraphicMap(boolean offlineMode) throws IOException {






















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

        if ((Utilitaire.netIsAvailable() != -1) && !offlineMode) {
            downloadAndUncompress(year + month);

        } else {
            DisplayAlertToImport();
        }

        /**
         * UPLOADER LA DERNEIRE DATE QUI EXISTE EN LOCAL SI TELECHARGEMENT HAS FAILED*
         */
        String file = Utilitaire.getLatesttAvailableFile();

        String date = this.getLatestAvailableDateOnFile(file);

        getDataForDateByCity(date, "all");
        for (Station station : stationList.values()) {
            dataBean = station
                    .getAndCreateAnnee(intYear)
                    .getAndCreateMois(intMonth)
                    .getAndCreateJour(intDay)
                    .getLatestReleve(station.getNom(),
                             Integer.parseInt(station.getId()),
                             intYear,
                             intMonth,
                             station.getPointObj().getX(),
                             station.getPointObj().getY());

            dataList.add(dataBean);
        }

        return dataList;
    }


    /**
     * Verification des textField Date correct
     *
     * @param year
     * @param month
     * @param day
     * @return Tableau d'erreur or null if any errors
     */
    @Override
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
    @Override
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


    /**
     * se charge de faire appel aux méthodes constructChartAffichage et constructTableview
     * est appelé a partir du controller lors d'une demande d'affichage de données
     * @param station
     * @param year
     * @param month
     * @param day
     * @param AfficheTemp
     * @param AfficheHum
     * @param AfficheNebul
     * @param tableView
     * @param MinOrMaxOrMoy
     * @param offlineMode
     * @param kelvin_celcius
     * @throws IOException
     */
    @Override
    public void Affichage(String station,
                          String year,
                          String month,
                          String day,
                          AreaChart<Number, Number> AfficheTemp,
                          AreaChart<Number, Number> AfficheHum,
                          AreaChart<Number, Number> AfficheNebul,
                          TableView<DataBean> tableView,
                          int MinOrMaxOrMoy,
                          boolean offlineMode,
                          String kelvin_celcius
    ) throws IOException {
        constructChartAffichage(station,
                year,
                month,
                day,
                AfficheTemp,
                AfficheHum,
                AfficheNebul,
                MinOrMaxOrMoy,
                true,
                offlineMode,
                kelvin_celcius
        );

        constructTableView(station,
                year,
                month,
                day,
                tableView,
                MinOrMaxOrMoy,
                false,
                offlineMode,
                kelvin_celcius
        );

    }

    /**
     * @param year
     * @return arrayList contenant les mois existants en local pour une année
     * mise en paramétre
     */
    @Override
    public ArrayList<String> getMonthsExistsForYear(String year) {
        ArrayList<String> list = new ArrayList<>();
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME + "/" + year);
        for (File file : file1.listFiles()) {
            list.add(file.toString().substring(8));
        }

        return list;
    }

    /**
     * Afficher une alert permettant d'importer un fichier
     *
     * @throws IOException
     */
    @Override
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
     * @return ArrayList contenant les dates existantes en local
     */
    @Override
    public ArrayList<String> getYearExists() {
        ArrayList<String> list = new ArrayList<>();
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME);
        for (File file : file1.listFiles()) {
            list.add(file.toString().substring(8));
        }

        return list;
    }

    /**
     * @return a List of String containing all station names
     */
    @Override
    public List<String> getStationNames() {
        List<String> tempList = new ArrayList<>();
        for(Station station : stationList.values()) {
            tempList.add(station.getNom());
        }
        return tempList;
    }
}
