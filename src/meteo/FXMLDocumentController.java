package meteo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import coordonnee.*;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author karim
 */
public class FXMLDocumentController implements Initializable {

    private List<VilleTemp> dataList;
    private MyModel model;
    private String showMode;
    private ArrayList<XYChart.Series> chartList;
  
    static int Interface = 0;
    static String kelvin_celcius = "celcius";
    static boolean onlineMode = true;
    static ChoiceBox LocationDefault;

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
    ProgressIndicator progressComparaison;
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
    ToggleGroup groupeChart, groupeRadioAffichage;
    @FXML
    VBox v, VboxPrincipal, v1, v2, VboxComparaison;

    @FXML
    private void handleButtonActionComparer() throws IOException {

        /*
        verifier le formulaire done
        afficher les donnée  done
        verifier si donnée existe sinon les telecharger
        verifier si ville selectionné
         */
        Year1Comparaison.setStyle("-fx-background-color: #333333, #D9E577 , #333333;");
        Year1Comparaison.setText("");
        Year2Comparaison.setStyle("-fx-background-color: #333333, #D9E577 , #333333;");
        Year2Comparaison.setText("");
        MonthComparaison.setStyle("-fx-background-color: #333333, #D9E577 , #333333;");
        MonthComparaison.setText("");
        DayComparaison.setStyle("-fx-background-color: #333333, #D9E577 , #333333;");
        DayComparaison.setText("");
        
        Map errors = model.validateDate(Year1Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());
        Map errors2 = model.validateDate(Year2Comparaison.getText(), MonthComparaison.getText(), DayComparaison.getText());

        if (!errors.get("Year").toString().equals("") & !errors2.get("Year").toString().equals("")) {
            if (!errors.get("Year").toString().equals("")) {
                Year1Comparaison.setStyle("-fx-background-color: #333333, red , #333333;");

                Year1Comparaison.setPromptText(errors.get("Year").toString());
            }
            if (!errors2.get("Year").toString().equals("")) {
                Year2Comparaison.setStyle("-fx-background-color: #333333, red , #333333;");
                Year2Comparaison.setPromptText(errors.get("Year").toString());
            }

        } else if (!errors.get("Month").equals("") || !errors.get("Day").equals("")) {
            if (!errors.get("Month").equals("")) {
                MonthComparaison.setStyle("-fx-background-color: #333333, red , #333333;");
                MonthComparaison.setPromptText(errors.get("Month").toString());
            }
            if (!errors.get("Day").equals("")) {
                DayComparaison.setStyle("-fx-background-color: #333333, red , #333333;");
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

            if (model.netIsAvailable() != -1) {
                onlineMode = true;
            } else {
                offline.setSelected(true);
            }

        }
        if (offline.isSelected()) {
            //onLine_offLine = "offLine";
            onlineMode = false;
        }
    }

    @FXML
    private void handleButtonActionAfficher() {
        /*
        Test si le formulaire est bien rempli
         */
         year.setStyle("-fx-background-color: #333333, #D9E577 , #333333;");
//        year.setText("");
        month.setStyle("-fx-background-color: #333333, #D9E577 , #333333;");
//        month.setText("");
        day.setStyle("-fx-background-color: #333333, #D9E577 , #333333;");
//        day.setText("");
      
        Map errors = model.validateDate(year.getText(), month.getText(), day.getText());
         String latestDate, latest;
               
        /*
        test si la année n'est pas vide (champs obligatoire)
         */
        if (!errors.get("Year").toString().equals("")) {
            year.setStyle("-fx-background-color: #333333, red , #333333;");
            year.setPromptText(errors.get("Year").toString());
            /*
            si année pas vide et correcte on test le mois et le jour (peuvent etre vide
             */
        } else if (!errors.get("Month").equals("") || !errors.get("Day").equals("")) {
            if (!errors.get("Month").equals("")) {
                month.setStyle("-fx-background-color: #333333, red , #333333;");
                month.setPromptText(errors.get("Month").toString());
            }
            if (!errors.get("Day").equals("")) {
                day.setStyle("-fx-background-color: #333333, red , #333333;");
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

        CreateMenu();
        switch (Interface) {
            case 0:
                InitInterfacePrincipal();
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
     * *****************Private Methode***************************************************************
     */
    /**
     * permet d'initialiser l'interface principale
     */
    public void InitInterfacePrincipal() {
        //au debut on verifie si il y a une connexion et on initialise online_offline
        onlineMode = model.netIsAvailable() != -1; // onLine_offLine = "onLine";
        //onLine_offLine = "offLine";
        VboxPrincipal.getChildren().add(0, menuBar);
        menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #E1E6FA 10%, #ABC8E2 100%);");
        //Coordonne.ConstructTabVille();

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

                        AfficherCarte();


                    }
                });
            }
        };

        timer.schedule(t, 01, 1000);

    }

    /**
     * permet d'initialiser l'interface setting
     */
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
        celcius.setSelected(true);

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

    /**
     * permet d'initialiser l'interface d'affichage et de comparaison des
     * données
     */
    public void InitInterfaceComparaison() {
        VboxPrincipal.getChildren().add(0, menuBar);
        menuBar.getStylesheets().add("/CSS/CSSComparaison.css");
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

    /**
     * permet d'initialiser l'interface permettant de savoir quelles données
     * l'utilisateur a sur sa machine
     */
    public void initInterfaceInformation() {
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
        treeView.setRoot(treeRoot);
    }

    /**
     * permet d'initialiser l'interface permettant de savoir si le serveur de
     * meteo france contenant les données nécessaires est bien en marche est
     * combien de temps a fallut pour faire un ping
     */
    public void initInterfaceEtatServeur() {
        double time = model.netIsAvailable();
        if (time != -1) {
            etatConnexion.setText("En marche " + Double.toString(time) + " Milli Secondes");
        } else {
            etatConnexion.setText("Hors service ");
        }
    }

    /**
     * Permet de crée le menu commun aux trois interface (principale, setting,
     * comparaison)
     */
    private void CreateMenu() {
        /*Commun a toutes les interface */
 /*creation d'un menu dynamiquement commun a toutes les interfaces*/
        menuBar = new MenuBar();

        Menu file = new Menu("_File");
        MenuItem ImportData = new MenuItem("Import Data");

        ImportData.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Import Dialog");
                alert.setHeaderText("Les données souhaitées sont malheureusement indisponibles ");
                alert.setContentText("Clickez sur Import pour importer des données téléchargé manuellement");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // ... user chose OK

                    //Open new dialog for import data and display it 
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Open File");
                    chooser.getExtensionFilters().addAll(new ExtensionFilter(".txt", ".csv"));

                    List<File> recupp = chooser.showOpenMultipleDialog(new Stage());
                    if (recupp != null) {

                        /*
                        what can we do??
                        2 case 
                        first user select a csv.zip file then we call function who decompress and read file and save data into arraylist
                        or user select only a csv file, in this case we read file and add data into a arrayList
                        Question: do we display this alert when user click into afficher or comparer 
                        or let user click into import from menu ???
                         */
                    }
                } else {
                    // ... user chose CANCEL or closed the dialog
                }
            }

        }
        );
        MenuItem VisiteWebSite = new MenuItem("Visit Web Site");

        VisiteWebSite.setOnAction(
                new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e
            ) {
                String url = "https://donneespubliques.meteofrance.fr/";

                //how to open default browser and visit url defined below
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }
        );
        MenuItem Information = new MenuItem("Informations sur les données");

        Information.setOnAction(
                new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e
            ) {

                Parent root;
                try {
                    Interface = 3;
                    root = FXMLLoader.load(getClass().getResource("InterfaceDataInformation.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        );
        MenuItem EtatServeur = new MenuItem("Etat du serveur MeteoFrance");

        EtatServeur.setOnAction(
                new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e
            ) {

                Parent root;
                try {
                    Interface = 4;
                    root = FXMLLoader.load(getClass().getResource("InterfaceInformations.fxml"));
                    Scene scene = new Scene(root);
                    Stage s = new Stage();
                    s.setScene(scene);
                    s.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        );
        MenuItem Close = new MenuItem("Close");

        Close.setOnAction(
                new EventHandler<ActionEvent>() {

            @Override
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
        statistic.getItems()
                .add(view);
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

        help.getItems()
                .addAll(aide, aPropos);
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
                RadioBtnNebul.setSelected(true);
            } else if (lineCharthum.isVisible()) {
                lineCharttemp.setVisible(val);//false
                lineCharthum.setVisible(false);
                lineChartnebul.setVisible(!val);//true
                RadioBtnTemp.setSelected(true);
            } else if (lineChartnebul.isVisible()) {
                lineCharttemp.setVisible(!val);//true
                lineCharthum.setVisible(val);//false
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

    /**
     * Permet d'afficher l'interface principal
     */
    private void AfficherCarte() {
        /*recuperation de la l'image de la carte de france*/
        int t;
        /*Constrution de la table des villes avec leurs données*/

        ArrayList<VilleTemp> tabVille = null;
        String X = "°C";

        StackPane stack = new StackPane();

        Text nomVille = new Text();
        nomVille.setFill(Color.CHOCOLATE);

        String nomVillle;
        /*C is a choiceBox who contain the name of all station*/

        nomVillle = (LocationDefault != null) ? LocationDefault.getValue().toString() : "BREST-GUIPAVAS";
        tabVille = model.getLatestAvailableData();

        /*Affichage sur l'interface principal le nom de la ville selectionnée par défaut et la derniere date connu
        avec les temératures,humidité et nébulosité adéquat +image representatif de la nébulosité*/
        nomVille.setText(nomVillle);
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
//        LocalDate localDate = LocalDate.now();
        Text DateActuelle = new Text();
        DateActuelle.setFill(Color.CHOCOLATE);

//        DateActuelle.setText(dtf.format(localDate));
        String date = tabVille.get(0).getDate().toString().substring(0, 4) + "/"
                + tabVille.get(0).getDate().toString().substring(4, 6) + "/"
                + tabVille.get(0).getDate().toString().substring(6, 8);

        DateActuelle.setText(date);
        Text Temp = new Text("temperature: ");
        Temp.setFill(Color.CHOCOLATE);
        Text Humidite = new Text("humidité: ");
        Humidite.setFill(Color.CHOCOLATE);
        Text nebulosite = new Text("nébulosité: ");
        nebulosite.setFill(Color.CHOCOLATE);

        for (int i = 0; i < tabVille.size(); i++) {
            //faux : on accede jamais comme sa , getters et setter -_-
            //System.out.println("nom ville:"+nomVillle+ "tabVille:"+tabVille.get(i).getCity().getNom());
            if (tabVille.get(i).getCity().getNom().equals(nomVillle)) {
                if (tabVille.get(i).getTemperature() != 101) {
                    Temp.setText("Température: " + Integer.toString((int) Math.ceil(tabVille.get(i).getTemperature())));
                } else {
                    Temp.setText("Température: " + "N/A");
                }

                if (tabVille.get(i).getHumidite() <= 100) {
                    Humidite.setText("Humidité: " + Integer.toString(tabVille.get(i).getHumidite()) + "%");
                } else {
                    Humidite.setText("Humidité: " + "N/A");
                }
                if (tabVille.get(i).getNebulosite() <= 100) {
                    nebulosite.setText("Nébulosité: " + Float.toString(tabVille.get(i).getNebulosite()) + "%");
                } else {
                    nebulosite.setText("Nébulosité: " + "N/A");
                }
                /*Affichage de l'image representatif de la nébulosité*/

                String s = "Image/".concat(getTempsActuel(tabVille.get(i).getNebulosite())).concat(".png");

                Image tempactuel = new Image(Meteo.class.getResourceAsStream(s));
                imgviewTempsActuel.setImage(tempactuel);

            }

        }

        v1.getChildren().setAll(nomVille, DateActuelle);
        v2.getChildren().setAll(Temp, Humidite, nebulosite);

        Image image = new Image(Meteo.class.getResourceAsStream("Image/country-fra.png"));

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
        // gridPane.gridLinesVisibleProperty().set(true);

        for (int i = 0; i < 20; i++) {
            ColumnConstraints column1 = new ColumnConstraints(10.0, 496.0, 496.0, Priority.SOMETIMES, HPos.CENTER, false);
            gridPane.getColumnConstraints().add(i, column1);
            RowConstraints row1 = new RowConstraints(10.0, 30.0, 30.0, Priority.ALWAYS, VPos.CENTER, false);/*(10.0,30.0,30.0);*/
            gridPane.getRowConstraints().add(i, row1);
        }

        for (int i = 0; i < tabVille.size(); i++) {
            if (tabVille.get(i).getCity().getPoint().getX() == 0 & tabVille.get(i).getCity().getPoint().getY() == 0) {
                continue;
            }

            double temp;

            temp = tabVille.get(i).getTemperature();
            if (kelvin_celcius.equals("kelvin")) {
                temp = tabVille.get(i).getTemperature() + 237.15;
                X = "k";
            }

            t = (int) Math.ceil(temp);
            Text text;
            if (t != 101) {
                text = new Text(Integer.toString(t) + X);
            } else {
                text = new Text("N/A");
            }

            text.setFill(Color.CHOCOLATE);
            gridPane.add(text, tabVille.get(i).getCity().getPoint().getY(), tabVille.get(i).getCity().getPoint().getX());// colonne-ligne
        }

        stack.getChildren().addAll(imgview, gridPane);
//        V.getChildren().set(1,H);
        VboxPrincipal.getChildren().set(2, stack);
    }

    /**
     *
     * @param date
     * @param station
     * @return Liste of DataBean
     */
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

    /**
     *
     * @param nebul
     * @return une chaine de caractére qui represente le nom de l'image associé
     * a la nébulosité d'une station donnée
     */
    private String getTempsActuel(float nebul) {

        if (nebul < 33) {
            return "ensoleille";
        } else if (nebul < 66) {
            return "nuageux";
        } else if (nebul <= 100) {
            return "pluvieux";
        } else {
            return "NA";
        }

    }
}
