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
import java.util.Scanner;

/**
 *
 * @author karim
 */
public class Coordonne {
    public ArrayList <VilleTemp>tabVille;

    public Coordonne() {
        tabVille=new ArrayList<>();
    }
    public void addVille(String nom,int id,int x,int y,float temperature){
        VilleTemp v=new VilleTemp(new Ville(nom, id, new Point(x, y)), temperature);
        tabVille.add(v);
    }
    public void ConstructTabVille() {
      
       
       try
    {
    String fichConfig="Configuration.txt";
     File file = new File (fichConfig);    
     FileReader fr = new FileReader (file);
    BufferedReader br = new BufferedReader (fr);
 
    try
        {
            String line = br.readLine();
            
            while (line != null)
            {
                //faire un split
                String[] param = line.split(";");
       //    public void addVille(String nom,int id,int x,int y,float temperature){
                
                addVille(param[1],Integer.parseInt(param[0]),Integer.parseInt(param[2]),Integer.parseInt(param[3]),0);
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
       
      
        //File file = ;
        /*if (file != null) {

            Scanner s = new Scanner(file);
            while (s.hasNext()) {

                String S = new String();
                S = s.next();
                
                

            } //end while

        }*/

    }
    
    
}
