package meteo;

import java.io.File;
import java.io.IOException;

/**
 *Cette class contient des variables globales qui seront utiliser par les autres classes
 * 
 * @author SEIF
 */
public class Configuration {
    //l'url de telechargement des donnée
    public static String DATA_GZIP_URL = "https://donneespubliques.meteofrance.fr/donnees_libres/Txt/Synop/Archive/synop.#.csv.gz";
    //le nom de fichier compressé qui sera telecharger
    public static String GZIP_FILE_SAVE_NAME = "output.csv.gz";
    //le nom de fichier temporaire qui sera telecharger avant faire la concaténation avec le fichier principal
    public static String GZIP_TEMPORARY_FILE_SAVE_NAME ="temporary.csv.gz";
    //le nom du fichier telecharger apres la decompression
    public static String CSV_FILE_NAME = "output.csv";
    
    public static String TEMPORARY_CSV_FILE_NAME = "temporary.csv";
    
    /**
     * Cette methode retourne le chemin de l'application
     * 
     */
    public static String getApplicationPath() throws IOException { 
            return (new File(".").getCanonicalPath());
    }
}
