/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

/**
 * TODO yearMode,MonthMode,DayMode
 *
 * if data doesn't exist or not updated we download it , and it depends if it is
 * only a month , or a whole year !
 *
 */
import coordonnee.Point;
import coordonnee.Ville;
import coordonnee.DataCity;
import coordonnee.aDate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import utilitaire.*;
import java.net.URLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import utilitaire.Utilitaire;

/**
 *
 * @author karim
 */
public class MyModel {

    //for singelton
    private static volatile MyModel instance = null;
    private Map<Integer, Ville> villes;

    private MyModel() {
        constructMapVilles();
    }

    
    
     /**
     * COnstruire la map qui contient la correspondance idVille => nomVille
     *
     * @return true if succesfully charged false if not
     */
    private boolean constructMapVilles() {
        FileReader fr = null;
        String line;
        try {
            File file = new File(Configuration.CITY_FILE_NAME);
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            line = br.readLine();

            villes = new HashMap<Integer, Ville>();

            while (line != null) {
                villes.put(Integer.parseInt(line.split(";")[0]),
                        new Ville(line.split(";")[1],
                                Integer.parseInt(line.split(";")[0]),
                                new Point(Integer.parseInt(line.split(";")[2]), Integer.parseInt(line.split(";")[3]))
                        ));
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

    private int getIdFromNameVille(String name) {
        Integer key = null;

        for (Map.Entry<Integer, Ville> entry : villes.entrySet()) {

            Ville value = entry.getValue();

            if (value.getNom().compareTo(name) == 0) {
                key = entry.getKey();
            }
        }
        return key;

    }

    private Ville getVilleFromId(int id) {
        return (villes.get(id));
    }

    
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

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    public ArrayList<String> getMissedMonthsFiles(String year) {
        /*
        int currentDay, currentMonth, currentYear;
        ArrayList<String> missedMonths = new ArrayList<String>();
        String yearMonth, month;
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        currentDay = localTime.getDayOfMonth();
        currentMonth = localTime.getMonthValue();
        currentYear = localTime.getYear();
        boolean stopSearch = false;
        if (String.valueOf(currentYear).equals(year)) {
            //si l'année donner est l'année courante, on met le boolean stopSearch a true pour ne pas
            //mettre les mois qui sont plus grand que le mois courant comme missed ! 
            stopSearch = true;
        }

        for (int i = 1; i <= 12; i++) {
            month = ("00" + i).substring(String.valueOf(i).length());
            yearMonth = year + month;
            if (stopSearch && Integer.parseInt(month) > currentMonth) //si le mois generer est supperieure au mois courant
            {
                break;
            }

            //si le fichier de ce moi n'existe pas OU si il est pas a jour alors on l'ajout comme missed
            if (!checkIfFileExists(getCsvFilePathFromDate(yearMonth)) || !isUpdatedMonth(yearMonth)) {
                missedMonths.add(yearMonth);
            }

        }
        return missedMonths;
    */
        return null;
        }
        
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
    public ArrayList<DataCity> getDataForDateByCity(String date, String cityId) {
    // EX: date=20140231 (31 fevrier 2014) ==> va chercher si le dossier 2014 exist et si'il contient le fichier 201402 , et si ce dernier fichier contient 
    //les données de la date demander
    //System.out.println("Recuperation des donnée de la ville => " + cityId + " et la date =>" + date);
    String fileName = Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv";
    String idVille;
    float nebu;
    double temperature;
    int himudite;
    aDate adate;
    //si le fichier de donnée correspondant n'existe pas 
    //System.out.println("file exist" + fileName);
    if (!Utilitaire.checkIfFileExists(fileName)) {
        //System.out.println("file doens't exist");
        return null;
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
                    ////System.out.println("match date=>"+dateLine);
                    // si on a bien matcher une date 
                    //recuperation des données apartir du fichier 
                    temperature = !splitedLine[7].equals("mq") ? Double.parseDouble(splitedLine[7]) - 273.15 : 101;
                    nebu = !splitedLine[14].equals("mq") ? Float.parseFloat(splitedLine[14]) : 101;
                    himudite = !splitedLine[9].equals("mq") ? Integer.parseInt(splitedLine[9]) : 101;
                    //                                 annéé                           mois                            jour                            heure
                    adate = new aDate(splitedLine[1].substring(0, 4), splitedLine[1].substring(4, 6), splitedLine[1].substring(6, 8), splitedLine[1].substring(8, 10));
                    //noter j'ai pa mis le nom de la ville a rajouter
                    ville = getVilleFromId(Integer.parseInt(idVille));
                    listDonnees.add(new DataCity(ville, temperature, himudite, nebu, adate));
                }

            }
            line = dataBR.readLine();
        }
        dataBR.close();
        return listDonnees;
    } catch (FileNotFoundException ex) {
        Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
        Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
}

    
    public ArrayList<DataCity> getDataForYearByCity(String date, String cityId) {
        String year = date.substring(0, 4);
        ArrayList<DataCity> liste = new ArrayList<DataCity>();
        ArrayList<DataCity> tempList = null;
        String yearMonth;
        for (int i = 1; i <= 12; i++) {
            if (i < 10) {
                yearMonth = year + ("00" + i).substring("i".length());
            } else {
                yearMonth = year + String.valueOf(i);
            }

            tempList = getDataForDateByCity(yearMonth, cityId);
            if (tempList != null) {
                liste.addAll(tempList);
                //System.out.println("list is null");
            }
            ////System.out.println("tour:"+i);
        }
        return liste;
    }

    /**
     * This method returns the latest available data localy
     *
     * @return latest available data that we have localy if exists null if no
     * data found localy
     */
    
    public ArrayList<DataCity> getLatestAvailableData() {
        ArrayList<DataCity> liste = null;
        String file = Utilitaire.getLatesttAvailableFile();
        //System.out.println("damnFile:"+file);
        String date = this.getLatestAvailableDateOnFile(file);
        if (date != null) {
            liste = this.getDataForDateByCity(date, "all");
        }

        return liste;
    }

    /**
     *
     * @param date
     * @param stationName
     * @return observableList for Chart
     */
    
    public ArrayList<DataCity> getListForChart(String date, String stationName) {
        int k = getIdFromNameVille(stationName);
        String t = Integer.toString(k);

        ArrayList<DataCity> Resultat = date.length() == 4
                ? this.getDataForYearByCity(date, t.length() == 5 ? t : '0' + t)
                : this.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);
        //MyModel.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);

        return Resultat;
    }

    private String getDateMode(String date) {
        switch (date.length()) {
            case 4:
                return "year";
            case 6:
                return "month";
            default:
                return "day";
        }

    }

    private void toMediane(ArrayList<DataCity> oldList, String dateMode) {
        ArrayList<DataCity> newList = null;
        newList.addAll(oldList);
        for (DataCity dataCity : oldList) {
            switch (dateMode) {
                case "year": {

                }

                case "month": {

                }

                case "day": {
//                    if (dataCity.getDate().getDay()) {

//                    }
                }

            }

        }
    }
    
    /**
     *
     * @param date
     * @param stationName
     * @return ArrayList of series that are parameters to ChartLine
     *
     */
    
    public boolean constructChartAffichage(boolean onlineMode,String date, String stationName, 
                AreaChart<Number, Number> AfficheTemp, 
                AreaChart<Number, Number> AfficheHum, 
                AreaChart<Number, Number> AfficheNebul
                
    ){
        if (onlineMode) {
            ArrayList<XYChart.Series> S = new ArrayList<>();

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
            XYChart.Series<Number, Number> series2 = new XYChart.Series<>();

            ArrayList<DataCity> Resultat = getListForChart(date, stationName);

            if (Resultat == null) {
                return false;
            }
            /*
            a partir de date : si c une année alors afficher la moyenne de chaque mois 
                                si c un mois alors afficher les 30 jours 
                                si c un jours afficher les 8 valeurs
             */

            for (int i = 0; i < Resultat.size(); i++) {

                series.getData().add(new XYChart.Data<>(i, Resultat.get(i).getTemperature()));
                series1.getData().add(new XYChart.Data<>(i, Resultat.get(i).getHumidite()));
                series2.getData().add(new XYChart.Data<>(i, Resultat.get(i).getNebulosite()));

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

        } else {
             /*
                tester si le fichier existe
                si oui l'afficher
                si non verifier si onligne
                si oui telecharger
                si non importer
                */
            try {
               DisplayAlertToImport();
            } catch (IOException ex) {
                Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
            }
            constructChartAffichage(true, date, stationName, 
                    AfficheTemp,
                    AfficheHum,
                    AfficheNebul);
            return true;
        }

        //return false;
    }

    
    public boolean downloadAndUncompress(String date) throws IOException {
        return (Utilitaire.downLoadCsvByDate(date)
                && Utilitaire.decompresserGzip(Utilitaire.getGzipFilePathFromDate(date)));
    }
    
    
    /**
     *
     * @param date
     * @param stationName
     * @return ArrayList of series that are parameters to ChartLine
     *
     */
    public boolean constructChartComparaison(boolean onlineMode, String date1, String date2, String stationName,
            LineChart<Number, Number> lineCharttemp,
            LineChart<Number, Number> lineCharthum,
            LineChart<Number, Number> lineChartnebul
    ){

        /*
            tester si le fichier existe 
            si oui l'afficher 
            si non verifier si onligne
                si oui telecharger
                si non importer
         */
        if (onlineMode) {
            ArrayList<XYChart.Series> S1 = new ArrayList<>();
            ArrayList<XYChart.Series> S2 = new ArrayList<>();

            XYChart.Series<Number, Number> series0 = new XYChart.Series<>();
            XYChart.Series<Number, Number> series01 = new XYChart.Series<>();
            XYChart.Series<Number, Number> series02 = new XYChart.Series<>();

            XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
            XYChart.Series<Number, Number> series11 = new XYChart.Series<>();
            XYChart.Series<Number, Number> series12 = new XYChart.Series<>();

            ArrayList<DataCity> Resultat1 = getListForChart(date1, stationName);
            ArrayList<DataCity> Resultat2 = getListForChart(date2, stationName);
            if (Resultat1 == null || Resultat2 == null) {
                return false;
            }

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
        } else {

            try {
                DisplayAlertToImport();
            } catch (IOException ex) {
                Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
            }

            constructChartComparaison(true, date1, date2, stationName,
                    lineCharttemp,
                    lineCharthum,
                    lineChartnebul
            );
            return true;
        }

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
                    System.out.println(pathOfFile);
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

    /**
     * Method classique qui retourne pour un mois donner le dernier jour de ce
     * moi EX: le mois JUIN(06) il contient 30 jours
     *
     * @param year
     * @param month
     * @return le nombre de jour de ce mois
     */
    public int getNumberDaysOfMonth(int year, int month) {
        int currentDay, currentMonth, currentYear;
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.of(year, Month.of(month), 3, 3, 3);
        LocalDateTime lastDay = localTime.with(TemporalAdjusters.lastDayOfMonth());

        return lastDay.getDayOfMonth();
    }

    /**
     * une methode qui prend en parametre un fichier de donnéer d'un mois sous
     * forme de yyyymm EX: 201405 et dis si ce fichier est a jour (contient tout
     * les donnée)
     *
     * @param date
     * @param month
     * @return TRUE si le fichier est a jour FALSE sinon
     */
    public boolean isUpdatedMonth(String date) {
        String lastDate, year, month, lastDay;
        //fichier n'existe pas
        if(!Utilitaire.checkIfFileExists(Utilitaire.getCsvFilePathFromDate(date)))
            return false;
        
        lastDate = getLatestAvailableDateOnFile(date);
        System.out.println("lastDate="+lastDate);
        lastDay = lastDate.substring(6, 8);

        year = date.substring(0, 4);
        month = date.substring(4, 6);

        if (date == null) {
            return false;
        }

        int currentYear;
        String currentDay, currentMonth;
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.now(zoneId);

        currentDay = String.valueOf(localTime.getDayOfMonth());
        //pour avoir 01 pour le premier jour de moi au lieu de 1
        currentDay = ("00" + currentDay).substring(currentDay.length());

        //pour avoir 01 pour janvier au lieu de 1
        currentMonth = String.valueOf(localTime.getMonthValue());
        currentMonth = ("00" + currentMonth).substring(currentMonth.length());

        currentYear = localTime.getYear();

        //System.out.println("lastDay:"+lastDay+" currentDay:"+currentDay);
        if (String.valueOf(currentYear).equals(year)
                && //si le fichier contient les donnees de l'année courante
                String.valueOf(currentMonth).equals(month)
                && //et si le fichier contient les donnees mois courant
                lastDay.equals(String.valueOf(currentDay)) //Donc on test si la derniere date de ce fichier est celle d'ajourdhui :D
                ) {
            return true;
        } else {
            int nbDays = getNumberDaysOfMonth(Integer.parseInt(year), Integer.parseInt(month));
            //System.out.println("nbDays:"+nbDays);
            if (String.valueOf(nbDays).equals(lastDay)) {
                return true;
            } else {
                return false;
            }
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
    
    public String getLatestAvailableDateOnFile(String date) {
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
     * Cette Methode retourne la date exacte des donnée les plus recents
     * yyyymmjjhh
     *
     * @param date sous la forme de yyyymm
     * @return date sous form yyyymmjjhh
     */
    
    public double netIsAvailable() {
        try {
            final URL url = new URL("http://donneespubliques.meteofrance.fr");
            long startTime = System.nanoTime();
            final URLConnection conn = url.openConnection();
            conn.setConnectTimeout(1000);
            conn.connect();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.

            return duration;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return -1;
        }
    }
    

    /**
     *
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
     *
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

}

/*
TODO: 
toMediane(date)
toMoyenne(date)
DeleteCVSFile(date) 

 */
