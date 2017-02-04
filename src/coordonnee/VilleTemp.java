/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coordonnee;

/**
 *
 * @author karim
 */

/*

*/
public class VilleTemp {
   /**
     * 
     * @param city
     * @param temperature 
     * Cette Classe fait le lien entre une ville et sa temperature
     */
   public Ville city;
   public double temperature;
   public int humidite;
   public float nebulosite;

    public VilleTemp(Ville city, double temperature, int humidite, float nebulosite) {
        this.city = city;
        this.temperature = temperature;
        this.humidite = humidite;
        this.nebulosite = nebulosite;
    }

    
    
    
    public void modifierTemperature(double f){
        temperature=f;
    }
    public void modifierHumidite(int h){
        humidite=h;
    }
    public void modifierNebulosite(float n){
        nebulosite=n;
    }
}
