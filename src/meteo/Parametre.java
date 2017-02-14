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
public class Parametre {

    private String nom;
    private String date;
    private String temperature;
    private String humidite;
    private String nebulosite;

    public Parametre(String nom, String date, String temperature, String humidite, String nebulosite) {
        this.nom = nom;
        this.date = date;
        this.temperature = temperature;
        this.humidite = humidite;
        this.nebulosite = nebulosite;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidite() {
        return humidite;
    }

    public void setHumidite(String humidite) {
        this.humidite = humidite;
    }

    public String getNebulosite() {
        return nebulosite;
    }

    public void setNebulosite(String nebulosite) {
        this.nebulosite = nebulosite;
    }

    
    
}
