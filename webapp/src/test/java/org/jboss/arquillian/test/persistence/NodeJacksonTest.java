/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.test.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.managers.NodeManager;
import org.jboss.arquillian.managers.WordManager;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.arquillian.model.routing.Word;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author spriadka
 */
@RunWith(Arquillian.class)
public class NodeJacksonTest {
    @Deployment
    public static WebArchive createArchive(){
        return ShrinkWrap.create(WebArchive.class, "node-test.war")
                .addAsResource("test-persistence.xml","META-INF/persistence.xml")
                .addClass(WordManager.class)
                .addClass(Word.class)
                .addClass(NodeManager.class)
                .addClass(Node.class);
    }
    
    @Test
    public void testSerializeFromJSON(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = "{\"nodeId\":25,\"word\":{\"value\":\"simple-put-2\",\"id\":14},\"parent\":25,\"children\":[25,{\"nodeId\":26,\"word\":{\"value\":\"org\",\"id\":2},\"parent\":25,\"children\":[{\"nodeId\":27,\"word\":{\"value\":\"jboss\",\"id\":3},\"parent\":26,\"children\":[{\"nodeId\":28,\"word\":{\"value\":\"arquillian\",\"id\":4},\"parent\":27,\"children\":[{\"nodeId\":29,\"word\":{\"value\":\"extension\",\"id\":5},\"parent\":28,\"children\":[{\"nodeId\":30,\"word\":{\"value\":\"testsuite\",\"id\":6},\"parent\":29,\"children\":[{\"nodeId\":31,\"word\":{\"value\":\"tests\",\"id\":7},\"parent\":30,\"children\":[{\"nodeId\":32,\"word\":{\"value\":\"AnotherTest\",\"id\":8},\"parent\":31,\"children\":[{\"nodeId\":33,\"word\":{\"value\":\"testGoogleAndTypeSomething\",\"id\":9},\"parent\":32,\"children\":[]},{\"nodeId\":34,\"word\":{\"value\":\"testSeznamAndTypeSomething\",\"id\":12},\"parent\":32,\"children\":[]}]}]}]}]}]}]}]}]}";
            Node fromJSON = mapper.readValue(json, Node.class);
            Assert.assertNotNull(fromJSON);
            StringBuilder sb = new StringBuilder("[");
            for (Node child : fromJSON.getChildren()){
                sb.append(child.getWord().getValue());
                sb.append(",");
            }
            sb.append("]");
            Logger.getLogger(NodeJacksonTest.class.getName()).log(Level.INFO,sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(NodeJacksonTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
