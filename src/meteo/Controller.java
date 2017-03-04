/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author SEIF
 */
public interface Controller extends Initializable {

    void InitInterfaceComparaison();

    void InitInterfacePrincipal();

    void initInterfaceSetting();

    void initialize(URL url, ResourceBundle rb);
    
}
