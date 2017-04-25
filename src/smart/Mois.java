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
 * @author karim
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
    /**
     * copie la map de jour passée en paramétre dans la map courante
     * @param list 
     */
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
    /**
     * donne une map de jour manquant dans le mois courant
     * @param year
     * @return map of jour
     */
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
    /**
     * construit les jours manquants du mois courant 
     * @param year 
     */
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
    /**
     * donne tous les relevés du mois courant
     * @param kelvin_celcius
     * @param stationName
     * @param idStation
     * @param annee
     * @param x
     * @param y
     * @return list of DataBean2 
     */
    public List<DataBean2> getAllReleves(String kelvin_celcius,String stationName,int idStation, int annee,int x,int y) {
        List<DataBean2> tempList = new ArrayList<DataBean2>();

        for (Jour jour : joursList.values()) {
            tempList.addAll(jour.getAllReleves(kelvin_celcius,stationName,idStation,annee,id,x,y));
        }
        return tempList;
    }
    /**
     * donne le jour demandé en paramétre si il existe sinon elle le crée a partir du fichier adéquant 
     * se trouvant en local
     * @param jour
     * @return un objet de type jour
     */
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
    /**
     * permet de savoir si un jour en particulier existe dans la map ou pas
     * @param jour
     * @return  true si le jour existe false sinon
     */
    public boolean jourExists(int jour) {
        return joursList.containsKey(jour);
    }

    public void showAll()
    {
        for(Jour jour : joursList.values()) {
            System.out.println("id:"+jour.getId());
        }
    }
   
     /**
      * donne les differentes moyenne de tous les jours du mois courant
      * @param kelvin_celcius
      * @param nomStation
      * @param idStation
      * @param idAnnee
      * @param x
      * @param y
      * @return  une list d'objet DataBean2 contenant les moyenne des jours du mois
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
    /**
     * donne les differentes valeur minimales de chaque jours du mois courant
     * @param kelvin_celcius
     * @param nomStation
     * @param idStation
     * @param idAnnee
     * @param x
     * @param y
     * @return liste d'objet DataBean2 
     */
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
     * donne les differentes valeur maximales de chaque jours du mois courant
     * @param kelvin_celcius
     * @param nomStation
     * @param idStation
     * @param idAnnee
     * @param x
     * @param y
     * @return liste de DataBean2
     */
    public ArrayList<DataBean2> getMaxParMois(String kelvin_celcius,String nomStation,int idStation,String idAnnee,int x,int y) {
        ArrayList<DataBean2> maxParMois = new ArrayList<>();
        for (Map.Entry<Integer, Jour> entry : joursList.entrySet()) {
            Integer key = entry.getKey();
            Jour value = entry.getValue();

            maxParMois.add(value.getMaxJour(kelvin_celcius,nomStation,idStation,idAnnee,Integer.toString(id),x,y));
        }
        return maxParMois;
    }

 
}
