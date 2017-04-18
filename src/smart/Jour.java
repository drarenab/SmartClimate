/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utilitaire.Utilitaire;

/**
 *
 * @author SEIF
 */
public class Jour {

    private int id;
    private Map<Integer, Releve> relevesList ;
 
    public Jour(int id) {
        this.id = id;
        relevesList= new HashMap<Integer,Releve>();
    }

    /*Needed for tests only*/
    /*public boolean createReleve(int ordre,Releve releve) {
        
    }*/
    
    
    public void copyAll(Map<Integer,Releve> list) {
        this.relevesList = list;
    }
    
    public Releve getAndCreateReleve(int ordre,Releve releve) 
    {   Boolean bool = relevesList.containsKey(ordre);
        if(!bool)
            relevesList.put(ordre,releve);
        
        return (relevesList.get(ordre));
    }
    
    /**
     * TESTED WITH JUNIT
     * 
     * Checks if data is updated for the day , and if it contains all needed data for all releves
     * @param year the year which this day belongs 
     * @param month the month which this day belongs    
     * @return true if data is fully updated
     *         false if data is not fully updated
     */
    public boolean isUpdated(int year,int month)
    {   int currentYear,currentMonth,currentDay;
        int currentHour;
        boolean today = false;
        int[] currentDate= Utilitaire.getCurrentDate();
        currentHour = currentDate[0];
        currentDay = currentDate[1];
        currentMonth = currentDate[2];
        currentYear = currentDate[3];
        if (currentYear==year
                && //si le fichier contient les donnees de l'année courante
                currentMonth==month
                && //et si le fichier contient les donnees mois courant
                currentDay==id
                ) 
            today = true;
        
        for(int i=0;i<24;i++) {
            if(i%3!=0)
                continue;
   
            //si date ajourd'hui et ordre de releve inferieure a l'heure actuelle,  donc le relevé doit forcement exister sinon erreure
            if(today&&
                    (i/3)<=(currentHour/3)&&
                    !relevesList.containsKey(i/3)) {
                return false;// donc le jour n'est pas a jour 
            }   
            else if(!today&&
                    !relevesList.containsKey(i/3)) {
                return false;  
            }
        }
        return true;
    }
     
    /**
     * Returns the missing <relevées>  on this object
     * @param year
     * @param month
     * @return List of missing <relevées>
     */
    public Map<Integer,Releve> getMissingData(int year,int month) {
        Map<Integer,Releve> missingList = new HashMap<Integer,Releve>();
        int currentYear,currentMonth,currentDay;
        int currentHour;
        boolean today = false;
        int[] currentDate= Utilitaire.getCurrentDate();
        currentHour = currentDate[0];
        currentDay = currentDate[1];
        currentMonth = currentDate[2];
        currentYear = currentDate[3];
        if (currentYear==year
                && //si le fichier contient les donnees de l'année courante
                currentMonth==month
                && //et si le fichier contient les donnees mois courant
                currentDay==id
                ) 
            today = true;
        
        for(int i=0;i<24;i++) {
            if(i%3!=0)
                continue;
   
            //si date ajourd'hui et ordre de releve inferieure a l'heure actuelle,  donc le relevé doit forcement exister sinon erreure
            if(today&&
                    (i/3)<=(currentHour/3)&&
                    !relevesList.containsKey(i/3)) {
                // donc le jour n'est pas a jour 
                missingList.put(i/3,new Releve(i/3,-1,-1,-1));
            }   
            else if(!today&&
                    !relevesList.containsKey(i/3)) {
                missingList.put(i/3,new Releve(i/3,-1,-1,-1));
            }
        }
        return missingList;
    }
    
    public Releve getReleve(int ordre) {

        return relevesList.get(ordre);

    }
    
    public boolean releveExists(int ordre) {
        return relevesList.containsKey(ordre);
    }
    
    public boolean addReleve(int ordre, float temperature, float humidite, float nebulosite)
    {
        return relevesList.put(ordre,new Releve(ordre,temperature,humidite,nebulosite))!=null;
    }
    
    /**
 * 
 * @return un releve representant la moyenne des données d'un jour
 */
    public Releve getMoyenneJour() {//gerer le cas de note manquante
        float temperature = 0;
        float humidite = 0;
        float nebulosite = 0;
        
        for (Map.Entry<Integer, Releve> entry : relevesList.entrySet()) {
           temperature+= entry.getValue().getTemperature();
           humidite+= entry.getValue().getHumidite();
           nebulosite+= entry.getValue().getNebulosite();
        }
          
        temperature/=relevesList.size();
        humidite/=relevesList.size();
        nebulosite/=relevesList.size();
        
        return new Releve(0, temperature, humidite, nebulosite);
    }

    public Map<Integer, Releve> getReleves() {
        return relevesList;
    }

}
