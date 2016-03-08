/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.managers;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
@Stateless
public class NodeManager {
    @Inject
    private EntityManager em;
    
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
    
    public Node updateNode(Node node){
        Node found = em.find(Node.class, node.getNodeId());
        found.setChildren(node.getChildren());
        return found;
    }
    
    public void getNode(Node node){
        em.remove(em.contains(node) ? node : em.merge(node));
    }
}
