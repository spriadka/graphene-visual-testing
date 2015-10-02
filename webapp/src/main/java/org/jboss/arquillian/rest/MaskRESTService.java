/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.New;
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
import org.arquillian.graphene.visual.testing.api.MaskFromREST;
import org.arquillian.graphene.visual.testing.api.builder.MaskFromRESTBuilder;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.logging.Logger;
import org.arquillian.graphene.visual.testing.api.event.CrawlMaskToSuiteEvent;
import org.arquillian.graphene.visual.testing.api.event.DeleteMaskFromSuiteEvent;
import org.arquillian.graphene.visual.testing.impl.JCRMaskHandler;
import org.jboss.rusheye.suite.MaskType;

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
    private Event<CrawlMaskToSuiteEvent> crawlMaskEvent;
    
    @Inject
    @New
    private Instance<JCRMaskHandler> maskHandler;

    @Inject
    private JCRBean jcrBean;

    private static Logger LOGGER = Logger.getLogger(MaskRESTService.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void createMask(Mask mask) {
        Mask result = maskManager.createMask(mask);
        addMaskToDatabase(result);
        LOGGER.info("Mask(s) in database created");
        addMaskToSuite(mask);
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{maskID: [0-9][0-9]*}")
    public Mask getMask(@PathParam("maskID")long maskId){
        return maskManager.getMask(maskId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{maskID: [0-9][0-9]*}")
    public Response deleteMask(@PathParam("maskID")long maskId) {
        Mask toRemove = maskManager.getMask(maskId);
        deleteMaskFromDatabase(toRemove);
        deleteMaskFromJCR(toRemove);
        return Response.ok().build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMask(Mask mask){
        maskManager.updateMask(mask);
        jcrBean.updateMaskSource(mask);
        return Response.ok().build();
    }
    
    private void deleteMaskFromJCR(Mask mask){
        String name = mask.getTestSuite().getName() + ":" + mask.getSample().getName();
        MaskFromREST maskFromREST = new MaskFromRESTBuilder().id(mask.getMaskID())
                .name(name)
                .sourceUrl(mask.getSourceUrl())
                .horizontalAlign(mask.getHorizotalAlignment())
                .verticalAlign(mask.getVerticalAlignment())
                .maskType(MaskType.SELECTIVE_ALPHA)
                .build();
        maskHandler.get().deleteMasks(new DeleteMaskFromSuiteEvent(maskFromREST, jcrBean.getDescriptorForMask(mask)));
    }
    
    private void deleteMaskFromDatabase(Mask mask){
        maskManager.deleteMask(mask);
    }

    private void addMaskToDatabase(Mask mask) {
        jcrBean.getMaskUrlFromData(mask);
        maskManager.updateMask(mask);
    }
    
    private void addMaskToSuite(Mask mask){
        List<MaskFromREST> masksToBeCrawled = new ArrayList<>();
        String name = mask.getTestSuite().getName() + ":" + mask.getSample().getName();
        MaskFromREST maskFromREST = new MaskFromRESTBuilder()
                .id(mask.getMaskID())
                .name(name)
                .verticalAlign(mask.getVerticalAlignment())
                .horizontalAlign(mask.getHorizotalAlignment())
                .maskType(MaskType.SELECTIVE_ALPHA)
                .sourceUrl(mask.getSourceUrl())
                .build();
        masksToBeCrawled.add(maskFromREST);
        System.out.println("--------------------------");
        maskHandler.get().uploadMasks(new CrawlMaskToSuiteEvent(masksToBeCrawled, jcrBean.getDescriptorForMask(mask)));
        //crawlMaskEvent.fire(new CrawlMaskToSuiteEvent(masksToBeCrawled, jcrBean.getDescriptorForMask(mask)));
        
    }

}
