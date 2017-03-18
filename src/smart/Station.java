/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

import java.util.List;

/**
 *
 * @author SEIF
 */
public class Station {
    private int id;
    private String nom;
    private List<Annee> anneesList;
    
    public List<Releve> getReleves(int annee,int mois,int jour) {
        return null;
    }
    
    public List<Releve> getMoyennesParMois(int mois) {
        return null;
    }
    
    public List<Releve> getMoyennesParJour(int mois) {
        return null;
    }
}
    