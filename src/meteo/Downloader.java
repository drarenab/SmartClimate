package meteo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Le role de cette classe est de Telecharger des données depuis le site de
 * meteofrance , Sauvegarder ces données dans un fichier csv ,
 *
 * @author SEIF
 */
public class Downloader {

    /**
     * Methode static qui telecharger et sauvegarde un fichier depuis un URL
     *
     * @param date la date de telechargement (yyyymm)
     * @param outputfile le nom de fichier apres le telechargement(*.csv.gz)
     */
    
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
    
    //a faire passer url dans les parametres
    public static void downLoadCsvByDate(String date) throws IOException {
        try {
            File saveFile;
            URL url;
            String newUrl;
            String directory;

            //avoir l'année depuis la date , pour telecharger le fichier dans le dossier qui correspond a l'année
            directory = Configuration.DATA_DIRECTORY_NAME+"/"+date.substring(0, 4);
            createDirectory(directory);
            

            //Creation d'un obj url qui pointe vers l'url qui se trouve dans la classe Configuration
            newUrl = Configuration.DATA_GZIP_URL.replace("#", date);
            System.out.println("url:" + newUrl);
            url = new URL(newUrl);

            //Creation d'un fichier ou on va sauvegarder le fichier telecharger
            saveFile = new File(Configuration.getApplicationPath() + "/" + directory + "/" + date + ".csv.gz");

            //utilisation de la methode copyURLToFile de apache , qui telecharger et sauvegarde un fichier
            FileUtils.copyURLToFile(url, saveFile);

        } catch (MalformedURLException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Methode static qui fait la decompression d'un fichier Gzip et sauvegarde
     * le fichier decompressé
     *
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
    
      //returns the file path from the file date 
    public static String getFilePathFromDate(String date) {
        return Configuration.DATA_DIRECTORY_NAME+"/"+date.substring(0,4);
    }

    
    //year
    //mois
    //jour  
    public static String checkIfDataForDateExists(String date){
        // EX: date=20140231 (31 fevrier 2014) ==> va chercher si le dossier 2014 exist et si'il contient le fichier 201402 , et si ce dernier fichier contient 
        //les données de la date demander
        String fileName = Configuration.DATA_DIRECTORY_NAME +"/"+ date.substring(0,4)+"/"+ date.substring(4,6)+".csv";
        
        //si le fichier de donnée correspondant n'existe pas 
        if(!checkIfFileExists(fileName)) {
            return null;
        }
        else {
           
            try {
                //parcourir le fichier correspondant et chercher si la date demander jour et heure
                File dateFile = new File(fileName);
                FileReader dataFR = new FileReader(dateFile);
                BufferedReader dataBR = new BufferedReader(dataFR);
                String line,dateLine;
                
                Pattern pattern = Pattern.compile(date+"*;");
                line = dataBR.readLine();
                while(line!=null) {
                    dateLine = line.split(";")[1];
                    if(pattern.matcher(dateLine).matches()) {
                        // si on a bien matcher une date 
                        
                        
                    }
                        
                    line = dataBR.readLine();
                }
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
            } 
           
        }
        return "haja";
    }
    
    /**
     * Cette methode determine si un fichier existe ou pas 
     * @param file le fichier qu'on va chercher
     * @return TRUE si le fichier existe, FALSE sinon
     */
    public static boolean checkIfFileExists(String file) {
        return (new File(file).exists());
    } 
    
    public static void addDateTofile(String datafile, String datesFile) {
        FileReader dataFR, availableDatesFR;
        FileWriter availableDatesFWTemp;

        BufferedReader dataBR, availableDatesBR;

        BufferedWriter availableDatesBWTemp;

        File dataFile, availableDatesFile, availableDatesFileTemp;
        String currentLine = "", previousLine = "", lastDate = "";
        try {

            dataFile = new File(datafile);
            dataFR = new FileReader(dataFile);
            dataBR = new BufferedReader(dataFR);

            availableDatesFile = new File(datesFile);
            availableDatesFR = new FileReader(availableDatesFile);
            availableDatesBR = new BufferedReader(availableDatesFR);

            availableDatesFileTemp = new File("new_" + datesFile);
            availableDatesFWTemp = new FileWriter(availableDatesFileTemp);
            availableDatesBWTemp = new BufferedWriter(availableDatesFWTemp);

            //aller jusqu'a la fin du fichier <datafile>
            while ((currentLine = dataBR.readLine()) != null) {
                previousLine = currentLine;
            }
            // on est a la fin du fichier
            // et previousLine contient la derneire ligne qui a été lus

            lastDate = previousLine.split(";")[1].substring(0, 10); //yyyymmddhh

            String date1 = lastDate.substring(0, 6);//yyyymm
            String date2;

            //mainentant on mit a jour le fichier des donnée 
            currentLine = availableDatesBR.readLine();

            while (currentLine != null) {
                date2 = currentLine.substring(0, 6);
                if (date1.equals(date2)) {
                    availableDatesBWTemp.write(currentLine.substring(0, 6) + "#" + lastDate + "\n");
                    System.out.println("match!");
                } else {
                    availableDatesBWTemp.write(currentLine + "\n");
                }

                currentLine = availableDatesBR.readLine();
            }
            dataBR.close();
            availableDatesBR.close();
            availableDatesBWTemp.close();

            //suppression de fichier <datafile> car on n'en a plus besoin , on a déja créer availableDatesFileTemp le tout!
            if (availableDatesFile.delete()) {
                System.out.println("File deleted");
            }

            //renommer <f3> en lui donnant le meme nom comme celui de <f2>
            availableDatesFileTemp.renameTo(new File(datesFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //format date : yyyymm
    //return un truc : yyyymmddhh EX: 20140609 => 9 juin 2014 a 9h
    public static String checkLastAvailableDataForDate(String date, String file) {
        File f;
        FileReader fr;
        BufferedReader br;
        String line;
        try {

            f = new File(file);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
                if (line.substring(0, 6).equals(date)) {
                    return line.substring(7, 17);
                }

                line = br.readLine();
            }

            fr.close();
            br.close();
            return null;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     *
     * @param file1 fichier qui contient les donnée (temperatures) d'un mois
     * @param file2 fichier qui contient les donnée (temperatures) de plusieurs
     * mois dates
     * @param newDate la date des donnée qui sont dans le <file1>
     */
    public static void concatenateCsvByDate(String file1, String file2) {
        try {

            String tempName = file2, newDate;
            File f1, f2, f3;
            FileReader fr1, fr2;
            FileWriter fw3;
            String line1, line2;

            BufferedReader br1, br2;
            BufferedWriter bw3;
            String oldDate = null;

            f1 = new File(file1);
            f2 = new File(file2);
            //fichier temporaire : on va l'utiliser pour fusionner les deux
            f3 = new File("new_" + tempName);

            fr1 = new FileReader(f1);
            fr2 = new FileReader(f2);
            fw3 = new FileWriter(f3);

            br1 = new BufferedReader(fr1);
            br2 = new BufferedReader(fr2);
            bw3 = new BufferedWriter(fw3);

            /**
             * étape1 : recuperer la date des donnée (yyyymm) depuis <file1>
             */
            //lire la premiere ligne depuis <file1>
            line1 = br1.readLine();

            //sauter la premiere ligne si elle contient un string , pour éviter les erreurs
            if (line1.startsWith("numer_sta")) {
                System.out.println("Premiere ligne sauter dans <" + file1 + ">");
                line1 = br1.readLine();
            }

            //Recupération de la date des temperatures depuis le petit fichier <file1>
            newDate = line1.split(";")[1].substring(0, 6);

            /**
             * étpae2: copiers le données de <file2> des dates inferieure a la
             * date de <file1> dans le fichier temporaire (pour avoire un
             * fichier fusioner ET Trié :p)
             */
            //lire une ligne depuis le <file2>
            line2 = br2.readLine();
            //sauter la premiere ligne si elle contient un string , pour éviter les erreurs
            if (line2.startsWith("numer_sta")) {
                System.out.println("Premiere ligne sauter dans <" + file2 + ">");
                line2 = br2.readLine();

            }

            //copier les donnée qui des dates inferieure a la date de <file1>  
            while (line2 != null) {
                //deviser la line en tab de string selon une regex et prendre la premiere case puis extraire la sous chaine qui contient la date(yyyymm)
                oldDate = line2.split(";")[1].substring(0, 6);

                //si on trouve une date plus grande que la date de fichier 1(date) on sort!
                if (Integer.parseInt(oldDate) >= Integer.parseInt(newDate)) {
                    break;
                }

                //ecrire dans le fichier qui va contenir la concat des deux autres fichiers
                bw3.write(line2 + "\n");
                //lire une nouvelle line
                line2 = br2.readLine();
            }

            /**
             * Etape 3 : supprimer (sauter) , les données de la meme dates que
             * celle de <file1>
             */
            //On supprime tout les donnée qui ont  la meme date que celle de file 1
            //EX: file1 contient les donnée de la date 201203 , alors avant de copier les donnée de file1 vers file2 on doit supprimer tout les donnée 
            //de la date 201203 depuis file2 (pour eviter d'avoir des doublons)
            while ((line2 != null)) {
                oldDate = line2.split(";")[1].substring(0, 6);
                if (Integer.parseInt(oldDate) > Integer.parseInt(newDate)) {
                    break;
                }
                System.out.println("tick");
                line2 = br2.readLine();

            }

            /**
             * Etape 4 : copiers les données de <file1>
             */
            //maintenant le curseur est apres la date donnée, on peut commencer a inserer les données de file1 dans file2    
            while (line1 != null) {
                bw3.write(line1 + "\n");
                line1 = br1.readLine();
            }

            /**
             * Etape5 : copier ce qui reste de file2
             */
            //copier ce qui reste de file2 dans file3
            while (line2 != null) {
                bw3.write(line2 + "\n");
                line2 = br2.readLine();
            }

            //on doit renomer les fichiers 
            bw3.close();
            br1.close();
            br2.close();
            fr1.close();
            fr2.close();
            fw3.close();

            //suppression de <f2> car on n'en a plus besoin , on a déja créer f3 qui contient la concaténation !
            if (f2.delete()) {
                System.out.println("File deleted");
            }

            //renommer <f3> en lui donnant le meme nom comme celui de <f2>
            f3.renameTo(new File(tempName));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
