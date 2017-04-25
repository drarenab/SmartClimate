package abstraction;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.TableView;
import main.MyModel;
import smart.DataBean;
import smart.DataBean2;
import smart.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AZ PC on 24/04/2017.
 */
public interface Model {



    boolean constructChartComparaison(String station,
                                      String year1,
                                      String month1,
                                      String day1,
                                      String year2,
                                      String month2,
                                      String day2,
                                      LineChart<Number, Number> lineCharttemp,
                                      LineChart<Number, Number> lineCharthum,
                                      LineChart<Number, Number> lineChartnebul,
                                      int MinOrMaxOrMoy,
                                      boolean offlineMode,
                                      String kelvin_celcius
    ) throws IOException;

    List<DataBean2> getLatestDataForGraphicMap(boolean offlineMode) throws IOException;

    Map validateDate(String year, String month, String day);

    boolean validateNotFuture(String year, String month, String day);

    void Affichage(String station,
                   String year,
                   String month,
                   String day,
                   AreaChart<Number, Number> AfficheTemp,
                   AreaChart<Number, Number> AfficheHum,
                   AreaChart<Number, Number> AfficheNebul,
                   TableView<DataBean> tableView,
                   int MinOrMaxOrMoy,
                   boolean offlineMode,
                   String kelvin_celcius
    ) throws IOException;

    ArrayList<String> getMonthsExistsForYear(String year);

    void DisplayAlertToImport() throws IOException;

    ArrayList<String> getYearExists();

    public List<String> getStationNames();
}
