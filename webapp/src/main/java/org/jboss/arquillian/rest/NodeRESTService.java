/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.managers.NodeManager;
import org.jboss.arquillian.model.routing.Node;
import org.jboss.arquillian.model.routing.Word;

/**
 *
 * @author spriadka
 */
@RequestScoped
@Path("/nodes")
public class NodeRESTService {
    
    @Inject
    private NodeManager nodeManager;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNode(Word word){
       Node toAdd = new Node();
       toAdd.setWordId(word);
       nodeManager.addNode(toAdd);
       return Response.ok(toAdd,MediaType.APPLICATION_JSON).build();
    }
    
}
