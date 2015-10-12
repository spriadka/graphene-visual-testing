package org.arquillian.graphene.visual.testing;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.http.client.methods.HttpGet;
import org.arquillian.graphene.visual.testing.impl.RestUtils;

/**
 *
 * @author jhuska
 */
public class Car {
    
    private int numberOfPassangers;
    
    
    public static final int CONSTANT = 5;
    
    public Car(int amount) {
        this.numberOfPassangers = amount;
    }
    
    public int passangerGetOut() {
        this.numberOfPassangers = this.numberOfPassangers - 1;
        return numberOfPassangers;
    }
    
    public static void main(String[] args) throws IOException {
        File f = new File("URLimage.png");
        Car porsche = new Car(4);
        int currentNumber  = porsche.passangerGetOut();
        try {
            URL url = new URL("http://redhat:redhat2@localhost:8080/modeshape-rest/graphene-visual-testing/default/binary/richfaces-beta-test70/masks/org.richfaces.showcase.ajax.ITestAjax/testEraseStringFromInputAndCheckTheOutputNew/after/63/jcr:content/jcr:data");
            ImageIO.read(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Car.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(currentNumber);
    }
}
