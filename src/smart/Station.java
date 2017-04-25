/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utilitaire.Utilitaire;

/**
 * This class is a secondary part of our Model , an object of this class represents a station
 * which it contains Map  of Different objects of type : Annee that reprensts Data for one year
 *
 */
public class Station {
    private int id;
    private String nom;
    private Point point;
    private Map<Integer,Annee> anneeList;
    
    public Station(int id,String nom,Point point) {
        this.id = id;
        this.nom = nom;
        this.point = point;
        anneeList = new HashMap<Integer,Annee>();
    }
    /**
     * donne une année demandé en paramétre si elle existe sinon elle le crée
     * @param annee
     * @return un objet de type année
     */
    public Annee getAndCreateAnnee(int annee) 
    {   Boolean bool = anneeList.containsKey(annee);
        if(!bool)
            anneeList.put(annee,new Annee(annee));
        return (anneeList.get(annee));
    }
    /**
     * permet de builder les années manquantes
     */
    public void buildMissingYears() {
        int[] currentDate = Utilitaire.getCurrentDate();
        int latestYear = currentDate[3];


        for (int j = 1996; j <= latestYear; j++) {
            Annee annee = new Annee(j);
            annee.buildMissingMonths();
            anneeList.put(j, annee);
        }
    }
    
    public Annee getAnnee(int annee) {
        return anneeList.get(annee);
    }
    
    public String getPoint() {
        return point.getX() +"x"+point.getY();
    }

    public Point getPointObj() {
        return this.point;
    }
    
    public String getNom() {
        return this.nom;
    }
    
    public String getId() {
        return String.valueOf(id);
    }

    /**
     * Checks if this station contains full DATA (data for all 
     * years AND data for all month for each year AND data for all days for each month)
     * 
     * @return true if station contains full data
     *         false otherwise
     */
    public boolean isUpdated() {
        int lastYear;
        
        lastYear = Utilitaire.getCurrentDate()[3];
       
        for (int i = 1996; i <= lastYear; i++) {
            if (!anneeExists(i) || !getAnnee(i).isUpdated()) {
                //si le jour n'existe pas , ou bien le jour exist mais il contient pas touts les relevés
                return false;
            }   
        }
        return true;
    }
    /**
     * permet de savoir si une année existe dans la structure ou pas
     * @param annee
     * @return true si l'année existe, false sinon
     */
    public boolean anneeExists(int annee) {
        return anneeList.containsKey(annee);
    }
   
}
    