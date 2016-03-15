/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.managers;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.ws.rs.Produces;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.arquillian.model.routing.Word;
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
            em.persist(node);
           return node;
       }
    }
    
    public Node addChildrenToNode(Node toUpdate,String wordValue){
        Word fromValue = wordManager.getWordFromValue(wordValue);
        Node result = em.find(Node.class, toUpdate.getNodeId());
        
        return result;
        
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
    

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }
}
