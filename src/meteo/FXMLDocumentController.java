package meteo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import coordonnee.Coordonne;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import coordonnee.*;
import java.util.Map;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static meteo.Downloader.getIdFromNameVille;
import static meteo.Model.ConstructChart;
import static meteo.Model.getListForChart;

/**
 *
 * @author karim
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    ImageView imgview;
    @FXML
    StackPane stackPane;
    @FXML
    Text text, text2;
    @FXML
    VBox v, VboxPrincipal;
    @FXML
    GridPane gridPane;
    static int Interface = 0;
    @FXML
    MenuBar menuBar;
    @FXML
    AnchorPane anchorSeting;
    @FXML
    RadioButton kelvin, celcius, online, offline;
    static String kelvin_celcius = "celcius";
    static String onLine_offLine = "onLine";
    @FXML
    VBox v1, v2, VboxComparaison;
    @FXML
    ImageView imgviewTempsActuel;
    @FXML
    HBox HboxLocation;
    // @FXML
    static ChoiceBox LocationDefault;
    @FXML
    ChoiceBox Station, StationComparaison;
    @FXML
    LineChart LineChartTemp;

    LineChart<Number, Number> AfficheTemp;
    LineChart<Number, Number> AfficheHum;
    LineChart<Number, Number> AfficheNebul;

    LineChart<Number, Number> lineCharttemp;
    LineChart<Number, Number> lineCharthum;
    LineChart<Number, Number> lineChartnebul;

    ToggleGroup groupeChart, groupeRadioAffichage;
    @FXML
    StackPane stack3;
    @FXML
    TextField year, month, day;
    @FXML
    RadioButton RadioBtnTemp, RadioBtnHum, RadioBtnNebul, radioBtnCourbes, radioBtnTableur;
    @FXML
    Tab tabVisualisation, tabComparaison;
    @FXML
    TableView<DataBean> tableView;
    @FXML
    TableColumn<DataBean, String> columnName;
    @FXML
    TableColumn<DataBean, String> columnDate;
    @FXML
    TableColumn<DataBean, String> columnTemp;
    @FXML
    TableColumn<DataBean, String> columnHum;
    @FXML
    TableColumn<DataBean, String> columnNebul;
    @FXML
    TextField Year1Comparaison, Year2Comparaison, MonthComparaison, DayComparaison;

    @FXML
    private void handleButtonActionChngTemp() {
        if (kelvin.isArmed()) {
            kelvin_celcius = "kelvin";

        }
        if (celcius.isArmed()) {
            kelvin_celcius = "celcius";
        }
    }

    @FXML
    private void handleButtonActionChngOnline() {
        if (online.isArmed()) {
            if (Downloader.netIsAvailable()) {
                onLine_offLine = "onLine";
            } else {
                offline.arm();
            }

        }
        if (offline.isArmed()) {
            onLine_offLine = "offLine";
        }
    }

    @FXML
    private void handleButtonActionAfficher() {

        /*
        Test si le formulaire est bien rempli
         */
        Map errors = Model.validateDate(year.getText(), month.getText(), day.getText());
        /*
        test si la année n'est pas vide (champs obligatoire)
         */
        if (!errors.get("Year").toString().equals("")) {
            year.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
            year.setPromptText(errors.get("Year").toString());
            /*
            si année pas vide et correcte on test le mois et le jour (peuvent etre vide
             */
        } else if (!errors.get("Month").equals("") || !errors.get("Day").equals("")) {
            if (!errors.get("Month").equals("")) {
                month.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                month.setPromptText(errors.get("Month").toString());
            }
            if (!errors.get("Day").equals("")) {
                day.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                day.setPromptText(errors.get("Day").toString());
            }
        } else {
            /*
                Chart
             */
            AfficheTemp.setTitle("Températures");
            AfficheHum.setTitle("Humidité");
            AfficheNebul.setTitle("Nébulosité");

            ArrayList<XYChart.Series> S = ConstructChart(year.getText()
                    + month.getText() + day.getText(), Station.getValue().toString());

            AfficheTemp.getData().setAll(S.get(0));
            AfficheHum.getData().setAll(S.get(1));
            AfficheNebul.getData().setAll(S.get(2));
            /*
                TableView
             */
            
            columnName.setCellValueFactory(new PropertyValueFactory<DataBean, String>("nomVille"));
            columnHum.setCellValueFactory(new PropertyValueFactory<DataBean, String>("humidite"));
            columnNebul.setCellValueFactory(new PropertyValueFactory<DataBean, String>("nebulosite"));
            columnTemp.setCellValueFactory(new PropertyValueFactory<DataBean, String>("temperature"));
            columnDate.setCellValueFactory(new PropertyValueFactory<DataBean, String>("date"));
            tableView.getItems().setAll(parseDataList(year.getText() + month.getText() + day.getText(),
                                                        Station.getValue().toString()
                                        ));
            
        }
    }
    
    private List<DataBean> parseDataList(String date,String station){
          ArrayList<VilleTemp> listDonnee = Model.getListForChart(date,station);
          ArrayList<DataBean> listDataBean = new ArrayList<DataBean>();
          if(listDonnee!=null) {
              for(VilleTemp villeTemp : listDonnee) {
                  listDataBean.add(villeTemp.toDataBean());
              }
          }
           return listDataBean;
    }
    
    @FXML
    private void handleButtonActionComparer() throws IOException {

        /*
        verifier le formulaire done
        afficher les donnée  done
        verifier si donnée existe sinon les telecharger
        verifier si ville selectionné
         */
        Map errors = Model.validateDate(Year1Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());
        Map errors2 = Model.validateDate(Year2Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());

        if (!errors.get("Year").toString().equals("") & !errors2.get("Year").toString().equals("")) {
            if (!errors.get("Year").toString().equals("")) {
                Year1Comparaison.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                Year1Comparaison.setPromptText(errors.get("Year").toString());
            }
            if (!errors2.get("Year").toString().equals("")) {
                Year2Comparaison.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                Year2Comparaison.setPromptText(errors.get("Year").toString());
            }

        } else if (!errors.get("Month").equals("") || !errors.get("Day").equals("")) {
            if (!errors.get("Month").equals("")) {
                MonthComparaison.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                MonthComparaison.setPromptText(errors.get("Month").toString());
            }
            if (!errors.get("Day").equals("")) {
                DayComparaison.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
                DayComparaison.setPromptText(errors.get("Day").toString());
            }
        } else {

//        downLoadCsvByDate(String date)
            String Date1 = Year1Comparaison.getText() + MonthComparaison.getText() + DayComparaison.getText();
            String Date2 = Year2Comparaison.getText() + MonthComparaison.getText() + DayComparaison.getText();

            ArrayList<XYChart.Series> S = ConstructChart(Date1, StationComparaison.getValue().toString());
            ArrayList<XYChart.Series> S2 = ConstructChart(Date2, StationComparaison.getValue().toString());
            lineCharttemp.getData().setAll(S.get(0), S2.get(0));
            lineCharthum.getData().setAll(S.get(1), S2.get(1));
            lineChartnebul.getData().setAll(S.get(2), S2.get(2));

        }
    }

    @FXML
    private void handleButtonActionChangerDroite() {
        if (tabComparaison.isSelected()) {

            if (lineCharttemp.isVisible()) {
                lineCharttemp.setVisible(false);
                lineCharthum.setVisible(true);
                lineChartnebul.setVisible(false);
                RadioBtnHum.setSelected(true);
            } else if (lineCharthum.isVisible()) {
                lineCharttemp.setVisible(false);
                lineCharthum.setVisible(false);
                lineChartnebul.setVisible(true);
                RadioBtnNebul.setSelected(true);
            } else if (lineChartnebul.isVisible()) {
                lineCharttemp.setVisible(true);
                lineCharthum.setVisible(false);
                lineChartnebul.setVisible(false);
                RadioBtnTemp.setSelected(true);
            }
        } else if (VboxComparaison.isVisible()) {
            tableView.setVisible(true);
            VboxComparaison.setVisible(false);
            radioBtnTableur.setSelected(true);
        } else {
            tableView.setVisible(false);
            VboxComparaison.setVisible(true);
            radioBtnCourbes.setSelected(true);
        }

    }

    @FXML
    private void handleButtonActionChangerGauche() {
        if (tabComparaison.isSelected()) {
            if (lineCharttemp.isVisible()) {
                lineCharttemp.setVisible(false);
                lineCharthum.setVisible(false);
                lineChartnebul.setVisible(true);
                RadioBtnNebul.setSelected(true);
            } else if (lineCharthum.isVisible()) {
                lineCharttemp.setVisible(true);
                lineCharthum.setVisible(false);
                lineChartnebul.setVisible(false);
                RadioBtnTemp.setSelected(true);
            } else if (lineChartnebul.isVisible()) {
                lineCharttemp.setVisible(false);
                lineCharthum.setVisible(true);
                lineChartnebul.setVisible(false);
                RadioBtnHum.setSelected(true);
            }
        } else if (VboxComparaison.isVisible()) {
            tableView.setVisible(true);
            VboxComparaison.setVisible(false);
            radioBtnTableur.setSelected(true);
        } else {
            tableView.setVisible(false);
            VboxComparaison.setVisible(true);
            radioBtnCourbes.setSelected(true);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {

        /* String date="2016";
        List<VilleTemp> listee = Downloader.getDataForYearByCity(date,"all");
        if(listee==null){
            try {
                Downloader.downLoadCsvByDate("201601");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201601"));  
                Downloader.downLoadCsvByDate("201602");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201602"));
                Downloader.downLoadCsvByDate("201603");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201603"));
                Downloader.downLoadCsvByDate("201604");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201604"));
                Downloader.downLoadCsvByDate("201605");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201605"));  
                Downloader.downLoadCsvByDate("201606");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201606"));  
                Downloader.downLoadCsvByDate("201607");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201607"));  
                Downloader.downLoadCsvByDate("201608");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201608"));  
                Downloader.downLoadCsvByDate("201609");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201609"));  
                Downloader.downLoadCsvByDate("201610");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201610")); 
                Downloader.downLoadCsvByDate("201611");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201611"));
                Downloader.downLoadCsvByDate("201612");
                Downloader.DecompresserGzip(Downloader.getGzipFilePathFromDate("201612"));  
                
                
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            listee = Downloader.getDataForYearByCity(date,"all");
        } */
        //testss
        // TODO
        /*Commun a toutes les interface */
 /*creation d'un menu dynamiquement commun a toutes les interfaces*/
        menuBar = new MenuBar();

        Menu file = new Menu("File");

        Menu edit = new Menu("Edit");
        Menu window = new Menu("Window");
        Menu statistic = new Menu("Statistic");
        MenuItem view = new MenuItem("View");
        view.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Interface = 2;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("interfaceComparaison.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        statistic.getItems().add(view);
        Menu seting = new Menu("Seting");
        MenuItem preference = new MenuItem("Preférence");
        preference.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Interface = 1;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("fxmlSetting.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        seting.getItems().add(preference);

        menuBar.getMenus().addAll(file, edit, window, statistic, seting);

        VboxPrincipal.getChildren().add(0, menuBar);
        if (Interface == 0) {

            menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #E1E6FA 10%, #ABC8E2 100%);");
            Coordonne.ConstructTabVille();

            InitInterfacePrincipal();

            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    // some code
//                           AfficheInterfacePrincipal.Afficher(stackPane/*,kelvin_celcius*/);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            /*
                            si il y a une connexion internet 
                            si temps % 3 = 0 alors telecharger
                            
                             */
                            //verifier continuellement si il y a une connexion internet

                            AfficheInterfacePrincipal.Afficher(VboxPrincipal, v1, v2, imgviewTempsActuel, LocationDefault, kelvin_celcius);

                        }
                    });
                }
            };

            timer.schedule(t, 01, 1000);

        } else if (Interface == 1) {

            menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #A2B5BF 5%, #375D81 90%);");

            initInterfaceSetting();
        } else {

            menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #A2B5BF 5%, #375D81 90%);");

            InitInterfaceComparaison();
        }

    }

    public void InitInterfacePrincipal() {
        //au debut on verifie si il y a une connexion et on initialise online_offline
        if (Downloader.netIsAvailable() == true) {
            onLine_offLine = "onLine";
        } else {
            onLine_offLine = "offLine";
        }
        Interface = 1;
    }

    public void initInterfaceSetting() {
        Image img = new Image(Meteo.class.getResourceAsStream("Image/BackgroundSetting2.jpg"));
        BackgroundImage background = new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        anchorSeting.setBackground(new Background(background));

        ToggleGroup kelvinCelcius = new ToggleGroup();
        kelvin.setToggleGroup(kelvinCelcius);

        celcius.setToggleGroup(kelvinCelcius);
        celcius.setSelected(true);
        //  celcius.setStyle("-fx-selected-color: yellow;-fx-unselected-color: blue;");
//celcius.getStyleClass().add("red-radio-button");
        ToggleGroup onOffLine = new ToggleGroup();
        online.setToggleGroup(onOffLine);
        online.setSelected(true);
        offline.setToggleGroup(onOffLine);

        //au debut on verifie si il y a une connexion et on initialise online_offline
        LocationDefault = new ChoiceBox();

        LocationDefault.setStyle("-fx-background-color: #7B8D8E/*#74828F*/;-fx-background-radius:20;-fx-border-width:3;");
        List L = new ArrayList();
        for (int i = 0; i < Coordonne.tabVille.size(); i++) {
            L.add(i, Coordonne.tabVille.get(i).getCity().getNom());
        }
        ObservableList<String> observableList = FXCollections.observableList(L);
        observableList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
            }
        });
        LocationDefault.getItems().clear();
        LocationDefault.setItems(observableList);
        LocationDefault.setValue(L.get(L.indexOf("BREST-GUIPAVAS")));
        HboxLocation.getChildren().add(1, LocationDefault);
        //Interface=0;
    }

    public void InitInterfaceComparaison() {
        List L = new ArrayList();

        for (int i = 0; i < Coordonne.tabVille.size(); i++) {
            L.add(i, Coordonne.tabVille.get(i).getCity().getNom());
        }
        ObservableList<String> observableList = FXCollections.observableList(L);
        observableList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change) {
            }
        });

        StationComparaison.setItems(observableList);
        Station.setItems(observableList);

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Température");
        AfficheTemp = new LineChart<Number, Number>(xAxis, yAxis);
        //AfficheTemp.setStyle("-fx-background-color:#9C9F84;");
        final NumberAxis xAxiss = new NumberAxis();
        final NumberAxis yAxiss = new NumberAxis();
        xAxiss.setLabel("Température");
        AfficheHum = new LineChart<Number, Number>(xAxiss, yAxiss);
        final NumberAxis xAxisss = new NumberAxis();
        final NumberAxis yAxisss = new NumberAxis();
        xAxisss.setLabel("Température");
        AfficheNebul = new LineChart<Number, Number>(xAxisss, yAxisss);
        VboxComparaison.getChildren().add(AfficheTemp);
        VboxComparaison.getChildren().add(AfficheHum);
        VboxComparaison.getChildren().add(AfficheNebul);

        /*
        Partie comparaison
         */
        final NumberAxis xAxis1 = new NumberAxis();
        final NumberAxis yAxis1 = new NumberAxis();
        lineCharttemp = new LineChart<Number, Number>(xAxis1, yAxis1);

        final NumberAxis xAxis2 = new NumberAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        lineCharthum = new LineChart<Number, Number>(xAxis2, yAxis2);

        final NumberAxis xAxis3 = new NumberAxis();
        final NumberAxis yAxis3 = new NumberAxis();
        lineChartnebul = new LineChart<Number, Number>(xAxis3, yAxis3);

        /*
        Mettre lineChart de humidité et nébulosité par defaut invisible
         */
        lineCharttemp.setVisible(true);
        lineCharthum.setVisible(false);
        lineChartnebul.setVisible(false);

        stack3.getChildren().addAll(lineCharttemp, lineCharthum, lineChartnebul);

        groupeChart = new ToggleGroup();
        RadioBtnTemp.setToggleGroup(groupeChart);
        RadioBtnHum.setToggleGroup(groupeChart);
        RadioBtnNebul.setToggleGroup(groupeChart);

        RadioBtnTemp.setSelected(true);

        VboxComparaison.setVisible(false);
        groupeRadioAffichage = new ToggleGroup();
        radioBtnCourbes.setToggleGroup(groupeRadioAffichage);
        radioBtnTableur.setToggleGroup(groupeRadioAffichage);
        radioBtnTableur.setSelected(true);
        //Interface=1;

        tableView.setEditable(true);
    }
}
