/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author karim
 */
public class FXMLDocumentController implements Initializable {
    
    
Image image;
@FXML
ImageView imgview ;
@FXML
StackPane stackPane ;
@FXML
Text text,text2;
@FXML
VBox v;
@FXML
GridPane gridPane;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        image = new Image(Meteo.class.getResourceAsStream("country-fra.png"));
        /*on Crée une gridPAne de ROWS*COLS (a definir) puis on met dans la case 
        correspendante la valeur de la temperature
        */
        imgview.setImage(image);
        text = new Text("20°");
        text.setLayoutX(50);
        text.setLayoutY(50);
        gridPane.add(text, 5, 10);
        
        
    }    
    
}
