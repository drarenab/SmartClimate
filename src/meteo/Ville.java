/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

/**
 *
 * @author karim
 */
public class Ville {
    String nom;
    Point point;
    

    public Ville(String nom, Point point) {
        this.nom = nom;
        this.point = point;
    }
    
    
    public void ModifierVille(String nom, Point point){
        this.nom=nom;
        this.point=point;
    }
    
}
