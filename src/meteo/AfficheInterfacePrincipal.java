/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import coordonnee.Coordonne;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author karim
 */
public class AfficheInterfacePrincipal {
 
    /**
     *
     * @param stack
     * @param kelvin_celcius
     * Methode static permettant d'afficher sur l'interface principale la temerature sur la carte en kelvin ou en celcius
     */
    public static void Afficher(StackPane stack,String kelvin_celcius){
  /*recuperation de la l'image de la carte de france*/
 
        Image image = new Image(Meteo.class.getResourceAsStream("Image/country-fra.png"));

        /*appel de la methode qui recupére les donnée du fichier configuration.txt
          et les mets dans la liste instancier précédement   
         */
        Coordonne.ConstructTabVille();
        ImageView imgview=new ImageView();
        imgview.setImage(image);
        imgview.setFitHeight(508);//508
        imgview.setFitWidth(553);//553f
        imgview.pickOnBoundsProperty().set(true);
        imgview.preserveRatioProperty().set(true);

        /*Affichage des temperature une par une sur la carte */
        GridPane gridPane=new GridPane();
        gridPane.getChildren().clear();
        
        gridPane.setMaxSize(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE);
        gridPane.setMinSize(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE);
        gridPane.setPrefSize(USE_COMPUTED_SIZE,USE_COMPUTED_SIZE);
        gridPane.gridLinesVisibleProperty().set(true);
        
        for (int i = 0; i < 20; i++) {
            ColumnConstraints column1 = new ColumnConstraints(10.0, 496.0, 496.0, Priority.SOMETIMES, HPos.CENTER, false);
            gridPane.getColumnConstraints().add(i, column1);
            RowConstraints row1=new RowConstraints(10.0, 30.0, 30.0, Priority.SOMETIMES,VPos.CENTER, false);/*(10.0,30.0,30.0);*/
            gridPane.getRowConstraints().add(i,row1);
    }
       /* for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Text texte = new Text();
                //gridPane.add(texte, i, j);
            }
    }*/
        for (int i = 0; i < Coordonne.tabVille.size(); i++) {
            double temp;
            Text text  = new Text();
            int t;
             if(kelvin_celcius.equals("kelvin")){
                  temp= Coordonne.tabVille.get(i).temperature+237.15;
                    t = (int) Math.ceil(temp);
                   text.setText(Integer.toString(t) + "k");
                   
              
            
            }
             else{
                  temp = Coordonne.tabVille.get(i).temperature;
                  t= (int) Math.ceil(temp);
                  text.setText(Integer.toString(t) + "°C");
             }
             //text.setFill(Color.WHITE);
             text.setFill(Color.BROWN);
             
            gridPane.add(text, Coordonne.tabVille.get(i).city.point.y, Coordonne.tabVille.get(i).city.point.x);// colonne-ligne
            
        } 
        
        stack.getChildren().addAll(imgview,gridPane);
}    
}
