/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.test.persistence;


import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.managers.NodeManager;
import org.jboss.arquillian.managers.WordManager;
import org.jboss.arquillian.model.routing.Word;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.logging.Logger;
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
public class NodeManagerTest {
    
    private Logger LOGGER = Logger.getLogger(NodeManagerTest.class);
    
    @Deployment
    public static WebArchive createArchive(){
        return ShrinkWrap.create(WebArchive.class, "node-test.war")
                .addAsResource("test-persistence.xml","META-INF/persistence.xml")
                .addClass(WordManager.class)
                .addClass(Word.class)
                .addClass(NodeManager.class)
                .addClass(Node.class);
    }
    
    
    @Inject
    private NodeManager nodeManager;
    
    @Inject
    private WordManager wordManager;
    
    @Test
    public void testAddWord(){
        Word toInsert = new Word();
        String insertedValue = "MyWord";
        toInsert.setValue(insertedValue);
        Word result = wordManager.addWord(toInsert);
        LOGGER.info(result.getId());
        Assert.assertEquals(result.getValue(), insertedValue);
        Assert.assertNotNull(result.getId());
    }
    
    @Test
    public void testAddNode(){
        String wordToInsert = "newWord";
        Word toInsert = new Word();
        toInsert.setValue(wordToInsert);
        Word expected = wordManager.addWord(toInsert);
        Assert.assertNotNull(expected);
        Assert.assertEquals(expected.getValue(), wordToInsert);
        Node nodeToBeInserted = new Node();
        nodeToBeInserted.setWordId(expected);
        Node expectedNode = nodeManager.addNode(nodeToBeInserted);
        expectedNode.setParent(expectedNode);
        expectedNode = nodeManager.updateNode(expectedNode);
        LOGGER.info("EXPECTED NODE PARENT");
        LOGGER.info(expectedNode.getParent().getNodeId());
        Assert.assertEquals(expectedNode.getWordId(), expected);       
    }
    
    @Test
    public void testAddWordsFromString(){
        String hierarchy = "org.jboss.arquillian.test.persistence";
        String[] tokens = hierarchy.split("\\.");
        Set<Node> addedNodes = new HashSet<>();
        for (String token : tokens){
            wordManager.addWordFromValue(token);
        }
        for (String token : tokens){
            Node toAdd = new Node();
            toAdd.setWordId(wordManager.getWordFromValue(token));
            addedNodes.add(nodeManager.addNode(toAdd));
        }
        Assert.assertEquals(tokens.length, addedNodes.size());
        
    }
    
}
