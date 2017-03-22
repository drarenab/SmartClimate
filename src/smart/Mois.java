/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utilitaire.Utilitaire;

/**
 *
 * @author SEIF
 */
public class Mois {
    private int id;
    private Map<Integer,Jour> joursList;

    public Mois(int id) {
        this.id = id;
        joursList = new HashMap<Integer,Jour>();
    }
    
    public int getId() {
        return id;
    }
    
    /**
     * Checks if this month contains all data for all days , and if each day contains all releve data
     * @param annee the year in which this month belongs
     * @return true if the month is fully updated
     *         false if the month is not fully updated
     */
    public boolean isUpdated(int annee) {
        return true;
    }
    
    public Jour getAndCreateJour(int jour) 
    {   Boolean bool = joursList.containsKey(jour);
        if(!bool)
            joursList.put(jour,new Jour(jour));
        
        return (joursList.get(jour));
    }

    public Jour getJour(int jour) 
    {
        return joursList.get(jour);
    }
    
    public boolean jourExists(int jour ){
        return joursList.containsKey(jour);
    }
    
    public List<Releve> getReleves(int jour) {
        return null;
    }
    
    public List<Releve> getMoyennesParJour(int jour) {
        return null;
    } 
    
    public Releve calculMoyenneMois(int jour) {
        return null;
    }

}
