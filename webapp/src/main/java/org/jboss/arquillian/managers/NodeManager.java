/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
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
        /*EntityGraph<Node> entityGraph = em.createEntityGraph(Node.class);
        entityGraph.addAttributeNodes("children");
        Map<String,Object> props = new HashMap<>();
        props.put("javax.persistence.loadgraph", entityGraph);*/
        Node ret = em.find(Node.class, nodeId);
        return ret;
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
        String toPut = "";
        if (node.isRoot() || node.getIndex() == 0){
            toPut = node.getWord().getValue();
        }
        else{
            toPut = node.getWord().getValue() + 
                    "." + node.getIndex() + 
                    "." + node.getParentAt((short)0).getNodeId();
        }
        entries.put(toPut,node.getNodeId());
        if (!node.getChildren().isEmpty()){
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
    
    public List<Node> getAllNodes(){
        return em.createQuery("SELECT n FROM NODE n").getResultList();
    }
}
