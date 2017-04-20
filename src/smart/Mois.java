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

import coordonnee.DataBean2;
import coordonnee.aDate;
import utilitaire.Utilitaire;

/**
 * @author SEIF
 */
public class Mois {

    private int id;
    private Map<Integer, Jour> joursList;

    public Mois(int id) {
        this.id = id;
        joursList = new HashMap<Integer, Jour>();
    }

    public int getId() {
        return id;
    }

    //needed for tests
    public void copyAll(Map<Integer, Jour> list) {
        this.joursList = list;
    }

    /**
     * Checks if this month contains all data for all days , and if each day
     * contains all releve data
     *
     * @param year the year in which this month belongs
     * @return true if the month is fully updated false if the month is not
     * fully updated
     */
    public boolean isUpdated(int year) {
        int lastDay;
        boolean currentDate = Utilitaire.isCurrentDate(year, id, -1, 1);

        if (currentDate) {
            lastDay = Utilitaire.getCurrentDate()[1];
        } else {
            lastDay = Utilitaire.getNumberDaysOfMonth(year, id);
        }
        /*
        if(jourExists(lastDay))
            return joursList.get(lastDay).isUpdated(year, id);
        else
            return false;
         */
        System.out.println("lastDay:" + lastDay);
        for (int i = 1; i <= lastDay; i++) {
            if (!jourExists(i) || !getJour(i).isUpdated(year, id)) {
                //si le jour n'existe pas , ou bien le jour exist mais il contient pas touts les relevés
                if (jourExists(i) && !getJour(i).isUpdated(year, id)) {
                    System.out.println("Not updated day id=" + id);
                } else if (!jourExists(i))
                    System.out.println("day doesn't exists id=" + i);

                return false;

            }
        }

        return true;
    }

    public Map<Integer, Jour> getMissingData(int year) {
        int lastDay, latestReleve;
        boolean isCurrentDate = Utilitaire.isCurrentDate(year, id, -1, 1);
        boolean isCurrentDay;
        int[] currentDate = Utilitaire.getCurrentDate();

        if (isCurrentDate) {
            lastDay = currentDate[1];
        } else {
            lastDay = Utilitaire.getNumberDaysOfMonth(year, id);
        }

        System.out.println("lastDay:" + lastDay);
        Map<Integer, Jour> missingJours = new HashMap<Integer, Jour>();
        Map<Integer, Releve> missingReleve;
        Jour missingDay;
        for (int i = 1; i <= lastDay; i++) {
            if (jourExists(i)) {
                //si le jour existe
                missingReleve = getJour(i).getMissingData(year, id);
                if (missingReleve.size() > 0) {
                    //et si il contient pas tout les données
                    missingDay = new Jour(i);
                    missingDay.copyAll(missingReleve);
                    missingJours.put(i, missingDay);
                }
            } else {
                /**
                 * ajouter les relevées qui manque pour ce jour !! (tout les relevées)
                 */
                missingDay = new Jour(i);
                missingDay.buildMissingReleves(year, id);
                missingJours.put(i, missingDay);
            }
        }

        return missingJours;
    }

    public void buildMissingDays(int year) {
        boolean isCurrentMonth = Utilitaire.isCurrentDate(year,id,-1, 1);
        int[] currentDate = Utilitaire.getCurrentDate();
        int latestDay;
        if(isCurrentMonth)
            latestDay = currentDate[1];
        else
            latestDay = Utilitaire.getNumberDaysOfMonth(year,id);

        for(int j=1;j<=latestDay;j++) {
            Jour jour = new Jour(j);
            jour.buildMissingReleves(year,j);
            joursList.put(j,jour);
        }
    }

    public List<DataBean2> getAllReleves(int idStation, int annee) {
        List<DataBean2> tempList = new ArrayList<DataBean2>();

        for (Jour jour : joursList.values()) {
            tempList.addAll(jour.getAllReleves(idStation,annee,id));
        }
        return tempList;
    }

    public Jour getAndCreateJour(int jour) {
        Boolean bool = joursList.containsKey(jour);
        if (!bool) {
            joursList.put(jour, new Jour(jour));
        }

        return (joursList.get(jour));
    }

    public Jour getJour(int jour) {
        return joursList.get(jour);
    }

    public boolean jourExists(int jour) {
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

    public void showAll()
    {
        for(Jour jour : joursList.values()) {
            System.out.println("id:"+jour.getId());
        }
    }
}
