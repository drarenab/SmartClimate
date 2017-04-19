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

import coordonnee.DataBean2;
import coordonnee.DataCity;
import coordonnee.aDate;
import utilitaire.Utilitaire;

import javax.rmi.CORBA.Util;

/**
 * @author SEIF
 */
public class Jour {

    private int id;
    private Map<Integer, Releve> relevesList;

    public Jour(int id) {
        this.id = id;
        relevesList = new HashMap<Integer, Releve>();
    }

    /*Needed for tests only*/
    /*public boolean createReleve(int ordre,Releve releve) {
        
    }*/


    public void copyAll(Map<Integer, Releve> list) {
        this.relevesList = list;
    }

    public Releve getAndCreateReleve(int ordre, Releve releve) {
        Boolean bool = relevesList.containsKey(ordre);
        if (!bool)
            relevesList.put(ordre, releve);

        return (relevesList.get(ordre));
    }

    /**
     * TESTED WITH JUNIT
     * <p>
     * Checks if data is updated for the day , and if it contains all needed data for all releves
     *
     * @param year  the year which this day belongs
     * @param month the month which this day belongs
     * @return true if data is fully updated
     * false if data is not fully updated
     */
    public boolean isUpdated(int year, int month) {
        int currentYear, currentMonth, currentDay;
        int currentHour;
        boolean today = false;
        int[] currentDate = Utilitaire.getCurrentDate();
        currentHour = currentDate[0];
        currentDay = currentDate[1];
        currentMonth = currentDate[2];
        currentYear = currentDate[3];
        if (currentYear == year
                && //si le fichier contient les donnees de l'année courante
                currentMonth == month
                && //et si le fichier contient les donnees mois courant
                currentDay == id
                )
            today = true;

        for (int i = 0; i < 24; i++) {
            if (i % 3 != 0)
                continue;

            //si date ajourd'hui et ordre de releve inferieure a l'heure actuelle,  donc le relevé doit forcement exister sinon erreure
            if (today &&
                    (i / 3) <= (currentHour / 3) &&
                    !relevesList.containsKey(i / 3)) {
                return false;// donc le jour n'est pas a jour 
            } else if (!today &&
                    !relevesList.containsKey(i / 3)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the missing <relevées>  on this object
     *
     * @param year
     * @param month
     * @return List of missing <relevées>
     */
    public Map<Integer, Releve> getMissingData(int year, int month) {
        Map<Integer, Releve> missingList = new HashMap<Integer, Releve>();
        int currentYear, currentMonth, currentDay;
        int currentHour;
        boolean today = false;
        int[] currentDate = Utilitaire.getCurrentDate();
        currentHour = currentDate[0];
        currentDay = currentDate[1];
        currentMonth = currentDate[2];
        currentYear = currentDate[3];
        if (currentYear == year
                && //si le fichier contient les donnees de l'année courante
                currentMonth == month
                && //et si le fichier contient les donnees mois courant
                currentDay == id
                )
            today = true;

        for (int i = 0; i < 24; i++) {
            if (i % 3 != 0)
                continue;

            //si date ajourd'hui et ordre de releve inferieure a l'heure actuelle,  donc le relevé doit forcement exister sinon erreure
            if (today &&
                    (i / 3) <= (currentHour / 3) &&
                    !relevesList.containsKey(i / 3)) {
                // donc le jour n'est pas a jour 
                missingList.put(i / 3, new Releve(i / 3, -1, -1, -1));
            } else if (!today &&
                    !relevesList.containsKey(i / 3)) {
                missingList.put(i / 3, new Releve(i / 3, -1, -1, -1));
            }
        }
        return missingList;
    }

    public void showAll()
    {
        for(Releve releve : relevesList.values()) {
            System.out.println("ordre:"+releve.getOrdre()+" temperature:"+releve.getTemperature());
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, Releve> getMissingReleves(int year, int month) {
        Map<Integer, Releve> missingList = new HashMap<Integer, Releve>();
        int currentYear, currentMonth, currentDay;
        int currentHour,latestReleve;
        boolean today = Utilitaire.isCurrentDate(year,month,id,0);
        int[] currentDate = Utilitaire.getCurrentDate();


        if(!today)
            latestReleve =7;
        else
            latestReleve =currentDate[0]/3;

        for(int i=0;i<=latestReleve;i++) {
            if(!releveExists(i))
                missingList.put(i,new Releve(i,-1,-1,-1));
        }

        return missingList;
    }


    public Releve getReleve(int ordre) {

        return relevesList.get(ordre);

    }

    public boolean releveExists(int ordre) {
        return relevesList.containsKey(ordre);
    }

    public boolean addReleve(int ordre, float temperature, float humidite, float nebulosite) {
        return relevesList.put(ordre, new Releve(ordre, temperature, humidite, nebulosite)) != null;
    }


    public void buildMissingReleves(int year, int month) {
        boolean isCurrentDay = Utilitaire.isCurrentDate(year, month, id, 0);
        int latestReleve;
        int[] currentDate = Utilitaire.getCurrentDate();
        if (isCurrentDay)
            latestReleve = currentDate[0] / 3;
        else
            latestReleve = 7;

        for (int j = 0; j <= latestReleve; j++) {
            relevesList.put(j, new Releve(j, -1, -1, -1));
        }
    }

    /**
     * @return un releve representant la moyenne des données d'un jour
     */
    public Releve getMoyenneJour() {//gerer le cas de note manquante
        float temperature = 0;
        float humidite = 0;
        float nebulosite = 0;

        for (Map.Entry<Integer, Releve> entry : relevesList.entrySet()) {
            temperature += entry.getValue().getTemperature();
            humidite += entry.getValue().getHumidite();
            nebulosite += entry.getValue().getNebulosite();
        }

        temperature /= relevesList.size();
        humidite /= relevesList.size();
        nebulosite /= relevesList.size();

        return new Releve(0, temperature, humidite, nebulosite);
    }

    public Map<Integer, Releve> getReleves() {
        return relevesList;
    }

    public List<DataBean2> getAllReleves(int idStation, int annee, int mois) {
        List<DataBean2> tempList = new ArrayList<DataBean2>();

        for (Releve releve : relevesList.values()) {
            DataBean2 dataBean = new DataBean2(
                    idStation,
                    releve.getTemperature(),
                    releve.getHumidite(),
                    releve.getNebulosite(),
                    new aDate(
                            String.valueOf(annee),
                            String.valueOf(mois),
                            String.valueOf(id),
                            String.valueOf(releve.getOrdre())
                    )
            );

            tempList.add(dataBean);
        }
        return tempList;
    }
}
