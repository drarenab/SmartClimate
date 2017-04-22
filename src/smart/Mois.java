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
 * @author SEIF
 */
public class Mois {

    private int id;
    private Map<Integer, Jour> joursList;

    public Map<Integer, Jour> getJoursList() {
        return joursList;
    }

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

    public List<DataBean2> getAllReleves(String kelvin_celcius,String stationName,int idStation, int annee,int x,int y) {
        List<DataBean2> tempList = new ArrayList<DataBean2>();

        for (Jour jour : joursList.values()) {
            tempList.addAll(jour.getAllReleves(kelvin_celcius,stationName,idStation,annee,id,x,y));
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
    
    
     /**
     *
     * @return un relevé representant la moyenne du mois les valeurs présentent
     * sont soit la moyenne soit 101 dans le cas ou aucune valeur n'est dispo
     */
//    public Releve calculMoyenneMois() {
//        float temperature = 0;
//        float humidite = 0;
//        float nebulosite = 0;
//        Boolean temp = false, hum = false, neb = false;
//
//        for (Map.Entry<Integer, Jour> entry : joursList.entrySet()) {
//            Integer key = entry.getKey();
//            Jour value = entry.getValue();
//
//            Releve moyenneJour = value.getMoyenneJour();
//            temperature += moyenneJour.getTemperature();
//            humidite += moyenneJour.getHumidite();
//            nebulosite += moyenneJour.getNebulosite();
//
//            if (moyenneJour.getTemperature() != 101) {
//                temperature += moyenneJour.getTemperature();
//                temp = true;
//            }
//            if (moyenneJour.getHumidite() != 101) {
//                humidite += moyenneJour.getTemperature();
//                hum = true;
//            }
//            if (moyenneJour.getNebulosite() != 101) {
//                nebulosite += moyenneJour.getTemperature();
//                neb = true;
//            }
//
//        }
//        temperature /= joursList.size();
//        humidite /= joursList.size();
//        nebulosite /= joursList.size();
//        return new Releve(10, (temp) ? temperature : 101, (hum) ? humidite : 101, (neb) ? nebulosite : 101);
//    }
     /**
     *
     * @return Hashmap comme clé l'id du jour et comme valeur le relevé
     * representant la moyenne du jour
     */
    public ArrayList<DataBean2> getMoyennesParJour(String kelvin_celcius,String nomStation,int idStation,String idAnnee,int x,int y) {
        ArrayList<DataBean2> moyenneParMois = new ArrayList<>();
        for (Map.Entry<Integer, Jour> entry : joursList.entrySet()) {
            Integer key = entry.getKey();
            Jour value = entry.getValue();

            moyenneParMois.add(value.getMoyenneJour(kelvin_celcius,nomStation,idStation,idAnnee,Integer.toString(id),x,y));
        }
        return moyenneParMois;
    }

    public ArrayList<DataBean2> getMinParMois(String kelvin_celcius,String nomStation,int idStation,String idAnnee,int x,int y) {
        ArrayList<DataBean2> minParMois = new ArrayList<>();
        for (Map.Entry<Integer, Jour> entry : joursList.entrySet()) {
            Integer key = entry.getKey();
            Jour value = entry.getValue();

            minParMois.add(value.getMinJour(kelvin_celcius,nomStation,idStation,idAnnee,Integer.toString(id),x,y));
        }
        return minParMois;
    }
    /**
     *
     * @return un relevé representant le minimum des donnée du Mois le resultat
     * est soit la moyenne soit 101 dans le cas ou aucune donnée n'est presente
     * pour le mois
     */
//    public Releve getMinParMois(int idStation,String idAnnee) {//gerer le cas de note manquante
//        float temperature = 101;
//        float humidite = 101;
//        float nebulosite = 101;
//
//        for (Map.Entry<Integer, Jour> entry : joursList.entrySet()) {
//            Jour value = entry.getValue();
//
//            Releve MinMois = value.getMinJour(idStation, idAnnee, Integer.toString(id));
//            if (MinMois.getTemperature() <= temperature) {
//                temperature = MinMois.getTemperature();
//            }
//            if (MinMois.getHumidite() <= humidite) {
//                humidite = MinMois.getHumidite();
//            }
//            if (MinMois.getNebulosite() <= nebulosite) {
//                nebulosite = MinMois.getNebulosite();
//            }
//
//        }
//
//        return new Releve(10, temperature, humidite, nebulosite);
//    }
    public ArrayList<DataBean2> getMaxParMois(String kelvin_celcius,String nomStation,int idStation,String idAnnee,int x,int y) {
        ArrayList<DataBean2> maxParMois = new ArrayList<>();
        for (Map.Entry<Integer, Jour> entry : joursList.entrySet()) {
            Integer key = entry.getKey();
            Jour value = entry.getValue();

            maxParMois.add(value.getMaxJour(kelvin_celcius,nomStation,idStation,idAnnee,Integer.toString(id),x,y));
        }
        return maxParMois;
    }

    /**
     *
     * @return un relevé representant le maximum des donnée du Mois le resultat
     * est soit la moyenne soit 101 dans le cas ou aucune donnée n'est presente
     * pour le mois
     */
//    public Releve getMaxMois() {//gerer le cas de note manquante
//        float temperature = -1;
//        float humidite = -1;
//        float nebulosite = -1;
//
//        for (Map.Entry<Integer, Jour> entry : joursList.entrySet()) {
//            Jour value = entry.getValue();
//
//            Releve MinMois = value.getMinJour();
//            if (MinMois.getTemperature() <= temperature) {
//                temperature = MinMois.getTemperature();
//            }
//            if (MinMois.getHumidite() <= humidite) {
//                humidite = MinMois.getHumidite();
//            }
//            if (MinMois.getNebulosite() <= nebulosite) {
//                nebulosite = MinMois.getNebulosite();
//            }
//
//        }
//
//        return new Releve(10, (temperature != -1) ? temperature : 101,
//                (humidite != -1) ? humidite : 101,
//                (nebulosite != -1) ? nebulosite : 101);
//    }
}
