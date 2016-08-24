/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.routing;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
import org.jboss.arquillian.model.util.Views;
import org.jboss.arquillian.serializer.NodeSerializer;

/**
 *
 * @author spriadka
 */

@Entity(name = "NODE")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property = "nodeId",scope = Node.class)
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonSerialize(using = NodeSerializer.class)
public class Node implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NODE_ID")
    private Long nodeId;
    
    @OneToOne(fetch = FetchType.EAGER)
    private Word word;
    
    private Short index = Short.MIN_VALUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "NODE_ID")
    private Node parent;
    
    
    @OneToMany(mappedBy = "parent",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
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
        return children;
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
    
    
    /*
    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (obj == this){
            return true;
        }
        if (this.getClass() != obj.getClass()){
            return false;
        }
        final Node fromObject = (Node)obj;
        if (!Objects.equals(word, fromObject.word)){
            return false;
        }
        if (!Objects.equals(parent.word, fromObject.parent.word)){
            return false;
        }
        Collection<Word> thisWords = Collections2.transform(children,new Function<Node, Word>(){
            @Override
            public Word apply(Node input) {
                return input.getWord();
            }
            
        });
        Collection<Word> toCompareWords = Collections2.transform(fromObject.children,new Function<Node, Word>(){

            @Override
            public Word apply(Node input) {
                return input.getWord();
            }
            
        });
        if (! CollectionUtils.isEqualCollection(thisWords, toCompareWords)){
            return false;
        }
        return true;
    }
     
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash * word.hashCode();
        return hash;
    }
    */
    
    public  boolean isRoot(){
        return this.getParent() == null;
    }

    /**
     * @return the index
     */
    public Short getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(Short index) {
        this.index = index;
    }
    
    public boolean hasParent(){
        return parent != null;
    }
    
    
    public Node getParentAt(short index){
        Node result = this;
        if (index < 0){
            throw new IllegalArgumentException("index must be >= 0");
        }
        else if (this.index < index){
            throw new IllegalArgumentException("cannot get parent at index:" + index);
        }
        else {
            for (short i = this.index; i > index; i--){
                result = result.getParent();
            }
        }
        return result;
    }
    
}
