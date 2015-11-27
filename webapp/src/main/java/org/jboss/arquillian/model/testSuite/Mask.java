/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.jboss.logging.Logger;

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

    @Column(name = "SOURCE_URL", unique = true, length = Diff.STRING_COLUMN_LENGTH)
    private String sourceUrl;
    
    @Column(name = "TEST_SUITE_NAME")
    private String testSuiteName;

    @JsonIgnore
    @Transient
    private String sourceData;

    @JoinColumn(name = "SAMPLE_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference(value = "sample-masks")
    private Sample sample;

    @JsonIgnore
    @Transient
    private ObjectMapper objectMapper = new ObjectMapper();

    @JsonIgnore
    @Transient
    private Logger LOGGER = Logger.getLogger(Mask.class);

    @Column(name = "WIDTH")
    private int width;

    @Column(name = "HEIGHT")
    private int height;

    @Column(name = "positionTop")
    private int top;

    @Column(name = "positionLeft")
    private int left;

    @JsonCreator
    public Mask(Map<String, Object> props) {
        this.maskID = objectMapper.convertValue(props.get("maskID"), Long.class);
        this.sample = objectMapper.convertValue(props.get("sample"), Sample.class);
        this.testSuiteName = (String)props.get("testSuiteName");
        this.sourceData = (String) props.get("sourceData");
        this.top = (int) props.get("top");
        this.left = (int) props.get("left");
        this.width = (int) props.get("width");
        this.height = (int) props.get("height");
    }

    public Mask() {

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.getSample());
        hash = 61 * hash + Objects.hashCode(this.getSourceUrl());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mask mask = (Mask) obj;
        if (!Objects.equals(this.sample, mask.sample)) {
            return false;
        }
        if (!Objects.equals(this.sourceUrl, mask.sourceUrl)) {
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

    /**
     * @return the sample
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * @return the sourceData
     */
    @JsonProperty
    public String getSourceData() {
        return sourceData;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the positionTop
     */
    public int getTop() {
        return top;
    }

    /**
     * @return the positionLeft
     */
    public int getLeft() {
        return left;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param top the positionTop to set
     */
    public void setTop(int top) {
        this.top = top;
    }

    /**
     * @param left the positionLeft to set
     */
    public void setLeft(int left) {
        this.left = left;
    }

    /**
     * @return the testSuiteName
     */
    public String getTestSuiteName() {
        return testSuiteName;
    }

    /**
     * @param testSuiteName the testSuiteName to set
     */
    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    /**
     * @param sourceData the sourceData to set
     */
    public void setSourceData(String sourceData) {
        this.sourceData = sourceData;
    }

}
