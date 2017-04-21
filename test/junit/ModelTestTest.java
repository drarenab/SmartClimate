/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package junit;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author SEIF
 */
public class ModelTestTest {
    /*
    MyModel model;
    public ModelTestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        model  = MyModel.getInstance();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of test method, of class ModelTest.
     */
    /*
    @Test
    
    public void testTest() {
        System.out.println("test");
        ModelTest instance = new ModelTest();
        instance.test();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    */



    @Test
    //utilitaire
    public void getCurrentDate() {
        int day = utilitaire.Utilitaire.getCurrentDate()[1];
        int month = utilitaire.Utilitaire.getCurrentDate()[2];
        int year = utilitaire.Utilitaire.getCurrentDate()[3];
        
        assertTrue(day==20);
        assertTrue(month==3);
        assertTrue(year==2017);
        System.out.println("y:"+year);
        System.out.println("m:"+month);
        System.out.println("d:"+day);
    }
    /*
    public void getDataForDateByCity() {
        String date = "20140101";
        String city="07005";
        model.getDataForDateByCity(date, city);
    }
    
    @Test
    //test for annee object
    public void containsFullMonths() {
        Annee annee = new Annee(2012);
        Annee annee2 = new Annee(2017);
        Annee annee3 = new Annee(2011);
        Map<Integer,Mois> l = new HashMap<Integer,Mois>();
        l.put(1,new Mois(1));
        l.put(2,new Mois(2));
        l.put(3,new Mois(3));
        l.put(4,new Mois(4));
        l.put(5,new Mois(5));
        l.put(6,new Mois(6));
        l.put(7,new Mois(7));
        l.put(8,new Mois(8));
        l.put(9,new Mois(9));
        l.put(10,new Mois(10));
        l.put(11,new Mois(11));
        l.put(12,new Mois(12));
        annee.copyMap(l);
        assertTrue(annee.containsFullMonths());
       
       
        Map<Integer,Mois> l2 = new HashMap<Integer,Mois>();
        l2.put(1,new Mois(1));
        l2.put(2,new Mois(2));
        l2.put(3,new Mois(3));
        annee2.copyMap(l);
        assertTrue(annee2.containsFullMonths());
    
        Map<Integer,Mois> l3 = new HashMap<Integer,Mois>();
        l3.put(1,new Mois(1));
        l3.put(2,new Mois(2));
        l3.put(3,new Mois(3));
        l3.put(4,new Mois(4));
        l3.put(5,new Mois(5));
        l3.put(6,new Mois(6));
        l3.put(7,new Mois(7));
        l3.put(8,new Mois(8));
        l3.put(10,new Mois(10));
        l3.put(11,new Mois(11));
        l3.put(12,new Mois(12));
        annee3.copyMap(l3);
        assertFalse(annee3.containsFullMonths());
       
    }
    */
}
