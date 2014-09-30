package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author jhuska
 */
@Entity(name = "TEST_SUITE")
public class TestSuite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_SUITE_ID")
    private Long testSuiteID;

    @Column(name = "TEST_SUITE_NAME")
    private String name;

    private int numberOfFunctionalTests;

    private int numberOfVisualComparisons;
    
    @OneToMany(mappedBy = "testSuite", fetch = FetchType.EAGER)
    @JsonBackReference
    private List<TestSuiteRun> runs;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfFunctionalTests() {
        return numberOfFunctionalTests;
    }

    public void setNumberOfFunctionalTests(int numberOfFunctionalTests) {
        this.numberOfFunctionalTests = numberOfFunctionalTests;
    }

    public int getNumberOfVisualComparisons() {
        return numberOfVisualComparisons;
    }

    public void setNumberOfVisualComparisons(int numberOfVisualComparisons) {
        this.numberOfVisualComparisons = numberOfVisualComparisons;
    }

    public long getTestSuiteID() {
        return testSuiteID;
    }

    public void setTestSuiteID(long testSuiteID) {
        this.testSuiteID = testSuiteID;
    }

    public List<TestSuiteRun> getRuns() {
        return runs;
    }

    public void setRuns(List<TestSuiteRun> runs) {
        this.runs = runs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + this.numberOfFunctionalTests;
        hash = 97 * hash + this.numberOfVisualComparisons;
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
        final TestSuite other = (TestSuite) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.numberOfFunctionalTests != other.numberOfFunctionalTests) {
            return false;
        }
        if (this.numberOfVisualComparisons != other.numberOfVisualComparisons) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TestSuite{" + "testSuiteID=" + testSuiteID + ", name=" + name + ", numberOfFunctionalTests=" + numberOfFunctionalTests + ", numberOfVisualComparisons=" + numberOfVisualComparisons + '}';
    }
}
