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

/**
 *
 * @author SEIF
 */
public class Annee {

    private int id;
    private Map<Integer, Mois> moisList;

    public Annee(int id) {
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
     * @return true if this year is updated
     *         false if this year is not updated
     */
    public boolean isUpdated() {
        return true;
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
    
    //to be deleted after
    public void copyMap(Map<Integer,Mois> l) {
        moisList = l;
    }
}
