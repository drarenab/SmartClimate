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
public class Annee {
    private int id;
    private Map<Integer,Mois> moisList;
    
    public Annee(int id) {
        moisList = new HashMap<Integer,Mois>();
    }
    
    public List<Releve> getReleves() {
        return null;
    }
    
    public List<Releve> getMoyenneParMois() {
        return null;
    }
}
