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
import javax.persistence.Subgraph;
import org.hibernate.Session;
import org.jboss.arquillian.model.meta.TestSuite_;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.arquillian.model.testSuite.TestSuite;

/**
 *
 * @author jhuska
 */
@Stateless
public class TestSuiteManager {

    @Inject
    private EntityManager em;

    public TestSuite findById(long id, String params) {
        EntityGraph<TestSuite> graph = null;
        boolean added = false;
        if (!params.equals("")) {
            graph = em.createEntityGraph(TestSuite.class);
            for (String field : params.split(",")) {
                switch (field) {
                    case "rootNode":
                        added = true;
                        Subgraph<Node> rootNode = graph.addSubgraph("rootNode");
                        rootNode.addAttributeNodes("children");
                        break;
                    case "runs":
                    case "patterns":
                        added = true;
                        graph.addAttributeNodes(field);
                        break;
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
        Session session = (Session)em.getDelegate();
        session.enableFilter("lastRunFilter");
        List result = em.createQuery("SELECT e FROM TEST_SUITE e JOIN FETCH e.runs").getResultList();
        session.close();
        return new LinkedHashSet<>(result);
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
