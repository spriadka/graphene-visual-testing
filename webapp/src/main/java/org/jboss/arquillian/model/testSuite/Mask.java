/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.jboss.arquillian.managers.TestSuiteManager;

/**
 *
 * @author spriadka
 */
@Entity(name = "MASK")
public class Mask {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MASK_ID")
    private Long maskID;
    
    @JoinColumn(name = "TEST_SUITE_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private TestSuite testSuite;
    
    private String sourceUrl;
    
    @Inject
    private TestSuiteManager testSuiteManager;
    
    @JsonCreator
    public Mask(Map<String,Object> json){
        this.testSuite = testSuiteManager.findById((long)json.get("testSuiteID"));
    }

    /**
     * @return the maskID
     */
    public Long getMaskID() {
        return maskID;
    }

    /**
     * @return the testSuite
     */
    public TestSuite getTestSuite() {
        return testSuite;
    }

    /**
     * @param testSuite the testSuite to set
     */
    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

    /**
     * @return the sourceUrl
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * @param sourceUrl the sourceUrl to set
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.testSuite);
        hash = 61 * hash + Objects.hashCode(this.sourceUrl);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final Mask mask = (Mask) obj;
        if (!Objects.equals(this.testSuite, mask.getTestSuite())){
            return false;
        }
        if (!Objects.equals(this.sourceUrl, mask.sourceUrl)){
            return false;
        }
        return true;
        
    }
    
}
