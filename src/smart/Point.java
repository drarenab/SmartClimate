/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smart;

/**
 * 
 * @author karim
 * 
 */

public class Point {
/**
 * 
 * @param x
 * @param y 
 * Cette Classe Crée un point de coordonnée X et Y 
 * qui represente un point sur la carte de france de l'interface principal
 */
    
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void modifierX(int x){
        this.x=x;
    }
    
    public void modifierY(int y){
        this.y=y;
    }
    /**
     * 
     * Modification d'un point 
     * 
     */
    public void modifierPoint(int X,int Y){
        modifierY(Y);
        modifierX(X);
    }
}
