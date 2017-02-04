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
    public static void downLoadCsvByDate(String date,String outputFile) throws IOException {
        try {
            File saveFile;
            URL url;
            String newUrl;
           
            //Creation d'un obj url qui pointe vers l'url qui se trouve dans la classe Configuration
            newUrl = Configuration.DATA_GZIP_URL.replace("#", date);
            System.out.println("url:"+newUrl);
            url = new URL(newUrl); 
            //Creation d'un fichier ou on va sauvegarder le fichier telecharger
            saveFile = new File(outputFile);
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
     * @param inputFile le nom de fichier qui va etre decompresser
     * @param outputFile le nom de fichier resultat apres la decompression
     */
    public static void DecompresserGzip(String inputFile,String outputFile) {
        byte[] buffer = new byte[1024];
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
            File temp =  new File(inputFile);
            System.out.println("Le fichier a été decompressé correctement ! ");
            if(temp.delete())
                System.out.println("le fichier '"+inputFile+"' a été supprimer");
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
     
    /**
     * 
     * @param file1 fichier qui contient les donnée d'une date d'un mois
     * @param file2 fichier qui contient les donnée de plusieurs dates
     * @param newDate la date des donnée qui sont dans le <file1>
     */
    public static void concatenateCsvByDate(String file1,String file2,String newDate) {
        try {
            
            
            String[] lineStringTable;
            
            String tempName=file2;
            File f1,f2,f3;
            FileReader fr1,fr2;
            FileWriter fw3;
            String line1,line2;
            
            BufferedReader br1,br2;
            BufferedWriter bw3;
            String oldDate;
            
            f1 = new File(file1);
            f2 = new File(file2);
            //fichier temporaire
            f3 = new File("new_"+tempName);
            
            fr1 = new FileReader(f1);
            fr2 = new FileReader(f2);
            fw3 = new FileWriter(f3);
            
            
            br1 = new BufferedReader(fr1);
            br2 = new BufferedReader(fr2);
            bw3 = new BufferedWriter(fw3);
            
            //lire une ligne
            line2 = br2.readLine();
            //sauter une line supplumentaire pour éviter le header
            line2 = br2.readLine();  
            
            // sauter et copier les lines qui sont avant le debut de la  date  donnée  
            while(line2!=null) {              
                
                //deviser la line en tab de string selon une regex et prendre la premiere case puis extraire la sous chaine qui contient la date(yyyymm)
                
                oldDate = line2.split(";")[1].substring(0,6);
           
                //si on trouve une date plus grande que la date de fichier 1(date) on sort!
                if(Integer.parseInt(oldDate)>Integer.parseInt(newDate))
                    break;
                
                //ecrire dans le fichier qui va contenir la concat des deux autres fichiers
                bw3.write(line2 + "\n");
                //lire une nouvelle line
                line2 = br2.readLine();
            }
            
            
            //maintenant le curseur est apres la date donnée (pattern), on peut commencer a inserer le fichier 
            line1 = br1.readLine();
            //sauter une line pour éviter le header
            line1 = br1.readLine();
            while(line1!=null) {
                bw3.write(line1 + "\n");
                line1 = br1.readLine();
            }
            
            
            //copier ce qui reste de file2 dans file3
            while(line2!=null) {
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
            if(f2.delete())
                System.out.println("File deleted");
            
            //renommer <f3> en lui donnant le meme nom comme celui de <f2>
            f3.renameTo(new File(tempName));
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
