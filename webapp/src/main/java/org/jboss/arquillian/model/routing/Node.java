/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.routing;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Node implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NODE_ID")
    private Long nodeId;
    
    @OneToOne
    private Word word;

    @ManyToOne
    @JoinColumn(referencedColumnName = "NODE_ID")
    private Node parent;
    
    @OneToMany(mappedBy = "parent",fetch = FetchType.EAGER)
    private Set<Node> children = Collections.<Node>emptySet();
    

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
    public Word getWord() {
        return word;
    }

    /**
     * @param word the wordId to set
     */
    public void setWord(Word word) {
        this.word = word;
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
    
    public boolean isParent(){
        return this.parent != null;
    }
}
