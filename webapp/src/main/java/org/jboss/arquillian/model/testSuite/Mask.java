/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import javax.persistence.Transient;
import org.jboss.arquillian.managers.SampleManager;
import org.jboss.arquillian.managers.TestSuiteManager;
import org.jboss.rusheye.suite.HorizontalAlign;
import org.jboss.rusheye.suite.VerticalAlign;

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
    
    @Column(name = "SOURCE_URL",unique = true, length = Diff.STRING_COLUMN_LENGTH)
    private String sourceUrl;
    
    @JsonIgnore(true)
    @Transient
    private String sourceData;
    
    @Inject
    @JsonIgnore(true)
    @Transient
    private TestSuiteManager testSuiteManager;
    
    @Inject
    @JsonIgnore(true)
    @Transient
    private SampleManager sampleManager;
    
    @Column(name = "HORIZONTAL_ALIGNMENT")
    private HorizontalAlign horizotalAlignment;
    
    @Column(name = "VERTICAL_ALIGNMENT")
    private VerticalAlign verticalAlignment;
    
    @Column(name = "NAME",length = Diff.STRING_COLUMN_LENGTH)
    private String name;
    
    @JsonCreator
    public Mask(@JsonProperty("sampleId") long sampleId, @JsonProperty("testSuiteID") long testSuiteID, @JsonProperty("sourceData") String sourceData, @JsonProperty(value = "horizontalAlignment",required = false) String horizontalAlignment, @JsonProperty(value = "verticalAlignment",required = false) String verticalAlignment){
        this.name = sampleManager.findById(sampleId).getName();
        this.testSuite = testSuiteManager.findById(testSuiteID);
        this.sourceData = sourceData;
        this.horizotalAlignment = HorizontalAlign.fromValue(horizontalAlignment);
        this.verticalAlignment = VerticalAlign.fromValue(verticalAlignment);
    }

    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.getTestSuite());
        hash = 61 * hash + Objects.hashCode(this.getSourceUrl());
        hash = 61 * hash + Objects.hashCode(this.getName());
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
        if (!Objects.equals(this.testSuite, mask.testSuite)){
            return false;
        }
        if (!Objects.equals(this.sourceUrl, mask.sourceUrl)){
            return false;
        }
        if (!Objects.equals(this.name, mask.name)){
            return false;
        }
        return true;
        
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
     * @return the sourceUrl
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the sourceData
     */
    public String getSourceData() {
        return sourceData;
    }

    /**
     * @param testSuite the testSuite to set
     */
    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

    /**
     * @param sourceUrl the sourceUrl to set
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * @return the horizotalAlignment
     */
    public HorizontalAlign getHorizotalAlignment() {
        return horizotalAlignment;
    }

    /**
     * @param horizotalAlignment the horizotalAlignment to set
     */
    public void setHorizotalAlignment(HorizontalAlign horizotalAlignment) {
        this.horizotalAlignment = horizotalAlignment;
    }

    /**
     * @return the verticalAlignment
     */
    public VerticalAlign getVerticalAlignment() {
        return verticalAlignment;
    }

    /**
     * @param verticalAlignment the verticalAlignment to set
     */
    public void setVerticalAlignment(VerticalAlign verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
