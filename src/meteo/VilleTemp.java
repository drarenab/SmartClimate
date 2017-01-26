/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

/**
 *
 * @author karim
 */
public class VilleTemp {
    Ville city;
    float temperature;

    public VilleTemp(Ville city, float temperature) {
        this.city = city;
        this.temperature = temperature;
    }
    
    public void modifierTemperature(float f){
        temperature=f;
    }
}
