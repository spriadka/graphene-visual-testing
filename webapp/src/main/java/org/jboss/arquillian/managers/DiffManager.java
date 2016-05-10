package org.jboss.arquillian.managers;

import static java.lang.ProcessBuilder.Redirect.from;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.arquillian.model.testSuite.Diff;
import org.jboss.arquillian.model.testSuite.Sample;

/**
 *
 * @author jhuska
 */
@Stateless
public class DiffManager {
    
    @Inject
    private EntityManager em;
    
    public Diff createDiff(Diff diff) {
        em.persist(diff);
        return diff;
    }
    
    public Diff getDiff(long diffID) {
        return em.find(Diff.class, diffID);
    }
    
    public List<Diff> getDiffsForRun(long testSuiteRunID) {
        Query query = em.createQuery("SELECT d FROM DIFF d WHERE d.testSuiteRun.testSuiteRunID = :testSuiteRunID");
        return query.setParameter("testSuiteRunID", testSuiteRunID).getResultList();
    }
    
    public void deleteDiff(Diff diff) {
        em.remove(em.contains(diff) ? diff : em.merge(diff));
    }
    
    public boolean areThereDiffs(long testSuiteRunID) {
        return !getDiffsForRun(testSuiteRunID).isEmpty();
    }
    
    public List<Diff> getFilteredDiffs(String name, Long testSuiteRunID){
        Query query = em.createQuery("SELECT d FROM DIFF d WHERE d.testSuiteRun.testSuiteRunID = :testSuiteRunID AND d.name LIKE :name");
        query.setParameter("name", name + "%");
        query.setParameter("testSuiteRunID", testSuiteRunID);
        return query.getResultList();
    }
}
