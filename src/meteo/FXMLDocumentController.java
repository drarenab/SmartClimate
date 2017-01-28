/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import java.io.IOException;

import coordonnee.Coordonne;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author karim
 */
public class FXMLDocumentController implements Initializable {

    Image image;
    @FXML
    ImageView imgview;
    @FXML
    StackPane stackPane;
    @FXML
    Text text, text2;
    @FXML
    VBox v,VboxPrincipal;
    @FXML
    GridPane gridPane;
    Coordonne emplacement;
    static int Interface;
    @FXML
    MenuBar menuBar;
    @FXML
    AnchorPane anchorSeting;
    @FXML
    RadioButton kelvin,celcius,online,offline;
    @FXML
    private void handleButtonActionPreference() throws IOException {
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        /*Commun a toutes les interface */
        
        /*creation d'un menu dynamiquement commun a toutes les interfaces*/
        menuBar=new MenuBar();
        
        Menu file =new Menu("File");
        
        Menu edit =new Menu("Edit");
        Menu window =new Menu("Window");
        Menu seting =new Menu("Seting");
        MenuItem preference=new MenuItem("Preférence");
        preference.setOnAction(new EventHandler<ActionEvent>(){
        

            @Override
            public void handle(ActionEvent event) {
                Interface=1;
                Parent root;
                try {
                    root = FXMLLoader.load(getClass().getResource("fxmlSetting.fxml"));
                     Scene scene = new Scene(root);
                     Stage s=new Stage();
                     s.setScene(scene);
                     s.show();
                    
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
        
       
            }
                });
        seting.getItems().add(preference);
        
        menuBar.getMenus().addAll(file,edit,window,seting);
        //menuBar.setEffect("-fx-background-color:linear-gradient(to bottom, #ffffff 0%, #f2f2f2 100%);");
        
        VboxPrincipal.getChildren().add(0, menuBar);
        if (Interface==0){
            InitInterfacePrincipal();
        }
        else{
            initInterfaceSetting();
        }
    }

    public void InitInterfacePrincipal(){
        Interface=0;
        /*recuperation de la l'image de la carte de france*/
        image = new Image(Meteo.class.getResourceAsStream("Image/country-fra.png"));
        /*instanciation de la liste des villes*/
        emplacement = new Coordonne();
        /*appel de la methode qui recupére les donnée du fichier configuration.txt
          et les mets dans la liste instancier précédement   
         */
        emplacement.ConstructTabVille();

        imgview.setImage(image);
        /*Affichage des temperature une par une sur la carte */
        for (int i = 0; i < emplacement.tabVille.size(); i++) {
            double temp = new Double(emplacement.tabVille.get(i).temperature);
            int t = (int) Math.ceil(temp);
            text = new Text(Integer.toString(t) + "°");
            text.setFill(Color.BROWN);
            gridPane.add(text, emplacement.tabVille.get(i).city.point.y, emplacement.tabVille.get(i).city.point.x);// colonne-ligne

        }
        
        try {
            /* Essai telechargement et decompression */
           Downloader.downLoadCsv();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Downloader.DecompresserGzip();
        } catch (Exception e) {
        }
        Interface=1;
    }
    public void initInterfaceSetting(){
    Image img=new Image(Meteo.class.getResourceAsStream("Image/BackgroundSetting2.jpg"));
        BackgroundImage background =new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        anchorSeting.setBackground(new Background(background));
        
        ToggleGroup kelvinCelcius=new ToggleGroup();
        kelvin.setToggleGroup(kelvinCelcius);
        
        celcius.setToggleGroup(kelvinCelcius);
        celcius.setSelected(true);
        ToggleGroup onOffLine =new ToggleGroup();
        online.setToggleGroup(onOffLine);
        online.setSelected(true);
        offline.setToggleGroup(onOffLine);
    }
}
