package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.jboss.arquillian.model.routing.Node;

/**
 *
 * @author jhuska
 */
@Entity(name = "TEST_SUITE")
@JsonIgnoreProperties(ignoreUnknown = true)
@FilterDef(name = "lastRunFilter")
public class TestSuite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEST_SUITE_ID")
    private Long testSuiteID;

    @Column(name = "TEST_SUITE_NAME", unique = true)
    private String name;

    private int numberOfFunctionalTests;

    private int numberOfVisualComparisons;

    @OneToMany(mappedBy = "testSuite", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "test-suite-runs")
    @Filter(name = "lastRunFilter", condition = 
            "TEST_SUITE_RUN_TIMESTAMP=(SELECT MAX(r.TEST_SUITE_RUN_TIMESTAMP) FROM TEST_SUITE_RUN r WHERE r.TEST_SUITE_ID=TEST_SUITE_ID)")
    private Set<TestSuiteRun> runs = Collections.EMPTY_SET;

    @OneToMany(mappedBy = "testSuite", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "test-suite-patterns")
    private Set<Pattern> patterns = Collections.EMPTY_SET;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "NODE_ID")
    private Node rootNode;

    public Set<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<Pattern> patterns) {
        this.patterns = patterns;
    }

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

    public Long getTestSuiteID() {
        return testSuiteID;
    }

    public void setTestSuiteID(long testSuiteID) {
        this.testSuiteID = testSuiteID;
    }

    public Set<TestSuiteRun> getRuns() {
        return runs;
    }

    public void setRuns(Set<TestSuiteRun> runs) {
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

    /**
     * @return the rootNode
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * @param rootNode the rootNode to set
     */
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

}
