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
import meteo.Downloader;

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
   
    public static void  addVille(String nom,int id,int x,int y,double temperature,int humidite,float nebulosite){
   //     VilleTemp v=new VilleTemp(new Ville(nom, id, new Point(x, y)), temperature,humidite,nebulosite);
      //  tabVille.add(v);
    }
    /**
     * 
     * @param id
     * @param temperature 
     * Modification de la temperature d'une ville donnée
     */
    public static void modifyVille(int id,double temperature,int f,float g){
        for (int i = 0; i < tabVille.size(); i++) {
            if(tabVille.get(i).getCity().getId()==id){
                tabVille.get(i).modifierTemperature(temperature);
                tabVille.get(i).modifierHumidite(f);
                tabVille.get(i).modifierNebulosite(g);
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
      /*
       readFile(Configuration.CITY_FILE_NAME);
       readFile(Configuration.CSV_FILE_NAME);
       b=false;
       
      */ 
        String file = Downloader.getLatesttAvailableFile();
        String date = Downloader.getLatestAvailableDateOnFile(file);       
        if(date!=null) {
           tabVille = Downloader.getDataForDateByCity(date, "all");
        }
       
    }
    /**
     * 
     * @param fichier 
     * Lecture d'un fichier et ajout/modification des donnée de tabVille
     */
    public static void readFile(String fichier){
        
        String f;
        String g;
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
                        
                     addVille(param[1],Integer.parseInt(param[0]),Integer.parseInt(param[2]),Integer.parseInt(param[3]),0,0,0);
  
                    }
                    else{
                        
//                        if(!param[7].equals("mq")){
//                          double  d=Double.parseDouble(param[7])-273.15;
//                            
//                        }
//                        else{
//                            d=100;
//                        }
//                        if(!param[9].equals("mq")){
//                            f=param[9];
//                        }
//                        else{
//                            f="101";
//                        }
//                        if(!param[14].equals("mq")){
//                            g=param[14];
//                        }
//                        else{
//                            g="101";
//                        }
                        //max = (a > b) ? a : b;
                    modifyVille(Integer.parseInt(param[0]),
                                /*d*/(!param[7].equals("mq")) ? Double.parseDouble(param[7])-273.15:101,
                                (!param[9].equals("mq"))?Integer.parseInt(param[9]):101,
                                (!param[14].equals("mq"))?Float.parseFloat(param[14]):101);//101 dans le cas ou les parametres ne sont pas connu
   
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
