/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.routing;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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
    
    @OneToOne
    private Word wordId;
    
    @JoinColumn(name = "PARENT_NODE_ID",referencedColumnName = "NODE_ID")
    private Long parentId;

    @ManyToOne
    @JoinColumn(name = "PARENT_NODE_ID")
    private Node parent;
    
    @OneToMany(mappedBy = "parent")
    private Set<Node> children = Collections.EMPTY_SET;
    

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

    /**
     * @return the parentId
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
}
