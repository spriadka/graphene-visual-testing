/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.managers;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.jboss.arquillian.model.testSuite.Mask;

/**
 *
 * @author spriadka
 */

@Stateless
public class MaskManager {
    
    @Inject
    private EntityManager em;
    
    public Mask createMask(Mask mask){
        em.persist(mask);
        return mask;
    }
    
    public void deleteMask(Mask mask){
        em.remove(em.contains(mask) ? mask : em.merge(mask));
    }
    
    public Mask getMask(long maskId){
        return em.find(Mask.class, maskId);
    }
    
    public List<Mask> getMasksForSuite(long testSuiteId){
        Query query = em.createQuery("SELECT m FROM MASK m WHERE m.testSuite.testSuiteID = :testSuiteID");
        return query.setParameter("testSuiteID", testSuiteId).getResultList();
    }
    
    public void updateMask(Mask mask){
        em.merge(mask);
    }
    
}
