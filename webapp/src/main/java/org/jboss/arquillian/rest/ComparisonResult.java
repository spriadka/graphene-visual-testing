package org.jboss.arquillian.rest;

import java.io.Serializable;
import java.util.List;
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.rest.builder.Builder;
import org.jboss.arquillian.rest.builder.DiffComparisonResultBuilder;
import org.jboss.arquillian.rest.builder.SampleComparisonResultBuilder;

/**
 *
 * @author jhuska
 */
public class ComparisonResult implements Serializable {

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

    public ComparisonResult() {
        
    }
    
    public ComparisonResult(SampleComparisonResultBuilder builder){
        this.patternID = builder.getPatternID();
        this.patternUrl = builder.getPatternUrl();
        this.patternModificationDate = builder.getPatternModificationDate();
        this.masks = builder.getMasks();
        this.sampleID = builder.getSampleID();
        this.sampleUrl = builder.getSampleUrl();
        this.sampleModificationDate = builder.getSampleModificationDate();
        this.testName = builder.getTestName();
        this.testClassName = builder.getTestClassName();
        this.diffID = null;
        this.diffUrl = null;
    }
    public ComparisonResult(DiffComparisonResultBuilder builder){
        this.patternID = builder.getPatternID();
        this.patternUrl = builder.getPatternUrl();
        this.patternModificationDate = builder.getPatternModificationDate();
        this.masks = builder.getMasks();
        this.sampleID = builder.getSampleID();
        this.sampleUrl = builder.getSampleUrl();
        this.sampleModificationDate = builder.getSampleModificationDate();
        this.testName = builder.getTestName();
        this.testClassName = builder.getTestClassName();
        this.diffID = builder.getDiffID();
        this.diffUrl = builder.getDiffUrl();
    }
    
    

    public ComparisonResult(String patternUrl, Long patternID, String patternModificationDate, String sampleUrl, Long sampleID, String sampleModificationDate,
            String diffUrl, Long diffID, String testClassName, String testName) {
        this.patternUrl = patternUrl;
        this.patternID = patternID;
        this.patternModificationDate = patternModificationDate;
        this.sampleUrl = sampleUrl;
        this.sampleID = sampleID;
        this.sampleModificationDate = sampleModificationDate;
        this.diffUrl = diffUrl;
        this.diffID = diffID;
        this.testClassName = testClassName;
        this.testName = testName;
        //this.masks = maskManager.getMasksForSample(sampleID);
    }

    public String getPatternUrl() {
        return patternUrl;
    }

    public void setPatternUrl(String patternUrl) {
        this.patternUrl = patternUrl;
    }

    public Long getPatternID() {
        return patternID;
    }

    public void setPatternID(Long patternID) {
        this.patternID = patternID;
    }

    public String getSampleUrl() {
        return sampleUrl;
    }

    public void setSampleUrl(String sampleUrl) {
        this.sampleUrl = sampleUrl;
    }

    public Long getSampleID() {
        return sampleID;
    }

    public void setSampleID(Long sampleID) {
        this.sampleID = sampleID;
    }

    public String getDiffUrl() {
        return diffUrl;
    }

    public void setDiffUrl(String diffUrl) {
        this.diffUrl = diffUrl;
    }

    public Long getDiffID() {
        return diffID;
    }

    public void setDiffID(Long diffID) {
        this.diffID = diffID;
    }

    public String getTestClassName() {
        return testClassName;
    }

    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * @return the patternModificationDate
     */
    public String getPatternModificationDate() {
        return patternModificationDate;
    }

    /**
     * @param patternModificationDate the patternModificationDate to set
     */
    public void setPatternModificationDate(String patternModificationDate) {
        this.patternModificationDate = patternModificationDate;
    }

    /**
     * @return the sampleModificationDate
     */
    public String getSampleModificationDate() {
        return sampleModificationDate;
    }

    /**
     * @param sampleModificationDate the sampleModificationDate to set
     */
    public void setSampleModificationDate(String sampleModificationDate) {
        this.sampleModificationDate = sampleModificationDate;
    }

    /**
     * @return the masks
     */
    public List<Mask> getMasks() {
        return masks;
    }

    /**
     * @param masks the masks to set
     */
    public void setMasks(List<Mask> masks) {
        this.masks = masks;
    }


    
}
