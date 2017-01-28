package meteo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
     * @throws IOException
     */
    public static void downLoadCsv() throws IOException {
        try {
            File saveFile;
            URL url;

            //Creation d'un obj url qui pointe vers l'url qui se trouve dans la classe Configuration
            url = new URL(Configuration.DATA_GZIP_URL);
            //Creation d'un fichier ou on va sauvegarder le fichier telecharger
            saveFile = new File(Configuration.GZIP_FILE_SAVE_NAME);
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
     */
    public static void DecompresserGzip() {
        byte[] buffer = new byte[1024];
        try {
            //initialiser notre flux d'entrer par le fichier gzip déja telecharger
            FileInputStream fileIn = new FileInputStream(Configuration.GZIP_FILE_SAVE_NAME);
            //initialiser un flux d'entrer de type gzip
            GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
            //creation d'un flux de sortie vers un fichier (le fichier ou on va mettre ce qu'on a decompressé) 
            FileOutputStream fileOutputStream = new FileOutputStream(new File(Configuration.getApplicationPath() + "/" + Configuration.CSV_FILE_NAME));
            int bytes_read;
            //on lit des obj byte depuis le flux d'entrer GZIP , et on les mets dans le flux de sortie 
            while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytes_read);
            }

            //fermetures des flux
            gZIPInputStream.close();
            fileOutputStream.close();
            System.out.println("Le fichier a été decompressé correctement ! ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
