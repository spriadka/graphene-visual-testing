/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.routing;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author spriadka
 */

@Entity(name = "NODE")
public class Node implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NODE_ID")
    private Long nodeId;
    
    @Column(name = "WORD_ID")
    private Word wordId;
    
    @OneToMany
    private Set<Node> children;
    

    /**
     * @return the nodeId
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the wordId
     */
    public Word getWordId() {
        return wordId;
    }

    /**
     * @param wordId the wordId to set
     */
    public void setWordId(Word wordId) {
        this.wordId = wordId;
    }

    /**
     * @return the children
     */
    public Set<Node> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Set<Node> children) {
        this.children = children;
    }
}
