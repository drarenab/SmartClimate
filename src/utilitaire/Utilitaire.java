/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilitaire;

/**
 * TODO yearMode,MonthMode,DayMode
 *
 * if data doesn't exist or not updated we download it , and it depends if it is
 * only a month , or a whole year !
 *
 */
import meteo.*;
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

        
/**
 *
 * @author karim
 */
public class Utilitaire {
    //public static ArrayList<VilleTemp> listDonnée;
    public static Map<Integer, Ville> villes;

    //*********************************public static SECTION ********************************************************// 
     /**
     * COnstruire la map qui contient la correspondance idVille => nomVille
     *
     * @return true if succesfully charged false if not
     */
    public static boolean constructMapVilles() {
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

    public static int getIdFromNameVille(String name) {
        Integer key = null;

        for (Map.Entry<Integer, Ville> entry : villes.entrySet()) {

            Ville value = entry.getValue();

            if (value.getNom().compareTo(name) == 0) {
                key = entry.getKey();
            }
        }
        return key;

    }

    public static Ville getVilleFromId(int id) {
        return (villes.get(id));
    }

    public static boolean createDirectory(String directory) {
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
    public static String getCsvFilePathFromDate(String date) {
        return Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv";
    }
    
     public static String getYearFilePathFromDate(String date) {
        return Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4);
    }

    /**
     * Cette mthode donne apartir une date de la forme yyymmjjhh.. , le chemin
     * vers le fichier csv.gz qui contient cette date
     *
     * @param date
     * @return
     */
    public static String getGzipFilePathFromDate(String date) {
        return Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv.gz";
    }

    /**
     * Cette methode cherche le fichier le plus recent (qui contient les données
     * les plus recentes)
     *
     * @return le chemin du fichier le plus recent
     */
    public static String getLatesttAvailableFile() {
        int maxYearTemp = 0, maxYear = 0, maxFileName = 0;
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME);
        File file2 = new File(Configuration.DATA_DIRECTORY_NAME);

        for (File file : file1.listFiles()) {

            if (Integer.parseInt(file.getName()) > maxYear) {
                maxYearTemp = Integer.parseInt(file.getName());
                file2 = new File(Configuration.DATA_DIRECTORY_NAME + "/" + maxYearTemp);

                //avant d'accepter la nouvelle annee on doit d'abord verifier que'elle contient des fichier! 
                if (file2.listFiles().length > 0) {
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
     * Cette methode determine si un fichier existe ou pas
     *
     * @param file le fichier qu'on va chercher
     * @return TRUE si le fichier existe, FALSE sinon
     */
    public static boolean checkIfFileExists(String file) {
        return (new File(file).exists());
    }

    /**
     * Methode static qui telecharger et sauvegarde un fichier depuis un URL
     *
     * @param date la date de telechargement (yyyymm)
     * @param outputfile le nom de fichier apres le telechargement(*.csv.gz)
     */
    public static boolean downLoadCsvByDate(String date) throws IOException {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("ProgressBar");
//        alert.setHeaderText("Progression du téléchargement");
//        alert.setContentText(date);
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.get() == ButtonType.OK) {
//        }

        try {

            long startTime = System.nanoTime();

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
            /*
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
            if (ProgressComparaison != null) {
                ProgressComparaison.setProgress(duration);
            }
            */
            return true;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
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
    public static boolean decompresserGzip(String inputFile) {
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

    /**
     *
     * @param recupp
     * @return booleen indiquant si le processus d'import a bien été fait sans
     * erreur cette fonction Copy le fichier du chemin selectionner vers le
     * dossier local contenant les données le décompresse si c'est un fichier de
     * type gz et renome le nouveau fichier dans les deux cas
     */
    public static boolean CopyFileImported(File recupp) {
        boolean wellDone = true;
        Path from = Paths.get(recupp.toURI());//chemin du fichier recupéré
        String[] str = recupp.getPath().split("/");
        String[] dates = str[str.length - 1].split(Pattern.quote("."));
        String directory = Configuration.DATA_DIRECTORY_NAME + "/" + dates[1].substring(0, 4);

        wellDone = createDirectory(directory);
        if (wellDone) {
            Path to = Paths.get(directory + "/" + str[str.length - 1]);
            CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
            };
            try {
                Files.copy(from, to, options);
            } catch (IOException ex) {
                wellDone = false;
                Logger
                        .getLogger(Utilitaire.class
                                .getName()).log(Level.SEVERE, null, ex);
            }
            File newName = new File(getCsvFilePathFromDate(dates[1]));
            File oldName;
            if (dates.length == 4) { //fichier .gz

                wellDone = decompresserGzip(to.toString());
                oldName = new File(to.toString().substring(0, to.toString().length() - 3));

            } else {//case file in cvs Format
                oldName = new File(to.toString().substring(0, to.toString().length()));

            }
            wellDone = oldName.renameTo(newName);
        }
        return wellDone;

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
    
    /**
     * supprimer le fichier de la date selectionner par l'utilisateur
     *
     * @param date sous format yyyymm 
     * @return boolean
     */
    public static boolean deleteCSVFile(String date) {
        
//        if(date.equals("donnes")){
//            //vider le dossier données
//        }
        String path=(date.length()==4)?getYearFilePathFromDate(date) :Utilitaire.getCsvFilePathFromDate(date) ;
        File file = new File(path);

        if (file.isDirectory()) {
            try {
                //suppression du dossier vide
                //suppression du dossier vide
                FileUtils.cleanDirectory(file);
            } catch (IOException ex) {
                Logger.getLogger(Utilitaire.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return (file.delete());
    }
    
    public static int[] getCurrentDate() {
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        int[] temp = new int[3];
        
        temp[0] = localTime.getHour();
        temp[1] = localTime.getDayOfMonth();
        temp[2] = localTime.getMonthValue();
        temp[3] = localTime.getYear();
        
        return temp;
    }
    
    /**
     * Method classique qui retourne pour un mois donner le dernier jour de ce
     * moi EX: le mois JUIN(06) il contient 30 jours
     *
     * @param year
     * @param month
     * @return le nombre de jour de ce mois
     */
    public static int getNumberDaysOfMonth(int year, int month) {
        int currentDay, currentMonth, currentYear;
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.of(year, Month.of(month), 3, 3, 3);
        LocalDateTime lastDay = localTime.with(TemporalAdjusters.lastDayOfMonth());

        return lastDay.getDayOfMonth();
    }
    
}

