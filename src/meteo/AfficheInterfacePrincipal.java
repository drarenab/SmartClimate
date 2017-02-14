/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import coordonnee.Coordonne;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
     * Methode static permettant d'afficher sur l'interface principale la temperature sur la carte en kelvin ou en celcius
     */
    public static void Afficher(VBox V,VBox v1,VBox v2,ImageView imgviewTempsActuel,ChoiceBox C,String kelvin_celcius){
     /*recuperation de la l'image de la carte de france*/
        int t;
        /*Constrution de la table des villes avec leurs données*/
        
        String X="°C";
        
        StackPane stack=new StackPane();
       
        Text nomVille=new Text();
        nomVille.setFill(Color.CHOCOLATE);
        
        String nomVillle;
        /*C is a choiceBox who contain the name of all station*/
        if(C!=null){
            
            nomVillle=C.getValue().toString();
            
        }
        else{
            nomVillle="BREST-GUIPAVAS";
            
        }
        /*Affichage sur l'interface principal le nom de la ville selectionnée par défaut et la derniere date connu
        avec les temératures,humidité et nébulosité adéquat +image representatif de la nébulosité*/
        nomVille.setText(nomVillle);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        Text DateActuelle=new Text();
        DateActuelle.setFill(Color.CHOCOLATE);
        
        DateActuelle.setText(dtf.format(localDate));
        
        
        Text Temp=new Text("temperature: ");
        Temp.setFill(Color.CHOCOLATE);
        Text Humidite=new Text("humidité: ");
        Humidite.setFill(Color.CHOCOLATE);
        Text  nebulosite=new Text("nébulosité: ");
        nebulosite.setFill(Color.CHOCOLATE);
         
        for (int i = 0; i < Coordonne.tabVille.size(); i++) {
            //faux : on accede jamais comme sa , getters et setter -_-
            System.out.println("nom ville:+"+nomVillle+ "tabVille:"+Coordonne.tabVille.get(i).getCity().getNom().toString());;
            if(Coordonne.tabVille.get(i).getCity().getNom().equals(nomVillle)){
                if(Coordonne.tabVille.get(i).getTemperature()!=101){
                    Temp.setText("Température: "+Integer.toString((int) Math.ceil(Coordonne.tabVille.get(i).getTemperature()))); 
                }
                else{
                    Temp.setText("Température: "+"N/A");
                }
                
               if(Coordonne.tabVille.get(i).getHumidite()<=100){
                    Humidite.setText("Humidité: "+Integer.toString(Coordonne.tabVille.get(i).getHumidite())+"%"); 
               }
             
               else{
                    Humidite.setText("Humidité: "+"N/A");
                }
               if(Coordonne.tabVille.get(i).getNebulosite()<=100){
                    nebulosite.setText("Nébulosité: "+Float.toString(Coordonne.tabVille.get(i).getNebulosite())+"%"); 
                }
                else{
                    nebulosite.setText("Nébulosité: "+"N/A");
                }
               /*Affichage de l'image representatif de la nébulosité*/
               
               String s="Image/".concat(getTempsActuel(Coordonne.tabVille.get(i).getNebulosite())).concat(".png");
                
                Image tempactuel = new Image(Meteo.class.getResourceAsStream(s));
                imgviewTempsActuel.setImage(tempactuel);

            }
                    
        }
        
        v1.getChildren().setAll(nomVille,DateActuelle);
        v2.getChildren().setAll(Temp,Humidite,nebulosite);

        Image image = new Image(Meteo.class.getResourceAsStream("Image/country-fra.png"));

        /*appel de la methode qui recupére les donnée du fichier configuration.txt
          et les mets dans la liste instancier précédement   
         */

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
       // gridPane.gridLinesVisibleProperty().set(true);
        
        for (int i = 0; i < 20; i++) {
            ColumnConstraints column1 = new ColumnConstraints(10.0, 496.0, 496.0, Priority.SOMETIMES, HPos.CENTER, false);
            gridPane.getColumnConstraints().add(i, column1);
            RowConstraints row1=new RowConstraints(10.0, 30.0, 30.0, Priority.ALWAYS,VPos.CENTER, false);/*(10.0,30.0,30.0);*/
            gridPane.getRowConstraints().add(i,row1);
    }
       
        for (int i = 0; i < Coordonne.tabVille.size(); i++) {
            if(Coordonne.tabVille.get(i).getCity().getPoint().getX()==0 &Coordonne.tabVille.get(i).getCity().getPoint().getY()==0 ){
                continue;
            }
            
            double temp;
            
            temp = Coordonne.tabVille.get(i).getTemperature();
             if(kelvin_celcius.equals("kelvin")){
                  temp= Coordonne.tabVille.get(i).getTemperature()+237.15;
                    X="k";
            }
            
             t = (int) Math.ceil(temp);
             Text text;
            if(t!=101){
                 text=new Text(Integer.toString(t)+X);
            }
            else{
                 text=new Text("N/A");
            }
             
             text.setFill(Color.CHOCOLATE);
            gridPane.add(text, Coordonne.tabVille.get(i).getCity().getPoint().getY(), Coordonne.tabVille.get(i).getCity().getPoint().getX());// colonne-ligne
        } 
        
        stack.getChildren().addAll(imgview,gridPane);
//        V.getChildren().set(1,H);
        V.getChildren().set(2,stack);
}   
    /**
     * 
     * @param nebul
     * @return une chaine de caractére qui represente 
     * le nom de l'image associé a la nébulosité d'une station donnée
     */
    public static String getTempsActuel(float nebul){
        
        if(nebul<33) return "ensoleille";
        else if(nebul<66) return "nuageux";
        
        else if (nebul<=100) return "pluvieux";
        else return "NA";
   
    }
}
