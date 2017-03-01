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

}