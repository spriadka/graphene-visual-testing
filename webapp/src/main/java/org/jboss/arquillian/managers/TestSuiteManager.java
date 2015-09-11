package org.jboss.arquillian.managers;

import java.util.List;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

    public TestSuite findById(long id) {
        return em.find(TestSuite.class, id);
    }

    public List<TestSuite> getAllTestSuites() {
        Query query = em.createQuery("SELECT e FROM TEST_SUITE e");
        List<TestSuite> result = query.getResultList();
        return result;
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
