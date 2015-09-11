package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author jhuska
 */
@Entity(name = "SAMPLE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SAMPLE_ID")
    private Long sampleID;
    
    @Column(name = "SAMPLE_NAME", length = Diff.STRING_COLUMN_LENGTH)
    private String name;
    
    @Column(name = "URL_SCREENSHOT", unique=true, length = Diff.STRING_COLUMN_LENGTH)
    private String urlOfScreenshot;
    
    @Column(name = "LAST_MODIFICATION_DATE",length = Diff.STRING_COLUMN_LENGTH)
    private String lastModificationDate;
    
    @ManyToOne
    @JoinColumn(name = "TEST_SUITE_RUN_ID")
    @JsonBackReference(value = "test-suite-run-sample")
    private TestSuiteRun testSuiteRun;

    public Long getSampleID() {
        return sampleID;
    }

    public void setSampleID(Long sampleID) {
        this.sampleID = sampleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlOfScreenshot() {
        return urlOfScreenshot;
    }

    public void setUrlOfScreenshot(String urlOfScreenshot) {
        this.urlOfScreenshot = urlOfScreenshot;
    }

    public TestSuiteRun getTestSuiteRun() {
        return testSuiteRun;
    }

    public void setTestSuiteRun(TestSuiteRun testSuiteRun) {
        this.testSuiteRun = testSuiteRun;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.getName());
        hash = 97 * hash + Objects.hashCode(this.getUrlOfScreenshot());
        hash = 97 * hash + Objects.hashCode(this.getTestSuiteRun());
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
        final Sample other = (Sample) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.urlOfScreenshot, other.urlOfScreenshot)) {
            return false;
        }
        if (!Objects.equals(this.testSuiteRun, other.testSuiteRun)) {
            return false;
        }
        return true;
    }

    /**
     * @return the lastModificationDate
     */
    public String getLastModificationDate() {
        return lastModificationDate;
    }

    /**
     * @param lastModificationDate the lastModificationDate to set
     */
    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
