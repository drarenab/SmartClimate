package coordonnee;

/**
 * Created by AZ PC on 18/04/2017.
 */
public class DataBean2 {
    private int idStation;
    private float temperature;
    private float humidite;
    private float nebulosite;
    private aDate date;

    public DataBean2(int idStation, float temperature, float humidite, float nebulosite, aDate date) {
        this.idStation = idStation;
        this.temperature = temperature;
        this.humidite = humidite;
        this.nebulosite = nebulosite;
        this.date = date;
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

    public DataBean toDataBean() {
        DataBean dataBean = new DataBean();
        dataBean.setDate(date.toString());
        dataBean.setHumidite(String.valueOf(humidite));
        dataBean.setNebulosite(String.valueOf(nebulosite));
        dataBean.setTemperature(String.format("%.1f",temperature));
        dataBean.setIdVille(String.valueOf(idStation));
        return dataBean;

    }
}
