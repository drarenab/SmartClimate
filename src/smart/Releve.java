/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

/**
 * This class is the last part of the Model chain , each object of this class represents information about temperature,
 * humidite, nebulosite for a station on a precise time of a day of the year
 */
public class Releve {
    private int ordre;
    private float temperature;
    private float humidite;
    private float nebulosite;

    public Releve(int ordre, float temperature, float humidite, float nebulosite) {
        this.ordre = ordre;
        this.temperature = temperature;
        this.humidite = humidite;
        this.nebulosite = nebulosite;
    }


    public String toString() {
        return
                "ordre: " + ordre +
                        " temperature: " + temperature +
                        " humidite: " + humidite +
                        " nebulosite: " + nebulosite;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidite() {
        return humidite;
    }

    public void setHumidite(float humidite) {
        this.humidite = humidite;
    }

    public float getNebulosite() {
        return nebulosite;
    }

    public void setNebulosite(float nebulosite) {
        this.nebulosite = nebulosite;
    }

}
