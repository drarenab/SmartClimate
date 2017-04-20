/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import smart.*;
/**
 *
 * @author SEIF
 */
public class JourTest {
    
    
    @Test
    public void isUpdated() {
        Jour j = new Jour(1);
        assertFalse(j.isUpdated(2015,1));
        
        Releve r = new Releve(1,1,1,1);
        j.getAndCreateReleve(1, r);
     // assertTrue(j.isUpdated(2015,1));
        r =  new Releve(2,1,1,1);
        j.getAndCreateReleve(2, r);
        
        r = new Releve(3,1,1,1);
        j.getAndCreateReleve(3, r);
        
        r = new Releve(4,1,1,1);
        j.getAndCreateReleve(4, r);
        
        r = new Releve(5,1,1,1);
        j.getAndCreateReleve(5, r);
        
        r = new Releve(6,1,1,1);
        j.getAndCreateReleve(6, r);
        
        r = new Releve(7,1,1,1);
        j.getAndCreateReleve(7, r);
        
     // assertFalse(j.isUpdated(2015,1));
        assertFalse(j.isUpdated(2015,1));

        r = new Releve(0,1,1,1);
        j.getAndCreateReleve(0, r);
    
        assertTrue(j.isUpdated(2015,1));
    }
    
    @Test
    public void getMissingData() {
       
        Jour jour = new Jour(1);
        for(int i=0;i<=6;i++) {
            jour.addReleve(i, 1, 1, 1);
        }
        assertFalse(jour.getMissingData(2016,3).size()==0);
        jour.addReleve(7, 1, 1, 1);
        assertTrue(jour.getMissingData(2016,3).size()==0);
        
    }
}
