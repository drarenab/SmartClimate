/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author SEIF
 */
public class Jour {

    private int id;
    private Map<Integer, Releve> relevesList ;
//    private List<Releve> relevesList;

    public Jour(int id) {
        this.id = id;
        relevesList= new HashMap<>();
    }

    
    public Releve getReleve(int ordre) {

        return relevesList.get(ordre);

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
