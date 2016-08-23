/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.logging.Logger;
import org.jboss.arquillian.event.CrawlMaskToSuiteEvent;
import org.jboss.arquillian.event.DeleteMaskFromSuiteEvent;

/**
 *
 * @author spriadka
 */
@Path("/masks")
@RequestScoped
public class MaskRESTService {

    @Inject
    private MaskManager maskManager;
    
    @Inject
    private Event<CrawlMaskToSuiteEvent> crawlEvent;
    
    @Inject
    private Event<DeleteMaskFromSuiteEvent> deleteEvent;

    @Inject
    private JCRBean jcrBean;

    private Logger LOGGER = Logger.getLogger(MaskRESTService.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void createMask(Mask mask) {
        Mask result = maskManager.createMask(mask);
        addMaskToDatabase(result);
        //LOGGER.info("Mask(s) in database created");
        addMaskToSuite(result);
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{maskID: mask[0-9][0-9]*}")
    public Mask getMask(@PathParam("maskID")String maskId){
        return maskManager.getMask(maskId);
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pattern/{patternID: [0-9][0-9]*}")
    public List<Mask> getMasks(@PathParam("patternID")long patternID){
        return maskManager.getMasksForPattern(patternID);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{maskID: mask[0-9][0-9]*}")
    public Response deleteMask(@PathParam("maskID")String maskId) {
        Mask toRemove = maskManager.getMask(maskId);
        deleteMaskFromSuite(toRemove);
        deleteMaskFromDatabase(toRemove);
        jcrBean.deleteMaskFromJCR(toRemove);
        return Response.ok().build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMask(Mask mask){
        LOGGER.info(mask.getMaskID());
        LOGGER.info(mask.getSourceUrl());
        Mask updatedMask = maskManager.updateMask(mask);
        jcrBean.updateMaskSource(updatedMask);
        return Response.ok().build();
    }
    
    private void deleteMaskFromSuite(Mask mask){
        String name = mask.getTestSuiteName() + ":" + mask.getPattern().getName();
        deleteEvent.fire(new DeleteMaskFromSuiteEvent(mask,jcrBean.getDescriptorForMask(mask)));
    }
    
    private void deleteMaskFromDatabase(Mask mask){
        maskManager.deleteMask(mask);
    }

    private void addMaskToDatabase(Mask mask) {
        jcrBean.getMaskUrlFromData(mask);
        maskManager.updateMask(mask);
    }
    
    private void addMaskToSuite(Mask mask){
        String name = mask.getTestSuiteName() + ":" + mask.getPattern().getName();
        System.out.println("--------------------------");
        crawlEvent.fire(new CrawlMaskToSuiteEvent(mask,jcrBean.getDescriptorForMask(mask)));
        
    }

}
