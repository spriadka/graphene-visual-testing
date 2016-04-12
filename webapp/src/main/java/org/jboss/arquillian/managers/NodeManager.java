/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.managers;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
@Stateless
public class NodeManager {
    
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private WordManager wordManager;
    
    private Logger LOGGER = Logger.getLogger(NodeManager.class);
    
    public Node addNode(Node node){
       Query query = em.createQuery("SELECT n FROM NODE n WHERE n.nodeId=:nodeId");
       query.setParameter("nodeId", node.getNodeId());
       Node result = null;
       try{
           result = (Node) query.getSingleResult();
           return result;
       }
       catch(NoResultException nre){
           //node.setParent(node);
           em.persist(node);
           return node;
       }
    }
    
    
    public Node updateNode(Node node){
        Node found = em.find(Node.class, node.getNodeId());
        found.setWord(node.getWord());
        found.setParent(node.getParent());
        found.setChildren(node.getChildren());
        return found;
    }
    
    public Node getNode(long nodeId){
        return em.find(Node.class, nodeId);
    }
    
    public void deleteNode(Node node){
        em.remove(em.contains(node) ? node : em.merge(node));
    }
    
    public Map<String,Long> createNodesMap(Node node){
        Node found = em.find(Node.class, node.getNodeId());
        Map<String,Long> result = new HashMap<>();
        createMapRecursively(result, found);
        return result;
    }
    
    public void createMapRecursively(Map<String,Long> entries,Node node){
        LOGGER.info(node.getNodeId());
        entries.put(node.getWord().getValue(), node.getNodeId());
        if (node.hasChildren()){
            for (Node entry : node.getChildren()){
                createMapRecursively(entries, entry);
            }
        }
    }
    

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }
}
