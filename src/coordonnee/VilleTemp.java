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
Cette Classe fait le lien entre une ville et sa temperature
*/
public class VilleTemp {
   public Ville city;
    public double temperature;

    public VilleTemp(Ville city, double temperature) {
        this.city = city;
        this.temperature = temperature;
    }
    
    public void modifierTemperature(double f){
        temperature=f;
    }
}
