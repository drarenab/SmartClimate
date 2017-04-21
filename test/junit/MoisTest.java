/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package junit;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import smart.*;
import junit.framework.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author SEIF
 */
public class MoisTest {
    
    @Test
    public void isUpdated() {
        /*PREMIER TEST : pour février 2017 : contient 28 jour et chaque jour doit contenir 8 relevé*/
        Mois mois = new Mois(2);
        Map<Integer,Jour>  list = new HashMap<Integer, Jour>();
        Jour j;
        for(int i=1;i<=28;i++) {
             j = new Jour(i);
             for(int k=0;k<=7;k++) 
                j.addReleve(k, 1, 1, 1);
            list.put(i,j);
        }
        
        mois.copyAll(list);
       
       assertTrue(mois.isUpdated(2017));
    
       /*DEUXIEME TEST : POUR LE MOI COURANT DE L'ANNEE COURANTE */
        int currentDate[] = utilitaire.Utilitaire.getCurrentDate();
        mois = new Mois( currentDate[2]);
        list = new HashMap<Integer, Jour>();
        
        /*BOUCLE POUR LES TOUT LES JOURS DU MOIS APART LE JOUR COURANT*/
        for(int i=1;i<=currentDate[1]-1;i++) {
             j = new Jour(i);
             //a chaqu'un ajouter 8 releve
             for(int k=0;k<=7;k++) 
                j.addReleve(k, 1, 1, 1);
            list.put(i,j);
        }
        
        
        /*POUR LE JOUR COURANT DU MOI */
        j = new Jour(currentDate[1]);
        //ajouter n releve , (simulation paraport a l'heure local )
       // System.out.println("last releve :"+ currentDate[0]/3);
        for(int k=0;k<=currentDate[0]/3;k++) 
           j.addReleve(k, 1, 1, 1);
        
        list.put(currentDate[1],j);
        mois.copyAll(list);
       
       assertTrue(mois.isUpdated(currentDate[3]));   
    }
    
    @Test
    public void getMissingData() {
       /*PREMIER TEST : pour février 2017 : contient 28 jour et chaque jour doit contenir 8 relevé*/
       Mois mois = new Mois(2);
       Map<Integer,Jour>  list = new HashMap<Integer, Jour>();
       Jour j;
       for(int i=1;i<=28;i++) {
             j = new Jour(i);
             for(int k=0;k<=7;k++) 
                j.addReleve(k, 1, 1, 1);
            list.put(i,j);
       }
       mois.copyAll(list);       
       assertTrue(mois.getMissingData(2017).size()==0);

    
       /*DEUXIEME TEST : POUR LE MOI COURANT DE L'ANNEE COURANTE */
        int currentDate[] = utilitaire.Utilitaire.getCurrentDate();
        mois = new Mois( currentDate[2]);
        list = new HashMap<Integer, Jour>();
        
        /*BOUCLE POUR LES TOUT LES JOURS DU MOIS APART LE JOUR COURANT*/
        for(int i=1;i<=currentDate[1]-1;i++) {
             j = new Jour(i);
             //a chaqu'un ajouter 8 releve
             for(int k=0;k<=7;k++) 
                j.addReleve(k, 1, 1, 1);
            list.put(i,j);
        }

        /*POUR LE JOUR COURANT DU MOI */
        
        //ajouter n releve , (simulation paraport a l'heure local )
        System.out.println("last releve :"+ currentDate[0]/3);
         mois.copyAll(list);
         assertTrue(mois.getMissingData(currentDate[3]).containsKey(currentDate[1]));
         assertTrue(mois.getMissingData(currentDate[3]).get(currentDate[1]).releveExists(0));
         
         assertFalse(mois.getMissingData(currentDate[3]).size()==0); 
            
       j = new Jour(currentDate[1]);
       for(int k=0;k<=currentDate[0]/3;k++) 
           j.addReleve(k, 1, 1, 1);
       
        list.put(currentDate[1],j);
        mois.copyAll(list);
        assertTrue(mois.getMissingData(currentDate[3]).size()==0); 
        
}
}
