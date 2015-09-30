/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest.builder;

import java.util.List;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.rest.ComparisonResult;

/**
 *
 * @author spriadka
 */
public class SampleComparisonResultBuilder extends Builder<ComparisonResult>{
    
    private String patternUrl;

    private Long patternID;

    private String patternModificationDate;

    private String sampleUrl;

    private Long sampleID;

    private String sampleModificationDate;

    private String diffUrl;

    private Long diffID;

    private String testClassName;

    private String testName;

    private List<Mask> masks;
    
    private Sample sample;
    
    private Pattern pattern;
    
    public SampleComparisonResultBuilder sample(Sample sample){
        this.sample = sample;
        return this;
    }
    
    public SampleComparisonResultBuilder pattern(Pattern pattern){
        this.pattern = pattern;
        return this;
    }
    
    
    public ComparisonResult build(){
        this.patternUrl = getPattern().getUrlOfScreenshot();
        this.patternID = getPattern().getPatternID();
        this.patternModificationDate = getPattern().getLastModificationDate();
        this.sampleID = getSample().getSampleID();
        this.sampleUrl = getSample().getUrlOfScreenshot();
        this.sampleModificationDate = getSample().getLastModificationDate();
        this.masks = null;
        this.testClassName = getTestClassName(this.getSample());
        this.testName = getTestName(this.getSample());
        return new ComparisonResult(this);
    }
    
    private String getTestClassName(Sample sample) {
        return sample.getName().split("/")[0];
    }
    
    private String getTestName(Sample sample) {
        return sample.getName().split("/")[1];
    }

    /**
     * @return the patternUrl
     */
    public String getPatternUrl() {
        return patternUrl;
    }

    /**
     * @return the patternID
     */
    public Long getPatternID() {
        return patternID;
    }

    /**
     * @return the patternModificationDate
     */
    public String getPatternModificationDate() {
        return patternModificationDate;
    }

    /**
     * @return the sampleUrl
     */
    public String getSampleUrl() {
        return sampleUrl;
    }

    /**
     * @return the sampleID
     */
    public Long getSampleID() {
        return sampleID;
    }

    /**
     * @return the sampleModificationDate
     */
    public String getSampleModificationDate() {
        return sampleModificationDate;
    }

    /**
     * @return the diffUrl
     */
    public String getDiffUrl() {
        return diffUrl;
    }

    /**
     * @return the diffID
     */
    public Long getDiffID() {
        return diffID;
    }

    /**
     * @return the testClassName
     */
    public String getTestClassName() {
        return testClassName;
    }

    /**
     * @return the testName
     */
    public String getTestName() {
        return testName;
    }

    /**
     * @return the masks
     */
    public List<Mask> getMasks() {
        return masks;
    }

    /**
     * @return the sample
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }
}
