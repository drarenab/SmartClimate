/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import java.io.IOException;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    VBox v,VboxPrincipal;
    @FXML
    GridPane gridPane;
    static int Interface;
    @FXML
    MenuBar menuBar;
    @FXML
    AnchorPane anchorSeting;
    @FXML
    RadioButton kelvin,celcius,online,offline;
    static String kelvin_celcius="celcius";
    AfficheInterfacePrincipal A;
    @FXML
    private void handleButtonActionChngTemp() {
        if (kelvin.isArmed()){
                   kelvin_celcius="kelvin";
                   
                 }  
        if(celcius.isArmed()){
            kelvin_celcius="celcius";
        }
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
        if(Interface==0){
           menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #ffffff 0%, #f2f2f2 100%);");
 
        }
        else{
           menuBar.setStyle("-fx-background-color:linear-gradient(to bottom, #A2B5BF 5%, #375D81 90%);");

        }
        
        VboxPrincipal.getChildren().add(0, menuBar);
        if (Interface==0){
            InitInterfacePrincipal();
          
        Timer timer = new Timer ();
        TimerTask t = new TimerTask () {
            @Override
            public void run () {
             // some code
//                           AfficheInterfacePrincipal.Afficher(stackPane/*,kelvin_celcius*/);
            Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println();
                      AfficheInterfacePrincipal.Afficher(stackPane,kelvin_celcius);
                      
                    }
                  });   
            }
        };

        timer.schedule (t, 01, 1000);
      
        }
        else{
            initInterfaceSetting();
        }
        
      
    }

    public void InitInterfacePrincipal(){
       
        
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
        Interface=1;
    }
    
}
