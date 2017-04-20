/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abstraction;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author SEIF
 */
public interface Controller extends Initializable{

    /**
     * permet d'initialiser l'interface d'affichage et de comparaison des
     * données
     */
    void InitInterfaceComparaison();

    /**
     * *****************Private Methode***************************************************************
     */
    /**
     * permet d'initialiser l'interface principale
     */
    void InitInterfacePrincipal();

    /**
     * permet d'initialiser l'interface permettant de savoir si le serveur de
     * main france contenant les données nécessaires est bien en marche est
     * combien de temps a fallut pour faire un ping
     */
    void initInterfaceEtatServeur();

    /**
     * permet d'initialiser l'interface permettant de savoir quelles données
     * l'utilisateur a sur sa machine
     */
    void initInterfaceInformation();

    /**
     * permet d'initialiser l'interface setting
     */
    void initInterfaceSetting();

    @Override
    public void initialize(URL location, ResourceBundle resources);
    
}
