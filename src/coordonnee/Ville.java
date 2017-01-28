/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coordonnee;

/**
 *
 * @author karim
 */

public class Ville {
    /**
     * 
     * @param nom
     * @param id
     * @param point
     * Cette classe represente une ville ayant un nom un id et une coordonnée 
     * de type point sur la carte
     */
    public String nom;
    public int id;
    public Point point;
    public Ville(String nom,int id,Point point) {
        this.nom = nom;
        this.id=id;
        this.point = point;
    }
    
    
    public void ModifierVille(String nom, Point point){
        this.nom=nom;
        this.point=point;
    }
    
}
