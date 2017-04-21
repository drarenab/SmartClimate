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
 *
 * @author SEIF
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

    public Station() {
    }
    
    public Annee getAndCreateAnnee(int annee) 
    {   Boolean bool = anneeList.containsKey(annee);
        if(!bool)
            anneeList.put(annee,new Annee(annee));
        System.out.println("where is a problem");
        return (anneeList.get(annee));
    }

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
       
        System.out.println("lastMonth:" + lastYear);
        for (int i = 1996; i <= lastYear; i++) {
            if (!anneeExists(i) || !getAnnee(i).isUpdated()) {
                //si le jour n'existe pas , ou bien le jour exist mais il contient pas touts les relevés
                return false;
            }   
        }
        return true;
    }

    public boolean anneeExists(int annee) {
        return anneeList.containsKey(annee);
    }
    
    public boolean addReleve(int annee,int mois,int jour,int ordre, float temperature, float humidite, float nebulosite) 
    {   
       return true;
    }
    
    public List<Releve> getReleves(int annee,int mois,int jour) {
        return null;
    }
    
    public List<Releve> getMoyennesParMois(int mois) {
        return null;
    }
    
    public List<Releve> getMoyennesParJour(int mois) {
        return null;
    }
}
    