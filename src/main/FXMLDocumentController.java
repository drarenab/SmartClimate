package main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import abstraction.Controller;
import abstraction.Model;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.paint.Color;
import smart.DataBean;
import smart.DataBean2;
import utilitaire.*;

/**
 * This class is the Controller part of the MVC
    it is the intermediate between the Views and the Model

 * it contains methods to intialise the different views
 * and event listeners for different actions from the views
 */
public class FXMLDocumentController implements Controller {
    private Model model;
    static int Interface = 0;
    static String kelvin_celcius = "celcius";
    static boolean onlineMode = true;
    static boolean userSelectOffline = false;
    static ChoiceBox LocationDefault;
    private ArrayList<String> stationNames;
    ArrayList<DataBean2> tabVille = null;
    @FXML
    AnchorPane anchorSeting, AnchorVisu, anchorComp;
    AreaChart<Number, Number> AfficheTemp, AfficheNebul, AfficheHum;
    @FXML
    Button rightVisu, leftVisu, rightComp, leftComp;
    @FXML
    ChoiceBox Station, StationComparaison;
    @FXML
    GridPane gridPane;
    @FXML
    HBox HboxLocation;
    @FXML
    ImageView imgview, imgviewTempsActuel;
    @FXML
    LineChart LineChartTemp;
    LineChart<Number, Number> lineCharttemp, lineCharthum, lineChartnebul;
    @FXML
    MenuBar menuBar;

    @FXML
    RadioButton kelvin, celcius, online, offline;
    @FXML
    RadioButton RadioBtnTemp, RadioBtnHum, RadioBtnNebul, radioBtnCourbes, radioBtnTableur;
    @FXML
    StackPane stackPane, stack3;
    @FXML
    SplitPane splitPane;
    @FXML
    Tab tabVisualisation, tabComparaison;
    @FXML
    TableView<DataBean> tableView;
    @FXML
    TableColumn<DataBean, String> columnName, columnNebul, columnHum, columnTemp, columnDate;
    @FXML
    TextField Year1Comparaison, Year2Comparaison, MonthComparaison, DayComparaison, year, month, day;
    @FXML
    TabPane tabPane;
    @FXML
    Text text, text2, etatConnexion;
    @FXML
    TreeView treeView;
    ToggleGroup groupeChart, groupeRadioAffichage, MinMaxMoyenne;
    @FXML
    VBox v, VboxPrincipal, v1, v2, VboxComparaison;
    @FXML
    RadioButton MoyRadio, MaxRadio, MinRadio;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        model = MyModel.getInstance();
        CreateMenu();
        switch (Interface) {
            case 0: {

                try {
                    InitInterfacePrincipal();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case 1:
                initInterfaceSetting();
                break;
            case 2:
                InitInterfaceComparaison();
                break;
            case 3:
                //informations sur les donnée
                initInterfaceInformation();
                break;
            case 4:
                //etat serveur
                initInterfaceEtatServeur();
                break;
            default:
                break;
        }

    }

     /**
     * permet d'initialiser l'interface principale
     */
    @Override
    public void InitInterfacePrincipal() throws IOException {

        //au debut on verifie si il y a une connexion et on initialise online_offline
        //onLine_offLine = "offLine";
        VboxPrincipal.getChildren().add(0, menuBar);
        menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #E1E6FA 10%, #ABC8E2 100%);");
        //Coordonne.ConstructTabVille();

        /*
        on telecharger si possible le dernier fichier a chaque execution du programme
         */
        onlineMode = Utilitaire.netIsAvailable() != -1; // verifier chaque seconde si il ya une connexion internet

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String[] laDate = (dateFormat.format(date)).split("/");
        try {
            tabVille = (ArrayList<DataBean2>) model.getLatestDataForGraphicMap(userSelectOffline);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RunTimer();
    }

    /**
     * permet d'initialiser l'interface setting
     */
    @Override
    public void initInterfaceSetting() {
        VboxPrincipal.getChildren().add(0, menuBar);
        menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #A2B5BF 5%, #375D81 90%);");

        Image img = new Image(Meteo.class
                .getResourceAsStream("Image/BackgroundSetting2.jpg"));
        BackgroundImage background = new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        anchorSeting.setBackground(new Background(background));

        ToggleGroup kelvinCelcius = new ToggleGroup();
        kelvin.setToggleGroup(kelvinCelcius);

        celcius.setToggleGroup(kelvinCelcius);

        if (kelvin_celcius.equals("celcius")) {
            celcius.setSelected(true);
        } else if (kelvin_celcius.equals("kelvin")) {
            kelvin.setSelected(true);
        }

        ToggleGroup onOffLine = new ToggleGroup();
        online.setToggleGroup(onOffLine);
        online.setSelected(true);
        offline.setToggleGroup(onOffLine);

        Timer timer = new Timer();
        TimerTask t = new TimerTask() {

            public void run() {
                // some code
//                           AfficheInterfacePrincipal.Afficher(stackPane/*,kelvin_celcius*/);
                Platform.runLater(new Runnable() {

                    public void run() {
                        onlineMode = Utilitaire.netIsAvailable() != -1; // verifier chaque seconde si il ya une connexion internet
                        if (online != null & userSelectOffline == false) {
                            if (onlineMode) {
                                online.setSelected(true);
                            } else {
                                offline.setSelected(true);
                            }
                        }
                    }
                });
            }
        };

        timer.schedule(t, 01, 1000);

        //au debut on verifie si il y a une connexion et on initialise online_offline
        LocationDefault = new ChoiceBox();

        LocationDefault.setStyle(
                "-fx-background-color: #7B8D8E/*#74828F*/;-fx-background-radius:20;-fx-border-width:3;");
        List L = new ArrayList();
        //dataList = model.getLatestAvailableData()
        stationNames = (ArrayList<String>) model.getStationNames();
        for (int i = 0;
                i < stationNames.size();
                i++) {
            L.add(i, stationNames.get(i));
        }
        ObservableList<String> observableList = FXCollections.observableList(L);

        observableList.addListener(
                new ListChangeListener() {

            public void onChanged(ListChangeListener.Change change
            ) {

            }
        });
        LocationDefault.getItems()
                .clear();
        LocationDefault.setItems(observableList);

        LocationDefault.setValue(L.get(L.indexOf("BREST-GUIPAVAS")));
        HboxLocation.getChildren()
                .add(1, LocationDefault);
        //Interface=0;
    }

    /**
     * permet d'initialiser l'interface d'affichage et de comparaison des
     * données
     */
    @Override
    public void InitInterfaceComparaison() {
        VboxPrincipal.getChildren().add(0, menuBar);
        menuBar.getStylesheets().add("/CSS/CSSComparaison.css");
        List L = new ArrayList();

        //dataList = model.getAllStations();
        stationNames = (ArrayList) model.getStationNames();
        for (int i = 0; i < stationNames.size(); i++) {
            L.add(i, stationNames.get(i));
        }

        ObservableList<String> observableList = FXCollections.observableList(L);
        observableList.addListener(new ListChangeListener() {

            public void onChanged(ListChangeListener.Change change) {
            }
        });

        StationComparaison.setItems(observableList);
        Station.setItems(observableList);

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Température");

        AfficheTemp = new AreaChart<Number, Number>(xAxis, yAxis);
        final NumberAxis xAxiss = new NumberAxis();
        xAxis.setAutoRanging(true);
        final NumberAxis yAxiss = new NumberAxis();

        xAxiss.setLabel("Humudité");
        AfficheHum = new AreaChart<Number, Number>(xAxiss, yAxiss);
        final NumberAxis xAxisss = new NumberAxis();
        final NumberAxis yAxisss = new NumberAxis();

        xAxisss.setLabel("Nebulosité");
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

        MinMaxMoyenne = new ToggleGroup();
        MoyRadio.setToggleGroup(MinMaxMoyenne);
        MinRadio.setToggleGroup(MinMaxMoyenne);
        MaxRadio.setToggleGroup(MinMaxMoyenne);
        MoyRadio.setSelected(true);
        //Interface=1;

        tableView.setEditable(true);
        tabPane.getStylesheets().add("/CSS/CSSTabPane.css");

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
    }

    /**
     * permet d'initialiser l'interface permettant de savoir quelles données
     * l'utilisateur a sur sa machine
     */
    @Override
    public void initInterfaceInformation() {

        ArrayList<String> listYear = model.getYearExists();

        HBox hboxDataDispo = new HBox();
        Text textDataDispo = new Text("Data Dispo");
        ImageView deleteimg = new ImageView(
                new Image(getClass().getResourceAsStream("Image/delete.png"),
                        20, 20, true, false));
        Button buttonDeleteAll = new Button();
        buttonDeleteAll.setStyle("-fx-background-color:transparent;");
        buttonDeleteAll.setGraphic(deleteimg);
        buttonDeleteAll.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                for (int i = 0; i < listYear.size(); i++) {
                    Utilitaire.deleteCSVFile(listYear.get(i));
                    initInterfaceInformation();
                }
            }
        });

        hboxDataDispo.getChildren().addAll(textDataDispo, buttonDeleteAll);
        final TreeItem<HBox> dispoData = new TreeItem<>(hboxDataDispo);
        dispoData.setExpanded(true);
        for (int i = 0; i < listYear.size(); i++) {
            String s = listYear.get(i);
            HBox hboxYear = new HBox();
            Text textYear = new Text(s);
            Button buttonDelete = new Button();
            ImageView deleteimg1 = new ImageView(
                    new Image(getClass().getResourceAsStream("Image/delete.png"),
                            20, 20, true, false));
            buttonDelete.setGraphic(deleteimg1);
            buttonDelete.setStyle("-fx-background-color:transparent;");

            buttonDelete.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent e) {
                    Utilitaire.deleteCSVFile(s);
                    initInterfaceInformation();
                }
            });

            hboxYear.getChildren().addAll(textYear, buttonDelete);
            final TreeItem<HBox> oneYear = new TreeItem<>(hboxYear);

            ArrayList<String> listMonth = model.getMonthsExistsForYear(listYear.get(i));

            for (int j = 0; j < listMonth.size(); j++) {
                HBox hboxMonth = new HBox();

                String str = listMonth.get(j).substring(5, 11);

                Text textMonth = new Text(listMonth.get(j).substring(9, 11));
                Button buttonDeleteMonth = new Button();
                ImageView deleteimg2 = new ImageView(
                        new Image(getClass().getResourceAsStream("Image/delete.png"),
                                20, 20, true, false));
                buttonDeleteMonth.setGraphic(deleteimg2);
                buttonDeleteMonth.setStyle("-fx-background-color:transparent;");
                buttonDeleteMonth.setOnAction(new EventHandler<ActionEvent>() {

                    public void handle(ActionEvent e) {

                        Utilitaire.deleteCSVFile(str);
                        initInterfaceInformation();
                    }
                });
                hboxMonth.getChildren().addAll(textMonth, buttonDeleteMonth);
                oneYear.getChildren().add(j, new TreeItem(hboxMonth));
            }

            dispoData.getChildren().add(i, oneYear);

        }
        treeView.setRoot(dispoData);
    }

    /**
     * permet d'initialiser l'interface permettant de savoir si le serveur de
     * main france contenant les données nécessaires est bien en marche est
     * combien de temps a fallut pour faire un ping
     */
    @Override
    public void initInterfaceEtatServeur() {
        double time = Utilitaire.netIsAvailable();
        if (time != -1) {
            etatConnexion.setText("En marche " + Double.toString(time) + " Milli Secondes");
        } else {
            etatConnexion.setText("Hors service ");
        }
    }
    
    @FXML
    private void handleButtonActionComparer() throws IOException {
        String latestDate1 = "", latestDate2 = "";
        /*
        verifier le formulaire done
        afficher les donnée  done
        verifier si donnée existe sinon les telecharger
        verifier si ville selectionné
         */
        Year1Comparaison.setStyle("-fx-background-color: #C9E2E9 , #D9E577 ,#C9E2E9;");

        Year2Comparaison.setStyle("-fx-background-color: #C9E2E9, #D9E577 , #C9E2E9;");

        MonthComparaison.setStyle("-fx-background-color: #C9E2E9, #D9E577 , #C9E2E9;");

        DayComparaison.setStyle("-fx-background-color: #C9E2E9, #D9E577 , #C9E2E9;");

        Map errors = model.validateDate(Year1Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());
        Map errors2 = model.validateDate(Year2Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());

        Station.setStyle("-fx-background-color:#000000,linear-gradient(#7ebcea, #2f4b8f),"
                + "linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);");
        if (StationComparaison.getValue() == null) {
            StationComparaison.setStyle("-fx-background-color:#000000,linear-gradient(#ed2d2d, #d60000),"
                    + "linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);");
        } else if (!errors.get("Year").toString().equals("") || !errors2.get("Year").toString().equals("")) {
            if (!errors.get("Year").toString().equals("")) {
                Year1Comparaison.setStyle("-fx-background-color: #C9E2E9, red , #C9E2E9;");
                Year1Comparaison.setPromptText(errors.get("Year").toString());
            }
            if (!errors2.get("Year").toString().equals("")) {
                Year2Comparaison.setStyle("-fx-background-color: #C9E2E9, red , #C9E2E9;");
                Year2Comparaison.setPromptText(errors.get("Year").toString());
            }

        } else if (!errors.get("Month").equals("") || !errors.get("Day").equals("")) {
            if (!errors.get("Month").equals("")) {
                MonthComparaison.setStyle("-fx-background-color: #C9E2E9, red , #C9E2E9;");
                MonthComparaison.setPromptText(errors.get("Month").toString());
            }
            if (!errors.get("Day").equals("")) {
                DayComparaison.setStyle("-fx-background-color: #C9E2E9, red , #C9E2E9;");
                DayComparaison.setPromptText(errors.get("Day").toString());
            }
        } else {
            boolean validated1, validated2;
            validated1 = model.validateNotFuture(Year1Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());
            validated2 = model.validateNotFuture(Year2Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());
            String yearMonth1 = "", yearMonth2 = "";
            if (validated1 && validated2) {
                AfficheTemp.setTitle("Températures");
                AfficheHum.setTitle("Humidité");
                AfficheNebul.setTitle("Nébulosité");


                model.constructChartComparaison(StationComparaison.getValue().toString(),
                        Year1Comparaison.getText(),
                        MonthComparaison.getText(),
                        DayComparaison.getText(),
                        Year2Comparaison.getText(),
                        MonthComparaison.getText(),
                        DayComparaison.getText(),
                        lineCharttemp,
                        lineCharthum,
                        lineChartnebul,
                        MinOrMaxOrMoy(MoyRadio, MaxRadio, MinRadio),
                        userSelectOffline,
                        kelvin_celcius
                );
            } else {
                //not logicaly valid date!
            }

            /*

             */
        }
    }

    @FXML
    private void handleButtonActionChangerDroite() {

        ChangeDirection(false);
    }

    @FXML
    private void handleButtonActionChangerGauche() {
        ChangeDirection(true);

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
        if (online.isSelected()) {

            if (Utilitaire.netIsAvailable() != -1) {
                onlineMode = true;
                userSelectOffline = false;
            } else {
                onlineMode = false;
                offline.setSelected(true);
            }

        }
        if (offline.isSelected()) {
            onlineMode = false;
            userSelectOffline = true;
        }
    }

    @FXML
    private void handleButtonActionAfficher() throws IOException {
        /*
        Test si le formulaire est bien rempli
         */
        year.setStyle("-fx-background-color: #C9E2E9, #D9E577 , #C9E2E9;");

        month.setStyle("-fx-background-color: #C9E2E9, #D9E577 , #C9E2E9;");

        day.setStyle("-fx-background-color: #C9E2E9, #D9E577 , #C9E2E9;");

        Map errors = model.validateDate(year.getText(), month.getText(), day.getText());

        /*
        test si la année n'est pas vide (champs obligatoire)
         */
        Station.setStyle("-fx-background-color:#000000,linear-gradient(#7ebcea, #2f4b8f),"
                + "linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);");
        if (Station.getValue() == null) {
            Station.setStyle("-fx-background-color:#000000,linear-gradient(#ed2d2d, #d60000),"
                    + "linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);");
        } else if (!errors.get("Year").toString().equals("")) {
            year.setStyle("-fx-background-color: #C9E2E9, red , #C9E2E9;");
            year.setPromptText(errors.get("Year").toString());
            /*
            si année pas vide et correcte on test le mois et le jour (peuvent etre vide
             */
        } else if (!errors.get("Month").equals("") || !errors.get("Day").equals("")) {
            if (!errors.get("Month").equals("")) {
                month.setStyle("-fx-background-color: #C9E2E9, red , #C9E2E9;");
                month.setPromptText(errors.get("Month").toString());
            }
            if (!errors.get("Day").equals("")) {
                day.setStyle("-fx-background-color: #C9E2E9, red , #C9E2E9;");
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
                String kelCel = (kelvin_celcius.equals("kelvin")) ? " K" : " °C";
                columnHum.setCellValueFactory(new PropertyValueFactory<DataBean, String>("humidite"));
                columnNebul.setCellValueFactory(new PropertyValueFactory<DataBean, String>("nebulosite"));
                columnTemp.setCellValueFactory(new PropertyValueFactory<DataBean, String>("temperature"));
                columnTemp.setText("Température" + kelCel);
                columnDate.setCellValueFactory(new PropertyValueFactory<DataBean, String>("date"));

                model.Affichage(Station.getValue().toString(),
                        year.getText(),
                        month.getText(),
                        day.getText(),
                        AfficheTemp,
                        AfficheHum,
                        AfficheNebul,
                        tableView,
                        MinOrMaxOrMoy(MoyRadio, MaxRadio, MinRadio),
                        userSelectOffline,
                        kelvin_celcius
                );

//                TableView
//                 */
//                /*Attention can be throw an exception if data is null ! */
            } else {
                //not logicaly valid date!
            }
        }

    }

    
    /**
     * *****************Private Methode***************************************************************
     */
   
    /**
     * Permet de lancer le thread qui se charge d'afficher la carte et
     * telecharger les données chaque 3 heures
     */
    private void RunTimer() {
        Timer timer = new Timer();
        TimerTask t = new TimerTask() {

            public void run() {
                Platform.runLater(new Runnable() {

                    public void run() {
                        onlineMode = Utilitaire.netIsAvailable() != -1; // verifier chaque seconde si il ya une connexion internet

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String[] laDate = (dateFormat.format(date)).split("/");

                        /*
                        1-verifier que la date est %3
                        2-verifier qu'on est en mode en ligne

                         */
                        //verifier continuellement si il y a une connexion internet
                        if (date.getHours() % 3 == 1 && date.getMinutes() == 0 && date.getSeconds() == 0) {
                            try {
                                tabVille = (ArrayList<DataBean2>) model.getLatestDataForGraphicMap(userSelectOffline);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        AfficherCarte();

                    }
                });
            }
        };

        timer.schedule(t, 01, 1000);
    }

    /**
     * Permet de crée le menu commun aux trois interface (principale, setting,
     * comparaison)
     */
    private void CreateMenu() {
        /*Commun a toutes les interface */
 /*creation d'un menu dynamiquement commun a toutes les interfaces*/
        menuBar = new MenuBar();

        Menu file = new Menu("_Fichier");
        MenuItem ImportData = new MenuItem("Importer les données");

        ImportData.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                try {
                    model.DisplayAlertToImport();
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        );
        MenuItem VisiteWebSite = new MenuItem("Visiter le site web");

        VisiteWebSite.setOnAction(
                new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e
            ) {
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

        Information.setOnAction(
                new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e
            ) {

                Parent root;
                try {
                    Interface = 3;
                    root = FXMLLoader.load(getClass().getResource("LocalDataView.fxml"));
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

        EtatServeur.setOnAction(
                new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e
            ) {

                Parent root;
                try {
                    Interface = 4;
                    root = FXMLLoader.load(getClass().getResource("ServerStateView.fxml"));
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
        MenuItem Close = new MenuItem("Fermer");

        Close.setOnAction(
                new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e
            ) {
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
                .addAll(ImportData, VisiteWebSite, Information, EtatServeur, Close);

        Menu window = new Menu("_Affichage");
        MenuItem maximize = new MenuItem("Plein écran");

        maximize.setOnAction(
                new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event
            ) {
//                Stage s = ((Stage) (((MenuItem)maximize).getScene().getWindow()));
                Stage s = (Stage) menuBar.getScene().getWindow();
                s.setMaximized(true);

            }
        }
        );

        MenuItem minimize = new MenuItem("Ecran fenêtré ");

        minimize.setOnAction(
                new EventHandler<ActionEvent>() {

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
        Menu statistic = new Menu("_Statistique");
        MenuItem view = new MenuItem("Visualiser/Comparer");

        view.setOnAction(
                new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event
            ) {
                Interface = 2;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("DataComparView.fxml"));
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
        statistic.getItems()
                .add(view);
        Menu seting = new Menu("_Paramétres");
        MenuItem preference = new MenuItem("Preférences");

        preference.setOnAction(
                new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event
            ) {
                Interface = 1;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("SettingView.fxml"));
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
        Menu help = new Menu("_Aide");

        MenuItem aPropos = new MenuItem("A Propos");

        aPropos.setOnAction(
                new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event
            ) {
                Interface = 1;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("AProposView.fxml"));
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

        help.getItems()
                .addAll(aPropos);
        menuBar.getMenus()
                .addAll(file, window, statistic, seting, help);

        if (Interface == 0) {

            //Enlever le droit du full screen
            maximize.setDisable(true);
            minimize.setDisable(true);
        }
    }

    /**
     * Permet de changer la direction du changement de slide (chart/Tableview )
     *
     * @param val boolean
     */
    private void ChangeDirection(boolean val) {
        if (tabComparaison.isSelected()) {
            if (lineCharttemp.isVisible()) {
                lineCharttemp.setVisible(false);
                lineCharthum.setVisible(!val);//true
                lineChartnebul.setVisible(val);//false
                RadioBtnNebul.setSelected(val);
                RadioBtnHum.setSelected(!val);
            } else if (lineCharthum.isVisible()) {
                lineCharttemp.setVisible(val);//false
                lineCharthum.setVisible(false);
                lineChartnebul.setVisible(!val);//true
                RadioBtnTemp.setSelected(val);
                RadioBtnNebul.setSelected(!val);
            } else if (lineChartnebul.isVisible()) {
                lineCharttemp.setVisible(!val);//true
                lineCharthum.setVisible(val);//false
                lineChartnebul.setVisible(false);
                RadioBtnHum.setSelected(val);
                RadioBtnTemp.setSelected(!val);
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

    /**
     * Permet d'afficher l'interface principal
     */
    private void AfficherCarte() {
        /*recuperation de la l'image de la carte de france*/
        int t;
        /*Constrution de la table des villes avec leurs données*/

        String X = "°C";
        StackPane stack = new StackPane();
        Text nomVille = new Text();
        nomVille.setFill(Color.CHOCOLATE);
        String nomVillle;
        /*C is a choiceBox who contain the name of all station*/
        nomVillle = (LocationDefault != null) ? LocationDefault.getValue().toString() : "BREST-GUIPAVAS";
        /**
         * HERE*
         */
        /*Affichage sur l'interface principal le nom de la ville selectionnée par défaut et la derniere date connu
        avec les temératures,humidité et nébulosité adéquat +image representatif de la nébulosité*/
        nomVille.setText(nomVillle);
        Text DateActuelle = new Text();
        DateActuelle.setFill(Color.CHOCOLATE);

        String date = tabVille.get(0).getDate().getDay() + "/"
                + tabVille.get(0).getDate().getMonth() + "/"
                + tabVille.get(0).getDate().getYear() + " a "
                + Integer.parseInt(tabVille.get(0).getDate().getTime()) * 3 + " Heures";

        DateActuelle.setText(date);
        Text Temp = new Text("temperature: ");
        Temp.setFill(Color.CHOCOLATE);
        Text Humidite = new Text("humidité: ");
        Humidite.setFill(Color.CHOCOLATE);
        Text nebulosite = new Text("nébulosité: ");
        nebulosite.setFill(Color.CHOCOLATE);
        float temperatureTemp;

        for (int i = 0; i < tabVille.size(); i++) {

            if (tabVille.get(i).getNomStation().equals(nomVillle)) {
                if (tabVille.get(i).getTemperature() != 101) {
                    temperatureTemp = tabVille.get(i).getTemperature();
                    if (kelvin_celcius.equals("kelvin")) {
                        temperatureTemp = (float) (temperatureTemp + 273.15);
                    }
                    Temp.setText("Température: " + Float.toString((int) Math.ceil(temperatureTemp)));
                } else {
                    Temp.setText("Température: " + "N/A");
                }

                if (tabVille.get(i).getHumidite() <= 100) {
                    Humidite.setText("Humidité: " + Float.toString(tabVille.get(i).getHumidite()) + "%");
                } else {
                    Humidite.setText("Humidité: " + "N/A");
                }
                if (tabVille.get(i).getNebulosite() <= 100) {
                    nebulosite.setText("Nébulosité: " + Float.toString(tabVille.get(i).getNebulosite()) + "%");
                } else {
                    nebulosite.setText("Nébulosité: " + "N/A");
                }
                /*Affichage de l'image representatif de la nébulosité*/

                String s = "Image/".concat(Utilitaire.getTempsActuel(tabVille.get(i).getNebulosite())).concat(".png");

                Image tempactuel = new Image(Meteo.class
                        .getResourceAsStream(s));
                imgviewTempsActuel.setImage(tempactuel);

            }

        }

        v1.getChildren().setAll(nomVille, DateActuelle);
        v2.getChildren().setAll(Temp, Humidite, nebulosite);

        Image image = new Image(Meteo.class
                .getResourceAsStream("Image/country-fra.png"));

        /*appel de la methode qui recupére les donnée du fichier configuration.txt
          et les mets dans la liste instancier précédement
         */
        ImageView imgview = new ImageView();
        imgview.setImage(image);
        imgview.setFitHeight(508);//508
        imgview.setFitWidth(553);//553f
        imgview.pickOnBoundsProperty().set(true);
        imgview.preserveRatioProperty().set(true);

        /*Affichage des temperature une par une sur la carte */
        GridPane gridPane = new GridPane();
        gridPane.getChildren().clear();

        gridPane.setMaxSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        gridPane.setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        gridPane.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        for (int i = 0; i < 20; i++) {
            ColumnConstraints column1 = new ColumnConstraints(10.0, 496.0, 496.0, Priority.SOMETIMES, HPos.CENTER, false);
            gridPane.getColumnConstraints().add(i, column1);
            RowConstraints row1 = new RowConstraints(10.0, 30.0, 30.0, Priority.ALWAYS, VPos.CENTER, false);/*(10.0,30.0,30.0);*/
            gridPane.getRowConstraints().add(i, row1);
        }

        for (int i = 0; i < tabVille.size(); i++) {
            if (tabVille.get(i).getX() == 0 & tabVille.get(i).getY() == 0) {
                continue;
            }

            double temp;

            temp = tabVille.get(i).getTemperature();
            if (kelvin_celcius.equals("kelvin")) {
                temp = tabVille.get(i).getTemperature() + 273.15;
                X = "k";
            }

            t = (int) Math.ceil(temp);
            Text text;
            if (kelvin_celcius.equals("kelvin")) {
                text = (t != 375) ? new Text(Integer.toString(t) + X) : new Text("N/A");
            } else {
                text = (t != 101) ? new Text(Integer.toString(t) + X) : new Text("N/A");
            }

            text.setFill(Color.CHOCOLATE);
            gridPane.add(text, tabVille.get(i).getY(), tabVille.get(i).getX());// colonne-ligne
        }

        stack.getChildren().addAll(imgview, gridPane);
        VboxPrincipal.getChildren().set(2, stack);
    }
   
    /**
     *
     * @param MoyRadio
     * @param MaxRadio
     * @param MinRadio
     * @return un entier representant quel radio button est selectionné min,max
     * ou bien moyenne
     */
    private int MinOrMaxOrMoy(RadioButton MoyRadio, RadioButton MaxRadio, RadioButton MinRadio) {
        if (MinRadio.isSelected()) {
            return 1;//pour le min
        }
        if (MaxRadio.isSelected()) {
            return 2;//pour le max
        }
        return 0;//par defaut c'est la moyenne
    }
}
