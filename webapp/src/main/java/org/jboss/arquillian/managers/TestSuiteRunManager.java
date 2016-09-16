package org.jboss.arquillian.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import org.jboss.arquillian.model.testSuite.TestSuiteRun;
import javax.inject.Inject;
import javax.persistence.Query;
import org.jboss.arquillian.model.testSuite.TestSuite;

/**
 *
 * @author jhuska
 */
@Stateless
public class TestSuiteRunManager {
    
    @Inject
    private EntityManager em;
    
    private Logger LOGGER = Logger.getLogger(TestSuiteRunManager.class.getName());

    public TestSuiteRun findById(long id) {
        return em.find(TestSuiteRun.class, id);
    }
    
    public List<TestSuiteRun> getAllTestSuiteRuns(Long testSuiteID) {
        Query q = em.createQuery
            ("SELECT tsr FROM TEST_SUITE ts JOIN ts.runs tsr WHERE ts.testSuiteID = :TEST_SUITE_ID");
        q.setParameter("TEST_SUITE_ID", testSuiteID);
        return q.getResultList();
    }
    
    public void deleteTestSuiteRun(TestSuiteRun testSuiteRun) {
        testSuiteRun.setSamples(null);
        testSuiteRun.setDiffs(null);
        em.remove(em.merge(testSuiteRun));
    }
    
    public TestSuiteRun createTestSuiteRun(TestSuiteRun testSuiteRun) {
        em.persist(testSuiteRun);
        return testSuiteRun;
    }

    public TestSuiteRun deleteById(long id){
        EntityGraph<TestSuiteRun> runGraph = em.createEntityGraph(TestSuiteRun.class);
        runGraph.addAttributeNodes("diffs");
        runGraph.addAttributeNodes("samples");
        Map<String,Object> hints = new HashMap<>();
        hints.put("javax.persistence.loadgraph",runGraph);
        return em.find(TestSuiteRun.class,id,hints);

    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
