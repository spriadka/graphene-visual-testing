package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author jhuska
 */
@Entity(name = "TEST_SUITE_RUN")
public class TestSuiteRun implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_SUITE_RUN_ID")
    private long testSuiteRunID;

    @Column(name = "TEST_SUITE_RUN_TIMESTAMP")
    private Date timestamp;

    private String projectRevision;

    private int numberOfFailedFunctionalTests;

    private int numberOfFailedComparisons;
    
    private int numberOfSuccessfulComparisons;
    
    @JsonIgnore
    @Transient
    private boolean needsToBeUpdated = false;
    

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEST_SUITE_ID")
    @JsonBackReference(value = "test-suite-runs")
    private TestSuite testSuite;

    @OneToMany(mappedBy = "testSuiteRun", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "test-suite-run-sample")
    private List<Sample> samples;

    @OneToMany(mappedBy = "testSuiteRun", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "test-suite-run-diff")
    private List<Diff> diffs;

    public long getTestSuiteRunID() {
        return testSuiteRunID;
    }

    public void setTestSuiteRunID(long testSuiteRunID) {
        this.testSuiteRunID = testSuiteRunID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getProjectRevision() {
        return projectRevision;
    }

    public void setProjectRevision(String projectRevision) {
        this.projectRevision = projectRevision;
    }

    public int getNumberOfFailedFunctionalTests() {
        return numberOfFailedFunctionalTests;
    }

    public void setNumberOfFailedFunctionalTests(int numberOfFailedFunctionalTests) {
        this.numberOfFailedFunctionalTests = numberOfFailedFunctionalTests;
    }

    public int getNumberOfFailedComparisons() {
        return numberOfFailedComparisons;
    }

    public void setNumberOfFailedComparisons(int numberOfFailedComparisons) {
        this.numberOfFailedComparisons = numberOfFailedComparisons;
    }


    public TestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public List<Diff> getDiffs() {
        return diffs;
    }

    public void setDiffs(List<Diff> diffs) {
        this.diffs = diffs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.timestamp);
        hash = 79 * hash + Objects.hashCode(this.projectRevision);
        hash = 79 * hash + this.numberOfFailedFunctionalTests;
        hash = 79 * hash + this.numberOfFailedComparisons;
        hash = 79 * hash + Objects.hashCode(this.testSuite);
        hash = 79 * hash + Objects.hashCode(this.samples);
        hash = 79 * hash + Objects.hashCode(this.diffs);
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
        final TestSuiteRun other = (TestSuiteRun) obj;
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        if (!Objects.equals(this.projectRevision, other.projectRevision)) {
            return false;
        }
        if (this.numberOfFailedFunctionalTests != other.numberOfFailedFunctionalTests) {
            return false;
        }
        if (this.numberOfFailedComparisons != other.numberOfFailedComparisons) {
            return false;
        }
        if (!Objects.equals(this.testSuite, other.testSuite)) {
            return false;
        }
        if (!Objects.equals(this.samples, other.samples)) {
            return false;
        }
        if (!Objects.equals(this.diffs, other.diffs)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TestSuiteRun: " + "TestSuite name: " + testSuite.getName() + ", TestSuite ID: " + testSuite.getTestSuiteID();
    }

    /**
     * @return the numberOfSuccessfulComparisons
     */
    public int getNumberOfSuccessfulComparisons() {
        return numberOfSuccessfulComparisons;
    }

    /**
     * @param numberOfSuccessfulComparisons the numberOfSuccessfulComparisons to set
     */
    public void setNumberOfSuccessfulComparisons(int numberOfSuccessfulComparisons) {
        this.numberOfSuccessfulComparisons = numberOfSuccessfulComparisons;
    }

    /**
     * @return the needsToBeUpdated
     */
    @JsonProperty("needsToBeUpdated")
    public boolean needsToBeUpdated() {
        return needsToBeUpdated;
    }

    /**
     * @param needsToBeUpdated the needsToBeUpdated to set
     */
    @JsonIgnore
    public void setNeedsToBeUpdated(boolean needsToBeUpdated) {
        this.needsToBeUpdated = needsToBeUpdated;
    }
    
    @JsonProperty("numberOfTests")
    public int getSumOfTests(){
        return numberOfFailedComparisons + numberOfFailedFunctionalTests + numberOfSuccessfulComparisons;
    }
    
    @JsonProperty("successfulPercentage")
    public float succesfulPercentage(){
        return 100 * numberOfSuccessfulComparisons / getSumOfTests();
    }
    
    @JsonProperty("failedPercentage")
    public float failedPercentage(){
        return 100 * numberOfFailedComparisons / getSumOfTests();
    }
    @JsonProperty("failedTestsPercentage")
    public float failedTestPercentage(){
        return 100 * numberOfFailedFunctionalTests / getSumOfTests();
    }
    
}
