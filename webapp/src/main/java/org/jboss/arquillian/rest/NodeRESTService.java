/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.rest;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
       toAdd.setWord(word);
       Node returnNode = nodeManager.addNode(toAdd);
       return Response.ok(returnNode,MediaType.APPLICATION_JSON).build();
    }
    
    @PUT
    @Path("/{parentId:[0-9][0-9]*}/{childId:[0-9][0-9]*}")
    public Response addChildToNode(@PathParam("parentId")Long parentId, @PathParam("childId")Long childId){
        Node parentNode = nodeManager.getNode(parentId);
        Node childNode = nodeManager.getNode(childId);
        if (!parentNode.isParent()){
            parentNode.setParent(parentNode);
        }
        Set<Node> children = new HashSet<>(parentNode.getChildren());
        children.add(childNode);
        parentNode.setChildren(children);
        childNode.setParent(parentNode);
        nodeManager.updateNode(childNode);
        parentNode = nodeManager.updateNode(parentNode);
        return Response.ok().build();
    }
    @GET
    @Path("/{nodeId:[0-9][0-9]*}")
    public Response getNode(@PathParam("nodeId")long nodeId){
        Node toReturn = nodeManager.getNode(nodeId);
        return toReturn != null ? Response.ok(toReturn,MediaType.APPLICATION_JSON).build() : Response.serverError().build();
    }
    
}
