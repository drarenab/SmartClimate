package meteo;

import coordonnee.VilleTemp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import coordonnee.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.FileUtils;

/**
 * TODO getLastAvaialbleDateFile() ==> private called by getLastAvailableData()
 * : specefique pour la map
 *
 * getDataByDate() ==> will not use x,y points , null on point object as well as
 * city name
 *
 * important: add more cities to config file to map : city to id
 *
 *
 * ==> move coordonnee methods to downloader and reorganise all :D démenagement
 */

/**
 *
 * Le role de cette classe est de Telecharger des données depuis le site de
 * meteofrance , Sauvegarder ces données dans un fichier csv ,
 *
 * @author SEIF
 */
public class Downloader {

    
    private static Map<Integer, Ville> villes;
    private static String line;

    static {

        FileReader fr = null;
        try {
            File file = new File(Configuration.CITY_FILE_NAME);
            fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            line = br.readLine();

            villes = new HashMap<Integer, Ville>() {
                {
                    while (line != null) {

                        put(Integer.parseInt(line.split(";")[0]),
                                new Ville(line.split(";")[1],
                                        Integer.parseInt(line.split(";")[0]),
                                        new Point(Integer.parseInt(line.split(";")[2]), Integer.parseInt(line.split(";")[3]))
                                ));
                        line = br.readLine();
                    }

                }
            };

            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    public static int getIdFromNameVille(String name) {
        Integer key = null;
        
        for (Map.Entry<Integer, Ville> entry : villes.entrySet()) {
            
            Ville value = entry.getValue();
            
            if(value.getNom().compareTo(name)==0){
                 key = entry.getKey();
            }
        }    
            return key;
        
    }
    public static Ville getVilleFromId(int id) {
        return (villes.get(id));
    }
    
    public static void createDirectory(String directory) {
        File theDir = new File(directory);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + directory);
            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
    }
    
    /**
     * Cette mthode donne apartir une date de la forme yyymmjjhh.. , le chemin vers le fichier csv qui contient cette date
     * @param date
     * @return 
     */
    public static String getCsvFilePathFromDate(String date) {
        return Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv";
    }

     /**
     * Cette mthode donne apartir une date de la forme yyymmjjhh.. , le chemin vers le fichier csv.gz qui contient cette date
     * @param date
     * @return 
     */
    public static String getGzipFilePathFromDate(String date) {
        return Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv.gz";
    }

    
    /**
     * Methode static qui telecharger et sauvegarde un fichier depuis un URL
     *
     * @param date la date de telechargement (yyyymm)
     * @param outputfile le nom de fichier apres le telechargement(*.csv.gz)
     */
    public static String downLoadCsvByDate(String date) throws IOException {
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
            System.out.println("url:" + newUrl);
            url = new URL(newUrl);

            //le chemin de fichier ou on va telecharger les donnés
            path = Configuration.getApplicationPath() + "/" + directory + "/" + date + ".csv.gz";
            //Creation d'un fichier ou on va sauvegarder le fichier telecharger
            saveFile = new File(path);

            //utilisation de la methode copyURLToFile de apache , qui telecharger et sauvegarde un fichier
            FileUtils.copyURLToFile(url, saveFile);

            return path;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
    public static String downLoadCsvByYear(String year) throws IOException {
        return "c";
    }
    
    /**
     * **
     * Methode static qui fait la decompression d'un fichier Gzip et sauvegarde
     * le fichier decompressé
     * @param inputFile le nom de fichier qui va etre decompresser
     * @param outputFile le nom de fichier resultat apres la decompression
     */
    public static void DecompresserGzip(String inputFile) {
        byte[] buffer = new byte[1024];
        //on garde le meme nom pour le fichier decompresser sauf .gz
        String outputFile = inputFile.substring(0, inputFile.length() - 3);

        System.out.println("output file : " + outputFile);
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
            System.out.println("Le fichier a été decompressé correctement ! ");
            if (temp.delete()) {
                System.out.println("le fichier '" + inputFile + "' a été supprimer");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Cette methode Donne les donnée qui correspond a une date dans une liste, 
     * @param date la date des donner quand veux recuperer dans la list : Si yyyymm fournit elle donne tout les donner du moi , 
     *                                                                    Si yyyymmjj elle donne les donner du jour jj,
     * @param cityId la ville des donner quand veux recuperer dans la list, si elle contient "all" la mthode retourne les donner de tout les villes
     * @return une arrayList de type VilleTemp qui contient les donner demander
     */
    public static ArrayList<VilleTemp> getDataForDateByCity(String date, String cityId) {
        // EX: date=20140231 (31 fevrier 2014) ==> va chercher si le dossier 2014 exist et si'il contient le fichier 201402 , et si ce dernier fichier contient 
        //les données de la date demander
        System.out.println("Recuperation des donnée de la ville => "+cityId+" et la date =>"+date);
        String fileName = Configuration.DATA_DIRECTORY_NAME + "/" + date.substring(0, 4) + "/" + date.substring(0, 6) + ".csv";
        String idVille;
        float nebu;
        double temperature;
        int himudite;
        aDate adate;
        //si le fichier de donnée correspondant n'existe pas 
        if (!checkIfFileExists(fileName))             
            return null;
        
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
                        //System.out.println("match date=>"+dateLine);
                        // si on a bien matcher une date 
                        //recuperation des données apartir du fichier 
                        temperature = !splitedLine[7].equals("mq") ? Double.parseDouble(splitedLine[7]) -273.15 : 101;
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
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    
    public static ArrayList<VilleTemp> getDataForYearByCity(String date, String cityId) {
        String year = date.substring(0,4);
        ArrayList<VilleTemp> liste= new ArrayList<VilleTemp>();
        ArrayList<VilleTemp> tempList = new ArrayList<VilleTemp>();
        String yearMonth;
        for(int i=1;i<=12;i++) {
            if(i<10) 
                yearMonth = year + ("00" + i).substring("i".length());
            else
                yearMonth = year + String.valueOf(i);
            
            tempList = getDataForDateByCity(yearMonth, cityId);
            if(tempList==null)
                return null;
            
            liste.addAll(tempList);
            //System.out.println("tour:"+i);
        }
        
        return liste;
    }
    
    
    /**
     * Cette methode cherche le fichier le plus recent (qui contient les données
     * les plus recentes)
     *
     * @return le chemin du fichier le plus recent
     */
    public static String getLatesttAvailableFile() {
        int maxYear = 0, maxFileName = 0;
        File file1 = new File(Configuration.DATA_DIRECTORY_NAME);
        for (File file : file1.listFiles()) {
            System.out.println("name:" + file.getName());
            if (Integer.parseInt(file.getName()) > maxYear) {
                maxYear = Integer.parseInt(file.getName());
            }
        }

        file1 = new File(Configuration.DATA_DIRECTORY_NAME + "/" + maxYear);
        for (File file : file1.listFiles()) {
            System.out.println("name:" + file.getName());
            if (Integer.parseInt(file.getName().substring(0, 6)) > maxFileName) {
                maxFileName = Integer.parseInt(file.getName().substring(0, 6));
            }
        }
        System.out.println("pathDate:" + maxFileName);
        if (maxFileName == 0) {
            return null;
        } else {
            return String.valueOf(maxFileName);
        }
    }

    /**
     * Cette methode retourne la date des donner la plus recente inclut dans un fichier 
     * @param date sous la forme de yyyymm correspond au nom du fichier qu'on veux chercher dedans
     * @return la date la plus recente dans le fichier qui correspond a @date
     */
    public static String getLatestAvailableDateOnFile(String date) {
        File f;
        FileReader fr;
        BufferedReader br;
        String line;
        int latestDate=0;
        String dateLine;
        String filePath;
        
        try {
            filePath = getCsvFilePathFromDate(date);
            if(!checkIfFileExists(filePath))
                return null;
            
            f = new File(filePath);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            line = br.readLine();
            
            if (line.startsWith("numer_sta")) {
                line = br.readLine();
            }

            
            while (line != null) {
                dateLine = line.split(";")[1].substring(0,10);
                
                if(Integer.parseInt(dateLine)>latestDate) {
                    latestDate = Integer.parseInt(dateLine);
                }
                
                line = br.readLine();
            }

            fr.close();
            br.close();
            return String.valueOf(latestDate);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
     *
     * @param date sous forme yyyymmjj
     * @return  false si pas de connexion ou si le fichier et a jour sinon elle retourne vrai si le fichier a était télécharger et decompresser
     */
    //manque la fonction qui telecharge toute une année si la date est du format yyyy
    
    public static boolean telechargerETdecompresser(String date, String onLine_offLine ) {
        
        /****
         verifier si netisavailable 
         * verifier si en mode en ligne
         * verifier si le fichier rechercher n'est pas deja présent  a faire avant de faire appel a cette methode
         * telecharger et décompresser
         
         ****/
        //verifier si le fichier du mois existe
        if (netIsAvailable() && onLine_offLine == "onLine" ) {
            //String lastDate = getLatestAvailableDateOnFile(date);
            //System.out.println(Integer.parseInt(lastDate) +"date" +Integer.parseInt(date));
           
            //if (lastDate == null || Integer.parseInt(lastDate.substring(6, 8)) < Integer.parseInt(date.substring(6, 8))) {
                //telechargement
                try {

                   return Downloader.downLoadCsvByDate(date.substring(0, 6))!=null;
                    
                } catch (IOException ex) {
                    Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
                }
                //decompression
                DecompresserGzip(getGzipFilePathFromDate(date));
            /*} else {
                System.out.println("pas besoin de telecharger les données nécessaires existes déja");
                
            }*/
        } else {
            
            System.out.println("No connection !");
            
        }
    return false;
    }
    
       /**
      * Cette Methode retourne la date exacte des donnée les plus recents yyyymmjjhh 
      * @param date sous la forme de yyyymm
      * @return date sous form yyyymmjjhh
      */
public static boolean netIsAvailable(){
        try {
        final URL url = new URL("http://donneespubliques.meteofrance.fr");
        final URLConnection conn = url.openConnection();
        conn.connect();
        return true;
    } catch (MalformedURLException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        return false;
    }
    }
   
}
