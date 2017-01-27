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
public class Point {
    int x;
    int y;

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
    public void modifierPoint(int X,int Y){
        modifierY(Y);
        modifierX(X);
    }
}
