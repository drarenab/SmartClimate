package abstraction;

import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by AZ PC on 24/04/2017.
 */
public interface Controller extends Initializable{
    void initialize(URL url, ResourceBundle rb);

    void InitInterfacePrincipal() throws IOException;

    void initInterfaceSetting();

    void InitInterfaceComparaison();

    void initInterfaceInformation();

    void initInterfaceEtatServeur();
}
