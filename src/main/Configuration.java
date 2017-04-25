package main;

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
        
    public static String DATA_DIRECTORY_NAME ="donnees";
    public static String CITY_FILE_NAME= "Configuration.txt";    
    public static String TEMPORARY_CSV_FILE_NAME = "temporary.csv";
    
    /**
     * Cette methode retourne le chemin de l'application
     * 
     */
    public static String getApplicationPath() throws IOException { 
            return (new File(".").getCanonicalPath());
    }
}
