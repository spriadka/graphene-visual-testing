/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.model.util;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.jboss.arquillian.managers.DiffManager;
import org.jboss.arquillian.managers.PatternManager;
import org.jboss.arquillian.managers.SampleManager;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Pattern;
import org.jboss.arquillian.model.testSuite.Sample;
import org.jboss.arquillian.rest.ComparisonResult;

/**
 *
 * @author spriadka
 */
@RequestScoped
public class TestSuiteRunUtils {
    
    @Inject
    private DiffManager diffManager;
    
    @Inject
    private PatternManager patternManager;
    @Inject
    private SampleManager sampleManager;
    
     public List<ComparisonResult> getComparisonResultsForRun(long testSuiteRunID, boolean diffsOnly) {
        List<ComparisonResult> result = new ArrayList<>();
        List<Diff> diffs = diffManager.getDiffsForRun(testSuiteRunID);
        List<Long> alreadyUploadedSamples = new ArrayList<>();
        if (diffManager.areThereDiffs(testSuiteRunID)) {
            for (Diff diff : diffs) {
                result.add(new ComparisonResult(diff));
                alreadyUploadedSamples.add(diff.getSample().getSampleID());
            }
        }
        if (!diffsOnly) {
            List<Sample> samples = sampleManager.getSamples(testSuiteRunID);
            for (Sample sample : samples) {
                Pattern pattern = patternManager.getPattern(sample.getName(), sample.getTestSuiteRun().getTestSuite().getTestSuiteID());
                if (!alreadyUploadedSamples.contains(sample.getSampleID())) {
                    result.add(new ComparisonResult(sample, pattern));
                }
            }
        }
        return result;
    }
    
    public List<ComparisonResult> getFilteredComparisonResultsForRun(String testClass,long testSuiteRunID, boolean diffsOnly) {
        List<ComparisonResult> result = new ArrayList<>();
        List<Diff> diffs = diffManager.getFilteredDiffs(testClass,testSuiteRunID);
        List<Long> alreadyUploadedSamples = new ArrayList<>();
        if (diffManager.areThereDiffs(testSuiteRunID)) {
            for (Diff diff : diffs) {
                result.add(new ComparisonResult(diff));
                alreadyUploadedSamples.add(diff.getSample().getSampleID());
            }
        }
        if (!diffsOnly) {
            List<Sample> samples = sampleManager.getFilteredSamples(testClass,testSuiteRunID);
            for (Sample sample : samples) {
                Pattern pattern = patternManager.getPattern(sample.getName(), sample.getTestSuiteRun().getTestSuite().getTestSuiteID());
                if (!alreadyUploadedSamples.contains(sample.getSampleID())) {
                    result.add(new ComparisonResult(sample, pattern));
                }
            }
        }
        return result;
    }
}
