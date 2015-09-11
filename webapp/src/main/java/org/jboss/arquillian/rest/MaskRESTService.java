/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.arquillian.graphene.visual.testing.api.MaskFromREST;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.logging.Logger;
import org.arquillian.graphene.visual.testing.api.event.CrawlMaskToSuiteEvent;
import org.jboss.arquillian.core.api.Event;

/**
 *
 * @author spriadka
 */
@Path("/masks")
public class MaskRESTService {

    @Inject
    private MaskManager maskManager;
    
    @Resource(lookup = "java:org.jboss.arquillian.core.api.Event")
    private Event<CrawlMaskToSuiteEvent> crawlMaskEvent;

    @Inject
    private JCRBean jcrBean;

    private static Logger LOGGER = Logger.getLogger(MaskRESTService.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void createMask(Mask mask) {
        LOGGER.info("called post request");
        Mask result = maskManager.createMask(mask);
        addMaskToDatabase(result);
        LOGGER.info("Mask(s) in database created");
        //addMaskToSuite(mask);        
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{maskID: [0-9][0-9]*}")
    public Response deleteMask(@PathParam("maskID") long maskId) {
        Mask toRemove = maskManager.getMask(maskId);
        maskManager.deleteMask(toRemove);
        return Response.ok().build();
    }

    private void addMaskToDatabase(Mask mask) {
        jcrBean.getMaskUrlFromData(mask);
        maskManager.updateMask(mask);
    }
    
    private void addMaskToSuite(Mask mask){
        List<MaskFromREST> masksToBeCrawled = new ArrayList<>();
        MaskFromREST maskFromREST = new MaskFromREST();
        maskFromREST.setId(mask.getMaskID());
        maskFromREST.setName(mask.getTestSuite().getName() + ":" + mask.getSample().getName());
        maskFromREST.setSourceUrl(mask.getSourceUrl());
        maskFromREST.setHorizontalAlign(mask.getHorizotalAlignment());
        maskFromREST.setVerticalAlign(mask.getVerticalAlignment());
        masksToBeCrawled.add(maskFromREST);
        CrawlMaskToSuiteEvent tobeFired = new CrawlMaskToSuiteEvent(masksToBeCrawled, jcrBean.getDescriptorForMask(mask));
        System.out.println("--------------------------");
        crawlMaskEvent.fire(new CrawlMaskToSuiteEvent(masksToBeCrawled, jcrBean.getDescriptorForMask(mask)));
        
    }

}
