/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest.builder;

import java.util.List;
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.arquillian.rest.ComparisonResult;

/**
 *
 * @author spriadka
 */
public class DiffComparisonResultBuilder extends Builder<ComparisonResult>{
    
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
    
    private Diff diff;
    
    private MaskManager maskManager;
    
    public DiffComparisonResultBuilder diff(Diff diff){
        this.diff = diff;
        return this;
    }
    public DiffComparisonResultBuilder manager(MaskManager maskManager){
        this.maskManager = maskManager;
        return this;
    }
    private String getTestClassName(Diff diff) {
        return diff.getSample().getName().split("/")[0];
    }

    
    private String getTestName(Diff diff) {
        return diff.getSample().getName().split("/")[1];
    }
    public ComparisonResult build(){
        this.diffID = getDiff().getDiffID();
        this.diffUrl = getDiff().getUrlOfScreenshot();
        this.sampleID = getDiff().getSample().getSampleID();
        this.sampleModificationDate = getDiff().getSample().getLastModificationDate();
        this.sampleUrl = getDiff().getSample().getUrlOfScreenshot();
        this.patternID = getDiff().getPattern().getPatternID();
        this.patternModificationDate = getDiff().getPattern().getLastModificationDate();
        this.patternUrl = getDiff().getPattern().getUrlOfScreenshot();
        this.testClassName = getTestClassName(this.getDiff());
        this.testName = getTestName(this.getDiff());
        this.masks = getMaskManager().getMasksForSample(this.getSampleID());
        return new ComparisonResult(this);
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
     * @return the diff
     */
    public Diff getDiff() {
        return diff;
    }

    /**
     * @return the maskManager
     */
    public MaskManager getMaskManager() {
        return maskManager;
    }
}
