/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package junit;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author SEIF
 */
public class UtilitaireTest {
 
    @Test
    public void isCurrentDate() {
        /*current year , month ,day */
        int currentDate[] = utilitaire.Utilitaire.getCurrentDate();
        
        boolean resonse = utilitaire.Utilitaire.isCurrentDate(currentDate[3],currentDate[2], currentDate[1],0);
        Assert.assertTrue(resonse);
        
        resonse = utilitaire.Utilitaire.isCurrentDate(2017,03, 22,0);
        Assert.assertFalse(resonse);
        
        
        /*current year , month */
        resonse = utilitaire.Utilitaire.isCurrentDate(currentDate[3],currentDate[2], -1,1);
        Assert.assertTrue(resonse);
        
        resonse = utilitaire.Utilitaire.isCurrentDate(2017,02, -1,1);
        Assert.assertFalse(resonse);
        
        
        /*current year */
        resonse = utilitaire.Utilitaire.isCurrentDate(currentDate[3],-1, -1,2);
        Assert.assertTrue(resonse);
        
        
        resonse = utilitaire.Utilitaire.isCurrentDate(2016,-1, -1,2);
        Assert.assertFalse(resonse);
        
    }
}
