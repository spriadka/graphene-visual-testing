package org.jboss.arquillian.managers;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.jboss.arquillian.model.testSuite.TestSuite;

/**
 *
 * @author jhuska
 */
@Stateless
public class TestSuiteManager {

    @Inject
    private EntityManager em;

    public TestSuite findById(long id, List<String> params) {
        EntityGraph<TestSuite> graph = null;
        boolean added = false;
        if (!params.isEmpty()) {
            graph = em.createEntityGraph(TestSuite.class);
            for (String field : params) {
                if (field.equals("rootNode") || field.equals("runs") || field.equals("patterns")) {
                    added = true;
                    graph.addAttributeNodes(field);
                }
            }
        }
        if (added) {
            HashMap<String, Object> hints = new HashMap<>();
            hints.put("javax.persistence.loadgraph", graph);
            return em.find(TestSuite.class, id, hints);
        }
        return em.find(TestSuite.class, id);
    }

    public Set<TestSuite> getAllTestSuites() {
        return new LinkedHashSet<>(em.createQuery("SELECT e FROM TEST_SUITE e JOIN FETCH e.runs").getResultList());
    }

    public TestSuite createTestSuite(TestSuite testSuite) {
        em.persist(testSuite);
        return testSuite;
    }

    public TestSuite getTestSuite(String nameOfTestSuite) {
        Query query = em.createQuery("SELECT e FROM TEST_SUITE e WHERE e.name = :name");
        query.setParameter("name", nameOfTestSuite);
        TestSuite result = null;
        try {
            result = (TestSuite) query.getSingleResult();
        } catch (NoResultException ex) {
            //OK
        }
        return result;
    }

    public void deleteTestSuite(TestSuite testSuite) {
        em.remove(em.contains(testSuite) ? testSuite : em.merge(testSuite));
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
