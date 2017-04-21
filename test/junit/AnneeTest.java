/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package junit;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import smart.*;

/**
 *
 * @author SEIF
 */
public class AnneeTest {
 
    @Test
    public void isUpdated() {
        /**********PREMIER CAS : ANNEE COURANTE**********/
        
        /*REMPLIR LES DONNEES TOUT LES MOIS APART LE MOIS COURANT*/
        int currentDate[] = utilitaire.Utilitaire.getCurrentDate();
        Annee annee = new Annee(currentDate[3]);
        
        Map<Integer,Mois>  listMois = new HashMap<Integer, Mois>();
        Map<Integer,Jour>  listJours;
        
        for(int i=1;i<=currentDate[2]-1;i++) {
            Mois mois = new Mois(i);
            listJours = new HashMap<Integer,Jour>();
            for(int k=1;k<=utilitaire.Utilitaire.getNumberDaysOfMonth(currentDate[3],i);k++) {
                Jour j = new Jour(k);
                for(int e=0;e<8;e++) {
                    j.addReleve(e, 1,1, 1);
                }
                listJours.put(k,j);           
            }
            mois.copyAll(listJours);
            listMois.put(i,mois);
        }
        
        /*REMPLIR LES DONNES DU MOIS COURANT*/
        Mois mois = new Mois(currentDate[2]);
        listJours = new HashMap<Integer,Jour>();
        for(int k=1;k<=currentDate[1];k++) {
            Jour j = new Jour(k);
            for(int e=0;e<8;e++) {
                j.addReleve(e, 1,1, 1);
            }
            listJours.put(k,j);           
        }
        mois.copyAll(listJours);
        listMois.put(currentDate[2],mois);
        annee.copyMap(listMois);
        assertTrue(annee.isUpdated());
        
        /**********DEUXIEME CAS : ANNEE NON COURANTE**********/
        listMois = new HashMap<Integer, Mois>();
        int year = 2012;
        annee = new Annee(year);

        for(int i=1;i<=12;i++) {
            mois = new Mois(i);
            listJours = new HashMap<Integer,Jour>();
            for(int k=1;k<=utilitaire.Utilitaire.getNumberDaysOfMonth(year,i);k++) {
                Jour j = new Jour(k);
                for(int e=0;e<8;e++) {
                    j.addReleve(e, 1,1, 1);
                }
                listJours.put(k,j);           
            }
            mois.copyAll(listJours);
            listMois.put(i,mois);
        }
            annee.copyMap(listMois);
            assertTrue(annee.isUpdated());
    }
}
