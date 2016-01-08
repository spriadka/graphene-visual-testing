package org.jboss.arquillian.model.testSuite;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author jhuska
 */
@Entity(name = "PATTERN")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PATTERN_ID")
    private Long patternID;

    @Column(name = "PATTERN_NAME", length = Diff.STRING_COLUMN_LENGTH)
    private String name;

    @Column(name = "URL_SCREENSHOT", unique = true, length = Diff.STRING_COLUMN_LENGTH)
    private String urlOfScreenshot;
    
    @Column(name = "LAST_MODIFICATION_DATE",length = Diff.STRING_COLUMN_LENGTH)
    private String lastModificationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEST_SUITE_ID")
    @JsonBackReference(value = "test-suite-patterns")
    private TestSuite testSuite;

    @OneToMany(mappedBy = "pattern", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "pattern-diff")
    private List<Diff> diffs;
    
    @JsonIgnore
    @OneToMany(mappedBy = "pattern",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "pattern-masks")
    private Set<Mask> masks;

    public Long getPatternID() {
        return patternID;
    }

    public void setPatternID(Long patternID) {
        this.patternID = patternID;
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

    public TestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuite testSuite) {
        this.testSuite = testSuite;
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
        hash = 23 * hash + Objects.hashCode(this.getName());
        hash = 23 * hash + Objects.hashCode(this.getUrlOfScreenshot());
        hash = 23 * hash + Objects.hashCode(this.getTestSuite());
        hash = 23 * hash + Objects.hashCode(this.getDiffs());
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
        final Pattern other = (Pattern) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.urlOfScreenshot, other.urlOfScreenshot)) {
            return false;
        }
        if (!Objects.equals(this.testSuite, other.testSuite)) {
            return false;
        }
        if (!Objects.equals(this.diffs, other.diffs)) {
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

    /**
     * @return the masks
     */
    public Set<Mask> getMasks() {
        return masks;
    }

    /**
     * @param masks the masks to set
     */
    public void setMasks(Set<Mask> masks) {
        this.masks = masks;
    }
}
