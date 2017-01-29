/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coordonnee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import meteo.Configuration;

/**
 *
 * @author karim
 * @param tabVille Arrayliste de villes+Temperature
 * @param b boolean
 */
public class Coordonne {
    
    public static ArrayList <VilleTemp>tabVille=new ArrayList<>();
   static boolean  b=false;
    /*constructeur*/
    /*public Coordonne() {
        tabVille=new ArrayList<>();
    }*/
    /**
     * 
     * @param nom
     * @param id
     * @param x
     * @param y
     * @param temperature 
     * Ajouter une ville a la liste tabVille
     */
    public static void  addVille(String nom,int id,int x,int y,double temperature){
        VilleTemp v=new VilleTemp(new Ville(nom, id, new Point(x, y)), temperature);
        tabVille.add(v);
    }
    /**
     * 
     * @param id
     * @param temperature 
     * Modification de la temperature d'une ville donnée
     */
    public static void modifyVille(int id,double temperature){
        for (int i = 0; i < tabVille.size(); i++) {
            if(tabVille.get(i).city.id==id){
                tabVille.get(i).modifierTemperature(temperature);
            }
        }
        //tabVille.get(id);
    }
    /**
     * Construction de la liste tabVille a partir d'un premier fichier contenant 
     * l'id, le nom et la position des villes dans la carte
     * et d'un deuxieme fichier contenant l'id et la temperature récuperer du site MeteoFrance.fr
     */
    public static void ConstructTabVille() {
      
       readFile(Configuration.CITY_FILE_NAME);
       readFile(Configuration.CSV_FILE_NAME);
      b=false;
        

    }
    /**
     * 
     * @param fichier 
     * Lecture d'un fichier et ajout/modification des donnée de tabVille
     */
    public static void readFile(String fichier){
        double d;
        try
        {
        String fichConfig=fichier;
                
         File file = new File (fichConfig);    
         FileReader fr = new FileReader (file);
        BufferedReader br = new BufferedReader (fr);

        try
            {
                String line = br.readLine();
                line = br.readLine();
                while (line != null)
                {
                    //faire un split
                    String[] param = line.split(";");
                    /*
                    Initialement le booleen est a false, a la fin de la lecture du fichier contenant
                    l'id, le nom et la positon des villes le booleen passe a vrai 
                    et cela a fin de permettre la modification des température initialement a 0
                    
                    */
                    if(b==false){
                        
                     addVille(param[1],Integer.parseInt(param[0]),Integer.parseInt(param[2]),Integer.parseInt(param[3]),0);
  
                    }
                    else{
                        
                        if(!param[7].equals("mq")){
                            d=Double.parseDouble(param[7])-273.15;
                            
                            
                        }
                        else{
                            d=100;
                        }
                        modifyVille(Integer.parseInt(param[0]),d);
   
                    }
                    line = br.readLine();
                }

                br.close();
                fr.close();
            }
            catch (IOException exception)
            {
                System.out.println ("Erreur lors de la lecture : " + exception.getMessage());
            }
        }
        catch (FileNotFoundException exception)
            {
                System.out.println ("Le fichier n'a pas été trouvé");
            }
        b=true;
}
    
}
