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
        String url = "http://localhost:8080/modeshape-rest/graphene-visual-testing/default/binary/richfaces-beta-test70/masks/org.richfaces.showcase.functions.ITestFunctions/testFunctionFindComponentCall/after/5/jcr:content/jcr:data";
        url = url.substring(url.lastIndexOf("masks/") + 6,url.lastIndexOf("/jcr:content/jcr:data"));
        String url2 = "mask" + url.substring(url.lastIndexOf("/") + 1);
        url = url.substring(0,url.lastIndexOf("/")+1) + url2;
        System.out.println(url2);
        System.out.println(url);
    }
}
