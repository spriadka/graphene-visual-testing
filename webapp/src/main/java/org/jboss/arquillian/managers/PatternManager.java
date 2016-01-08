package org.jboss.arquillian.managers;

import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.jboss.arquillian.model.testSuite.Pattern;

/**
 *
 * @author jhuska
 */
@Stateless
public class PatternManager {

    @Inject
    private EntityManager em;
    
    private Logger LOGGER = Logger.getLogger(PatternManager.class.getName());
    
    public Pattern createPattern(Pattern pattern) {
        em.persist(pattern);
        return pattern;
    }
    
    public void updatePattern(Pattern pattern){
        em.merge(pattern);
    }
    
    public Pattern getPattern(Long patternID){
        Query query = em.createQuery("SELECT p FROM PATTERN p WHERE p.patternID = :patternID");
        query.setParameter("patternID", patternID);
        return (Pattern)query.getSingleResult();
    }
    
    public Pattern getPattern(String nameOfPattern, Long testSuiteID) {
        Query query = 
                em.createQuery("SELECT e FROM PATTERN e WHERE e.name = :name AND e.testSuite.testSuiteID = :testSuiteID");
        query.setParameter("name", nameOfPattern);
        query.setParameter("testSuiteID", testSuiteID);
        Pattern result = null;
        try {
            result = (Pattern) query.getSingleResult();
        } catch (NoResultException ex) {
            //OK
        }
        return result;
    }
    
    public List<Pattern> getPatterns(Long testSuiteID) {
        Query query = em.createQuery("SELECT e FROM PATTERN e WHERE e.testSuite.testSuiteID = :testSuiteID");
        query.setParameter("testSuiteID", testSuiteID);
        return query.getResultList();
    }
}
