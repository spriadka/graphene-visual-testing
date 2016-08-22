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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
@Entity(name = "MASK")
public class Mask {

    @Id
    @GeneratedValue(generator = "mask-generator")
    @GenericGenerator(name = "mask-generator",strategy = "org.jboss.arquillian.generator.MaskGenerator")
    @Column(name = "MASK_ID")
    private String maskID;

    @Column(name = "SOURCE_URL", unique = true, length = Diff.STRING_COLUMN_LENGTH)
    private String sourceUrl;
    
    @Column(name = "TEST_SUITE_NAME")
    private String testSuiteName;

    @JsonIgnore
    @Transient
    private String sourceData;
    
    @JoinColumn(name = "PATTERN_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference(value = "pattern-masks")
    private Pattern pattern;

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
    public  Mask(Map<String, Object> props) {
        this.maskID = (String)props.get("maskID");
        this.pattern = objectMapper.convertValue(props.get("pattern"), Pattern.class);
        this.testSuiteName = (String)props.get("testSuiteName");
        this.sourceData = (String) props.get("sourceData");
        this.sourceUrl = (String) props.get("sourceUrl");
        this.top = (int) props.get("top");
        this.left = (int) props.get("left");
        this.width = (int) props.get("width");
        this.height = (int) props.get("height");
    }

    public Mask() {

    }


    /*
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mask mask = (Mask) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(this.width, mask.width);
        equalsBuilder.append(this.height, mask.height);
        equalsBuilder.append(this.left, mask.left);
        equalsBuilder.append(this.top, mask.top);
        equalsBuilder.append(this.getPattern(), mask.getPattern());
        return equalsBuilder.isEquals();

    }*/

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.getPattern());
        hash = 11 * hash + this.width;
        hash = 11 * hash + this.height;
        hash = 11 * hash + this.top;
        hash = 11 * hash + this.left;
        return hash;
    }

    /**
     * @return the maskID
     */
    public String getMaskID() {
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
     * @return the sourceData
     */
    @JsonProperty
    public String getSourceData() {
        return sourceData;
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
    
    @Override
    public String toString(){
        return "MASK ID: " + maskID + "PATTERN ID: " + getPattern().getPatternID() + "SOURCE URL: " + sourceUrl;
    }

    /**
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

}
