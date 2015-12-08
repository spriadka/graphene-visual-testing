package org.jboss.arquillian.rest;

import java.io.Serializable;
import java.util.List;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;

/**
 *
 * @author jhuska
 */
public class ComparisonResult implements Serializable {
    
    private String patternUrl = null;
    private Long patternID = null;
    private String patternModificationDate = null;
    private String sampleUrl = null;
    private Long sampleID = null;
    private String sampleModificationDate = null;
    private String diffUrl = null;
    private Long diffID = null;
    private String testClassName = null;
    private String testName = null;
    private List<Mask> masks = null;
    
    
    private ComparisonResult(){
        
    }
    
    private void setComparisonResultFromSampleAndPattern(Sample sample, Pattern pattern){
        this.patternID = pattern.getPatternID();
        this.patternModificationDate = pattern.getLastModificationDate();
        this.patternUrl = pattern.getUrlOfScreenshot();
        this.sampleID = sample.getSampleID();
        this.sampleModificationDate = sample.getLastModificationDate();
        this.sampleUrl = sample.getUrlOfScreenshot();
        this.testClassName = getTestClassName(sample);
        this.testName = getTestName(sample);
        this.masks = sample.getMasks();
    }
    
    private void setComparisonResultFromDiff(Diff diff){
        Sample sample = diff.getSample();
        Pattern pattern = diff.getPattern();
        this.diffID = diff.getDiffID();
        this.diffUrl = diff.getUrlOfScreenshot();
        setComparisonResultFromSampleAndPattern(sample, pattern);
    }
    
    
    public static ComparisonResult successful(Sample sample, Pattern pattern){
        ComparisonResult result = new ComparisonResult();
        result.setComparisonResultFromSampleAndPattern(sample, pattern);
        return result;     
    }
    
    public static ComparisonResult withDiff(Diff diff){
        ComparisonResult result = new ComparisonResult();
        result.setComparisonResultFromDiff(diff);
        return result;
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
    
    private static String getTestClassName(Sample sample) {
        return sample.getName().split("/")[0];
    }
    
    private static String getTestName(Sample sample) {
        return sample.getName().split("/")[1];
    }

}
