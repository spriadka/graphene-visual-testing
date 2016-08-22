/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.test.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.managers.NodeManager;
import org.jboss.arquillian.managers.WordManager;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.arquillian.model.routing.Word;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author spriadka
 */
@RunWith(Arquillian.class)
public class NodeLazyLoadTest {
    
    @Deployment(testable = false)
    public static WebArchive createArchive(){
        return ShrinkWrap.create(WebArchive.class, "node-test.war")
                .addAsResource("test-persistence.xml","META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addClass(WordManager.class)
                .addClass(Word.class)
                .addClass(NodeManager.class)
                .addClass(Node.class);
       
    }
    
    
    @Inject
    private NodeManager manager;  
      
    @Test
    @RunAsClient
    public void test() throws JsonProcessingException{
        System.out.println(manager);
        System.out.println(manager.getAllNodes().size());
        Assert.assertNotEquals(0, manager.getAllNodes().size());
        /*Node myNode = manager.getNode(653,true);
        String json = mapper.writeValueAsString(myNode);
        System.out.println(json);*/
    }
    
    
}
