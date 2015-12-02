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
    private SampleManager sampleManager;
    
    @Inject 
    private TestSuiteManager testSuiteManager;
    
    @Inject
    private EntityManager em;
    
    public Mask createMask(Mask mask){
        em.persist(mask);
        return mask;
    }
    
    public void deleteMask(Mask mask){
        em.remove(em.contains(mask) ? mask : em.merge(mask));
    }
    
    public Mask getMask(String maskId){
        return em.find(Mask.class, maskId);
    }
    
    public List<Mask> getMasksForSuite(long testSuiteId){
        Query query = em.createQuery("SELECT m FROM MASK m WHERE m.testSuite.testSuiteID = :testSuiteID");
        return query.setParameter("testSuiteID", testSuiteId).getResultList();
    }
    
    public Mask updateMask(Mask mask){
        Mask maskToBeUpdated = em.find(Mask.class, mask.getMaskID());
        maskToBeUpdated.setSourceData(mask.getSourceData());
        maskToBeUpdated.setHeight(mask.getHeight());
        maskToBeUpdated.setLeft(mask.getLeft());
        maskToBeUpdated.setWidth(mask.getWidth());
        maskToBeUpdated.setTop(mask.getTop());
        maskToBeUpdated.setSourceUrl(mask.getSourceUrl());
        return maskToBeUpdated;
    }
    
    public List<Mask> getMasksForSample(Long sampleId){
        Query query = em.createQuery("SELECT m FROM MASK m WHERE m.sample.sampleID = :sampleId");
        return query.setParameter("sampleId", sampleId).getResultList();
    }
    
    
}
