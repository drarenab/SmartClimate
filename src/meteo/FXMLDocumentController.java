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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    ImageView imgview;
    @FXML
    StackPane stackPane;
    @FXML
    Text text, text2;
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
       
            image = new Image(Meteo.class.getResourceAsStream("Image/country-fra.png"));
            emplacement = new Coordonne();
            emplacement.ConstructTabVille();
            imgview.setImage(image);
            for (int i = 0; i < emplacement.tabVille.size(); i++) {
                double temp = new Double(emplacement.tabVille.get(i).temperature);
                int t = (int) Math.ceil(temp);
                text = new Text(Integer.toString(t) + "°");
                text.setFill(Color.BROWN);
                gridPane.add(text, emplacement.tabVille.get(i).city.point.y, emplacement.tabVille.get(i).city.point.x);// colonne-ligne
                
            }
            
        try { 
            Downloader.downLoadCsvByDate("200010", "output.csv.gz");
            Downloader.DecompresserGzip("output.csv.gz", "output.csv");
            
            Downloader.downLoadCsvByDate("199605", "temporaire.csv.gz");
            Downloader.DecompresserGzip("temporaire.csv.gz", "temporaire.csv");
            
            Downloader.concatenateCsvByDate("temporaire.csv","output.csv","199605");
            
            Downloader.downLoadCsvByDate("199601", "temporaire.csv.gz");
            Downloader.DecompresserGzip("temporaire.csv.gz", "temporaire.csv");
           
            Downloader.concatenateCsvByDate("temporaire.csv","output.csv","199601");
         
            Downloader.downLoadCsvByDate("199603", "temporaire.csv.gz");
            Downloader.DecompresserGzip("temporaire.csv.gz", "temporaire.csv");
           
            
            Downloader.concatenateCsvByDate("temporaire.csv","output.csv","199603");
         
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

}
