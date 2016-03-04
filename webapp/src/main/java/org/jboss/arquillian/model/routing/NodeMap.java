/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.routing;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * Class representing relation between nodes
 * @author spriadka
 */
@Entity(name = "NODEMAP")
public class NodeMap implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NODEMAP_ID")
    private Long nodeMapId;
    
    @Column(name = "CHILDREN")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "NODE_ID")
    private Set<Node> children;

    /**
     * @return the id
     */
    public Long getId() {
        return nodeMapId;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.nodeMapId = id;
    }

    /**
     * @return the children
     */
    public Set<Node> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Set<Node> children) {
        this.children = children;
    }
    
    
    
}
