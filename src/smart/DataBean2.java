package smart;

/**
 * This class represents one Releve of a station,  on a certain date and time , it contains extra information than the
 * Releve Class about coordinates : x,y (used to determine the place of each city on the map) , and the date of the info
 * it is used as a way to communicate between the methods of our Model
 */
public class DataBean2 {
    private String nomStation;
    private int idStation;
    private float temperature;
    private float humidite;
    private float nebulosite;
    private int x;
    private int y;
    private aDate date;


    public DataBean2(String nomStation,int idStation, float temperature, float humidite, float nebulosite, aDate date,int x,int y) {
        this.nomStation = nomStation;
        this.idStation = idStation;
        this.temperature = temperature;
        this.humidite = humidite;
        this.nebulosite = nebulosite;
        this.date = date;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIdStation() {
        return idStation;
    }

    public void setIdStation(int idStation) {
        this.idStation = idStation;
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

    public aDate getDate() {
        return date;
    }

    public void setDate(aDate date) {
        this.date = date;
    }

    public String getNomStation() {
        return nomStation;
    }

    public void setNomStation(String nomStation) {
        this.nomStation = nomStation;
    }

    public DataBean toDataBean() {
        DataBean dataBean = new DataBean();
        dataBean.setDate(date.toString());
        dataBean.setHumidite((humidite!=101)?String.format("%.1f",humidite):"N/A");
        dataBean.setNebulosite((nebulosite!=101)?String.format("%.1f",nebulosite):"N/A");
        dataBean.setTemperature((temperature!=101)?String.format("%.1f",temperature):"N/A");
        dataBean.setIdVille(String.valueOf(idStation));
        return dataBean;

    }


}
