/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

/**
 * TODO
 * getMissedMonthsFiles : retourne les mois qui manque
 */
import coordonnee.Point;
import coordonnee.Ville;
import coordonnee.VilleTemp;
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
import java.net.URLConnection;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javafx.scene.chart.XYChart;
import org.apache.commons.io.FileUtils;
/**
 *
 * @author karim
 */
public class MyModel {

    //private ArrayList<VilleTemp> listDonnée;
    private Map<Integer, Ville> villes;

    public MyModel() {
        constructMapVilles();
    }

    //*********************************PRIVATE SECTION ********************************************************// 
    
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
                    Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        fr.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
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

            private boolean createDirectory(String directory) {
                File theDir = new File(directory);
                boolean result = false;
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    //System.out.println("creating directory: " + directory);
                    try {
                        theDir.mkdir();
                        result = true;
                    } catch (SecurityException se) {
                        //handle it
                    }
                    if (result) {
                        //System.out.println("DIR created");
                    }
                }
                return result;
            }

            /**
             * Cette mthode donne apartir une date de la forme yyymmjjhh.. , le chemin
             * vers le fichier csv qui contient cette date
             *
             * @param date
             * @return
             */
            private String getCsvFilePathFromDate(String date) {
                return Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv";
            }

            /**
             * Cette mthode donne apartir une date de la forme yyymmjjhh.. , le chemin
             * vers le fichier csv.gz qui contient cette date
             *
             * @param date
             * @return
             */
            private String getGzipFilePathFromDate(String date) {
                return Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv.gz";
            }

            /**
             * Cette methode cherche le fichier le plus recent (qui contient les données
             * les plus recentes)
             *
             * @return le chemin du fichier le plus recent
             */
            private String getLatesttAvailableFile() {
                int maxYearTemp = 0, maxYear=0,maxFileName = 0;
                File file1 = new File(Configuration.DATA_DIRECTORY_NAME);
                File file2 = new File(Configuration.DATA_DIRECTORY_NAME);
                
                for (File file : file1.listFiles()) {
                    
                    if (Integer.parseInt(file.getName()) > maxYear) {
                        maxYearTemp = Integer.parseInt(file.getName());
                        file2 = new File(Configuration.DATA_DIRECTORY_NAME + "/" + maxYearTemp);
                        
                        //avant d'accepter la nouvelle annee on doit d'abord verifier que'elle contient des fichier! 
                        if(file2.listFiles().length>0) {
                            maxYear = maxYearTemp;
                            file1 = file2;
                        }
                    }
                }

                
                for (File file : file1.listFiles()) {
                   // //System.out.println("name:" + file.getName());
                    if (Integer.parseInt(file.getName().substring(0, 6)) > maxFileName) {
                        maxFileName = Integer.parseInt(file.getName().substring(0, 6));
                    }
                }
                //System.out.println("pathDate:" + maxFileName);
                if (maxFileName == 0) {
                    return null;
                } else {
                    return String.valueOf(maxFileName);
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
                    filePath = getCsvFilePathFromDate(date);
                    if (!checkIfFileExists(filePath)) {
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
                    Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            /**
             * Cette methode determine si un fichier existe ou pas
             *
             * @param file le fichier qu'on va chercher
             * @return TRUE si le fichier existe, FALSE sinon
             */
            private boolean checkIfFileExists(String file) {
                return (new File(file).exists());
            }

            /**
             * Methode static qui telecharger et sauvegarde un fichier depuis un URL
             *
             * @param date la date de telechargement (yyyymm)
             * @param outputfile le nom de fichier apres le telechargement(*.csv.gz)
             */
            private boolean downLoadCsvByDate(String date) throws IOException {
                try {
                    File saveFile;
                    URL url;
                    String newUrl;
                    String directory;
                    String path;

                    //avoir l'année depuis la date , pour telecharger le fichier dans le dossier qui correspond a l'année
                    directory = Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4);
                    createDirectory(directory);

                    //Creation d'un obj url qui pointe vers l'url qui se trouve dans la classe Configuration
                    newUrl = Configuration.DATA_GZIP_URL.replace("#", date);
                    //System.out.println("url:" + newUrl);
                    url = new URL(newUrl);

                    //le chemin de fichier ou on va telecharger les donnés
                    path = Configuration.getApplicationPath() + "/" + directory + "/" + date + ".csv.gz";
                    //Creation d'un fichier ou on va sauvegarder le fichier telecharger
                    saveFile = new File(path);

                    //utilisation de la methode copyURLToFile de apache , qui telecharger et sauvegarde un fichier
                    FileUtils.copyURLToFile(url, saveFile);

                    return true;
                } catch (MalformedURLException ex) {
                    Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                return false;
            }

            /**
             *
             * Methode static qui fait la decompression d'un fichier Gzip et sauvegarde
             * le fichier decompressé
             *
             * @param inputFile le nom de fichier qui va etre decompresser
             * @param outputFile le nom de fichier resultat apres la decompression
             */
            private boolean decompresserGzip(String inputFile) {
                byte[] buffer = new byte[1024];
                //on garde le meme nom pour le fichier decompresser sauf .gz
                String outputFile = inputFile.substring(0, inputFile.length() - 3);

                //System.out.println("output file : " + outputFile);
                try {
                    //initialiser notre flux d'entrer par le fichier gzip déja telecharger
                    FileInputStream fileIn = new FileInputStream(inputFile);
                    //initialiser un flux d'entrer de type gzip
                    GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
                    //creation d'un flux de sortie vers un fichier (le fichier ou on va mettre ce qu'on a decompressé) 
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(Configuration.getApplicationPath() + "/" + outputFile));
                    int bytes_read;
                    //on lit des obj byte depuis le flux d'entrer GZIP , et on les mets dans le flux de sortie 
                    while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bytes_read);
                    }
                    //fermetures des flux
                    gZIPInputStream.close();
                    fileOutputStream.close();
                    File temp = new File(inputFile);
                    //System.out.println("Le fichier a été decompressé correctement ! ");
                    if (temp.delete()) {
                        //System.out.println("le fichier '" + inputFile + "' a été supprimer");
                        return true;
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return false;
            }

    //*********************************PUBLIC SECTION ********************************************************// 
            
            
    /**
     * this method returns for a given year all month files that doesn't exist
     * EX: inside folder 2014 if we have all months files except 201401.csv,
     * then method will return it
     *
     * @param year corresponds to the year folder we'll look at
     * @return a list of missed months
     */
    public ArrayList<String> getMissedMonthsFiles(String year) {
        int currentDay, currentMonth, currentYear;
        ArrayList<String> missedMonths = new ArrayList<String>();
        String yearMonth,month;
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        currentDay = localTime.getDayOfMonth();
        currentMonth = localTime.getMonthValue();
        currentYear = localTime.getYear();
        boolean stopSearch=false;
        if(String.valueOf(currentYear).equals(year)) {
            //si l'année donner est l'année courante, on met le boolean stopSearch a true pour ne pas
            //mettre les mois qui sont plus grand que le mois courant comme missed ! 
            stopSearch=true;
        }
        
        for (int i = 1; i <= 12; i++) {
            month = ("00" + i).substring(String.valueOf(i).length());
            yearMonth = year + month;
            if(stopSearch&&Integer.parseInt(month)>currentMonth)
                //si le mois generer est supperieure au mois courant
                break;
            
            if (!checkIfFileExists(getCsvFilePathFromDate(yearMonth))) {
                missedMonths.add(yearMonth);
            }

        }
        return missedMonths;
    }

    public boolean downloadAndUncompress(String date) throws IOException {
        return (downLoadCsvByDate(date)
                && decompresserGzip(getGzipFilePathFromDate(date)));
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
     * @return une arrayList de type VilleTemp qui contient les donner demander
     */
    public ArrayList<VilleTemp> getDataForDateByCity(String date, String cityId) {
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
        if (!checkIfFileExists(fileName)) {
            //System.out.println("file doens't exist");
            return null;
        }

        try {
            //parcourir le fichier correspondant et chercher si la date demander jour et heure
            File dateFile = new File(fileName);
            FileReader dataFR = new FileReader(dateFile);
            BufferedReader dataBR = new BufferedReader(dataFR);
            String line, dateLine, splitedLine[];

            ArrayList<VilleTemp> listDonnees = new ArrayList<VilleTemp>();

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
                        listDonnees.add(new VilleTemp(ville, temperature, himudite, nebu, adate));
                    }

                }
                line = dataBR.readLine();
            }
            dataBR.close();
            return listDonnees;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<VilleTemp> getDataForYearByCity(String date, String cityId) {
        String year = date.substring(0, 4);
        ArrayList<VilleTemp> liste = new ArrayList<VilleTemp>();
        ArrayList<VilleTemp> tempList = null;
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
    public ArrayList<VilleTemp> getLatestAvailableData() {
        ArrayList<VilleTemp> liste = null;
        String file = this.getLatesttAvailableFile();
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
    public ArrayList<VilleTemp> getListForChart(String date, String stationName) {
        int k = getIdFromNameVille(stationName);
        String t = Integer.toString(k);

        ArrayList<VilleTemp> Resultat = date.length() == 4
                ? this.getDataForYearByCity(date, t.length() == 5 ? t : '0' + t)
                : this.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);
        //MyModel.getDataForDateByCity(date, t.length() == 5 ? t : '0' + t);

        return Resultat;
    }

    /**
     *
     * @param date
     * @param stationName
     * @return ArrayList of series that are parameters to ChartLine
     *
     */
    public ArrayList<XYChart.Series> constructChart(String date, String stationName) {
        ArrayList<XYChart.Series> S = new ArrayList<>();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();

        ArrayList<VilleTemp> Resultat = getListForChart(date, stationName);

        if (Resultat == null) {
            return null;
        }

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
     * @return true if valide , false if not
     */
    public boolean validateDateLogically(String year, String month, String day) {
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
    
    
    
    
    /*********************KARIM**************************/
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

    public ArrayList<String> getYearExists() {
        ArrayList<String> list=new ArrayList<>();
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME);
        for (File file : file1.listFiles()) {
            list.add(file.toString().substring(8));
        }

        return list;
    }
     public ArrayList<String> getMonthsExistsForYear(String year) {
        ArrayList<String> list=new ArrayList<>();
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME+"/"+year);
        for (File file : file1.listFiles()) {
            list.add(file.toString().substring(8));
        }

        return list;
    }
}