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
        if (!bool) {
            relevesList.put(ordre, releve);
        }

        return (relevesList.get(ordre));
    }

    /**
     * TESTED WITH JUNIT
     * <p>
     * Checks if data is updated for the day , and if it contains all needed
     * data for all releves
     *
     * @param year the year which this day belongs
     * @param month the month which this day belongs
     * @return true if data is fully updated false if data is not fully updated
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
                currentDay == id) {
            today = true;
        }

        for (int i = 0; i < 24; i++) {
            if (i % 3 != 0) {
                continue;
            }

            //si date ajourd'hui et ordre de releve inferieure a l'heure actuelle,  donc le relevé doit forcement exister sinon erreure
            if (today
                    && (i / 3) <= (currentHour / 3)
                    && !relevesList.containsKey(i / 3)) {
                return false;// donc le jour n'est pas a jour 
            } else if (!today
                    && !relevesList.containsKey(i / 3)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the missing <relevées> on this object
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
                currentDay == id) {
            today = true;
        }

        for (int i = 0; i < 24; i++) {
            if (i % 3 != 0) {
                continue;
            }

            //si date ajourd'hui et ordre de releve inferieure a l'heure actuelle,  donc le relevé doit forcement exister sinon erreure
            if (today
                    && (i / 3) <= (currentHour / 3)
                    && !relevesList.containsKey(i / 3)) {
                // donc le jour n'est pas a jour 
                missingList.put(i / 3, new Releve(i / 3, -1, -1, -1));
            } else if (!today
                    && !relevesList.containsKey(i / 3)) {
                missingList.put(i / 3, new Releve(i / 3, -1, -1, -1));
            }
        }
        return missingList;
    }

    public void showAll() {
        for (Releve releve : relevesList.values()) {
            System.out.println("ordre:" + releve.getOrdre() + " temperature:" + releve.getTemperature());
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
        int currentHour, latestReleve;
        boolean today = Utilitaire.isCurrentDate(year, month, id, 0);
        int[] currentDate = Utilitaire.getCurrentDate();

        if (!today) {
            latestReleve = 7;
        } else {
            latestReleve = currentDate[0] / 3;
        }

        if (!today)
            latestReleve = 7;
        else
            latestReleve = currentDate[0] / 3;

        for (int i = 0; i <= latestReleve; i++) {
            if (!releveExists(i))
                missingList.put(i, new Releve(i, -1, -1, -1));

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

    public DataBean2 getLatestReleve(String nomStation, int idStation, int annnee, int mois, int x, int y) {
        Releve releveTemp = relevesList.get(0);
        for(Releve releve : relevesList.values()) {
            releveTemp = releve;
        }
        float temperature,humidite,nebulosite;
        String ordre;

        if(relevesList.isEmpty()) {
            temperature = 1000;
            humidite = 1000;
            nebulosite = 1000;
            ordre = "8";
        }
        else {
            temperature = releveTemp.getTemperature() ;
            humidite = releveTemp.getHumidite();
            nebulosite = releveTemp.getNebulosite();
            ordre = String.valueOf(releveTemp.getOrdre());
        }

        DataBean2 dataBean2 = new DataBean2(
                nomStation
                , idStation
                , temperature
                , humidite
                , nebulosite
                , new aDate(String.valueOf(annnee)
                                , String.valueOf(mois)
                                , String.valueOf(id)
                                , String.valueOf(ordre)
                )
                ,x
                ,y
                );

        return dataBean2;
    }


    public void buildMissingReleves(int year, int month) {
        boolean isCurrentDay = Utilitaire.isCurrentDate(year, month, id, 0);
        int latestReleve;
        int[] currentDate = Utilitaire.getCurrentDate();
        if (isCurrentDay) {
            latestReleve = currentDate[0] / 3;
        } else {
            latestReleve = 7;
        }

        for (int j = 0; j <= latestReleve; j++) {
            relevesList.put(j, new Releve(j, -1, -1, -1));
        }
    }

    /**
     *
     * @param idStation
     * @param idAnnee
     * @param idMois
     * @return un releve representant la moyenne des données d'un jour
     */
    public DataBean2 getMoyenneJour(String kelvin_celcius, String nomStation,int idStation, String idAnnee, String idMois,int x,int y) {//gerer le cas de note manquante  //probleme faire null au lieu de 101
        float temperature = 0;
        float humidite = 0;
        float nebulosite = 0;
        Boolean temp = false, hum = false, neb = false;
        for (Map.Entry<Integer, Releve> entry : relevesList.entrySet()) {

            if (entry.getValue().getTemperature() != 101) {
                temperature += entry.getValue().getTemperature();
                temp = true;
            }
            if (entry.getValue().getHumidite() != 101) {
                humidite += entry.getValue().getHumidite();
                hum = true;
            }
            if (entry.getValue().getNebulosite() != 101) {
                nebulosite += entry.getValue().getNebulosite();
                neb = true;
            }
        }
        temperature /= relevesList.size();
        humidite /= relevesList.size();
        nebulosite /= relevesList.size();


        if(kelvin_celcius.equals("kelvin"))
            temperature  = (float) (temperature + 273.15);

        return new DataBean2(nomStation,idStation, (temp) ? temperature : 101, (hum) ? humidite : 101, (neb) ? nebulosite : 101,
                new aDate(idAnnee, idMois, Integer.toString(id), Integer.toString(4)),x,y);

//        return new Releve(10, (temp) ? temperature : 101, (hum) ? humidite : 101, (neb) ? nebulosite : 101);
    }

    /**
     *
     * @return Les valeurs min de l'humidité la température et la nébulosité
     * d'un jour Si aucune valeur dispo retourne un relevé avec 101 partout
     */
    public DataBean2 getMinJour(String kelvin_celcius,String nomStation,int idStation, String idAnnee, String idMois,int x,int y) {//gerer le cas de note manquante
        float temperature = 101;
        float humidite = 101;
        float nebulosite = 101;

        for (Map.Entry<Integer, Releve> entry : relevesList.entrySet()) {
            Releve value = entry.getValue();
            if (value.getTemperature() <= temperature) {
                temperature = value.getTemperature();
            }
            if (value.getHumidite() <= humidite) {
                humidite = value.getHumidite();
            }
            if (value.getNebulosite() <= nebulosite) {
                nebulosite = value.getNebulosite();
            }

        }

        if(kelvin_celcius.equals("kelvin"))
            temperature  = (float) (temperature + 273.15);

        return new DataBean2(nomStation,idStation, temperature, humidite, nebulosite,
                new aDate(idAnnee, idMois, Integer.toString(id), Integer.toString(4)
                ),x,y);

    }

    /**
     *
     * @return Les valeurs max de l'humidité la température et la nébulosité
     * d'un jour Si aucune valeur dispo retourne un relevé avec 101 la ou il
     * n'ya pas de données
     */
    public DataBean2 getMaxJour(String kelvin_celcius,String nomStation,int idStation, String idAnnee, String idMois,int x,int y) {//gerer le cas de note manquante
        float temperature = -1;
        float humidite = -1;
        float nebulosite = -1;

        for (Map.Entry<Integer, Releve> entry : relevesList.entrySet()) {
            Releve value = entry.getValue();
            if (value.getTemperature() >= temperature && value.getTemperature() != 101) {
                temperature = value.getTemperature();
            }
            if (value.getHumidite() >= humidite && value.getHumidite() != 101) {
                humidite = value.getHumidite();
            }
            if (value.getNebulosite() >= nebulosite && value.getNebulosite() != 101) {
                nebulosite = value.getNebulosite();
            }

        }

        if(kelvin_celcius.equals("kelvin")&&temperature!=-1)
            temperature  = (float) (temperature + 273.15);

        return new DataBean2(nomStation
                ,idStation
                ,(temperature != -1) ? temperature : 101
                ,(humidite != -1) ? humidite : 101
                ,(nebulosite != -1) ? nebulosite : 101
                ,new aDate(idAnnee
                        , idMois
                        , Integer.toString(id)
                        , Integer.toString(4))
                ,x
                ,y
        );


    }

    public Map<Integer, Releve> getReleves() {
        return relevesList;
    }

    public List<DataBean2> getAllReleves(String kelvin_celcius,String nomStation, int idStation, int annee, int mois, int x, int y) {
        List<DataBean2> tempList = new ArrayList<DataBean2>();
        float temperature=0;
        for (Releve releve : relevesList.values()) {
            temperature  =  releve.getTemperature();

            if(kelvin_celcius.equals("kelvin"))
                temperature  = (float) (temperature + 273.15);

            DataBean2 dataBean = new DataBean2(
                    nomStation,
                    idStation,
                    temperature,
                    releve.getHumidite(),
                    releve.getNebulosite(),
                    new aDate(
                            String.valueOf(annee),
                            String.valueOf(mois),
                            String.valueOf(id),
                            String.valueOf(releve.getOrdre())
                    ),
                    x,
                    y
            );

            tempList.add(dataBean);
        }
        return tempList;
    }
}
