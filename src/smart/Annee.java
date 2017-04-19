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
import utilitaire.Utilitaire;

/**
 * @author SEIF
 */
public class Annee {

    private int id;
    private Map<Integer, Mois> moisList;

    public Annee(int id) {
        this.id = id;
        moisList = new HashMap<Integer, Mois>();
    }

    public Mois getAndCreateMois(int mois) {
        Boolean bool = moisList.containsKey(mois);
        if (!bool) {
            moisList.put(mois, new Mois(mois));
        }

        return (moisList.get(mois));
    }

    /**
     * Checks if this Year is fully updated (contains the entire data) , in terms of months, days in a month , releve in days
     *
     * @return true if this year is updated
     * false if this year is not updated
     */
    public boolean isUpdated() {
        int lastMonth;
        boolean currentDate = Utilitaire.isCurrentDate(id, -1, -1, 2);

        if (currentDate) {
            lastMonth = Utilitaire.getCurrentDate()[2];
        } else {
            lastMonth = 12;
        }
        /*
        if(jourExists(lastDay))
            return joursList.get(lastDay).isUpdated(year, id);
        else
            return false;
         */
        System.out.println("lastMonth:" + lastMonth);
        for (int i = 1; i <= lastMonth; i++) {
            if (!moisExists(i) || !getMois(i).isUpdated(id)) {
                //si le jour n'existe pas , ou bien le jour exist mais il contient pas touts les relevés
                if (moisExists(i) && !getMois(i).isUpdated(id)) {
                    System.out.println("Not updated month id=" + i);
                }
                return false;
            }

        }

        return true;
    }

    /**
     * Checks if this Year is fully updated (contains the entire data) , in terms of months, days in a month , releve in days
     *
     * @return true if this year is updated
     * false if this year is not updated
     */
    public Map<Integer, Mois> getMissingData() {
        int lastMonth, latestDay;
        int[] currentDate = Utilitaire.getCurrentDate();
        boolean isCurrentDate = Utilitaire.isCurrentDate(id, -1, -1, 2);
        boolean isCurrentMonth;
        if (isCurrentDate) {
            lastMonth = currentDate[2];
        } else {
            lastMonth = 12;
        }

        Map<Integer, Mois> missingMois = new HashMap<Integer, Mois>();
        Map<Integer, Jour> misssingJours;
        Mois missingMonth;
        System.out.println("lastMonth:" + lastMonth);
        for (int i = 1; i <= lastMonth; i++) {
            if (moisExists(i)) {
                //si le mois exist
                misssingJours = getMois(i).getMissingData(id);
                //et si le mois ne contient pas tout les données
                if (misssingJours.size() > 0) {
                    missingMonth = new Mois(i);
                    missingMonth.copyAll(misssingJours);
                    missingMois.put(i, missingMonth);
                }
            } else {
                //si le mois n'existe pas
                missingMonth = new Mois(i);
                missingMonth.buildMissingDays(id);
                missingMois.put(i, missingMonth);
            }
        }
        return missingMois;
    }

    public void showAll()
    {
        for(Mois mois : moisList.values()) {
            System.out.println("id:"+mois.getId());
        }
    }

    public void buildMissingMonths() {
        boolean isCurrentYear = Utilitaire.isCurrentDate(id, -1, -1, 2);
        int[] currentDate = Utilitaire.getCurrentDate();
        int latestMonth;
        if (isCurrentYear)
            latestMonth = currentDate[2];
        else
            latestMonth = 12;

        for (int j = 1; j <= latestMonth; j++) {
            Mois mois = new Mois(j);
            mois.buildMissingDays(id);
            moisList.put(j, mois);
        }
    }

    public List<DataBean2> getAllReleves(int idStation) {
        List<DataBean2> tempList = new ArrayList<DataBean2>();

        for (Mois mois : moisList.values()) {
            tempList.addAll(mois.getAllReleves(idStation,id));
        }
        return tempList;
    }

    public boolean containsFullMonths() {
        int currentDay, currentMonth, currentYear;
        ArrayList<String> missedMonths = new ArrayList<String>();
        String yearMonth, month;
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        LocalDateTime localTime = LocalDateTime.now(zoneId);
        currentDay = localTime.getDayOfMonth();
        currentMonth = localTime.getMonthValue();
        currentYear = localTime.getYear();
        boolean stopSearch = false;
        boolean foundMonth = false;
        if (String.valueOf(currentYear).equals(id)) {
            //si l'année donner est l'année courante, on met le boolean stopSearch a true pour ne pas
            //mettre les mois qui sont plus grand que le mois courant comme missed ! 
            stopSearch = true;
        }

        for (int i = 1; i <= 12; i++) {
            month = ("00" + i).substring(String.valueOf(i).length());
            if (stopSearch && Integer.parseInt(month) > currentMonth) //si le mois generer est supperieure au mois courant
                break;

            for (Mois m : moisList.values()) {
                if (m.getId() == Integer.parseInt(month)) {
                    foundMonth = true;
                    break;
                }
            }

            if (!foundMonth) {
                return false;
            }

            foundMonth = false;
        }
        return true;
    }

    public Mois getMois(int mois) {
        return moisList.get(mois);
    }

    public boolean moisExists(int mois) {
        return moisList.containsKey(mois);
    }

    public List<Releve> getReleves() {
        return null;
    }

    public List<Releve> getMoyenneParMois() {
        return null;
    }

    /*needed for tests : dangerous */
    public void copyMap(Map<Integer, Mois> l) {
        moisList = l;
    }
}
