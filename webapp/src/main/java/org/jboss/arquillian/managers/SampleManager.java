package org.jboss.arquillian.managers;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.arquillian.model.testSuite.Sample;

/**
 *
 * @author jhuska
 */
@Stateless
public class SampleManager {
    
    @Inject
    private EntityManager em;
    
    public Sample findById(long id) {
        return em.find(Sample.class, id);
    }
    
    public Sample createTestSuiteRun(Sample sample) {
        em.persist(sample);
        return sample;
    }
    
    public void deleteSample(Sample sample) {
        em.remove(em.contains(sample) ? sample : em.merge(sample));
    }
    
    public List<Sample> getSamples(Long testSuiteRunID){
        Query query = em.createQuery("SELECT s FROM SAMPLE s WHERE s.testSuiteRun.testSuiteRunID = :testSuiteRunID");
        query.setParameter("testSuiteRunID", testSuiteRunID);
        return query.getResultList();
    }
}
