package junit;

import main.MyModel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by AZ PC on 18/04/2017.
 */
public class MyModelTest {
    MyModel model;
    /*@Before
    public void init() {
       model = MyModel.getInstance();
        try {
            model.getData("NICE","2011","02","01",1);
            //System.out.println("------------------------------------------------");
            //model.getData("07149","2011","01","01",0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    @Test
    public void getData() {

    }
}
