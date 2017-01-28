/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;
import coordonnee.*;
import coordonnee.Coordonne;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
Coordonne emplacement;
    @FXML
    private void handleButtonAction(ActionEvent event) {
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        /*recuperation de la l'image de la carte de france*/
        image = new Image(Meteo.class.getResourceAsStream("Image/country-fra.png"));
        /*instanciation de la liste des villes*/
        emplacement=new Coordonne();
        /*appel de la methode qui recupére les donnée du fichier configuration.txt
          et les mets dans la liste instancier précédement   
        */
        emplacement.ConstructTabVille();
        
        imgview.setImage(image);
        /*Affichage des temperature une par une sur la carte */
        for (int i = 0; i < emplacement.tabVille.size(); i++) {
            double temp=new Double(emplacement.tabVille.get(i).temperature);
            int t=(int)Math.ceil(temp);
            text=new Text(Integer.toString(t)+"°");
            text.setFill(Color.BROWN);
            gridPane.add(text, emplacement.tabVille.get(i).city.point.y, emplacement.tabVille.get(i).city.point.x);// colonne-ligne
            
        }
                
        
        
        
    }    
    
}
