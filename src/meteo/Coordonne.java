/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import java.util.ArrayList;

/**
 *
 * @author karim
 */
public class Coordonne {
    ArrayList <VilleTemp>tabVille;

    public Coordonne() {
        tabVille=new ArrayList<>();
    }
    public void ConstructTabVille(int nbVille){
        for (int i=0;i<nbVille;i++){
            //recuperer les donnée a partir d'un fichier de configuration contenant 
            //NomVille,id ex 7011 et point
           // VilleTemp=new VilleTemp(city, nbVille);
            //tabVille.add(VilleTemp);
        }
    }
    
    
}
