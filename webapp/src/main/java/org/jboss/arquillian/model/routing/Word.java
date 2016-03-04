/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.routing;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Class representing the words database
 * Entries are going to look like: 1 | org
 *                                 2 | com
 *                                 3 | jboss
 * @author spriadka
 */
@Entity(name = "WORD")
public class Word implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORD_ID",unique = true)
    private Long wordId;
    
    @Column(name = "VALUE",unique = true)
    private String value;

    /**
     * @return the id
     */
    public Long getId() {
        return wordId;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.wordId = id;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString(){
        return value;
    }
    
    
}
