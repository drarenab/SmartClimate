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
import main.*;
import smart.Point;
import smart.Ville;

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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import main.*;
        
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
            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
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
            if (Integer.parseInt(file.getName().substring(0, 6)) > maxFileName) {
                maxFileName = Integer.parseInt(file.getName().substring(0, 6));
            }
        }
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
            url = new URL(newUrl);

            //le chemin de fichier ou on va telecharger les donnés
            path = Configuration.getApplicationPath() + "/" + directory + "/" + date + ".csv.gz";
            //Creation d'un fichier ou on va sauvegarder le fichier telecharger
            saveFile = new File(path);

            //utilisation de la methode copyURLToFile de apache , qui telecharger et sauvegarde un fichier
            FileUtils.copyURLToFile(url, saveFile);
            
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
            if (temp.delete()) {
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
       String nameChemin = str[str.length - 1].substring(str[str.length - 1].indexOf("synop"));
            Path to = Paths.get(directory + "/" +nameChemin
            );
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
            if (dates[dates.length-1].equals("gz")) { //fichier .gz
                wellDone = decompresserGzip(to.toString());
                oldName = new File(to.toString().substring(0, to.toString().length() - 3));

            } else {//case file in cvs Format
                oldName = new File(to.toString().substring(0, to.toString().length()));

            }
            wellDone = oldName.renameTo(newName);
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
        Boolean fileIsDeleted =file.delete();
        File DirectoryIsEmpty=new File(getYearFilePathFromDate(date.substring(0,4)));
        if(DirectoryIsEmpty.isDirectory() && DirectoryIsEmpty.listFiles().length==0){
            DirectoryIsEmpty.delete();
        }
        return (fileIsDeleted);
    }
    
    /**
     * get the current date for EUROPE/PARIS ZONE
     * @return array of integers that contains at : 
     *                                  index 0 : HH
     *                                  index 1 : DD
     *                                  index 2 : MM
     *                                  index 3 : YYYY
     */
    public static int[] getCurrentDate() {
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        int[] temp = new int[4];
        
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


    /**
     * Cette Methode retourne la date exacte des donnée les plus recents
     * yyyymmjjhh
     */
    public static double netIsAvailable() {
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
     * This method tells if the date given on parameters corresponds to the CURRENT DATE ( TODAY)
     * @param year year to be checked
     * @param month month to be checked
     * @param day day to be checked
     * @param mode 0 comparing :year + month + day
     *             1 comparing :year + month
     *             2 comparing :year
     * @return TRUE if the date given on parameters is a current date
     *         FALSE else
     */
    public static boolean isCurrentDate(int year,int month,int day,int mode) {
        int currentYear,currentMonth,currentDay;
        int currentHour;
        boolean today = false;
        int[] currentDate= Utilitaire.getCurrentDate();
        currentHour = currentDate[0];
        //pour avoir 01 pour le premier jour de moi au lieu de 1
        currentDay = currentDate[1];
       // currentDay = ("00" + currentDay).substring(currentDay.length());
        
        currentMonth = currentDate[2];
        //currentMonth = ("00" + currentMonth).substring(currentMonth.length());

        currentYear = currentDate[3];
        
        
        if(mode==0)
            return currentYear == year
                    &&currentMonth == month
                    &&currentDay ==day;
        else if(mode==1)
            return currentYear == year
                    &&currentMonth==month;
        else if(mode==2)
            return currentYear==year;
        else
            return false;
}


    public static int whichMode(String year, String month, String day) {
        if (year.length() > 0 && month.length() > 0 && day.length() > 0)
            return 0;
        else if (year.length() > 0 && month.length() > 0)
            return 1;
        else if (year.length() > 0)
            return 2;

        else return -1; // invalid date !
    }
    
    /**
     * @param nebul
     * @return une chaine de caractére qui represente le nom de l'image associé
     * a la nébulosité d'une station donnée
     */
    public static String getTempsActuel(float nebul) {

        if (nebul < 33) {
            return "ensoleille";
        } else if (nebul < 66) {
            return "nuageux";
        } else if (nebul <= 100) {
            return "pluvieux";
        } else {
            return "NA";
        }

    }

}

