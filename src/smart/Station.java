/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

import coordonnee.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        
        return (anneeList.get(annee));
    }
    
    public String getPoint() {
        return point.getX() +"x"+point.getY();
    }
    
    
    public String getNom() {
        return this.nom;
    }
    
    public String getId() {
        return String.valueOf(id);
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
    