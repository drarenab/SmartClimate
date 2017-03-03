package meteo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.awt.Desktop;
import java.net.URI;
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
import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Map;
import javafx.application.HostServices;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
//import static meteo.Downloader.getIdFromNameVille;

/**
 *
 * @author karim
 */
public class FXMLDocumentController implements Initializable {

    private List<VilleTemp> dataList;
    private MyModel model;
    private String showMode;
    private ArrayList<XYChart.Series> chartList;
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
    //static String onLine_offLine = "onLine";

    static boolean onlineMode = true;
    @FXML
    VBox v1, v2, VboxComparaison;
    @FXML
    ImageView imgviewTempsActuel;
    @FXML
    HBox HboxLocation;
    // @FXMLE
    static ChoiceBox LocationDefault;
    @FXML
    ChoiceBox Station, StationComparaison;
    @FXML
    LineChart LineChartTemp;

    AreaChart<Number, Number> AfficheTemp;
    AreaChart<Number, Number> AfficheHum;
    AreaChart<Number, Number> AfficheNebul;

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
    TabPane tabPane;
    @FXML
    SplitPane splitPane;
    @FXML
    Button rightVisu, leftVisu, rightComp, leftComp;
    @FXML
    AnchorPane AnchorVisu, anchorComp;
    @FXML
    ProgressIndicator progressComparaison;
    @FXML
    Text etatConnexion;
    @FXML
    TreeView treeView;

    private List<DataBean> parseDataList(String date, String station) {
        ArrayList<VilleTemp> listDonnee = model.getListForChart(date, station);
        ArrayList<DataBean> listDataBean = new ArrayList<DataBean>();
        if (listDonnee != null) {
            for (VilleTemp villeTemp : listDonnee) {
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
        Map errors = model.validateDate(Year1Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());
        Map errors2 = model.validateDate(Year2Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());

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

            ArrayList<XYChart.Series> S = model.constructChart(Date1, StationComparaison.getValue().toString());
            ArrayList<XYChart.Series> S2 = model.constructChart(Date2, StationComparaison.getValue().toString());
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
            if (model.netIsAvailable() != -1) {
                //onLine_offLine = "onLine";
                onlineMode = true;
            } else {
                offline.arm();
            }

        }
        if (offline.isArmed()) {
            //onLine_offLine = "offLine";
            onlineMode = false;
        }
    }

    @FXML
    private void handleButtonActionAfficher() {
        /*
        Test si le formulaire est bien rempli
         */
        Map errors = model.validateDate(year.getText(), month.getText(), day.getText());
         String latestDate, latest;
               
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
            boolean validated;
            validated = model.validateNotFuture(year.getText(), month.getText(), day.getText());
            String yearMonth = "", yearMonthDay = "";
            if (validated) {
                progressComparaison.setVisible(true);
                AfficheTemp.setTitle("Températures");
                AfficheHum.setTitle("Humidité");
                AfficheNebul.setTitle("Nébulosité");
                /**
                 * CAN BE PUT INSIDE A METHOD
                 */
                // si l'utilisateur a saisie l'année et le mois et le jour donc on est sur le mode "day"
                if (month.getText().length() > 0 && day.getText().length() > 0) {
                    showMode = "day";

                    yearMonth = year.getText() + month.getText();
                    yearMonthDay = yearMonth + day.getText();
                } // si l'utilisateur a saisie que le mois alors on est sur le mode "month"
                else if (month.getText().length() > 0) {
                    showMode = "month";
                    yearMonth = year.getText() + month.getText();
                } // si l'utilisateur a saisie que l'année alors on est sur le mode "year"
                else {
                    showMode = "year";
                }

                if (showMode.equals("day")) {
                    //si on est sur le day mode 
                    System.out.println("Day MODE ,looking for data for whole day=" + day.getText());
                    latestDate = model.getLatestAvailableDateOnFile(yearMonth);
                    // si la derniere date est null or si la dernier date dans le fichier est inferieure a la date demander 
                    if (latestDate == null
                            || Integer.parseInt(latestDate.substring(6, 8)) < Integer.parseInt(day.getText())) {
                        System.out.println("Data asked cannot be found , Downloading data for whole YearMonth = " + yearMonth + " ...");
                        try {
                            model.downloadAndUncompress(year.getText() + month.getText());
                        } catch (IOException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                   // si on est sur le month mode
                } else if (showMode.equals("month")) {
                    System.out.println("Month MODE ,looking for data for whole yearMonth=" + yearMonth);
                    //si le mois n'est pas a jour
                    if (!model.isUpdatedMonth(yearMonth)) {
                        System.out.println("Data of the wanted month is not completed, Dowloading data for whole yearMonth = " + yearMonth + " ...");
                        try {
                            //on lance le télechargement
                            model.downloadAndUncompress(year.getText() + month.getText());
                        } catch (IOException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                  // si on est sur le year mode
                } else if (showMode.equals("year")) {
                    System.out.println("Year MODE ,looking for data for whole year=" + year.getText());
                    //retourner la liste des mois qui manques dans le dossier de l'année
                    ArrayList<String> missedMonths = model.getMissedMonthsFiles(year.getText());
                    for (String month : missedMonths) {
                        try {
                            System.out.println("Data for The month=" + month + " is not completed , Downloading data for the whole month ...");
                            //on lance le telechargement des mois qui ne sont pas a jour
                            model.downloadAndUncompress(month);
                        } catch (IOException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                System.out.println("Everything looks good, Trying to construct the chart");
                //on lance la construction de chart
                chartList = model.constructChart(year.getText()
                        + month.getText() + day.getText(), Station.getValue().toString());

                if (chartList != null) {
                    AfficheTemp.getData().setAll(chartList.get(0));
                    AfficheHum.getData().setAll(chartList.get(1));
                    AfficheNebul.getData().setAll(chartList.get(2));
                    System.out.println("Chart constructed succefully ");
                } else {
                    System.out.println("Opps ,Chart cannot be constructed please submit a bug report ");
                }
                /*
                TableView
                 */
 /*Attention can be throw an exception if data is null ! */
                columnName.setCellValueFactory(new PropertyValueFactory<DataBean, String>("nomVille"));
                columnHum.setCellValueFactory(new PropertyValueFactory<DataBean, String>("humidite"));
                columnNebul.setCellValueFactory(new PropertyValueFactory<DataBean, String>("nebulosite"));
                columnTemp.setCellValueFactory(new PropertyValueFactory<DataBean, String>("temperature"));
                columnDate.setCellValueFactory(new PropertyValueFactory<DataBean, String>("date"));
                tableView.getItems().setAll(parseDataList(year.getText() + month.getText() + day.getText(),
                        Station.getValue().toString()
                ));
            } else {
                //not logicaly valid date!
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {

        model = new MyModel();

        /*Commun a toutes les interface */
 /*creation d'un menu dynamiquement commun a toutes les interfaces*/
        menuBar = new MenuBar();

        Menu file = new Menu("_File");
        MenuItem VisiteWebSite = new MenuItem("Visit Web Site");

        VisiteWebSite.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                String url = "https://donneespubliques.meteofrance.fr/";

                //how to open default browser and visit url defined below
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));

                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } catch (URISyntaxException ex) {
                        Logger.getLogger(FXMLDocumentController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
        );
        MenuItem Information = new MenuItem("Informations sur les données");

        Information.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                Parent root;
                try {
                    Interface = 3;
                    root = FXMLLoader.load(getClass().getResource("InterfaceDataInformation.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        );
        MenuItem EtatServeur = new MenuItem("Etat du serveur MeteoFrance");

        EtatServeur.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                Parent root;
                try {
                    Interface = 4;
                    root = FXMLLoader.load(getClass().getResource("InterfaceInformations.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        MenuItem Close = new MenuItem("Close");

        Close.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (Interface == 0) {
                    System.exit(0);

                } else {
                    Stage s = (Stage) menuBar.getScene().getWindow();
                    s.close();
                }

            }

        }
        );
        file.getItems()
                .addAll(VisiteWebSite, Information, EtatServeur, Close);

        Menu window = new Menu("_Window");
        MenuItem maximize = new MenuItem("Full Screen");

        maximize.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event
            ) {
//                Stage s = ((Stage) (((MenuItem)maximize).getScene().getWindow()));
                Stage s = (Stage) menuBar.getScene().getWindow();
                s.setMaximized(true);

            }
        }
        );

        MenuItem minimize = new MenuItem("Windowed Screen");

        minimize.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event
            ) {
//                Stage s = ((Stage) (((MenuItem)maximize).getScene().getWindow()));
                Stage s = (Stage) menuBar.getScene().getWindow();
                s.setMaximized(false);

            }
        }
        );
        window.getItems()
                .addAll(maximize, minimize);
        Menu statistic = new Menu("_Statistic");
        MenuItem view = new MenuItem("View");

        view.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event
            ) {
                Interface = 2;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("interfaceComparaison.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );
        statistic.getItems().add(view);
        Menu seting = new Menu("_Seting");
        MenuItem preference = new MenuItem("Preférence");

        preference.setOnAction(
                new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event
            ) {
                Interface = 1;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("fxmlSetting.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );
        seting.getItems()
                .add(preference);
        Menu help = new Menu("_Help");
        MenuItem aide = new MenuItem("Aide");
        MenuItem aPropos = new MenuItem("A Propos");

        aPropos.setOnAction(
                new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event
            ) {
                Interface = 1;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("APropos.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );

        help.getItems().addAll(aide, aPropos);
        menuBar.getMenus().addAll(file, window, statistic, seting, help);

        if (Interface == 0) {
            VboxPrincipal.getChildren().add(0, menuBar);
            menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #E1E6FA 10%, #ABC8E2 100%);");

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

                            //AfficheInterfacePrincipal.Afficher(VboxPrincipal, v1, v2, imgviewTempsActuel, LocationDefault, kelvin_celcius, model);
                        }
                    });
                }
            };

            timer.schedule(t, 01, 1000);

            //Enlever le droit du full screen
            maximize.setDisable(true);
            minimize.setDisable(true);
        } else if (Interface == 1) {
            VboxPrincipal.getChildren().add(0, menuBar);
            menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #A2B5BF 5%, #375D81 90%);");

            initInterfaceSetting();
        } else if (Interface == 2) {
            VboxPrincipal.getChildren().add(0, menuBar);
            menuBar.getStylesheets().add("/CSS/CSSComparaison.css");
            InitInterfaceComparaison();
        } else if (Interface == 3) {//informations sur les donnée

            final TreeItem<String> treeRoot = new TreeItem<>("Data Disponible");
            treeRoot.setExpanded(true);
            ArrayList<String> list = model.getYearExists();

            for (int i = 0; i < list.size(); i++) {
                final TreeItem<String> fruitItem = new TreeItem<>(list.get(i));
                ArrayList<String> liste = model.getMonthsExistsForYear(list.get(i));

                for (int j = 0; j < liste.size(); j++) {
                    fruitItem.getChildren().add(i, new TreeItem(liste.get(j).substring(9, 11)));
                }

                fruitItem.setExpanded(true);
                treeRoot.getChildren().add(i, fruitItem);

            }

////            treeRoot.getChildren().setAll(fruitItem, vegetableItem);
//            final TreeItem<String> fruitItem = new TreeItem<>("Fruits");
//            fruitItem.getChildren().setAll(
//                    new TreeItem("Fraise"),
//                    new TreeItem("Pomme"),
//                    new TreeItem("Poire")
//            );
//            fruitItem.setExpanded(true);
//            final TreeItem<String> vegetableItem = new TreeItem<>("Légumes");
//            vegetableItem.getChildren().setAll(
//                    new TreeItem("Artichaut"),
//                    new TreeItem("Laitue"),
//                    new TreeItem("Radis")
//            );
//            vegetableItem.setExpanded(true);
            treeView.setRoot(treeRoot);

        } else if (Interface == 4) {//etat serveur
            double time = model.netIsAvailable();
            if (time != -1) {
                etatConnexion.setText("En marche " + Double.toString(time) + " Milli Secondes");
            } else {
                etatConnexion.setText("Hors service ");
            }

        }

    }

    public void InitInterfacePrincipal() {
        //au debut on verifie si il y a une connexion et on initialise online_offline
        if (model.netIsAvailable() != -1) {
            // onLine_offLine = "onLine";
            onlineMode = true;
        } else {
            //onLine_offLine = "offLine";
            onlineMode = false;
        }
        Interface = 1;
    }

    public void initInterfaceSetting() {
        Image img = new Image(Meteo.class
                .getResourceAsStream("Image/BackgroundSetting2.jpg"));
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
        dataList = model.getLatestAvailableData();
        for (int i = 0; i < dataList.size(); i++) {
            L.add(i, dataList.get(i).getCity().getNom());
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
        dataList = model.getLatestAvailableData();
        for (int i = 0; i < dataList.size(); i++) {
            L.add(i, dataList.get(i).getCity().getNom());
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
        AfficheTemp = new AreaChart<Number, Number>(xAxis, yAxis);
        //AfficheTemp.setStyle("-fx-background-color:#9C9F84;");
        final NumberAxis xAxiss = new NumberAxis();
        final NumberAxis yAxiss = new NumberAxis();
        xAxiss.setLabel("Température");
        AfficheHum = new AreaChart<Number, Number>(xAxiss, yAxiss);
        final NumberAxis xAxisss = new NumberAxis();
        final NumberAxis yAxisss = new NumberAxis();
        xAxisss.setLabel("Température");
        AfficheNebul = new AreaChart<Number, Number>(xAxisss, yAxisss);
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
        tabPane.getStylesheets().add("/CSS/CSSTabPane.css");
        System.out.println("styleSheet");

        AnchorVisu.getStylesheets().add("/CSS/CSSSplitPane.css");
        anchorComp.getStylesheets().add("/CSS/CSSSplitPane.css");
        ImageView image_view_btn_right = new ImageView(
                new Image(getClass().getResourceAsStream("Image/right1.png"),
                        35, 35, true, false));
        rightVisu.setGraphic(image_view_btn_right);

        ImageView image_view_btn_left = new ImageView(
                new Image(getClass().getResourceAsStream("Image/left1.png"),
                        35, 35, true, false));
        leftVisu.setGraphic(image_view_btn_left);
        image_view_btn_right = new ImageView(
                new Image(getClass().getResourceAsStream("Image/right1.png"),
                        35, 35, true, false));
        rightComp.setGraphic(image_view_btn_right);

        image_view_btn_left = new ImageView(
                new Image(getClass().getResourceAsStream("Image/left1.png"),
                        35, 35, true, false));
        leftComp.setGraphic(image_view_btn_left);
        progressComparaison.setVisible(false);
    }
}
