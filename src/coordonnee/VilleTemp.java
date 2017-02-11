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
   
    
   private Ville city;
   private double temperature;
   private int humidite;
   private float nebulosite;
   private aDate date;
   
    public VilleTemp(Ville city, double temperature, int humidite, float nebulosite, aDate date) {
        this.city = city;
        this.temperature = temperature;
        this.humidite = humidite;
        this.nebulosite = nebulosite;
        this.date = date;
    }

    public aDate getDate() {
        return date;
    }

    public void setDate(aDate date) {
        this.date = date;
    }

    
    public Ville getCity() {
        return city;
    }

    public void setCity(Ville city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidite() {
        return humidite;
    }

    public void setHumidite(int humidite) {
        this.humidite = humidite;
    }

    public float getNebulosite() {
        return nebulosite;
    }

    public void setNebulosite(float nebulosite) {
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
    
    public void afficher() {
        System.out.println("name:"+city.getNom()+" id:"+city.getId()+"["+temperature+","+humidite+","+nebulosite+"]"+ " date:"+date.getYear()+
                "/"+date.getMonth()+"/"+date.getDay()+":"+date.getTime());
    }
}
