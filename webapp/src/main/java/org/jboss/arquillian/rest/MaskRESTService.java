/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.bean.JCRBean;
import org.jboss.arquillian.managers.MaskManager;
import org.jboss.arquillian.model.testSuite.Mask;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
@Path("/masks")
public class MaskRESTService {
    
    @Inject
    private MaskManager maskManager;
    
    @Inject
    private JCRBean jcrBean;
    
    private static Logger LOGGER = Logger.getLogger(MaskRESTService.class.getName());
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Mask createMask(Mask mask){
        Mask result = maskManager.createMask(mask);
        jcrBean.getMaskUrlFromData(result);
        maskManager.updateMask(result);
        return result;
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{maskID: [0-9][0-9]*}")
    public Response deleteMask(@PathParam("maskID")long maskId){
        Mask toRemove = maskManager.getMask(maskId);
        maskManager.deleteMask(toRemove);
        return Response.ok().build();
    }
    
}
